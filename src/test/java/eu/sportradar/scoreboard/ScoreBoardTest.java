package eu.sportradar.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreBoardTest {

    private ScoreBoard scoreBoard;

    @BeforeEach
    public void setUp() {
        scoreBoard = new ScoreBoardImpl();
    }

    @Test
    public void testStartMatch() {
        scoreBoard.startMatch("Mexico", "Canada");
        assertEquals(1, scoreBoard.getSummary().size());
    }

    @Test
    public void testTeamAlreadyPlaying() {
        scoreBoard.startMatch("Mexico", "Canada");
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> scoreBoard.startMatch("Mexico", "Brazil"),
                "Expected startMatch() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("One of the teams is already playing a match"));
    }

    @Test
    public void testUpdateScore() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.updateScore("Mexico", "Canada", 0, 5);
        assertTrue(scoreBoard.getSummary().containsKey("Mexico:Canada"));
    }

    @Test
    public void testUpdateScoreWithoutStarting() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> scoreBoard.updateScore("Mexico", "Canada", 0, 5),
                "Expected updateScore() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("Match not found"));
    }

    @Test
    public void testUpdateScoreWithLesserValue() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.updateScore("Mexico", "Canada", 3, 2);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> scoreBoard.updateScore("Mexico", "Canada", 2, 1),
                "Expected updateScore() to throw, but it didn't"
        );
        assertTrue(thrown.getMessage().contains("New scores must be greater than or equal to current scores"));
    }

    @Test
    public void testFinishMatch() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.finishMatch("Mexico", "Canada");
        assertEquals(0, scoreBoard.getSummary().size());
        scoreBoard.startMatch("Mexico", "Canada");
        assertEquals(1, scoreBoard.getSummary().size());
    }

    @Test
    public void testGetSummary() throws InterruptedException {
        scoreBoard.startMatch("Mexico", "Canada");
        Thread.sleep(100);
        scoreBoard.startMatch("Spain", "Brazil");
        Thread.sleep(100);
        scoreBoard.startMatch("Germany", "France");
        Thread.sleep(100);
        scoreBoard.startMatch("Uruguay", "Italy");
        Thread.sleep(100);
        scoreBoard.startMatch("Argentina", "Australia");

        scoreBoard.updateScore("Mexico", "Canada", 0, 5);
        scoreBoard.updateScore("Spain", "Brazil", 10, 2);
        scoreBoard.updateScore("Germany", "France", 2, 2);
        scoreBoard.updateScore("Uruguay", "Italy", 6, 6);
        scoreBoard.updateScore("Argentina", "Australia", 3, 1);

        Map<String, Match> summaryMap = scoreBoard.getSummary();

        String[] expectedOrder = {
                "Uruguay 6 - Italy 6",
                "Spain 10 - Brazil 2",
                "Mexico 0 - Canada 5",
                "Argentina 3 - Australia 1",
                "Germany 2 - France 2"
        };

        assertEquals(expectedOrder.length, summaryMap.size());

        int i = 0;
        for (String key : summaryMap.keySet()) {
            assertEquals(expectedOrder[i++], summaryMap.get(key).toString());
        }

    }
}