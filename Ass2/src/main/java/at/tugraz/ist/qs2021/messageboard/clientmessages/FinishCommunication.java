package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Message sent from client to worker to end the communication/session.
 */
public class FinishCommunication extends ClientMessage {

    public FinishCommunication(long communicationId) {
        super(communicationId);
    }

    @Override
    public int getDuration() {
        return 3;
    }

    @Override
    public String toString() {
        return "FinishCommunication(" + communicationId + ')';
    }
}
