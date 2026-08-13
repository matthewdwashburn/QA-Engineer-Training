package com.revature;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class assertionTests {
    @Test
    @DisplayName("test reverse manual samples")
    void manualReverseTest() {
        assertEquals("olleh", StringUtils.reverse("hello"));
        assertEquals("a", StringUtils.reverse("a"));
        assertEquals("", StringUtils.reverse(""));
    }

    @Test
    @DisplayName("isEmpty tests")
    void isEmpty() {
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("  "));
        assertFalse(StringUtils.isEmpty("null"));
    }

    @Test
    @DisplayName("findFirst tests") 
    void findFirst() {
        assertNull(StringUtils.findFirst(null, null));
        String[] list = {"wefwef", "awgwgwg", "hihello", "hellohi"};
        assertNotNull(StringUtils.findFirst(list, "hi"));
    }

    @Test
    @DisplayName("split tests")
    void splitTests() {
        String[] list = {"a", "b", "c"};
        assertArrayEquals(list, StringUtils.split("a,b,c",","));
    }

    @Test
    @DisplayName("test parse user")
    void parseUser() {
        User user = new User("John", "Smith", 30, "jonsmith@hotmail.com");
        assertAll(
            () -> assertEquals("John", user.getFirstName()),
            () -> assertEquals("Smith", user.getLastName()),
            () -> assertEquals(30, user.getAge()),
            () -> assertEquals("jonsmith@hotmail.com", user.getEmail())
        );
    }
    

}
