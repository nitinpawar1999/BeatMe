package com.example.beatme.ui.tournaments.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.beatme.R;
import com.example.beatme.ui.tournaments.Fragment.BracketsColumnFragment;
import com.example.beatme.ui.tournaments.UpdateTournamentFragment;
import com.example.beatme.ui.tournaments.model.MatchData;
import com.example.beatme.ui.tournaments.viewholder.BracketsCellViewHolder;

import java.util.ArrayList;

public class BracketsCellAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private BracketsColumnFragment fragment;
    private Context context;
    private ArrayList<MatchData> list;
    private boolean handler;
    private String tournament_uid;

    public BracketsCellAdapter(BracketsColumnFragment bracketsColumnFragment, Context context, ArrayList<MatchData> list, String tournament_uid) {

        this.fragment = bracketsColumnFragment;
        this.context = context;
        this.list = list;
        this.tournament_uid = tournament_uid;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_cell_brackets, parent, false);
        return new BracketsCellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BracketsCellViewHolder viewHolder = null;
        if (holder instanceof BracketsCellViewHolder){
            viewHolder = (BracketsCellViewHolder) holder;
            setFields(viewHolder, position);
            viewHolder.rootLayout.setOnClickListener(v->{
                UpdateTournamentFragment updateTournamentFragment = new UpdateTournamentFragment(list.get(position), tournament_uid);
                updateTournamentFragment.show(fragment.getChildFragmentManager(), "dialogScoreTournament");

            });
        }


    }

    private void setFields(final BracketsCellViewHolder viewHolder, final int position) {
        handler = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewHolder.setAnimation(list.get(position).getHeight());
            }
        }, 100);

        viewHolder.getTeamOneName().setText(list.get(position).getCompetitorOne().getName());
        viewHolder.getTeamTwoName().setText(list.get(position).getCompetitorTwo().getName());
        viewHolder.getTeamOneScore().setText(list.get(position).getCompetitorOne().getScore());
        viewHolder.getTeamTwoScore().setText(list.get(position).getCompetitorTwo().getScore());
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void setList(ArrayList<MatchData> colomnList) {
        this.list = colomnList;
        notifyDataSetChanged();
    }
}
