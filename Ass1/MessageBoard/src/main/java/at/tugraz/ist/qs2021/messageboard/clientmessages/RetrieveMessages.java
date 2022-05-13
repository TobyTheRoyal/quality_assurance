package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Message sent from client to worker to retrieve all user messages written by a given author.
 */
public class RetrieveMessages extends ClientMessage {
    /**
     * The author of whom the messages should be looked up
     */
    public final String author;

    public RetrieveMessages(String author, long communicationId) {
        super(communicationId);
        this.author = author;
    }

    @Override
    public int getDuration() {
        return 3;
    }
}
