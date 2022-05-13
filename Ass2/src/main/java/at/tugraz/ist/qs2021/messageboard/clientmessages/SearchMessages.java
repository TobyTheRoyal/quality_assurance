package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Message sent from client to worker to search for the given message
 * either in the Author or the Message and return all matching messages
 */
public class SearchMessages extends ClientMessage {
    /**
     * The text to search for
     */
    public final String searchText;

    public SearchMessages(String searchText, long communicationId) {
        super(communicationId);
        this.searchText = searchText;
    }

    @Override
    public int getDuration() {
        return 3;
    }
}
