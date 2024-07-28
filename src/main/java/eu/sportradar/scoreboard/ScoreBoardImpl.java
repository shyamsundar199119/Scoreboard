package eu.sportradar.scoreboard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ScoreBoardImpl implements ScoreBoard {
    private final Set<String> activeTeams;

    private Map<String, Match> matches;
    private final ReentrantLock lock = new ReentrantLock();

    public ScoreBoardImpl() {
        matches = new ConcurrentHashMap<>();
        activeTeams = ConcurrentHashMap.newKeySet();
    }

    private String getKey(String homeTeam, String awayTeam) {
        return homeTeam + ":" + awayTeam;
    }

    public void startMatch(String homeTeam, String awayTeam) {
        lock.lock();
        try {
            if (isTeamPlaying(homeTeam) || isTeamPlaying(awayTeam)) {
                throw new IllegalArgumentException("One of the teams is already playing a match");
            }

            String key = getKey(homeTeam, awayTeam);
            matches.put(key, new Match(homeTeam, awayTeam));
            activeTeams.add(homeTeam);
            activeTeams.add(awayTeam);
        } finally {
            lock.unlock();
        }
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        String key = getKey(homeTeam, awayTeam);
        Match match = matches.get(key);
        if (match == null) {
            throw new IllegalArgumentException("Match not found");
        }
        synchronized (match) {
            validateScores(homeScore, awayScore, match.getHomeScore(), match.getAwayScore());
            match.setScore(homeScore, awayScore);
        }
    }

    public void finishMatch(String homeTeam, String awayTeam) {
        lock.lock();
        try {
            String key = getKey(homeTeam, awayTeam);
            if (matches.get(key) == null)
                throw new IllegalArgumentException("Match not found");

            matches.remove(key);
            activeTeams.remove(homeTeam);
            activeTeams.remove(awayTeam);
        } finally {
            lock.unlock();
        }
    }

    private void validateScores(int newHomeScore, int newAwayScore, int currentHomeScore, int currentAwayScore) {
        if (newHomeScore < currentHomeScore || newAwayScore < currentAwayScore) {
            throw new IllegalArgumentException("New scores must be greater than or equal to current scores");
        }
    }

    public Map<String, Match> getSummary() {
        Map<String, Match> summaryMap = new LinkedHashMap<>();
        lock.lock();
        try {
            matches.values().stream().sorted(Comparator.comparingInt(Match::getTotalScore)
                            .thenComparing(Match::getStartTime)
                            .reversed())
                    .forEach(match -> summaryMap.put(getKey(match.getHomeTeam(), match.getAwayTeam()), match));

        } finally {
            lock.unlock();
        }
        return summaryMap;
    }

    private boolean isTeamPlaying(String team) {
        return activeTeams.contains(team);
    }
}
