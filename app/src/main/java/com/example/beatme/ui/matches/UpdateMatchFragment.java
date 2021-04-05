package com.example.beatme.ui.matches;

import android.app.Dialog;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class UpdateMatchFragment extends DialogFragment {
    private static final String TAG = "UpdateMatchFragment";
    String match_uid;
    long match_millisUntilFinished;
    CountDownTimer countDownTimer;
    boolean timerActive = false;
    TextView match_update_status;

    public UpdateMatchFragment(String match_uid){
        this.match_uid = match_uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View updateMatchFragment = inflater.inflate(R.layout.fragment_match_update, container, false);

        LinearProgressIndicator match_update_progress = updateMatchFragment.findViewById(R.id.match_update_progress);
        TextView match_update_label = updateMatchFragment.findViewById(R.id.match_update_label);
        TextView match_update_timer = updateMatchFragment.findViewById(R.id.match_update_timer);
        TextView match_update_timer_play = updateMatchFragment.findViewById(R.id.match_update_timer_play);
        TextView match_update_timer_pause = updateMatchFragment.findViewById(R.id.match_update_timer_pause);
        TextView match_update_team1 = updateMatchFragment.findViewById(R.id.match_update_team1);
        TextView match_update_team2 = updateMatchFragment.findViewById(R.id.match_update_team2);
        TextView match_update_score_team1 = updateMatchFragment.findViewById(R.id.match_update_score_team1);
        TextView match_update_score_team2 = updateMatchFragment.findViewById(R.id.match_update_score_team2);
        Button match_update_close = updateMatchFragment.findViewById(R.id.match_update_close);
        Button match_update_score_team1_plus1 = updateMatchFragment.findViewById(R.id.match_update_score_team1_plus1);
        Button match_update_score_team1_plus2 = updateMatchFragment.findViewById(R.id.match_update_score_team1_plus2);
        Button match_update_score_team1_plus5 = updateMatchFragment.findViewById(R.id.match_update_score_team1_plus5);
        Button match_update_score_team1_minus1 = updateMatchFragment.findViewById(R.id.match_update_score_team1_minus1);
        Button match_update_score_team1_minus2 = updateMatchFragment.findViewById(R.id.match_update_score_team1_minus2);
        Button match_update_score_team1_minus5 = updateMatchFragment.findViewById(R.id.match_update_score_team1_minus5);
        Button match_update_score_team2_plus1 = updateMatchFragment.findViewById(R.id.match_update_score_team2_plus1);
        Button match_update_score_team2_plus2 = updateMatchFragment.findViewById(R.id.match_update_score_team2_plus2);
        Button match_update_score_team2_plus5 = updateMatchFragment.findViewById(R.id.match_update_score_team2_plus5);
        Button match_update_score_team2_minus1 = updateMatchFragment.findViewById(R.id.match_update_score_team2_minus1);
        Button match_update_score_team2_minus2 = updateMatchFragment.findViewById(R.id.match_update_score_team2_minus2);
        Button match_update_score_team2_minus5 = updateMatchFragment.findViewById(R.id.match_update_score_team2_minus5);
        match_update_status = updateMatchFragment.findViewById(R.id.match_update_status);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef = db.collection("matches").document(match_uid);

        match_update_progress.setVisibility(View.VISIBLE);
        matchRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    String label = document.get("team1") + " vs "+ document.get("team2");
                    match_millisUntilFinished = (long) document.get("millis_until_finished");
                    match_update_label.setText(label);
                    match_update_timer.setText(milliToString((long) document.get("millis_until_finished")));
                    match_update_team1.setText((String) document.get("team1"));
                    match_update_team2.setText((String) document.get("team2"));
                    String score1 = document.get("team1_score")+"";
                    String score2 = document.get("team2_score")+"";
                    match_update_score_team1.setText(score1);
                    match_update_score_team2.setText(score2);
                    if((long) document.get("millis_until_finished") == 0)
                    match_update_status.setText("Match Finished");
                    else {
                        match_update_status.setText((String) document.get("status"));
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
            match_update_progress.setVisibility(View.INVISIBLE);
        });

        match_update_timer_pause.setOnClickListener(v -> {
            if(timerActive && match_millisUntilFinished != 0){
                countDownTimer.cancel();
                matchRef.update("millis_until_finished", match_millisUntilFinished);
                match_update_timer.setText(milliToString(match_millisUntilFinished));
                timerActive = false;
                match_update_status.setText("Match Paused");
                matchRef.update("status", "Match Paused");
            }
        });

        match_update_timer_play.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            if(!timerActive && match_millisUntilFinished != 0){
                matchRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            matchRef.update("millis_until_finished", match_millisUntilFinished);
                            countDownTimer = countDownTimer(match_update_timer, (long) Objects.requireNonNull(document.get("millis_until_finished")));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            }
            match_update_progress.setVisibility(View.INVISIBLE);
            match_update_status.setText("Match In Progress");

            matchRef.update("status", "Match In Progress");
        });

        match_update_score_team1_plus1.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score;
            score = Integer.parseInt(match_update_score_team1.getText().toString())+1;
            matchRef.update("team1_score", score);
            String s = score+"";
            match_update_score_team1.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });
        match_update_score_team1_plus2.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score;
            score = Integer.parseInt(match_update_score_team1.getText().toString())+2;
            matchRef.update("team1_score", score);
            String s = score+"";
            match_update_score_team1.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });
        match_update_score_team1_plus5.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team1.getText().toString())+5;
            matchRef.update("team1_score", score);
            String s = score+"";
            match_update_score_team1.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });

        match_update_score_team1_minus1.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team1.getText().toString())-1;
            matchRef.update("team1_score", score);
            String s = score+"";
            match_update_score_team1.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });
        match_update_score_team1_minus2.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team1.getText().toString())-2;
            matchRef.update("team1_score", score);
            String s = score+"";
            match_update_score_team1.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });
        match_update_score_team1_minus5.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team1.getText().toString())-5;
            matchRef.update("team1_score", score);
            String s = score+"";
            match_update_score_team1.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });

        match_update_score_team2_plus1.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team2.getText().toString())+1;
            matchRef.update("team2_score", score);
            String s = score+"";
            match_update_score_team2.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });
        match_update_score_team2_plus2.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team2.getText().toString())+2;
            matchRef.update("team2_score", score);
            String s = score+"";
            match_update_score_team2.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });
        match_update_score_team2_plus5.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team2.getText().toString())+5;
            matchRef.update("team2_score", score);
            String s = score+"";
            match_update_score_team2.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });

        match_update_score_team2_minus1.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team2.getText().toString())-1;
            matchRef.update("team2_score", score);
            String s = score+"";
            match_update_score_team2.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });
        match_update_score_team2_minus2.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team2.getText().toString())-2;
            matchRef.update("team2_score", score);
            String s = score+"";
            match_update_score_team2.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });
        match_update_score_team2_minus5.setOnClickListener(v -> {
            match_update_progress.setVisibility(View.VISIBLE);
            int score = Integer.parseInt(match_update_score_team2.getText().toString())-5;
            matchRef.update("team2_score", score);
            String s = score+"";
            match_update_score_team2.setText(s);
            match_update_progress.setVisibility(View.INVISIBLE);

        });

        match_update_close.setOnClickListener(v -> {

            dismiss();
        });


        return updateMatchFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference matchRef = db.collection("matches").document(match_uid);
        matchRef.update("status", "Match Dismissed/Incomplete");
        matchRef.update("millis_until_finished", match_millisUntilFinished);
        if(match_millisUntilFinished == 0){
            matchRef.update("status","Match Finished");
        }
    }

    public CountDownTimer countDownTimer(TextView match_timer, long millis){

        timerActive = true;
        return new CountDownTimer(millis, 1000) {

           public void onTick(long millisUntilFinished) {

               match_timer.setText(milliToString(millisUntilFinished));
               match_millisUntilFinished = millisUntilFinished;
           }

           public void onFinish() {
               match_timer.setText(milliToString(0));
               match_update_status.setText("Match Finished");
               FirebaseFirestore.getInstance().collection("matches").document(match_uid).update("status", "Match Finished");
               FirebaseFirestore.getInstance().collection("matches").document(match_uid).update("millis_until_finished", 0);

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
