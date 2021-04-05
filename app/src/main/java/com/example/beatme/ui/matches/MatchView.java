package com.example.beatme.ui.matches;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.beatme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class MatchView extends DialogFragment {
    private static final String TAG = "MatchView";

    CountDownTimer countDownTimer;
    String match_uid;
    public MatchView(String match_uid){
        this.match_uid = match_uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View matchViewFragment = inflater.inflate(R.layout.fragment_match_view, container, false);
        Button match_view_close = matchViewFragment.findViewById(R.id.match_view_close);
        TextView match_view_label = matchViewFragment.findViewById(R.id.match_view_label);
        TextView match_view_timer = matchViewFragment.findViewById(R.id.match_view_timer);
        TextView match_view_team1 = matchViewFragment.findViewById(R.id.match_view_team1);
        TextView match_view_score_team1 = matchViewFragment.findViewById(R.id.match_view_score_team1);
        TextView match_view_team2 = matchViewFragment.findViewById(R.id.match_view_team2);
        TextView match_view_score_team2 = matchViewFragment.findViewById(R.id.match_view_score_team2);
        TextView match_view_by = matchViewFragment.findViewById(R.id.match_view_by);
        TextView match_view_status = matchViewFragment.findViewById(R.id.match_view_status);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("matches").document(match_uid);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: " + snapshot.getData());
                String label = snapshot.get("team1")+" vs " +snapshot.get("team2");
                String prevStatus = match_view_status.getText().toString();

                match_view_label.setText(label);
                match_view_team1.setText((String) snapshot.get("team1"));
                match_view_team2.setText((String) snapshot.get("team2"));
                String score1 = snapshot.get("team1_score")+"";
                match_view_score_team1.setText(score1);
                String score2 = snapshot.get("team2_score")+"";
                match_view_score_team2.setText(score2);
                match_view_by.setText((String) snapshot.get("user"));
                match_view_status.setText((String) snapshot.get("status"));
                //status Not Started/Match Paused/Match In Progress/Match Finished/Match Dismissed/Incomplete
                String status = (String) snapshot.get("status");

                assert status != null;
                if(status.equals("Match In Progress") && !prevStatus.equals(status) ){
                    if(countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    countDownTimer =  countDownTimer(match_view_timer, (long) snapshot.get("millis_until_finished"));
                }else if( status.equals("Match Paused") || status.equals("Match Dismissed/Incomplete")){
                    if(countDownTimer != null)
                    countDownTimer.cancel();
                    match_view_timer.setText(milliToString((long) snapshot.get("millis_until_finished")));
                }else{
                    if(countDownTimer == null)
                    match_view_timer.setText(milliToString((long) snapshot.get("millis_until_finished")));
                }

            } else {
                Log.d(TAG, "Current data: null");
            }
        });



        match_view_close.setOnClickListener(v -> dismiss());
        return matchViewFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    public CountDownTimer countDownTimer(TextView match_timer, long millis){

        return new CountDownTimer(millis, 1000) {

            public void onTick(long millisUntilFinished) {

                match_timer.setText(milliToString(millisUntilFinished));
            }

            public void onFinish() {
                match_timer.setText(milliToString(0));
                FirebaseFirestore.getInstance().collection("matches").document(match_uid).update("status", "Match Finished");
            }
        }.start();

    }
    public String milliToString(long millisUntilFinished){
        long hour = (millisUntilFinished / 3600000) % 24;
        long min = (millisUntilFinished / 60000) % 60;
        long sec = (millisUntilFinished / 1000) % 60;
        return hour+":"+min+":"+sec;
    }
}
