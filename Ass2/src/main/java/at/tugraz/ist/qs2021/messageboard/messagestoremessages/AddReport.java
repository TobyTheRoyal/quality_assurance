package at.tugraz.ist.qs2021.messageboard.messagestoremessages;

/**
 * Message used to signal that a report to a user.
 */
public class AddReport extends MessageStoreMessage {
    /**
     * user which should be reported
     */
    public final String reportedClientName;

    /**
     * name of the person which reported the user
     */
    public final String clientName;

    public AddReport(String clientName, long commId, String reportedClientName) {
        this.clientName = clientName;
        this.communicationId = commId;
        this.reportedClientName = reportedClientName;
    }
}
