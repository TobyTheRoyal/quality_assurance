package at.tugraz.ist.qs2021.actorsystem;

import java.util.List;

/**
 * Interface for CommunicationChannel class.
 * <p>
 * The class simulates a communication channel existing between two actors, which
 * causes some delay for the transmission of messages. This corresponds e.g. to
 * TCP/IP connections in actor applications.
 */
public interface ICommunicationChannel {

    /**
     * This method is used to send messages via this channel,
     * it adds a new message to the messages currently transmitted via this channel.
     * Depending on the actual implementation varying delays can be added, or
     * messages can be dropped (e.g. to test parts of the application).
     *
     * @param message The message to send.
     */
    void send(Message message);

    /**
     * This method is used to signal to the channel object that one time unit has passed.
     * The ticks left for all messages in transit should be decremented,
     * except for those having zero ticks left, those messages should be returned,
     * because they reached their destinations.
     *
     * @return All messages having zero ticks left (upon entering the method).
     */
    List<Message> tick();
}
