import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestCoordinator {

    @Test
    public void testAddMember() {
        Coordinator coordinator = new Coordinator();
        Member member = new Member("1", "localhost", 8080);
        coordinator.addMember(member);

        assertTrue(coordinator.getMembers().contains(member), "Member should be added.");
    }

    @Test
    public void testRemoveMember() {
        Coordinator coordinator = new Coordinator();
        Member member = new Member("1", "localhost", 8080);
        coordinator.addMember(member);
        coordinator.removeMember("1");

        assertFalse(coordinator.getMembers().contains(member), "Member should be removed.");
    }
}