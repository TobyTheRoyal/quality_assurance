package at.tugraz.ist.qs2021.messageboard.clientmessages;

import at.tugraz.ist.qs2021.actorsystem.SimulatedActor;

/**
 * Message sent from worker to client to tell the client
 * that the communication initiation was successful
 */
public class InitAck extends ClientMessage {
    /**
     * The worker serving the client during this communication/session
     * this worker reference can be used to send messages to
     */
    public final SimulatedActor worker;

    public InitAck(SimulatedActor worker, long communicationId) {
        super(communicationId);
        this.worker = worker;
    }

    @Override
    public int getDuration() {
        return 1;
    }
}
