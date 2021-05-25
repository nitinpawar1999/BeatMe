package com.example.beatme.ui.tournaments.viewholder;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.beatme.R;
import com.example.beatme.ui.tournaments.animation.SlideAnimation;

public class BracketsCellViewHolder extends RecyclerView.ViewHolder {

    private TextView teamOneName;
    private TextView teamTwoName;
    private TextView teamOneScore;
    private TextView teamTwoScore;
    private Animation animation;
    public RelativeLayout rootLayout;

    public BracketsCellViewHolder(View itemView) {
        super(itemView);
        teamOneName = (TextView) itemView.findViewById(R.id.team_one_name);
        teamTwoName = (TextView) itemView.findViewById(R.id.team_two_name);
        teamOneScore = (TextView) itemView.findViewById(R.id.team_one_score);
        teamTwoScore = (TextView) itemView.findViewById(R.id.team_two_score);
        rootLayout = (RelativeLayout) itemView.findViewById(R.id.layout_root);
    }

    public void setAnimation(int height){
        animation = new SlideAnimation(rootLayout, rootLayout.getHeight(),
                height);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(200);
        rootLayout.setAnimation(animation);
        rootLayout.startAnimation(animation);
    }

    public TextView getTeamTwoName() {
        return teamTwoName;
    }

    public TextView getTeamOneScore() {
        return teamOneScore;
    }

    public TextView getTeamTwoScore() {
        return teamTwoScore;
    }

    public TextView getTeamOneName() {
        return teamOneName;
    }
}
