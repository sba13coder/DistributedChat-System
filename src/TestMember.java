import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMember {

    @Test
    public void testMemberCreation() {
        Member member = new Member("1", "localhost", 8080);
        assertEquals("1", member.getId(), "Member ID should be '1'");
    }

    @Test
    public void testMemberToString() {
        Member member = new Member("1", "localhost", 8080);
        String expected = "1(localhost:8080)";
        assertEquals(expected, member.toString(), "toString method should return correct format.");
    }
}