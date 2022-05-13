package at.tugraz.ist.qs2021.messageboard.clientmessages;

import at.tugraz.ist.qs2021.actorsystem.Message;

/**
 * The abstract base class for all messages sent between clients of
 * the message board (people who want to post messages) and the dispatcher
 * respectively worker.
 * <p>
 * All communications have a communication ID, which is defined in this
 * class. For simplicity, we assume that clients choose this number
 * and that it will be unique across all clients.
 * <p>
 * Client messages are generally requests from clients to which the workers
 * react with an appropriate response (e.g. operation acknowledge/failure).
 */
public abstract class ClientMessage implements Message {
    /**
     * some unique ID, identifies one communication/session
     */
    public final Long communicationId;

    public ClientMessage(Long communicationId) {
        this.communicationId = communicationId;
    }
}

