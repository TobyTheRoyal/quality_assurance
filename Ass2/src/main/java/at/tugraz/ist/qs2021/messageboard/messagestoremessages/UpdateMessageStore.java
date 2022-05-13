package at.tugraz.ist.qs2021.messageboard.messagestoremessages;

import at.tugraz.ist.qs2021.messageboard.UserMessage;

/**
 * Message which signals that a new user message should be added to the store.
 */
public class UpdateMessageStore extends MessageStoreMessage {

    /**
     * The actual user message to be added
     */
    public final UserMessage message;

    public UpdateMessageStore(UserMessage message, long commId) {
        this.message = message;
        this.communicationId = commId;
    }
}
