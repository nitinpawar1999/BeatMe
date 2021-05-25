package com.example.beatme.ui.tournaments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class TournamentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "TournamentFragment";
    View root;
    TournamentAdapter tournamentAdapter;
    RecyclerView tournamentRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser user;
    FirebaseFirestore db;
    LinearProgressIndicator linearProgressIndicator;
    int currentTab = 0;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        root = inflater.inflate(R.layout.fragment_tournaments, container, false);
        FloatingActionButton tournament_fb = root.findViewById(R.id.tournament_fb);
        TabLayout tournamentTL = root.findViewById(R.id.tournamentTabLayout);
        linearProgressIndicator = root.findViewById(R.id.tournament_load_progress);
        tournamentRecyclerView = root.findViewById(R.id.tournament_recycler_view);
        swipeRefreshLayout = root.findViewById(R.id.tournament_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.black),
                getResources().getColor(android.R.color.white),
                getResources().getColor(android.R.color.darker_gray),
                getResources().getColor(android.R.color.white));
        getAllTournaments();

        tournamentTL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    currentTab = 0;
                    getAllTournaments();
                }
                if (tab.getPosition() == 1) {
                    currentTab = 1;
                    getCurrentUsersTournaments();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    currentTab = 0;
                    getAllTournaments();
                }
                if (tab.getPosition() == 1) {
                    currentTab = 1;
                    getCurrentUsersTournaments();
                }
            }
        });
        tournament_fb.setOnClickListener(v -> {
            AddTournamentFragment addTournamentFragment = new AddTournamentFragment();
            addTournamentFragment.show(getParentFragmentManager(), "dialogAddTournament");
        });
        return root;
    }

    public void getAllTournaments() {
        linearProgressIndicator.setVisibility(View.VISIBLE);
        List<Tournament> list = new ArrayList<>();
        if(user != null){
            db.collection("tournaments").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String tournament_uid = documentSnapshot.getId();
                        String userString;
                        if (Objects.equals(user.getEmail(), documentSnapshot.get("user"))) {
                            userString = "You";
                        } else {
                            userString = (String) documentSnapshot.get("user");
                        }
                        String tournamentName = (String) documentSnapshot.get("tournamentName");
                        long teamCount = (long) documentSnapshot.get("teamCount");

                        //Log.d(TAG, documentSnapshot.toString());
                        List<Map<String, Object>> matches = new ArrayList<>();
                        db.collection("tournaments").document(documentSnapshot.getId()).collection("matches").get().addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot1 : task1.getResult()){
                                TreeMap<String, Object> sorted = new TreeMap<>();
                                sorted.putAll(documentSnapshot1.getData());
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
                        });

                        Tournament tournament = new Tournament(tournament_uid, userString, tournamentName, teamCount, matches);
                        list.add(tournament);
                    }
                    tournamentAdapter = new TournamentAdapter(list, currentTab, getChildFragmentManager());
                    initializeRecyclerView(tournamentRecyclerView, tournamentAdapter);
                } else {
                    Log.d(TAG, "Error getting data", task.getException());
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                linearProgressIndicator.setVisibility(View.INVISIBLE);
            });
        }

    }

    public void getCurrentUsersTournaments() {
        linearProgressIndicator.setVisibility(View.VISIBLE);
        List<Tournament> list = new ArrayList<>();
        String[] matches_uid = new String[1];
        if(user != null){
            db.collection("tournaments").whereEqualTo("user", user.getEmail()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String tournament_uid = documentSnapshot.getId();
                        String userString;
                        if (Objects.equals(user.getEmail(), documentSnapshot.get("user"))) {
                            userString = "You";
                        } else {
                            userString = (String) documentSnapshot.get("user");
                        }
                        String tournamentName = (String) documentSnapshot.get("tournamentName");
                        long teamCount = (long) documentSnapshot.get("teamCount");

                        List<Map<String, Object>> matches = new ArrayList<>();
                        db.collection("tournaments").document(documentSnapshot.getId()).collection("matches").get().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                for(QueryDocumentSnapshot documentSnapshot1 : task1.getResult()){
                                    TreeMap<String, Object> sorted = new TreeMap<>();
                                    sorted.putAll(documentSnapshot1.getData());
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
                        });

                        Tournament tournament = new Tournament(tournament_uid, userString, tournamentName, teamCount, matches);
                        list.add(tournament);
                    }
                    tournamentAdapter = new TournamentAdapter(list, currentTab, getChildFragmentManager());
                    initializeRecyclerView(tournamentRecyclerView, tournamentAdapter);
                } else {
                    Log.d(TAG, "Error getting data", task.getException());
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                linearProgressIndicator.setVisibility(View.INVISIBLE);
            });
        }
    }


    public void initializeRecyclerView(@NotNull RecyclerView recyclerView, TournamentAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        if (currentTab == 0) getAllTournaments();
        if (currentTab == 1) getCurrentUsersTournaments();
        swipeRefreshLayout.setRefreshing(false);
    }
}