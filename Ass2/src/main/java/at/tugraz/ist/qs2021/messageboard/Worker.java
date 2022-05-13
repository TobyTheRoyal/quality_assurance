package at.tugraz.ist.qs2021.messageboard;

import at.tugraz.ist.qs2021.actorsystem.Message;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActor;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActorSystem;
import at.tugraz.ist.qs2021.messageboard.clientmessages.*;
import at.tugraz.ist.qs2021.messageboard.dispatchermessages.Stop;
import at.tugraz.ist.qs2021.messageboard.dispatchermessages.StopAck;
import at.tugraz.ist.qs2021.messageboard.messagestoremessages.*;

import java.util.HashMap;
import java.util.Map;

public class Worker extends SimulatedActor {
    /**
     * actor responsible for persistence-related tasks
     */
    private final SimulatedActor messageStore;

    /**
     * dispatcher actor, which manages all workers
     */
    private final SimulatedActor dispatcher;

    /**
     * currently active communications with clients, the key of
     * the dictionary is a communication ID and the value a reference to the client
     */
    private final Map<Long, SimulatedActor> ongoingCommunications;

    /**
     * system used to spawn actors
     */
    private final SimulatedActorSystem system;

    /**
     * flag which is set if the worker is about to be stopped
     */
    private boolean stopping;

    /**
     * the maximum allowed length of a message
     */
    public final static int MAX_MESSAGE_LENGTH = 10;

    /**
     * Constructs a new Worker object
     *
     * @param dispatcher   the dispatcher
     * @param messageStore the message store responsible for persistence
     * @param system       the actor system simulation
     */
    public Worker(SimulatedActor dispatcher, SimulatedActor messageStore, SimulatedActorSystem system) {
        this.dispatcher = dispatcher;
        this.messageStore = messageStore;
        this.ongoingCommunications = new HashMap<>();
        this.system = system;
        this.stopping = false;
    }

    /**
     * Receive method which chooses the actions to perform depending on the message type.
     * Accepts the Stop message from the dispatcher and all ClientMessage messages except
     * the reply message OperationAck, InitAck,FinishAck and OperationFailed.
     * It does not accept any messages while stopping and responds with back
     * OperationFailed messages during stopping.
     * If an unknown communication ID is used for ClientMessage messages, an UnknownClientException-
     * exception is thrown. Further documentation can be found above helper methods named processMessageType.
     *
     * @param message Non-null message received
     * @throws UnknownClientException thrown if communication id of message is unknown
     */
    @Override
    public void receive(Message message) throws UnknownClientException {
        if (stopping && message instanceof ClientMessage) {
            // all operations while stopping fail
            ClientMessage clientMessage = (ClientMessage) message;
            if (!ongoingCommunications.containsKey(clientMessage.communicationId))
                throw new UnknownClientException("Unknown communication ID");
            ongoingCommunications.get(clientMessage.communicationId).tell(new OperationFailed(clientMessage.communicationId));
        } else if (message instanceof InitCommunication) {
            processInitCommunication(message);
        } else if (message instanceof FinishCommunication) {
            processFinishCommunication(message);
        } else if (message instanceof Stop) {
            processStop();
        } else if (message instanceof Publish)
            processPublish(message);
        else if (message instanceof RetrieveMessages) {
            processRetrieveMessages(message);
        } else if (message instanceof Like) {
            processLike(message);
        } else if (message instanceof Dislike) {
            processDislike(message);
        } else if (message instanceof Report) {
            processReport(message);
        } else if (message instanceof SearchMessages) {
            processSearchMessages(message);
        }
    }

    /**
     * Initiates communication with a client and sends an InitAck message to it,
     * which contains a reference to <c>this</c>.
     * After that other ClientMessage messages can be sent to this worker
     * using the communication ID given in the received message
     *
     * @param message non-null message of type InitCommunication
     */
    private void processInitCommunication(Message message) {
        InitCommunication initC = (InitCommunication) message;
        ongoingCommunications.put(initC.communicationId, initC.client);
        initC.client.tell(new InitAck(this, initC.communicationId));
    }


    /**
     * Finishes communication with a client and sends a FinishAck message to it.
     * After that other ClientMessage messages, using the communication ID given
     * in the received message, cannot be sent to this worker anymore
     *
     * @param message non-null message of type FinishCommunication
     * @throws UnknownClientException thrown if communication id of message is unknown
     */
    private void processFinishCommunication(Message message) throws UnknownClientException {
        FinishCommunication finC = (FinishCommunication) message;

        if (!ongoingCommunications.containsKey(finC.communicationId))
            throw new UnknownClientException("Unknown communication ID");
        SimulatedActor client = ongoingCommunications.get(finC.communicationId);
        ongoingCommunications.remove(finC.communicationId);
        client.tell(new FinishAck(finC.communicationId));
    }

    /**
     * Changes into stopping mode and acknowledges stopping to the dispatcher.
     */
    private void processStop() {
        dispatcher.tell(new StopAck(this));
        stopping = true;
    }

