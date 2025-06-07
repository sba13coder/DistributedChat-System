class Member {
    private final String id;
    private final String ip;
    private final int port;

    public Member(String id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + "(" + ip + ":" + port + ")";
    }
}