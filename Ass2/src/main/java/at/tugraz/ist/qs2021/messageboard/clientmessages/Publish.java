package at.tugraz.ist.qs2021.messageboard.clientmessages;

import at.tugraz.ist.qs2021.messageboard.UserMessage;

/**
 * Message sent from client to worker to publish new user messages.
 */
public class Publish extends ClientMessage {
    /**
     * The actual user message to be posted
     */
    public final UserMessage message;

    public Publish(UserMessage message, long communicationId) {
        super(communicationId);
        this.message = message;
    }

    @Override
    public int getDuration() {
        return 3;
    }

    @Override
    public String toString() {
        return "Publish(" + message + ", " + communicationId + ')';
    }
}
