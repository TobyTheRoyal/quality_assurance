package at.tugraz.ist.qs2021.messageboard;

import at.tugraz.ist.qs2021.actorsystem.DeterministicChannel;
import at.tugraz.ist.qs2021.actorsystem.Message;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActor;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActorSystem;
import at.tugraz.ist.qs2021.messageboard.clientmessages.OperationFailed;
import at.tugraz.ist.qs2021.messageboard.messagestoremessages.MessageStoreMessage;

/**
 * Helper which should only send one message to the message store
 * and then forward the response to the client. As all workers share
 * one message store, messages could get dropped (this can be simulated
 * using different channel implementations for the message store), so
 * this actor will resend messages, if it does not receive a response
 * for a predefined amount of time.
 * <p>
 * Such simple actors are common in programs using the actor model.
 */
public class WorkerHelper extends SimulatedActor {
    /**
     * The message which should be sent to the message store
     */
    protected MessageStoreMessage message;

    /**
     * the message store actor
     */
    private final SimulatedActor messageStore;

    /**
     * the client to which the response should be forwarded
     */
    protected SimulatedActor client;

    /**
     * the actor system which is used for stopping after forwarding the response
     */
    private final SimulatedActorSystem system;

    /**
     * counts the number of ticks since the message was sent to the message store
     */
    private int timeSinceLastSent;

    /**
     * Used to mark that the actor is stopping and should not try resending the message anymore
     */
    private boolean stopping;

    /**
     * count how often the message was resent
     */
    private int retries;

    /**
     * maximum number of resends
     */
    private final int MAX_RETRIES = 2;

    /**
     * Constructs a new WorkerHelper object.
     *
     * @param messageStore message store which receives messages from helper
     * @param client       client to which the message from the store gets forwarded
     * @param message      the message to be sent to the message store
     * @param system       actor system used to stop the helper
     */
    public WorkerHelper(SimulatedActor messageStore, SimulatedActor client, MessageStoreMessage message, SimulatedActorSystem system) {
        this.message = message;
        this.message.storeClient = this;
        this.messageStore = messageStore;
        this.client = client;
        this.system = system;
        this.timeSinceLastSent = 0;
        this.stopping = false;
        this.retries = 0;

        // good connection between WorkerHelper and MessageStore -> no delay
        this.channel = new DeterministicChannel(0);
    }

    /**
     * After spawning the message should be sent for the first time to the message store.
     */
    @Override
    public void atStartUp() {
        messageStore.tell(message);
        timeSinceLastSent = 0;
    }

    /**
     * We assume that the helper only receives reply messages from the message store,
     * which it must forward to clients.
     *
     * @param message Non-null message received
     */
    @Override
    public void receive(Message message) {
        client.tell(message);
        system.stop(this);
        stopping = true; // mark as stopping,
    }

    /**
     * Overridden tick()-method, which counts the time units passed since
     * the message was sent the last time and the number of sending
     * retries.
     */
    @Override
    public void tick() throws UnknownClientException {
        super.tick();
        // as all workers share one MessageStore instance, it might happen that messages are dropped
        if (!stopping && timeSinceLastSent++ >= 3) {
            if (retries == MAX_RETRIES) {
                client.tell(new OperationFailed(message.communicationId));
                system.stop(this);
            } else {
                messageStore.tell(message);
                timeSinceLastSent = 0;
                retries++;
            }
        }
    }
}
