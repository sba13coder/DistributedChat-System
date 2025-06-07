import java.util.*;

class Coordinator {
    private Member coordinator;
    private final List<Member> members;

    public Coordinator() {
        this.members = new ArrayList<>();
        startPeriodicCheck();
    }

    public synchronized void addMember(Member member) {
        for (Member m : members) {
            if (m.getId().equals(member.getId())) {
                System.out.println("Error: Duplicate ID attempt: " + member.getId());
                return;
            }
        }

        if (members.isEmpty()) {
            coordinator = member;
            System.out.println("New Coordinator: " + coordinator);
        }

        members.add(member);
    }

    public synchronized boolean removeMember(String id) {
        boolean removed = members.removeIf(member -> member.getId().equals(id));

        if (removed) {
            System.out.println(id + " has left the chat.");
        }

        if (coordinator != null && coordinator.getId().equals(id)) {
            if (!members.isEmpty()) {
                coordinator = members.getFirst();
                System.out.println("New Coordinator: " + coordinator);
                DistributedChatServer.broadcastNewCoordinator();
            } else {
                coordinator = null;
                System.out.println("Coordinator left, no members remaining.");
            }
        }
        return removed;
    }

    public synchronized Member getCoordinator() {
        return coordinator;
    }

    public synchronized List<Member> getMembers() {
        return new ArrayList<>(members);
    }

    public synchronized String getMemberDetails() {
        StringBuilder details = new StringBuilder("Active Members:\n");
        for (Member m : members) {
            details.append(m.toString()) .append("\n");
        }
        if (coordinator != null) {
            details.append("Current Coordinator: ") .append(coordinator);
        } else {
            details.append("No active coordinator.");
        }
        return details.toString();
    }

    private void startPeriodicCheck() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkActiveMembers();
            }
        }, 20000, 20000);
    }

    private synchronized void checkActiveMembers() {
        System.out.println("Checking active members...");
        System.out.println("Active members: " + members);
    }
}