package eu.sportradar.scoreboard;

import java.util.*;

public class ScoreBoardImpl implements ScoreBoard {
    private List<Match> matches;

    public ScoreBoardImpl() {
        matches = new ArrayList<>();
    }

    public void startMatch(String homeTeam, String awayTeam) {
        if (isTeamPlaying(homeTeam) || isTeamPlaying(awayTeam)) {
            throw new IllegalArgumentException("One of the teams is already playing a match");
        }

        matches.add(new Match(homeTeam, awayTeam));
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        for (Match match : matches) {
            if ((match.getHomeTeam().equals(homeTeam) && match.getAwayTeam().equals(awayTeam)) ||
                    (match.getHomeTeam().equals(awayTeam) && match.getAwayTeam().equals(homeTeam))) {

                if (homeTeam.equals(match.getHomeTeam())) {
                    if (homeScore < match.getHomeScore() || awayScore < match.getAwayScore()) {
                        throw new IllegalArgumentException("New scores must be greater than or equal to current scores");
                    }
                    match.setScore(homeScore, awayScore);
                    return;
                } else {
                    if (homeScore < match.getAwayScore() || awayScore < match.getHomeScore()) {
                        throw new IllegalArgumentException("New scores must be greater than or equal to current scores");
                    }
                    match.setScore(awayScore, homeScore);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Match not found");
    }

    public void finishMatch(String homeTeam, String awayTeam) {
        matches.removeIf(match -> match.getHomeTeam().equals(homeTeam) && match.getAwayTeam().equals(awayTeam));
    }

    public List<Match> getSummary() {
        List<Match> summary = new ArrayList<>(matches);
        summary.sort(Comparator.comparingInt(Match::getTotalScore)
                .thenComparing(Match::getStartTime)
                .reversed());
        return summary;
    }

    private boolean isTeamPlaying(String team) {
        for (Match match : matches) {
            if (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team)) {
                return true;
            }
        }
        return false;
    }
}
