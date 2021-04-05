package com.example.beatme.ui.matches;

public class Match {
    private String match_uid;
    private String team1;
    private String team2;
    private String team1_score;
    private String team2_score;
    private String user;
    private String status;
    private String duration;

    public Match(String match_uid, String username, String team1, String team2, String status, String duration) {
        this.match_uid = match_uid;
        this.team1 = team1;
        this.team2 = team2;
        this.user = username;
        this.status = status;
        this.duration = duration;
    }

    public String getMatch_uid() {
        return match_uid;
    }

    public void setMatch_uid(String match_uid) {
        this.match_uid = match_uid;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getTeam1_score() {
        return team1_score;
    }

    public void setTeam1_score(String team1_score) {
        this.team1_score = team1_score;
    }

    public String getTeam2_score() {
        return team2_score;
    }

    public void setTeam2_score(String team2_score) {
        this.team2_score = team2_score;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
