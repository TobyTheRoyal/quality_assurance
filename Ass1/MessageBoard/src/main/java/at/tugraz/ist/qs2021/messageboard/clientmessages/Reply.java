package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Reply message base class sent from worker to client to show that a request succeeded or failed.
 */
public abstract class Reply extends ClientMessage {
    public Reply(Long communicationId) {
        super(communicationId);
    }

    @Override
    public int getDuration() {
        return 1;
    }
}
