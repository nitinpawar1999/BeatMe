package com.example.beatme.ui.matches;

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
import java.util.List;
import java.util.Objects;

public class MatchesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final String TAG = "MatchFragment";
    View root;
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
                    initializeRecyclerView(matchRecyclerView, matchAdapter);

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
                    initializeRecyclerView(matchRecyclerView, matchAdapter);

                } else {
                    Log.d(TAG, "Error getting data", task.getException());
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                linearProgressIndicator.setVisibility(View.INVISIBLE);
            });
        }
    }

    public void initializeRecyclerView(@NotNull RecyclerView recyclerView, MatchAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }
}