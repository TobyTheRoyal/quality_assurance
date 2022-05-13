package at.tugraz.ist.qs2021.actorsystem;

public class MessageInDelivery {
    private int remainingTicks;
    private Message message;

    /**
     * Constructs a MessageInDelivery object.
     *
     * @param duration The total number of ticks the message needs to be delivered.
     * @param message  The message.
     */
    public MessageInDelivery(int duration, Message message) {
        this.remainingTicks = duration;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    /**
     * Check if message already arrived at its destination.
     * If not, reduce number of remaining ticks.
     *
     * @return True if message arrived at its destination, false if not.
     */
    boolean tick() {
        if (this.remainingTicks == 0) {
            return true;
        } else {
            this.remainingTicks--;
            return false;
        }
    }
}
