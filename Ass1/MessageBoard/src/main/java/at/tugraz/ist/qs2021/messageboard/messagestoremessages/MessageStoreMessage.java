package at.tugraz.ist.qs2021.messageboard.messagestoremessages;

import at.tugraz.ist.qs2021.actorsystem.Message;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActor;

/**
 * Base class for all messages sent to the message store.
 */
public abstract class MessageStoreMessage implements Message {


    /**
     * The actor to which the message store sends its replies.
     */
    public SimulatedActor storeClient;

    /**
     * The id of the communication during which this persistence operation
     * is performed
     */
    public long communicationId;

    public int getDuration() {
        return 1; // store is supposed to be fast
    }

}
