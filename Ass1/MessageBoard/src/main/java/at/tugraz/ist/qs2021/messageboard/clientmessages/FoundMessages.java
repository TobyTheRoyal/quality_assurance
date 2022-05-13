package at.tugraz.ist.qs2021.messageboard.clientmessages;

import at.tugraz.ist.qs2021.messageboard.UserMessage;

import java.util.List;

/**
 * The response to the {@link RetrieveMessages} message sent from worker to client
 * containing all user messages written by the author defined in the message above.
 */
public class FoundMessages extends ClientMessage {
    /**
     * List of user messages written by one author
     */
    public final List<UserMessage> messages;

    public FoundMessages(List<UserMessage> messages, long communicationId) {
        super(communicationId);
        this.messages = messages;
    }

    @Override
    public int getDuration() {
        return 1;
    }
}
