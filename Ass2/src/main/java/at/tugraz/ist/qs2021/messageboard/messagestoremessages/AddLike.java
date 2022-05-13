package at.tugraz.ist.qs2021.messageboard.messagestoremessages;

/**
 * Message used to signal that a like should be added to a message.
 */
public class AddLike extends MessageStoreMessage {
    /**
     * user message id of the user message which should be liked
     */
    public final long messageId;

    /**
     * name of the person which likes the message
     */
    public final String clientName;

    public AddLike(String clientName, long messageId, long commId) {
        this.clientName = clientName;
        this.messageId = messageId;
        this.communicationId = commId;
    }
}
