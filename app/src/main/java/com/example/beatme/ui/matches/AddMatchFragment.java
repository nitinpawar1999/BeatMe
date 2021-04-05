package com.example.beatme.ui.matches;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.beatme.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddMatchFragment extends DialogFragment {
    private static final String TAG = "AddMatchFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View addMatchFragment = inflater.inflate(R.layout.fragment_add_match, container, false);

        TextInputLayout team1 = addMatchFragment.findViewById(R.id.match_view_team1);
        TextInputLayout team2 = addMatchFragment.findViewById(R.id.match_view_team2);
        TimePicker timePicker = addMatchFragment.findViewById(R.id.match_duration);
        LinearProgressIndicator linearProgressIndicator = addMatchFragment.findViewById(R.id.match_add_progress);
        timePicker.setIs24HourView(true);
        timePicker.setHour(0);
        timePicker.setMinute(0);


        Button add = addMatchFragment.findViewById(R.id.match_add_btn);
        add.setOnClickListener(v -> {
            linearProgressIndicator.setVisibility(View.VISIBLE);
            String stringTeam1 = Objects.requireNonNull(team1.getEditText()).getText().toString();
            String stringTeam2 = Objects.requireNonNull(team2.getEditText()).getText().toString();
            long millis_until_finished = timePicker.getHour()*60*60*1000+timePicker.getMinute()*60*1000;

            if(stringTeam1.isEmpty() || stringTeam2.isEmpty() || millis_until_finished == 0){
                Toast.makeText(getContext(),"Enter Valid Entries!!",Toast.LENGTH_SHORT).show();
            }else{
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    Map<String,Object> matchData = new HashMap<>();
                    matchData.put("user", user.getEmail());
                    matchData.put("team1", stringTeam1);
                    matchData.put("team1_score", 0);
                    matchData.put("team2", stringTeam2);
                    matchData.put("team2_score", 0);
                    matchData.put("status", "Not Started");
                    matchData.put("millis_until_finished", millis_until_finished);

                    db.collection("matches").add(matchData).addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "Match Saved",Toast.LENGTH_SHORT).show();
                        linearProgressIndicator.setVisibility(View.INVISIBLE);
                        dismiss();
                        UpdateMatchFragment updateMatchFragment = new UpdateMatchFragment(documentReference.getId());
                        updateMatchFragment.show(getParentFragmentManager(), "dialogUpdateMatch");



                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(), "Error Match Not Saved",Toast.LENGTH_SHORT).show();
                        linearProgressIndicator.setVisibility(View.INVISIBLE);
                        dismiss();
                    });

                }
            }
        });



        return addMatchFragment;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
