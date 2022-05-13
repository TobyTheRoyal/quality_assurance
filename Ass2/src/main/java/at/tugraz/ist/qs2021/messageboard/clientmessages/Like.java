package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Message sent from client to worker to signal that a like should be added to a given user message.
 */
public class Like extends ClientMessage {
    /**
     * The user message id of the message to be liked
     */
    public final long messageId;

    /**
     * The name of the person who likes the message
     */
    public final String clientName;

    public Like(String clientName, long communicationId, long mId) {
        super(communicationId);
        this.clientName = clientName;
        this.messageId = mId;
    }

    @Override
    public int getDuration() {
        return 1;
    }

    @Override
    public String toString() {
        return "Like(" + clientName + ", " + communicationId + ", " + messageId + ')';
    }
}
