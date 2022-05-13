package at.tugraz.ist.qs2021.actorsystem;

import at.tugraz.ist.qs2021.messageboard.UnknownClientException;

import java.util.List;

/**
 * Interface for SimulatedActor class.
 * <p>
 * It simulates actors known from other programming languages/frameworks like
 * Erlang or Akka, which is available for Java and Scala.
 * <p>
 * Actors are basically concurrently running entities, which communicate via
 * asynchronous message-passing. In our model, we will simulate time, respectively
 * concurrency, by using {@link ISimulatedActor#tick()}, which essentially signals that one time unit
 * has passed. This method is called on all currently active actors at each time step
 * and by that we simulate concurrency.
 * <p>
 * In general actors should be loosely coupled and their should only be seen
 * and manipulated by the actor itself. Hence, it is also suggested to test
 * actors mainly by sending messages and checking the responses. However,
 * this is not a requirement for the exercises, unless otherwise specified.
 */
public interface ISimulatedActor {
    /**
     * @return Unique id assigned to each actor
     */
    long getId();

    /**
     * Sets the unique id assigned to each actor
     *
     * @param id Unique actor id
     */
    void setId(long id);

    /**
     * @return All messages sent to this actor, this includes messages in transit,
     * already processed messages and messages in the <c>messageBox</c>.
     * It is used to alleviate debugging and testing.
     */
    List<Message> getMessageLog();

    /**
     * Time since the system was started.
     * Initially (after the construction) it is -1, shall be set to the current system time
     * right after {@link ISimulatedActor#atStartUp()} is called and shall be incremented
     * when {@link ISimulatedActor#tick()} is called.
     * If {@link SimulatedActor#receive(Message)} throws an exception, it might not correctly
     * reflect the current system time, but for the sake of simplicity, we ignore this fact.
     *
     * @return Time since start of system.
     */
    int getTimeSinceSystemStart();

    /**
     * Sets the time since the system was started.
     *
     * @param timeSinceSystemStart Time since start of system.
     * @see ISimulatedActor#getTimeSinceSystemStart()
     */
    void setTimeSinceSystemStart(int timeSinceSystemStart);

    /**
     * This method shall be implemented by all concrete actors individually
     * and shall contain all actor-specific logic (e.g. state updates).
     *
     * @param message Non-null message received
     * @throws UnknownClientException thrown if client is not known
     */
    void receive(Message message) throws UnknownClientException;

    /**
     * This is method is used to send messages to the actor represented
     * by <c>this</c>. Messages should also be logged.
     *
     * @param message Non-null message to be sent.
     */
    void tell(Message message);

    /**
     * Method to signal to the actor that one time-unit has passed.
     * If the actor is currently busy, the busyFor-time shall be decreased,
     * if busyFor reaches zero, it means that message processing is finished
     * and the application logic corresponding to the active message can be
     * triggered using a call to {@link ISimulatedActor#receive(Message)}.
     * <p>
     * If the actor can process a new message, it shall take a new message from
     * the <c>messageBox</c> and set the <c>busyFor</c>-time appropriately.
     */
    void tick() throws UnknownClientException;

    /**
     * Method which is called when the actor is spawned.
     */
    void atStartUp();
}
