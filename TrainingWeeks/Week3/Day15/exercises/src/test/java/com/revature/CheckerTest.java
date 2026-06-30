package com.revature;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class CheckerTest {

    @Test
    public void testSortByScoreDescThenNameAsc() {
        Player[] players = {
            new Player("amy", 100),
            new Player("david", 100),
            new Player("heraldo", 50),
            new Player("aakansha", 75),
            new Player("aleksa", 150)
        };

        Arrays.sort(players, new Checker());

        assertEquals("aleksa", players[0].name);
        assertEquals(150, players[0].score);

        assertEquals("amy", players[1].name);
        assertEquals(100, players[1].score);

        assertEquals("david", players[2].name);
        assertEquals(100, players[2].score);

        assertEquals("aakansha", players[3].name);
        assertEquals(75, players[3].score);

        assertEquals("heraldo", players[4].name);
        assertEquals(50, players[4].score);
    }
}