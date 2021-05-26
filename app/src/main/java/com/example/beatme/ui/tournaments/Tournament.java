package com.example.beatme.ui.tournaments;

import java.util.List;
import java.util.Map;

public class Tournament {

    private String tournament_uid;
    private String user;
    private String email;
    private String tournamentName;
    private long teamCount;
    private List<Map<String, Object>> matches;



    private String matches_uid;
    public Tournament(){

    }
    public Tournament(String tournament_uid, String matches_uid, String user, String email, String tournamentName, long teamCount, List<Map<String, Object>> matches) {
        this.tournament_uid = tournament_uid;
        this.user = user;
        this.tournamentName = tournamentName;
        this.teamCount = teamCount;
        this.matches = matches;
        this.matches_uid = matches_uid;
        this.email = email;
    }

    public String getTournament_uid() {
        return tournament_uid;
    }

    public void setTournament_uid(String tournament_uid) {
        this.tournament_uid = tournament_uid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public long getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public List<Map<String, Object>> getMatches() {
        return matches;
    }

    public void setMatches(List<Map<String, Object>> matches) {
        this.matches = matches;
    }
    public String getMatches_uid() {
        return matches_uid;
    }

    public void setMatches_uid(String matches_uid) {
        this.matches_uid = matches_uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