    /**
     * Spawns a worker helper which communicates with the message store to retrieve
     * messages of the author given in the message passed as parameter.
     *
     * @param message non-null message of type RetrieveMessages
     * @throws UnknownClientException thrown if communication id of message is unknown
     */
    private void processRetrieveMessages(Message message) throws UnknownClientException {
        RetrieveMessages retrMessages = (RetrieveMessages) message;
        if (!ongoingCommunications.containsKey(retrMessages.communicationId))
            throw new UnknownClientException("Unknown communication ID");
        SimulatedActor client = ongoingCommunications.get(retrMessages.communicationId);

        MessageStoreMessage retrievedMessages = new RetrieveFromStore(retrMessages.author, retrMessages.communicationId);
        WorkerHelper helper = new WorkerHelper(messageStore, client, retrievedMessages, system);
        system.spawn(helper);
    }

    /**
     * Spawns a worker helper which communicates with the message store to add a like
     * to a user message given in the message passed as parameter.
     *
     * @param message non-null message of type Like
     * @throws UnknownClientException thrown if communication id of message is unknown
     */
    private void processLike(Message message) throws UnknownClientException {
        Like like = (Like) message;
        if (!ongoingCommunications.containsKey(like.communicationId))
            throw new UnknownClientException("Unknown communication ID");
        SimulatedActor client = ongoingCommunications.get(like.communicationId);
        MessageStoreMessage retrievedMessages = new AddLike(like.clientName, like.messageId, like.communicationId);
        WorkerHelper helper = new WorkerHelper(messageStore, client, retrievedMessages, system);
        system.spawn(helper);
    }

    /**
     * message non-null message of type Dislike
     * Spawns a worker helper which communicates with the message store to add a dislike
     * to a user message given in the message passed as parameter.
     *
     * @param message The dislike message
     * @throws UnknownClientException thrown if communication id of message is unknown
     */
    private void processDislike(Message message) throws UnknownClientException {
        Dislike dislike = (Dislike) message;
        if (!ongoingCommunications.containsKey(dislike.communicationId))
            throw new UnknownClientException("Unknown communication ID");
        SimulatedActor client = ongoingCommunications.get(dislike.communicationId);
        MessageStoreMessage retrievedMessages =
                new AddDislike(dislike.clientName, dislike.messageId, dislike.communicationId);
        WorkerHelper helper = new WorkerHelper(messageStore, client, retrievedMessages, system);
        system.spawn(helper);
    }

    /**
     * Performs checks on a user message, which should be published. If the
     * checks are passed, a worker helper is spawned, which communicates with
     * the message store to store the new user message.
     * New messages must have zero likes, must not have a message ID assigned
     * and must not be (strictly) longer than MAX_MESSAGE_LENGTH characters.
     *
     * @param message non-null message of type Publish
     * @throws UnknownClientException thrown if communication id of message is unknown
     */
    private void processPublish(Message message) throws UnknownClientException {
        Publish publish = (Publish) message;
        if (!ongoingCommunications.containsKey(publish.communicationId))
            throw new UnknownClientException("Unknown communication ID");
        SimulatedActor client = ongoingCommunications.get(publish.communicationId);
        UserMessage userMessage = publish.message;
        if (userMessage.getLikes().size() > 0 || userMessage.getDislikes().size() > 0 ||
                userMessage.getMessageId() != UserMessage.NEW_ID ||
                userMessage.getMessage().length() > MAX_MESSAGE_LENGTH) {
            client.tell(new OperationFailed(publish.communicationId));
        } else {
            MessageStoreMessage updatedMessages = new UpdateMessageStore(userMessage, publish.communicationId);
            WorkerHelper helper = new WorkerHelper(messageStore, client, updatedMessages, system);
            system.spawn(helper);
        }
    }

    /**
     * Spawns a worker helper which communicates with the message store to add a report
     * to a user passed as parameter.
     *
     * @param message non-null message of type Report
     * @throws UnknownClientException thrown if communication id of report is unknown
     */
    private void processReport(Message message) throws UnknownClientException {
        Report report = (Report) message;
        if (!ongoingCommunications.containsKey(report.communicationId))
            throw new UnknownClientException("Unknown communication ID");
        SimulatedActor client = ongoingCommunications.get(report.communicationId);
        MessageStoreMessage reportedMessage = new AddReport(report.clientName, report.communicationId, report.reportedClientName);
        WorkerHelper helper = new WorkerHelper(messageStore, client, reportedMessage, system);
        system.spawn(helper);
    }

    /**
     * Spawns a worker helper which communicates with the message store to search
     * messages of the given search querry for author or Text.
     *
     * @param message non-null message of type SearchMessages
     * @throws UnknownClientException thrown if communication id of message is unknown
     */
    private void processSearchMessages(Message message) throws UnknownClientException {
        SearchMessages searchMessage = (SearchMessages) message;
        if (!ongoingCommunications.containsKey(searchMessage.communicationId))
            throw new UnknownClientException("Unknown communication ID");
        SimulatedActor client = ongoingCommunications.get(searchMessage.communicationId);

        MessageStoreMessage searchResults = new SearchInStore(searchMessage.searchText, searchMessage.communicationId);
        WorkerHelper helper = new WorkerHelper(messageStore, client, searchResults, system);
        system.spawn(helper);
    }
}
