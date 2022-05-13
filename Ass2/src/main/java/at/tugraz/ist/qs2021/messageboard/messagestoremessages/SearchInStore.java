package at.tugraz.ist.qs2021.messageboard.messagestoremessages;

/**
 * Message used to signal that messages should be retrieved from the store.
 */
public class SearchInStore extends MessageStoreMessage {
    /**
     * The author of the message which should be looked up
     */
    public final String searchText;

    public SearchInStore(String author, long commId) {
        this.searchText = author;
        this.communicationId = commId;
    }
}
