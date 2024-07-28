package eu.sportradar.scoreboard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBoardImpl implements ScoreBoard {
    private final Set<String> activeTeams;

    private Map<String, Match> matches;

    public ScoreBoardImpl() {
        matches = new ConcurrentHashMap<>();
        activeTeams = new HashSet<>();
    }

    private String getKey(String homeTeam, String awayTeam) {
        return homeTeam + ":" + awayTeam;
    }

    public void startMatch(String homeTeam, String awayTeam) {
        if (isTeamPlaying(homeTeam) || isTeamPlaying(awayTeam)) {
            throw new IllegalArgumentException("One of the teams is already playing a match");
        }

        String key = getKey(homeTeam, awayTeam);
        matches.put(key, new Match(homeTeam, awayTeam));
        activeTeams.add(homeTeam);
        activeTeams.add(awayTeam);
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        String key = getKey(homeTeam, awayTeam);
        Match match = matches.get(key);
        if (match == null) {
            throw new IllegalArgumentException("Match not found");
        }

        validateScores(homeScore, awayScore, match.getHomeScore(), match.getAwayScore());
        match.setScore(homeScore, awayScore);
    }

    public void finishMatch(String homeTeam, String awayTeam) {
        String key = getKey(homeTeam, awayTeam);
        matches.remove(key);
    }

    private void validateScores(int newHomeScore, int newAwayScore, int currentHomeScore, int currentAwayScore) {
        if (newHomeScore < currentHomeScore || newAwayScore < currentAwayScore) {
            throw new IllegalArgumentException("New scores must be greater than or equal to current scores");
        }
    }

    public Map<String, Match> getSummary() {
        Map<String, Match> summaryMap = new LinkedHashMap<>();
        matches.values().stream().sorted(Comparator.comparingInt(Match::getTotalScore)
                .thenComparing(Match::getStartTime)
                .reversed())
                .forEach(match -> summaryMap.put(getKey(match.getHomeTeam(),match.getAwayTeam()), match));
        return summaryMap;
    }

    private boolean isTeamPlaying(String team) {
        return activeTeams.contains(team);
    }
}
