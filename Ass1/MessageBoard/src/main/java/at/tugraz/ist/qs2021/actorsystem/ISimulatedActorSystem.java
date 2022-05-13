package at.tugraz.ist.qs2021.actorsystem;

import at.tugraz.ist.qs2021.messageboard.UnknownClientException;

import java.util.List;

/**
 * Interface for SimulatedActorSystem class.
 * <p>
 * Its purpose is to manage a list of simulated actors. It is responsible for
 * starting and stopping of actors. All active actors in the system
 * are notified by this class when time units have passed, i.e.
 * the {@link ISimulatedActor#tick()} method should only be called
 * by this class.
 */
public interface ISimulatedActorSystem {

    /**
     * @return A list containing all actors, which have been started but not stopped.
     */
    List<SimulatedActor> getActors();

    /**
     * @return The number of ticks passed since this object was created.
     */
    int getCurrentTime();

    /**
     * actor ID assigned to new (not yet started) actors
     */
    long NEW_ACTOR = -1;

    /**
     * Starts a new actor, i.e. assigns it an ID, register it
     * in the list of active actors and call {@link ISimulatedActor#atStartUp()}.
     *
     * @param actor Actor to be started.
     */
    void spawn(SimulatedActor actor);

    /**
     * Runs the system for the number of ticks (time units) passed as parameter.
     * The system is run by calling {@link ISimulatedActor#tick()} on all active actors.
     *
     * @param numberOfTicks defines how long the system should be run including tick at endTime
     */
    void runFor(int numberOfTicks) throws UnknownClientException;


    /**
     * Runs the system until the current time (time units passed since object creation)
     * equals the number passed as parameter. The last tick shall be executed when the
     * current time is equal to the time passed as parameter. So the current time will
     * actually be equal to endTime + 1 after the call to this method.
     *
     * @param endTime the target time until which the system should be run
     */
    void runUntil(int endTime) throws UnknownClientException;

    /**
     * Stops the actor passed as parameter,
     * by removing it from the list of active actors.
     *
     * @param actor The actor to be stopped.
     */
    void stop(SimulatedActor actor);

    /**
     * Helper method used to iterate all actors and calling {@link ISimulatedActor#tick()} on it.
     */
    void tick() throws UnknownClientException;


}
