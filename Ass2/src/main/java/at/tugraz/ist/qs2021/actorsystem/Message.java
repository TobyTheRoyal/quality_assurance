package at.tugraz.ist.qs2021.actorsystem;

/**
 * Interface which all messages need to implement.
 * Message instances are used for communication between actors.
 */
public interface Message {

    /**
     * To simulate that the processing of messages takes a certain amount of time,
     * we use this method to return the number of ticks it should take an actor
     * to process the message. As a simplification all implementing classes return
     * a constant value. However, the only restriction is that return value must
     * be greater or equal to zero.
     *
     * @return Number of ticks it takes to process this message.
     */
    int getDuration();
}
