package com.example.beatme.ui.tournaments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beatme.R;
import com.example.beatme.ui.tournaments.Fragment.BracketsFragment;

import java.util.List;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.ViewHolder> {

    private static final String TAG = "TournamentAdapter";
    private final List<Tournament> tournamentList;
    private final int currentTab;
    private final FragmentManager fragmentManager;

    public TournamentAdapter(List<Tournament> tournamentList, int currentTab, FragmentManager fragmentManager) {
        this.tournamentList = tournamentList;
        this.currentTab = currentTab;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View tournamentItem = layoutInflater.inflate(R.layout.tournament_item, parent, false);
        return new ViewHolder(tournamentItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tournament tournament = tournamentList.get(position);
        String label = tournament.getTournamentName();
        String by = "By: "+tournament.getUser();
        holder.tournamentLabel.setText(label);
        holder.tournamentUsername.setText(by);
        holder.constraintLayout.setOnClickListener(v -> {
            BracketsFragment bracketsFragment = new BracketsFragment(tournament);
            bracketsFragment.show(fragmentManager, "dialogTournament");
        });
    }

    @Override
    public int getItemCount() {
        return tournamentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tournamentLabel;
        public TextView tournamentUsername;
        public androidx.constraintlayout.widget.ConstraintLayout constraintLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tournamentLabel = itemView.findViewById(R.id.tournamentLabel);
            this.tournamentUsername = itemView.findViewById(R.id.tournamentUsername);
            this.constraintLayout = itemView.findViewById(R.id.tournament_item);
        }
    }

}
