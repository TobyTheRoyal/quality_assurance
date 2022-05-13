package at.tugraz.ist.qs2021;

import at.tugraz.ist.qs2021.actorsystem.Message;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActor;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActorSystem;
import at.tugraz.ist.qs2021.messageboard.Dispatcher;

import java.util.LinkedList;
import java.util.Queue;


public class SUTMessageBoard {
    private final SimulatedActorSystem system;
    private final Dispatcher dispatcher;
    private final TestClient client;

    private final long commId;

    public SUTMessageBoard() {
        commId = 1;
        system = new SimulatedActorSystem();
        dispatcher = new Dispatcher(system, 2);
        client = new TestClient();
        system.spawn(dispatcher);
        system.spawn(client);
    }

    public SimulatedActorSystem getSystem() {
        return system;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public TestClient getClient() {
        return client;
    }

    public long getCommId() {
        return commId;
    }
}


/**
 * Simple actor, which can be used in tests, e.g. to check if the correct messages are sent by workers.
 * This actor can be sent to workers as client.
 */
class TestClient extends SimulatedActor {

    /**
     * Messages received by this actor.
     */
    final Queue<Message> receivedMessages;

    TestClient() {
        receivedMessages = new LinkedList<>();
    }

    /**
     * does not implement any logic, only saves the received messages
     *
     * @param message Non-null message received
     */
    @Override
    public void receive(Message message) {
        receivedMessages.add(message);
    }
}
