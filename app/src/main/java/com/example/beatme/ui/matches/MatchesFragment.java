package com.example.beatme.ui.matches;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.beatme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MatchesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final String TAG = "MatchFragment";
    View root;
    List<Match> matchList;
    MatchAdapter matchAdapter;
    RecyclerView matchRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser user;
    FirebaseFirestore db;
    LinearProgressIndicator linearProgressIndicator;
    int currentTab = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        root = inflater.inflate(R.layout.fragment_matches, container, false);
        FloatingActionButton matchesFb = root.findViewById(R.id.matches_floating_action_button);
        TabLayout matchesTL = root.findViewById(R.id.matchesTabLayout);
        linearProgressIndicator = root.findViewById(R.id.matches_load_progress);
        matchRecyclerView = root.findViewById(R.id.matches_recycler_view);
        swipeRefreshLayout = root.findViewById(R.id.matches_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.black),
                getResources().getColor(android.R.color.white),
                getResources().getColor(android.R.color.darker_gray),
                getResources().getColor(android.R.color.white));
        getAllMatches();

        matchesTL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    currentTab = 0;
                    getAllMatches();
                }
                if (tab.getPosition() == 1) {
                    currentTab = 1;
                    getCurrentUsersMatches();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    currentTab = 0;
                    getAllMatches();
                }
                if (tab.getPosition() == 1) {
                    currentTab = 1;
                    getCurrentUsersMatches();
                }
            }
        });


        matchesFb.setOnClickListener(v -> {
            AddMatchFragment addMatchFragment = new AddMatchFragment();
            addMatchFragment.show(getParentFragmentManager(), "dialogAddMatch");
        });

        return root;
    }

    @Override
    public void onRefresh() {
        if (currentTab == 0) getAllMatches();
        if (currentTab == 1) getCurrentUsersMatches();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void getAllMatches() {
        linearProgressIndicator.setVisibility(View.VISIBLE);
        List<Match> list = new ArrayList<>();
        if (user != null) {
            db.collection("matches").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {

                        Log.d(TAG, "SEE_THIS_SEE_THIS_SEE_THIS" + documentSnapshot.getId() + " => " + documentSnapshot.getData());
                        String match_uid = documentSnapshot.getId();
                        String userString;
                        if (Objects.equals(user.getEmail(), documentSnapshot.get("user"))) {
                            userString = "You";
                        } else {
                            userString = (String) documentSnapshot.get("user");
                        }
                        String team1 = (String) documentSnapshot.get("team1");
                        String team2 = (String) documentSnapshot.get("team2");
                        String status = (String) documentSnapshot.get("status");
                        String duration = (String) documentSnapshot.get("duration");

                        Match match = new Match(match_uid, userString, team1, team2, status, duration);
                        list.add(match);
                    }
                    matchAdapter = new MatchAdapter(list, currentTab, getChildFragmentManager());
                    initializeRecyclerView(matchRecyclerView, matchAdapter, list);

                } else {
                    Log.d(TAG, "Error getting data", task.getException());
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                linearProgressIndicator.setVisibility(View.INVISIBLE);
            });
        }
    }

    public void getCurrentUsersMatches() {
        linearProgressIndicator.setVisibility(View.VISIBLE);
        List<Match> list = new ArrayList<>();
        if (user != null) {
            db.collection("matches").whereEqualTo("user", user.getEmail()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        Log.d(TAG, "SEE_THIS_SEE_THIS_SEE_THIS" + documentSnapshot.getId() + " => " + documentSnapshot.getData());

                        String match_uid = documentSnapshot.getId();
                        String userString;
                        if (Objects.equals(user.getEmail(), documentSnapshot.get("user"))) {
                            userString = "You";
                        } else {
                            userString = (String) documentSnapshot.get("user");
                        }
                        String team1 = (String) documentSnapshot.get("team1");
                        String team2 = (String) documentSnapshot.get("team2");
                        String status = (String) documentSnapshot.get("status");
                        String duration = (String) documentSnapshot.get("duration");

                        Match match = new Match(match_uid, userString, team1, team2, status, duration);
                        list.add(match);
                    }

                    matchAdapter = new MatchAdapter(list, currentTab, getChildFragmentManager());
                    initializeRecyclerView(matchRecyclerView, matchAdapter, list);

                } else {
                    Log.d(TAG, "Error getting data", task.getException());
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                linearProgressIndicator.setVisibility(View.INVISIBLE);
            });
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private final ClickListener clicklistener;
        private final GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(@NotNull RecyclerView rv, @NotNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void initializeRecyclerView(RecyclerView recyclerView, MatchAdapter adapter, List<Match> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(currentTab == 0) {
                    MatchView matchView = new MatchView(list.get(position).getMatch_uid());
                    matchView.show(getChildFragmentManager(), "dialogMatchView");
                }
                if(currentTab == 1){
                    UpdateMatchFragment updateMatchFragment = new UpdateMatchFragment(list.get(position).getMatch_uid());
                    updateMatchFragment.show(getChildFragmentManager(), "dialogUpdateMatch");
                }
            }
        })
        );
        */


    }
}