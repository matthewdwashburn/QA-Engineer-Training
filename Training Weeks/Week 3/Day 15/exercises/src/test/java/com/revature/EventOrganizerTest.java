package com.revature;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EventOrganizerTest {

    @Test
    public void testSampleInput() {
        List<String> events = Arrays.asList(
            "ENTER John 3.75 50",
            "ENTER Mark 3.8 24",
            "ENTER Shafaet 3.7 35",
            "SERVED",
            "SERVED",
            "ENTER Samiha 3.85 36",
            "SERVED",
            "ENTER Ashley 3.9 42",
            "ENTER Maria 3.6 46",
            "ENTER Anik 3.95 49",
            "ENTER Dan 3.95 50",
            "SERVED"
        );

        List<Student> result = new Priorities().getStudents(events);

        assertEquals(4, result.size());
        assertEquals("Dan", result.get(0).getName());
        assertEquals("Ashley", result.get(1).getName());
        assertEquals("Shafaet", result.get(2).getName());
        assertEquals("Maria", result.get(3).getName());
    }
}
