package com.example.beatme.ui.tournaments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;

import com.example.beatme.R;
import com.example.beatme.ui.matches.UpdateMatchFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTournamentFragment extends DialogFragment {

    private static final String TAG = "AddTournamentFragment";
    List<TextInputLayout> teamList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View addTournamentFragment = inflater.inflate(R.layout.fragment_add_tournament, container, false);
        LinearProgressIndicator progressIndicator = addTournamentFragment.findViewById(R.id.fragment_add_tournament_progeressbar);
        NestedScrollView nestedScrollView = addTournamentFragment.findViewById(R.id.fragment_add_tournament_scrollView);
        TextInputLayout tournamentName = addTournamentFragment.findViewById(R.id.fragment_add_tournament_name);
        LinearLayout linearLayout = addTournamentFragment.findViewById(R.id.fragment_add_tournament_linearLayout);
        TextInputLayout team1 = addTournamentFragment.findViewById(R.id.fragment_add_tournament_team1);
        TextInputLayout team2 = addTournamentFragment.findViewById(R.id.fragment_add_tournament_team2);
        teamList.add(team1);
        teamList.add(team2);
        FloatingActionButton addTeamFb = addTournamentFragment.findViewById(R.id.fragment_add_tournament_add_team);
        FloatingActionButton removeTeamFb = addTournamentFragment.findViewById(R.id.fragment_add_tournament_remove_team);
        CheckBox shuffle = addTournamentFragment.findViewById(R.id.fragment_add_tournament_shuffle);
        Button submitBtn = addTournamentFragment.findViewById(R.id.fragment_add_tournament_btn);

        addTeamFb.setOnClickListener(v -> {
            View team = LayoutInflater.from(getContext()).inflate(R.layout.add_team, null);
            TextInputLayout teamx = team.findViewById(R.id.fragment_add_tournament_textInputLayout);
            teamx.setHint("Team "+ (teamList.size()+1));
            teamList.add(teamx);
            linearLayout.addView(teamx);
            nestedScrollView.fullScroll(View.FOCUS_DOWN);

            nestedScrollView.post(() -> nestedScrollView.fullScroll(View.FOCUS_DOWN));
        });

        removeTeamFb.setOnClickListener(v -> {
            if(teamList.size()>0) {
                linearLayout.removeViewAt(teamList.size() - 1);
                teamList.remove(teamList.size() - 1);
            }
        });
        submitBtn.setOnClickListener(v -> {
            progressIndicator.setVisibility(View.VISIBLE);
            boolean flag = true;
            List<String> teamName = new ArrayList<>();
            String tournamentNameString =  tournamentName.getEditText().getText().toString();
            if(tournamentNameString.isEmpty()){
                flag = false;
                tournamentName.setError("Cannot be Empty!!!");
            }else{
                flag = true;
                tournamentName.setErrorEnabled(false);
            }

            for(TextInputLayout temp : teamList){
                String tempTeamString = temp.getEditText().getText().toString();
                if(tempTeamString.isEmpty()){
                    flag = false;
                    temp.setError("Cannot be Empty!!!");
                }else{
                    flag = true;
                    teamName.add(tempTeamString);
                    temp.setErrorEnabled(false);
                }
            }
            if(shuffle.isChecked()){
                Collections.shuffle(teamName);
            }

            if(flag && teamList.size()>1){
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    int matchCounter = 0;
                    Map<String, Object> tournamentData = new HashMap<>();
                    List<Map<String, Object>> tournamentMatches = new ArrayList<>();
                    tournamentData.put("user", user.getEmail());
                    tournamentData.put("tournamentName", tournamentNameString);
                    tournamentData.put("teamCount", teamName.size());
                    for(int i=0;i<teamName.size()-1;i+=2){
                        Map<String, String> map0 = new HashMap<>();
                        map0.put("Score", "?");
                        map0.put("TeamName", teamName.get(i));
                        Map<String, String> map1 = new HashMap<>();
                        map1.put("Score", "?");
                        map1.put("TeamName", teamName.get(i+1));
                        List<Map<String, String>> list = new ArrayList<>();
                        list.add(map0);
                        list.add(map1);


                        Map<String, Object> match = new HashMap<>();
                        match.put("match_0_"+matchCounter, list);
                        tournamentMatches.add(match);

                        //tournamentData.put("match_0_"+matchCounter , list);
                        matchCounter++;
                    }
                    if(teamName.size() % 2 != 0){
                        Map<String, String> map0 = new HashMap<>();
                        map0.put("Score", "?");
                        map0.put("TeamName", teamName.get(teamName.size()-1));
                        List<Map<String, String>> list = new ArrayList<>();
                        list.add(map0);

                        Map<String, Object> match = new HashMap<>();
                        match.put("match_0_"+matchCounter, list);
                        tournamentMatches.add(match);

                        //tournamentData.put("match_0_"+matchCounter , list);
                    }
                    tournamentData.put("matches", tournamentMatches);

                    db.collection("tournaments").add(tournamentData).addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "Tournament Saved",Toast.LENGTH_SHORT).show();
                        progressIndicator.setVisibility(View.INVISIBLE);
                        dismiss();


                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(), "Error Tournament Not Saved",Toast.LENGTH_SHORT).show();
                        progressIndicator.setVisibility(View.INVISIBLE);
                        dismiss();
                    });
                }
            }else{
                Toast.makeText(getContext(), "Fields Cannot be Empty!", Toast.LENGTH_SHORT).show();
            }

        });
        return addTournamentFragment;
    }
}
