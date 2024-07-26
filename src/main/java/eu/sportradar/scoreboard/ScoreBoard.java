package eu.sportradar.scoreboard;

import java.util.ArrayList;
import java.util.List;

public interface ScoreBoard {

    public void startMatch(String homeTeam, String awayTeam);


    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore);


    public void finishMatch(String homeTeam, String awayTeam);

    public List<Match> getSummary();
}
