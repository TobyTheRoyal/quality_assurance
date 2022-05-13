package at.tugraz.ist.qs2021.messageboard.clientmessages;

import at.tugraz.ist.qs2021.actorsystem.SimulatedActor;

/**
 * This message is sent from clients to the dispatcher and then forwarded
 * to workers to initiate communication.
 */
public class InitCommunication extends ClientMessage {
    /**
     * The client trying to set up the communication
     */
    public final SimulatedActor client;

    public InitCommunication(SimulatedActor client, long communicationId) {
        super(communicationId);
        this.client = client;
    }

    @Override
    public int getDuration() {
        return 2;
    }
}
