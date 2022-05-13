package at.tugraz.ist.qs2021.messageboard.clientmessages;

/**
 * Message sent from client to worker to signal to report a user
 */
public class Report extends ClientMessage {
    /**
     * The user to be reported
     */
    public final String reportedClientName;

    /**
     * The name of the person who reported the user
     */
    public final String clientName;

    public Report(String clientName, long communicationId, String reportedClientName) {
        super(communicationId);
        this.clientName = clientName;
        this.reportedClientName = reportedClientName;
    }

    @Override
    public int getDuration() {
        return 1;
    }

    @Override
    public String toString() {
        return "Report(" + clientName + ", " + communicationId + ", " + reportedClientName + ')';
    }
}
