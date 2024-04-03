package biblioSession;

public class SessionManager {
    private static int userId = -1;

    public static void setUserLoggedIn(int id) {
        userId = id;
    }

    public static int getLoggedInUserId() {
        return userId;
    }
}
