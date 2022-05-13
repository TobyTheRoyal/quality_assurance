package at.tugraz.ist.qs2021.messageboard;

import at.tugraz.ist.qs2021.actorsystem.Message;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActor;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActorSystem;
import at.tugraz.ist.qs2021.messageboard.clientmessages.InitCommunication;
import at.tugraz.ist.qs2021.messageboard.clientmessages.OperationFailed;
import at.tugraz.ist.qs2021.messageboard.dispatchermessages.Stop;
import at.tugraz.ist.qs2021.messageboard.dispatchermessages.StopAck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Dispatcher mode which can either be normal or stopping,
 * which means that new communication requests are ignored.
 */
enum Mode {NORMAL, STOPPING}

/**
 * Dispatcher class, which acts as an interface to new clients.
 * Upon communication initialization it selects a worker and forwards
 * the communication request to it. It is also responsible for stopping
 * the system.
 */
public class Dispatcher extends SimulatedActor {

    /**
     * mode property defining the mode currently active
     */
    private Mode mode;

    /**
     * Worker actors, which are managed by this actor
     */
    private final List<Worker> workers;

    /**
     * number of workers
     */
    private final int numberOfWorkers;

    /**
     * The system, which is used to spawn actors.
     */
    private final SimulatedActorSystem system;

    /**
     * List of acknowledgement messages to collect, which is only non-empty
     * in stopping mode- The list elements correspond to the actor-IDs of workers,
     * which have not yet acknowledged the stop messages sent to them.
     */
    private final List<Long> acksToCollect;

    /**
     * message store, which is used by workers to persist application data.
     */
    protected MessageStore messageStore;

    public Dispatcher(SimulatedActorSystem system, int numberOfWorkers) {
        this.system = system;
        this.workers = new ArrayList<>(numberOfWorkers);
        this.numberOfWorkers = numberOfWorkers;
        this.mode = Mode.NORMAL;
        this.acksToCollect = new ArrayList<>();
    }

    /**
     * Depending on messages sent and the mode, different actions are performed.
     *
     * @param message Non-null message received
     */
    @Override
    public void receive(Message message) {
        if (mode == Mode.NORMAL) {
            normalOperation(message);
        }
        if (mode == Mode.STOPPING) {
            stopping(message);
        }
    }

    /**
     * Creates all Workers and the message store
     */
    @Override
    public void atStartUp() {
        messageStore = new MessageStore();
        for (int i = 0; i < numberOfWorkers; i++) {
            Worker w = new Worker(this, messageStore, system);
            system.spawn(w);
            workers.add(w);
        }
        system.spawn(messageStore);
    }

    /**
     * In stopping mode, InitCommunication always fail, which is signal
     * using an OperationFailed message sent to the client.
     * In this mode, only StopAck-messages are expected and if all stop acknowledgements
     * have been collected, the Dispatcher stop itself.
     *
     * @param message received message
     */
    private void stopping(Message message) {
        if (message instanceof InitCommunication) {
            InitCommunication initM = ((InitCommunication) message);
            initM.client.tell(new OperationFailed(initM.communicationId));
        } else if (message instanceof StopAck) {
            SimulatedActor actor = ((StopAck) message).sender;
            acksToCollect.remove(actor.getId());
            system.stop(actor);
            if (acksToCollect.size() == 0) {
                system.stop(messageStore);
                system.stop(this);
            }
        }
    }

    /**
     * In normal operation messages are forwarded to workers.
     * A InitCommunication-message is forwarded to one worker
     * which is selected based on the communication id set in the message.
     * The selection scheme is (if workers are numbered from 0 to n - 1)
     * selected_worker_number = communication % n, where a % b is the non-negative
     * remainder of the integer division a/b.
     * If a Stop message is sent, it is broadcast to all workers and the mode
     * is switched to STOPPING.
     *
     * @param message message received
     */
    private void normalOperation(Message message) {
        if (message instanceof Stop) {
            for (Worker w : workers) {
                acksToCollect.add(w.getId());
                w.tell(new Stop());
            }
            mode = Mode.STOPPING;
        } else if (message instanceof InitCommunication) {
            // decide upon id for now, maybe switch to login credentials TODO
            InitCommunication initC = ((InitCommunication) message);
            Random random = new Random(initC.communicationId);
            int rnd = random.nextInt();
            int index = (((rnd % workers.size()) + workers.size()) % workers.size());
            Worker w = workers.get(index);
            w.tell(message);
        }
    }
}
