package com.example.beatme.ui.tournaments.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.beatme.R;
import com.example.beatme.ui.tournaments.Tournament;
import com.example.beatme.ui.tournaments.adapter.BracketsSectionAdapter;
import com.example.beatme.ui.tournaments.customviews.WrapContentHeightViewPager;
import com.example.beatme.ui.tournaments.model.ColumnData;
import com.example.beatme.ui.tournaments.model.CompetitorData;
import com.example.beatme.ui.tournaments.model.MatchData;
import com.example.beatme.ui.tournaments.utility.BracketsUtility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class BracketsFragment extends DialogFragment implements ViewPager.OnPageChangeListener {

    private static final String TAG = "BracketFragment";
    private WrapContentHeightViewPager viewPager;
    private BracketsSectionAdapter sectionAdapter;
    private ArrayList<ColumnData> sectionList;
    private int mNextSelectedScreen;
    private int mCurrentPagerState;
    private Tournament tournament;
    public BracketsFragment(Tournament tournament){
        super();
        this.tournament = tournament;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View bracketsFragment = inflater.inflate(R.layout.fragment_brackets, container, false);

        FloatingActionButton fb = bracketsFragment.findViewById(R.id.brackets_floating_action_button);

        fb.setOnClickListener(v -> {
            List<Map<String, Object>> matches = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                db.collection("tournaments").document(tournament.getTournament_uid()).collection("matches").get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                           TreeMap<String, Object> sorted = new TreeMap<>();
                           sorted.putAll(documentSnapshot.getData());
                           for(Map.Entry<String, Object> entry : sorted.entrySet()){
                               Map<String, Object> map = new HashMap<>();
                               map.put(entry.getKey(), entry.getValue());
                               matches.add(map);
                           }
                       }
                   }else {
                       Log.d(TAG, "Error getting data", task.getException());
                       Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                   }
                   tournament.setMatches(matches);
                    initViews();
                    setData();
                    intialiseViewPagerAdapter();
                });
            }
        });

        return bracketsFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        setData();
        intialiseViewPagerAdapter();
    }

    private void setData() {

        List<Map<String, Object>> matches = tournament.getMatches();


        sectionList = new ArrayList<>();
        int sectionCount = 1;
        long teamCount = tournament.getTeamCount();

        if(teamCount>2 && teamCount<=4){
            sectionCount = 2;
        }else if(teamCount>4 && teamCount<=8){
            sectionCount = 3;
        }else if(teamCount>8 && teamCount<=16){
            sectionCount = 4;
        }else if(teamCount>16 && teamCount<=32){
            sectionCount = 5;
        }

        int current = 0;
        for(int i=sectionCount;i>=1;i--){
            ArrayList<MatchData> ColumnMatchesList = new ArrayList<>();
            switch (i){
                case 1:{
                    Map<String, Object> tempMap = matches.get(current);
                    for(Entry<String, Object> entry: tempMap.entrySet()){
                        List<Map<String, String>> temp = (List<Map<String, String>>) entry.getValue();
                        MatchData matchData = new MatchData(new CompetitorData(temp.get(0).get("TeamName"), temp.get(0).get("Score")),new CompetitorData(temp.get(1).get("TeamName"), temp.get(1).get("Score")));
                        matchData.setMatch_id(entry.getKey());
                        matchData.setUser(tournament.getEmail());
                        ColumnMatchesList.add(matchData);
                    }
                    break;
                }
                case 2:{
                    for(int j=0;j<2;j++){
                        Map<String, Object> tempMap = matches.get(current+j);
                        for(Entry<String, Object> entry: tempMap.entrySet()){
                            List<Map<String, String>> temp = (List<Map<String, String>>) entry.getValue();
                            MatchData matchData = new MatchData(new CompetitorData(temp.get(0).get("TeamName"), temp.get(0).get("Score")),new CompetitorData(temp.get(1).get("TeamName"), temp.get(1).get("Score")));
                            matchData.setMatch_id(entry.getKey());
                            matchData.setUser(tournament.getEmail());
                            ColumnMatchesList.add(matchData);
                        }
                    }
                    current+=2;
                    break;
                }
                case 3:{
                    for(int j=0;j<4;j++){
                        Map<String, Object> tempMap = matches.get(current+j);
                        for(Entry<String, Object> entry: tempMap.entrySet()){
                            List<Map<String, String>> temp = (List<Map<String, String>>) entry.getValue();
                            MatchData matchData = new MatchData(new CompetitorData(temp.get(0).get("TeamName"), temp.get(0).get("Score")),new CompetitorData(temp.get(1).get("TeamName"), temp.get(1).get("Score")));
                            matchData.setMatch_id(entry.getKey());
                            matchData.setUser(tournament.getEmail());
                            ColumnMatchesList.add(matchData);
                        }
                    }
                    current+=4;
                    break;
                }
                case 4:{
                    for(int j=0;j<8;j++){
                        Map<String, Object> tempMap = matches.get(current+j);
                        for(Entry<String, Object> entry: tempMap.entrySet()){
                            List<Map<String, String>> temp = (List<Map<String, String>>) entry.getValue();
                            MatchData matchData = new MatchData(new CompetitorData(temp.get(0).get("TeamName"), temp.get(0).get("Score")),new CompetitorData(temp.get(1).get("TeamName"), temp.get(1).get("Score")));
                            matchData.setMatch_id(entry.getKey());
                            matchData.setUser(tournament.getEmail());
                            ColumnMatchesList.add(matchData);
                        }
                    }
                    current+=8;
                    break;
                }
                case 5:{
                    for(int j=0;j<16;j++){
                        Map<String, Object> tempMap = matches.get(current+j);
                        for(Entry<String, Object> entry: tempMap.entrySet()){
                            List<Map<String, String>> temp = (List<Map<String, String>>) entry.getValue();
                            MatchData matchData = new MatchData(new CompetitorData(temp.get(0).get("TeamName"), temp.get(0).get("Score")),new CompetitorData(temp.get(1).get("TeamName"), temp.get(1).get("Score")));
                            matchData.setMatch_id(entry.getKey());
                            matchData.setUser(tournament.getEmail());
                            ColumnMatchesList.add(matchData);
                        }
                    }
                    current+=16;
                    break;
                }
            }

            sectionList.add(new ColumnData(ColumnMatchesList));
        }

    }

    private void intialiseViewPagerAdapter() {

        sectionAdapter = new BracketsSectionAdapter(getChildFragmentManager(),this.sectionList, tournament.getTournament_uid());
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(sectionAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setPageMargin(-200);
        viewPager.setHorizontalFadingEdgeEnabled(true);
        viewPager.setFadingEdgeLength(50);

        viewPager.addOnPageChangeListener(this);
    }

    private void initViews() {

        viewPager = (WrapContentHeightViewPager) getView().findViewById(R.id.container);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (mCurrentPagerState != ViewPager.SCROLL_STATE_SETTLING) {
            // We are moving to next screen on right side
            if (positionOffset > 0.5) {
                // Closer to next screen than to current
                if (position + 1 != mNextSelectedScreen) {
                    mNextSelectedScreen = position + 1;
                    //update view here
                    if (getBracketsFragment(position).getColomnList().get(0).getHeight()
                            != BracketsUtility.dpToPx(131))
                        getBracketsFragment(position).shrinkView(BracketsUtility.dpToPx(131));
                    if (getBracketsFragment(position + 1).getColomnList().get(0).getHeight()
                            != BracketsUtility.dpToPx(131))
                        getBracketsFragment(position + 1).shrinkView(BracketsUtility.dpToPx(131));
                }
            } else {
                // Closer to current screen than to next
                if (position != mNextSelectedScreen) {
                    mNextSelectedScreen = position;
                    //updateViewhere

                    if (getBracketsFragment(position + 1).getCurrentBracketSize() ==
                            getBracketsFragment(position + 1).getPreviousBracketSize()) {
                        getBracketsFragment(position + 1).shrinkView(BracketsUtility.dpToPx(131));
                        getBracketsFragment(position).shrinkView(BracketsUtility.dpToPx(131));
                    } else {
                        int currentFragmentSize = getBracketsFragment(position + 1).getCurrentBracketSize();
                        int previousFragmentSize = getBracketsFragment(position + 1).getPreviousBracketSize();
                        if (currentFragmentSize != previousFragmentSize) {
                            getBracketsFragment(position + 1).expandHeight(BracketsUtility.dpToPx(262));
                            getBracketsFragment(position).shrinkView(BracketsUtility.dpToPx(131));
                        }
                    }
                }
            }
        } else {
            // We are moving to next screen left side
            if (positionOffset > 0.5) {
                // Closer to current screen than to next
                if (position + 1 != mNextSelectedScreen) {
                    mNextSelectedScreen = position + 1;
                    //update view for screen

                }
            } else {
                // Closer to next screen than to current
                if (position != mNextSelectedScreen) {
                    mNextSelectedScreen = position;
                    //updateviewfor screem
                }
            }
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public BracketsColumnFragment getBracketsFragment(int position) {
        BracketsColumnFragment bracktsFrgmnt = null;
        if (getChildFragmentManager() != null) {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof BracketsColumnFragment) {
                        bracktsFrgmnt = (BracketsColumnFragment) fragment;
                        if (bracktsFrgmnt.getSectionNumber() == position)
                            break;
                    }
                }
            }
        }
        return bracktsFrgmnt;
    }
}
