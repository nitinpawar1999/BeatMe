package com.example.beatme.ui.tournaments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.beatme.R;
import com.example.beatme.ui.tournaments.model.CompetitorData;
import com.example.beatme.ui.tournaments.model.MatchData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UpdateTournamentFragment extends DialogFragment {

    private static final String TAG = "AddTournamentFragment";
    private MatchData matchData;
    private String tournament_uid;

    public UpdateTournamentFragment(MatchData matchData, String tournament_uid){
        this.matchData = matchData;
        this.tournament_uid = tournament_uid;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View tournamentScoreUpdate = inflater.inflate(R.layout.tournament_score_update, container, false);
        EditText tournamentScoreTeamName1 = tournamentScoreUpdate.findViewById(R.id.tournament_score_teamName1);
        EditText tournamentScoreTeamName2 = tournamentScoreUpdate.findViewById(R.id.tournament_score_teamName2);
        EditText tournamentScoreTeamScore1 = tournamentScoreUpdate.findViewById(R.id.tournament_score_teamScore1);
        EditText tournamentScoreTeamScore2 = tournamentScoreUpdate.findViewById(R.id.tournament_score_teamScore2);
        if(tournamentScoreTeamName1.getText().equals("BYE")) tournamentScoreTeamScore1.setText(0);
        if(tournamentScoreTeamName2.getText().equals("BYE")) tournamentScoreTeamScore1.setText(0);


        tournamentScoreTeamName1.setText(matchData.getCompetitorOne().getName());
        tournamentScoreTeamName2.setText(matchData.getCompetitorTwo().getName());
        tournamentScoreTeamScore1.setText(matchData.getCompetitorOne().getScore());
        tournamentScoreTeamScore2.setText(matchData.getCompetitorTwo().getScore());

        Button tournamentScoreUpdateBtn = tournamentScoreUpdate.findViewById(R.id.tournament_score_update_btn);

        tournamentScoreUpdateBtn.setOnClickListener(v -> {
            if(tournamentScoreTeamName1.getText().equals("BYE")) tournamentScoreTeamScore1.setText(0);
            if(tournamentScoreTeamName2.getText().equals("BYE")) tournamentScoreTeamScore1.setText(0);

            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> map1 = new HashMap<>();
            Map<String, String> map2 = new HashMap<>();
            map1.put("Score", tournamentScoreTeamScore1.getText().toString());
            map1.put("TeamName", tournamentScoreTeamName1.getText().toString());
            map2.put("Score", tournamentScoreTeamScore2.getText().toString());
            map2.put("TeamName", tournamentScoreTeamName2.getText().toString());
            list.add(map1);
            list.add(map2);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                String match_id = matchData.getMatch_id();
                final String[] matches_uid = {""};
                db.collection("tournaments").document(tournament_uid).collection("matches").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        db.collection("tournaments").document(tournament_uid).collection("matches").document(queryDocumentSnapshots.getDocuments().get(0).getId()).update(match_id, list).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
                    }
                });
            }
            dismiss();
        });
        return tournamentScoreUpdate;
    }
}
