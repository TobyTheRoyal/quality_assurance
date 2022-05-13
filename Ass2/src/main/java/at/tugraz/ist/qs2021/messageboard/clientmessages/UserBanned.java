package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Reply message sent from worker to client to signal that he is banned.
 */
public class UserBanned extends Reply {
    public UserBanned(long communicationId) {
        super(communicationId);
    }

    @Override
    public String toString() {
        return "UserBanned(" + communicationId + ')';
    }
}
