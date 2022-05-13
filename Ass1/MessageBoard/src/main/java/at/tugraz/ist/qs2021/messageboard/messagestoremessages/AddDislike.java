package at.tugraz.ist.qs2021.messageboard.messagestoremessages;

/**
 * Message used to signal that a like should be added to a message.
 */
public class AddDislike extends MessageStoreMessage {
    /**
     * User message id of the user message which should be disliked
     */
    public final long messageId;
    /**
     * Name of the person which likes the message
     */
    public final String clientName;

    public AddDislike(String clientName, long messageId, long commId) {
        this.clientName = clientName;
        this.messageId = messageId;
        this.communicationId = commId;
    }
}
