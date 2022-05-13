package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Message sent from worker to client to show that the communication teardown was successful.
 */
public class FinishAck extends ClientMessage {

    public FinishAck(long communicationId) {
        super(communicationId);
    }

    @Override
    public int getDuration() {
        return 1;
    }
}
