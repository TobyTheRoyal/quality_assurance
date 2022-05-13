package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Reply message sent from worker to client if a request succeeded.
 */
public class OperationAck extends Reply {
    public OperationAck(long communicationId) {
        super(communicationId);
    }

    @Override
    public String toString() {
        return "OperationAck(" + communicationId + ')';
    }
}
