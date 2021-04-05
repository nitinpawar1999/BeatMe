package com.example.beatme.ui.matches;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beatme.R;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {

    private final List<Match> matchData;
    private final int currentTab;
    private final FragmentManager fragmentManager;

    public MatchAdapter(List<Match> matchData, int currentTab, FragmentManager fragmentManager) {
        this.matchData = matchData;
        this.currentTab = currentTab;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View matchItem = layoutInflater.inflate(R.layout.match_item, parent, false);
        return new ViewHolder(matchItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Match match = matchData.get(position);
        String label = match.getTeam1() + " vs " + match.getTeam2();
        String by = "By: " + match.getUser();
        holder.matchLabel.setText(label);
        holder.matchUsername.setText(by);
        holder.matchStatus.setText(matchData.get(position).getStatus());
        holder.constraintLayout.setOnClickListener(v -> {
            if(currentTab == 0) {
                MatchView matchView = new MatchView(matchData.get(position).getMatch_uid());
                matchView.show(fragmentManager, "dialogMatchView");
            }
            if(currentTab == 1){
                UpdateMatchFragment updateMatchFragment = new UpdateMatchFragment(matchData.get(position).getMatch_uid());
                updateMatchFragment.show(fragmentManager, "dialogUpdateMatch");
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView matchLabel;
        public TextView matchUsername;
        public TextView matchStatus;
        public androidx.constraintlayout.widget.ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.matchLabel = itemView.findViewById(R.id.matchLabel);
            this.matchUsername = itemView.findViewById(R.id.matchUsername);
            this.matchStatus = itemView.findViewById(R.id.matchStatus);
            this.constraintLayout = itemView.findViewById(R.id.match_item);
        }
    }
}
