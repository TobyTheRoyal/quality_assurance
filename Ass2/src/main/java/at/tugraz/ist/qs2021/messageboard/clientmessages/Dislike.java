package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Message sent from client to worker to signal that a dislike should be added to a given user message.
 */
public class Dislike extends ClientMessage {
    /**
     * The user message id of the message to be disliked
     */
    public final long messageId;

    /**
     * The name of the person who dislikes the message
     */
    public final String clientName;

    public Dislike(String clientName, long communicationId, long mId) {
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
        return "Dislike(" + clientName + ", " + communicationId + ", " + messageId + ')';
    }
}
