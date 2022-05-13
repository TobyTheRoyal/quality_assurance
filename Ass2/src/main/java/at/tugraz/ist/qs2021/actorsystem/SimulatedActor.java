package at.tugraz.ist.qs2021.actorsystem;

import at.tugraz.ist.qs2021.messageboard.UnknownClientException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class SimulatedActor implements ISimulatedActor {

    /**
     * Channel, which simulates the communication channel built-up when
     * messages are sent to an actors. A more accurate approximation
     * of the real world would include one channel per communication/pair
     * of actors, but for the sake of simplicity we use only one per actor.
     */
    protected CommunicationChannel channel = new DeterministicChannel(1);

    /**
     * Unique id assigned to each actor
     */
    private long id = SimulatedActorSystem.NEW_ACTOR;

    /**
     * Remaining number of ticks, for which this actor is busy processing a message.
     * This way we simulate that messages take time to process.
     */
    private int busyFor = 0;

    /**
     * The message currently being processed.
     */
    private Message activeMessage = null;

    /**
     * All messages, that have already been sent via <c>channel</c>,
     * but have not been processed yet.
     */
    private final Queue<Message> messageBox = new LinkedList<>();

    /**
     * All messages sent to this actor, this includes messages in transit,
     * already processed messages and messages in the <c>messageBox</c>.
     * It is used to alleviate debugging and testing.
     */
    private final List<Message> messageLog = new ArrayList<>();

    /**
     * Time since the system was started.
     * Initially (after the construction) it is -1, shall be set to the current system time
     * right after {@link ISimulatedActor#atStartUp()} is called and shall be incremented
     * when {@link ISimulatedActor#tick()} is called.
     * If SimulatedActor.receive() throws an exception, it might not correctly
     * reflect the current system time, but for the sake of simplicity, we ignore this fact.
     */
    int timeSinceSystemStart = -1;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public List<Message> getMessageLog() {
        return this.messageLog;
    }

    @Override
    public int getTimeSinceSystemStart() {
        return this.timeSinceSystemStart;
    }

    @Override
    public void setTimeSinceSystemStart(int timeSinceSystemStart) {
        this.timeSinceSystemStart = timeSinceSystemStart;
    }

    @Override
    public void tell(Message message) {
        channel.send(message);
        messageLog.add(message);
    }

    @Override
    public void tick() throws UnknownClientException {
        timeSinceSystemStart++;
        List<Message> newlyDelivered = channel.tick();
        messageBox.addAll(newlyDelivered);

        if (busyFor > 0) {
            busyFor--;
            return;
        }

        Message messageToProcess = null;
        // busyFor is zero, so if there is an activeMessage, we are
        // finished processing it, so we can use receive() for changes
        // to take effect

        if (activeMessage != null) {
            messageToProcess = activeMessage;
            activeMessage = null;
        } else if (!messageBox.isEmpty()) {
            activeMessage = messageBox.remove();
            busyFor = activeMessage.getDuration();
        }
        // might throw an exception, but all the other code should still be executed,
        // but not in a finally block
        // so we use this variable for intermediately storing the message
        if (messageToProcess != null) {
            receive(messageToProcess);
        }
    }

    /**
     * Default implementation of {@link ISimulatedActor#atStartUp()} doing nothing.
     */
    @Override
    public void atStartUp() {

    }
}
