package at.tugraz.ist.qs2021.actorsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract CommunicationChannel class implementing the Tick method,
 * which is common to all concrete implementations of communication channels.
 */
public abstract class CommunicationChannel implements ICommunicationChannel {

    private List<MessageInDelivery> messagesInDelivery = new ArrayList<>();

    /**
     * Adds the given message to the list.
     *
     * @param messageInDelivery The object to add to the list.
     */
    protected void addMessageInDelivery(MessageInDelivery messageInDelivery) {
        this.messagesInDelivery.add(messageInDelivery);
    }

    @Override
    public List<Message> tick() {
        List<Message> messagesDelivered = new ArrayList<>();
        List<MessageInDelivery> newMessagesInDelivery = new ArrayList<>();

        for (MessageInDelivery messageInDelivery : messagesInDelivery) {
            if (messageInDelivery.tick()) {
                // message arrived at destination
                messagesDelivered.add(messageInDelivery.getMessage());
            } else {
                // add message with decremented remainingTicks again
                newMessagesInDelivery.add(messageInDelivery);
            }
        }

        this.messagesInDelivery = newMessagesInDelivery;
        return messagesDelivered;
    }
}
