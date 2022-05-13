package at.tugraz.ist.qs2021.messageboard.messagestoremessages;

/**
 * Message used to signal that messages should be retrieved from the store.
 */
public class RetrieveFromStore extends MessageStoreMessage {
    /**
     * The author of the message which should be looked up
     */
    public final String author;

    public RetrieveFromStore(String author, long commId) {
        this.author = author;
        this.communicationId = commId;
    }
}
