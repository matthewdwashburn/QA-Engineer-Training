import org.junit.Test;

import com.revature.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class FunctionalProgrammingTest {
    @Test
    public void testJohnSmith() throws Exception {
    Student jSmith = new Student("John", "Smith", "js123");
    Student jOak = new Student("John", "Oak", "jo123");

    assertTrue("John Smith with student number js123 did not return true", Student.f.apply(jSmith));
    assertFalse("John Oak with student number jo123 did not return false", Student.f.apply(jOak));
    }

    @Test
    public void testArea() throws Exception {
        Triangle t = new Triangle(5, 10);
        assertEquals("Incorrect area returned", 25D, Triangle.f.applyAsDouble(t), 0.001);
        assertEquals("Incorrect area in Triangle object", 25D, t.getArea(), 0.001);
    }

    @Test
    public void testMakeFunction() throws Exception {
        assertEquals("Created add 1 function; gave it 4; did not get 5 back",
                5, AdderFactory.create(1).applyAsInt(4));
    }
}
