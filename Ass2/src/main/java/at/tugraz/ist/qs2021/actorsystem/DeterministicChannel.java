package at.tugraz.ist.qs2021.actorsystem;

public class DeterministicChannel extends CommunicationChannel {

    /**
     * Fixed delay for this channel. All messages transmitted via this
     * channel take (delay + 1) calls to {@link ICommunicationChannel#tick()} to send.
     */
    private final int delay;

    /**
     * Constructs a new DeterministicChannel object.
     *
     * @param delay Fixed delay for each message.
     *              Set it to zero for instant transmission of messages
     *              (arrive at next call of {@link ICommunicationChannel#tick()})
     */
    public DeterministicChannel(int delay) {
        this.delay = delay;
    }

    @Override
    public void send(Message message) {
        addMessageInDelivery(new MessageInDelivery(delay, message));
    }
}
