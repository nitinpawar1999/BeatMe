package com.example.beatme.ui.tournaments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.beatme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TournamentsFragment extends Fragment {

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tournaments, container, false);
        FloatingActionButton tournament_fb = root.findViewById(R.id.tournament_fb);



        tournament_fb.setOnClickListener(v -> {
            AddTournamentFragment addTournamentFragment = new AddTournamentFragment();
            addTournamentFragment.show(getParentFragmentManager(), "dialogAddTournament");
        });
        return root;
    }
}