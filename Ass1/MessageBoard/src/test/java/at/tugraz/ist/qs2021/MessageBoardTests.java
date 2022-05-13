package at.tugraz.ist.qs2021;

import at.tugraz.ist.qs2021.actorsystem.DeterministicChannel;
import at.tugraz.ist.qs2021.actorsystem.Message;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActor;
import at.tugraz.ist.qs2021.actorsystem.SimulatedActorSystem;
import at.tugraz.ist.qs2021.messageboard.*;
import at.tugraz.ist.qs2021.messageboard.clientmessages.*;
import at.tugraz.ist.qs2021.messageboard.dispatchermessages.Stop;
import at.tugraz.ist.qs2021.messageboard.dispatchermessages.StopAck;
import at.tugraz.ist.qs2021.messageboard.messagestoremessages.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

/**
 * Simple actor, which can be used in tests, e.g. to check if the correct messages are sent by workers.
 * This actor can be sent to workers as client.
 */
class TestClient extends SimulatedActor {

    /**
     * Messages received by this actor.
     */
    final Queue<Message> receivedMessages;

    TestClient() {
        receivedMessages = new LinkedList<>();
    }

    /**
     * does not implement any logic, only saves the received messages
     *
     * @param message Non-null message received
     */
    @Override
    public void receive(Message message) {
        receivedMessages.add(message);
    }
}

class NewMessage implements Message {

    public NewMessage() {
    }

    @Override
    public int getDuration() {
        return 2;
    }
}

class FalseMessage implements Message {

    public SimulatedActor storeClient = null;

    public FalseMessage() {
    }

    public FalseMessage(SimulatedActor client) {
        storeClient = client;
    }

    @Override
    public int getDuration() {
        return 2;
    }
}


class TestWorker extends SimulatedActor {

    // TODO: Class documentation

    SimulatedActorSystem system;
    TestClient client;
    Dispatcher dispatcher;
    Worker worker;

    TestWorker()
    {
        system = new SimulatedActorSystem();
        client = new TestClient();
        system.spawn(client);
        dispatcher = new Dispatcher(system, 1);
        system.spawn(dispatcher);

        ListIterator<SimulatedActor> sys_actors = system.getActors().listIterator();
        SimulatedActor test_worker;

        while(sys_actors.hasNext())
        {
            if((test_worker = sys_actors.next()).getClass() == Worker.class)
            {
                worker = (Worker) test_worker;
                break;
            }
        }
    }

    @Override
    public void receive(Message message) throws UnknownClientException {

        worker.receive(message);

        if(FalseMessage.class == message.getClass())
        {
            system.runFor(10);
            Assert.assertEquals(client.receivedMessages.size(), 0);
            return;
        }

        while (client.receivedMessages.size() == 0)
            system.runFor(1);
    }

    public void restart() throws UnknownClientException {

        dispatcher.tell(new Stop());

        while(system.getActors().contains(dispatcher))
        {
            system.runFor(1);
        }

        dispatcher = new Dispatcher(system, 1);
        system.spawn(dispatcher);

        ListIterator<SimulatedActor> sys_actors = system.getActors().listIterator();
        SimulatedActor test_worker;

        while(sys_actors.hasNext())
        {
            if((test_worker = sys_actors.next()).getClass() == Worker.class)
            {
                worker = (Worker) test_worker;
                break;
            }
        }

        receive(new InitCommunication(client, 0));
        client.receivedMessages.remove();
    }


    public void init(Message message) throws UnknownClientException {

    receive(message);

    Message initAckMessage = client.receivedMessages.remove();
    Assert.assertEquals(InitAck.class, initAckMessage.getClass());
    InitAck initAck = (InitAck) initAckMessage;

    Assert.assertEquals(initAck.worker, worker);
    }


    public void finish(Message message, boolean active_client) {

        String unknown_client = null;
        try
        {
            receive(message);
        }
        catch(UnknownClientException com_ended)
        {
            unknown_client = com_ended.getMessage();
        }

        if(!active_client)
            Assert.assertNotNull(unknown_client);
        else
        {
            Message finAckMessage = client.receivedMessages.remove();
            Assert.assertEquals(FinishAck.class, finAckMessage.getClass());
        }
    }

    public void stop(Message message, boolean active_client) throws UnknownClientException {

        // Stopping worker
        worker.receive(message);

        // Check if worker was really stopped
        String unknown_client = null;
        try
        {
            // Arbitrary "tell" worker, to test branches for after stop
            receive(new FinishCommunication(0));
        }
        catch(UnknownClientException com_ended)
        {
            unknown_client = com_ended.getMessage();
        }

        if(!active_client)
            Assert.assertNotNull(unknown_client);
        else
        {
            Message opFailedMessage = client.receivedMessages.remove();
            Assert.assertEquals(OperationFailed.class, opFailedMessage.getClass());
        }

    }

    public void publish(Message message, boolean active_client) {


        long current_id = 0;
        if(Publish.class == message.getClass())
            current_id = ((Publish) message).message.getMessageId();

        String unknown_client = null;
        try
        {
            receive(message);
        }
        catch(UnknownClientException com_ended)
        {
            unknown_client = com_ended.getMessage();
        }

        if(!active_client)
        {
            Assert.assertNotNull(unknown_client);
            return;
        }


        if(Publish.class == message.getClass())
        {
            Message stateMessage = client.receivedMessages.remove();

            // Message too long test
            if(((Publish) message).message.getMessage().length() > 10)
            {
                Assert.assertEquals(OperationFailed.class, stateMessage.getClass());
            }
            // Likes/dislikes on a not published post
            else if(((Publish) message).message.getLikes().size() > 0
                || ((Publish) message).message.getDislikes().size() > 0)
            {
                Assert.assertEquals(OperationFailed.class, stateMessage.getClass());
            }
            // Republish test
            else if(current_id != UserMessage.NEW_ID)
            {
                Assert.assertEquals(OperationFailed.class, stateMessage.getClass());
            }
            // Normal Publish test
            else
            {
                Assert.assertEquals(OperationAck.class, stateMessage.getClass());
                OperationAck opAck = (OperationAck) stateMessage;

                Assert.assertEquals(opAck.communicationId, ((Publish) message).communicationId);
            }
        }

    }

    public void storeOperation(Message message, boolean active_client) {


        String unknown_client = null;
        try
        {
            receive(message);
        }
        catch(UnknownClientException com_ended)
        {
            unknown_client = com_ended.getMessage();
        }

        if(!active_client)
            Assert.assertNotNull(unknown_client);
        else
        {
            Message opAckMessage = client.receivedMessages.remove();
            Assert.assertEquals(OperationAck.class, opAckMessage.getClass());

            if(Like.class == message.getClass())
            {
                Assert.assertEquals(((OperationAck) opAckMessage).communicationId, ((Like) message).communicationId);
            }
            else if(Dislike.class == message.getClass())
            {
                Assert.assertEquals(((OperationAck) opAckMessage).communicationId, ((Dislike) message).communicationId);
            }
            else
            {
                Assert.assertEquals(((OperationAck) opAckMessage).communicationId, ((Report) message).communicationId);
            }

        }

    }

    public void searchRetrieve(Message message, boolean active_client) {
        String unknown_client = null;
        try
        {
            receive(message);
        }
        catch(UnknownClientException com_ended)
        {
            unknown_client = com_ended.getMessage();
        }

        if(!active_client)
            Assert.assertNotNull(unknown_client);
        else
        {
            Message foundMessage = client.receivedMessages.remove();
            Assert.assertEquals(FoundMessages.class, foundMessage.getClass());

            if(SearchMessages.class == message.getClass()) {
                Assert.assertEquals(((FoundMessages) foundMessage).communicationId,
                    ((SearchMessages) message).communicationId);

                ListIterator<UserMessage> found_messages = ((FoundMessages) foundMessage).messages.listIterator();
                while (found_messages.hasNext()) {

                    Assert.assertEquals(found_messages.next().getMessage(),
                        ((SearchMessages) message).searchText);
                }
            }
            else {
                Assert.assertEquals(((FoundMessages) foundMessage).communicationId,
                    ((RetrieveMessages) message).communicationId);

                ListIterator<UserMessage> found_messages = ((FoundMessages) foundMessage).messages.listIterator();
                while (found_messages.hasNext()) {

                    Assert.assertEquals(found_messages.next().getAuthor(),
                        ((RetrieveMessages) message).author);
                }
            }
        }
    }
}

public class MessageBoardTests {

    /**
     * Simple first test initiating a communication and closing it afterwards.
     */
    @Test
    public void testCommunication() throws UnknownClientException {
        // testing only the acks
        SimulatedActorSystem system = new SimulatedActorSystem();
        Dispatcher dispatcher = new Dispatcher(system, 2);
        system.spawn(dispatcher);
        TestClient client = new TestClient();
        system.spawn(client);

        // send request and run system until a response is received
        // communication id is chosen by clients
        dispatcher.tell(new InitCommunication(client, 10));
        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Message initAckMessage = client.receivedMessages.remove();
        Assert.assertEquals(InitAck.class, initAckMessage.getClass());
        InitAck initAck = (InitAck) initAckMessage;
        Assert.assertEquals(Long.valueOf(10), initAck.communicationId);

        SimulatedActor worker = initAck.worker;

        // end the communication
        worker.tell(new FinishCommunication(10));
        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Message finAckMessage = client.receivedMessages.remove();
        Assert.assertEquals(FinishAck.class, finAckMessage.getClass());
        FinishAck finAck = (FinishAck) finAckMessage;

        Assert.assertEquals(Long.valueOf(10), finAck.communicationId);
        dispatcher.tell(new Stop());

        // TODO: run system until workers and dispatcher are stopped
    }

    @Test
    public void testSimulatedActor() throws UnknownClientException {

        SimulatedActorSystem sim_actor = new SimulatedActorSystem();
        Dispatcher dispatcher = new Dispatcher(sim_actor, 1);
        sim_actor.spawn(dispatcher);
        TestClient client = new TestClient();
        Assert.assertEquals(client.getId(), -1);
        sim_actor.spawn(client);
        Assert.assertEquals(client.getTimeSinceSystemStart(), 0);
        client.tick();
        Assert.assertEquals(client.getTimeSinceSystemStart(), 1);
        Assert.assertEquals(client.getId(), 3);

        Message message = new InitCommunication(client, 1);
        client.tell(message);
        Assert.assertEquals(client.getMessageLog().size(), 1);
        sim_actor.runFor(10);
        Assert.assertEquals(client.getTimeSinceSystemStart(), 11);

    }

    @Test
    public void testSimulatedActorSystem() throws UnknownClientException {
        SimulatedActorSystem sim_actor = new SimulatedActorSystem();

        TestClient client = new TestClient();
        sim_actor.spawn(client);
        Assert.assertEquals(sim_actor.getActors().size(), 1);
        Assert.assertEquals(sim_actor.getCurrentTime(), 0);

        sim_actor.runUntil(10);
        Assert.assertEquals(sim_actor.getCurrentTime(), 11);

        sim_actor.runFor(4);
        Assert.assertEquals(sim_actor.getCurrentTime(), 15);

        sim_actor.stop(client);
        Assert.assertEquals(sim_actor.getActors().size(), 0);

        sim_actor.spawn(client);
        Assert.assertEquals(sim_actor.getActors().size(), 1);

        NewMessage message2 = new NewMessage();
        client.receive(message2);
        Assert.assertEquals(client.receivedMessages.size(), 1);

        client.receive(message2);
        Assert.assertEquals(client.receivedMessages.size(), 2);
    }

    @Test
    public void testDispatcherMessages() throws UnknownClientException {
        SimulatedActorSystem sim_actor = new SimulatedActorSystem();
        TestClient client = new TestClient();
        sim_actor.spawn(client);
        Stop stop = new Stop();
        Assert.assertEquals(stop.getDuration(), 2);
        StopAck stopAck = new StopAck(client);
        Assert.assertEquals(stopAck.getDuration(), 2);

    }

    @Test
    public void testDispatcherStopping() throws UnknownClientException {

        SimulatedActorSystem system = new SimulatedActorSystem();
        TestClient client = new TestClient();
        system.spawn(client);
        Dispatcher dispatcher = new Dispatcher(system, 1);
        system.spawn(dispatcher);

        Stop stop = new Stop();
        dispatcher.receive(stop);

        // Send init, after stop
        InitCommunication init = new InitCommunication(client, 1);
        dispatcher.receive(init);

        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Message op_failed = client.receivedMessages.remove();
        Assert.assertEquals(OperationFailed.class, op_failed.getClass());

        // Dispatcher waiting for Stopacks from multiple workers
        Dispatcher dispatcher2 = new Dispatcher(system, 5);
        system.spawn(dispatcher2);

        dispatcher2.receive(stop);

        ListIterator<SimulatedActor> sys_actors = system.getActors().listIterator();
        while(sys_actors.hasNext())
        {
            if(sys_actors.next().getId() == dispatcher2.getId())
            {
                system.runFor(1);
                sys_actors = system.getActors().listIterator();
            }
        }
    }

    @Test
    public void testMessageStoreMessages() throws UnknownClientException {

        AddReport addReport = new AddReport("client", 1, "clientRep");
        Assert.assertEquals(addReport.clientName, "client");
        Assert.assertEquals(addReport.reportedClientName, "clientRep");
        Assert.assertEquals(addReport.communicationId, 1);
        Assert.assertEquals(addReport.getDuration(), 1);

        AddLike addLike = new AddLike("client", 1, 0);
        Assert.assertEquals(addLike.communicationId, 0);
        Assert.assertEquals(addLike.clientName, "client");
        Assert.assertEquals(addLike.messageId, 1);
        Assert.assertEquals(addLike.getDuration(), 1);

        AddDislike addDislike = new AddDislike("client", 1, 0);
        Assert.assertEquals(addDislike.communicationId, 0);
        Assert.assertEquals(addDislike.clientName, "client");
        Assert.assertEquals(addDislike.messageId, 1);
        Assert.assertEquals(addDislike.getDuration(), 1);

        RetrieveFromStore retrieveFromStore = new RetrieveFromStore("Autor", 0);
        Assert.assertEquals(retrieveFromStore.communicationId, 0);
        Assert.assertEquals(retrieveFromStore.author, "Autor");
        Assert.assertEquals(retrieveFromStore.getDuration(), 1);

        UserMessage message = new UserMessage("Autor", "Das ist eine Nachricht");

        UpdateMessageStore updateMessageStore = new UpdateMessageStore(message, 0);
        Assert.assertEquals(updateMessageStore.communicationId, 0);
        Assert.assertEquals(updateMessageStore.message.getMessage(), message.getMessage());
        Assert.assertEquals(updateMessageStore.getDuration(), 1);


        SearchInStore searchInStore = new SearchInStore("Autor", 0);
        Assert.assertEquals(searchInStore.communicationId, 0);
        Assert.assertEquals(searchInStore.searchText, "Autor");
    }

    @Test
    public void testUserMessages() throws UnknownClientException {
        SimulatedActorSystem sim_actor = new SimulatedActorSystem();
        TestClient client = new TestClient();
        sim_actor.spawn(client);
        UserMessage message = new UserMessage("Autor", "Hey alles klar?");
        Assert.assertEquals(message.getAuthor(), "Autor");
        Assert.assertEquals(message.getMessage(), "Hey alles klar?");
        Assert.assertEquals(message.toString(), "Autor:Hey alles klar? liked by : disliked by :");
        Assert.assertEquals(message.getLikes().size(), 0);
        Assert.assertEquals(message.getDislikes().size(), 0);
        Assert.assertEquals(message.getMessageId(), UserMessage.NEW_ID);
        message.setMessageId(0);
        Assert.assertEquals(message.getMessageId(), 0);


    }

    @Test
    public void testWorkerHelper() throws UnknownClientException {

        SimulatedActorSystem sim_actor = new SimulatedActorSystem();
        TestClient client = new TestClient();
        MessageStoreMessage addLike = new AddLike("client", 1, 0);
        Dispatcher dispatcher = new Dispatcher(sim_actor, 1);
        sim_actor.spawn(dispatcher);
        sim_actor.spawn(client);

        dispatcher.tell(new InitCommunication(client, 10));
        while (client.receivedMessages.size() == 0)
            sim_actor.runFor(1);

        Message initAckMessage = client.receivedMessages.remove();
        Assert.assertEquals(InitAck.class, initAckMessage.getClass());
        InitAck initAck = (InitAck) initAckMessage;
        Assert.assertEquals(Long.valueOf(10), initAck.communicationId);

        MessageStore messageStore = new MessageStore();
        WorkerHelper helper = new WorkerHelper(messageStore, client, addLike, sim_actor);
        Assert.assertEquals(sim_actor.getActors().size(), 4);
        sim_actor.spawn(helper);
        Assert.assertEquals(messageStore.getMessageLog().size(), 1);
        Assert.assertEquals(sim_actor.getActors().size(), 5);

        while (client.receivedMessages.size() == 0)
            sim_actor.runFor(1);
        Message message = client.receivedMessages.remove();
        Assert.assertEquals(sim_actor.getActors().size(), 4);
        helper.receive(message);

        Assert.assertEquals(OperationFailed.class, message.getClass());
        Assert.assertEquals(client.getMessageLog().size(), 3);
        sim_actor.spawn(helper);
        helper.receive(message);
        Assert.assertEquals(sim_actor.getActors().size(), 4);

        helper.tick();
        Assert.assertEquals(helper.getTimeSinceSystemStart(), 29);
        Assert.assertEquals(messageStore.getMessageLog().size(), 4);
    }

    @Test
    public void testWorker() throws UnknownClientException {
        TestWorker test_worker = new TestWorker();

        /*
        /* Active client tests
        */

        /* Init client */
        Message init_message = new InitCommunication(test_worker.client, 0);
        test_worker.init(init_message);

        /* Publish Messages */
        // Invalid UserMessages
        Message false_message = new FalseMessage();
        test_worker.receive(false_message);

        UserMessage invalid_user_message = new UserMessage("WorkerTester", "Let's test publishing a too long message");
        Publish invalid_publish_message = new Publish(invalid_user_message, 0);
        test_worker.publish(invalid_publish_message, true);

        // Invalid publish of unpublished message, which already contains likes or dislikes
        UserMessage invalid_user_message_like = new UserMessage("WorkerTester", "Cheating");
        invalid_user_message_like.getLikes().add("WorkerTester");
        Publish publish_message_like = new Publish(invalid_user_message_like, 0);
        test_worker.publish(publish_message_like, true);

        UserMessage invalid_user_message_dis = new UserMessage("WorkerTester", "Cheating");
        invalid_user_message_dis.getDislikes().add("WorkerTester");
        Publish publish_message_dis = new Publish(invalid_user_message_dis, 0);
        test_worker.publish(publish_message_dis, true);

        // Valid UserMessage
        UserMessage user_message = new UserMessage("WorkerTester", "Now though");
        Publish publish_message = new Publish(user_message, 0);
        test_worker.publish(publish_message, true);

        // Publishing already published message
        test_worker.publish(publish_message, true);

        // MessageStore operations - Like / Dislike / Report
        Like like_message = new Like("WorkerTester", 0, user_message.getMessageId());
        test_worker.storeOperation(like_message, true);

        Dislike dislike_message = new Dislike("WorkerTester", 0, user_message.getMessageId());
        test_worker.storeOperation(dislike_message, true);

        // Search / Retrieve Message
        SearchMessages search_message = new SearchMessages("Now though", 0);
        test_worker.searchRetrieve(search_message, true);

        RetrieveMessages retrieve_message = new RetrieveMessages("Now though", 0);
        test_worker.searchRetrieve(retrieve_message, true);

        // Another Valid UserMessage
        UserMessage user_message_1 = new UserMessage("WorkerTester1", "HelloWorld");
        Publish publish_message_1 = new Publish(user_message, 0);
        test_worker.publish(publish_message_1, true);

        // Report
        Report report_message = new Report("WorkerTester", 0, "WorkerTester1");
        test_worker.storeOperation(report_message, true);

        // Stop worker
        Message stop_message = new Stop();
        test_worker.stop(stop_message, true);

        test_worker.restart();

        /*
         /* Inactive client tests
         */

        // End communication with client
        Message fin_message = new FinishCommunication(0);
        test_worker.finish(fin_message, true);
        Assert.assertEquals(fin_message.getDuration(), 3);

        // Publish messages
        UserMessage no_client_user_message = new UserMessage("WorkerTester", "Offline");
        Publish no_client_publish_message = new Publish(no_client_user_message, 0);
        test_worker.publish(no_client_publish_message, false);

        // MessageStore operations - Like / Dislike / Report
        test_worker.storeOperation(like_message, false);
        test_worker.storeOperation(dislike_message, false);
        test_worker.storeOperation(report_message, false);

        // Search / Retrieve Message
        test_worker.searchRetrieve(search_message, false);
        test_worker.searchRetrieve(retrieve_message, false);

        // End communication with client
        test_worker.finish(fin_message, false);

        // Stop worker
        test_worker.stop(stop_message, false);
    }


    @Test
    public void testFindByAuthorAndAuthorOrText() throws UnknownClientException {
        SimulatedActorSystem system = new SimulatedActorSystem();
        Dispatcher dispatcher = new Dispatcher(system, 2);
        system.spawn(dispatcher);
        TestClient client1 = new TestClient();
        long id1 = 1;
        system.spawn(client1);
        TestClient client2 = new TestClient();
        long id2 = 2;
        system.spawn(client2);

        dispatcher.tell(new InitCommunication(client1, id1));
        dispatcher.tell(new InitCommunication(client2, id2));

        while (client1.receivedMessages.size() == 0 || client2.receivedMessages.size() == 0)
            system.runFor(1);

        Message initAckMessage1 = client1.receivedMessages.remove();
        InitAck initAck1 = (InitAck) initAckMessage1;

        Message initAckMessage2 = client2.receivedMessages.remove();
        InitAck initAck2 = (InitAck) initAckMessage2;

        SimulatedActor worker1 = initAck1.worker;
        SimulatedActor worker2 = initAck2.worker;

        // Test non existing message class
        MessageStore messagestore = new MessageStore();
        messagestore.receive(new FalseMessage(client1));
        system.runFor(20);
        Assert.assertEquals(client1.receivedMessages.size(), 0);

        // ------------------- Search for Author
        Publish AuthorMessage = new Publish(new UserMessage("Jaco", "Author"), id1);
        worker1.receive(AuthorMessage);
        while (client1.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client1.receivedMessages.element().getClass(), OperationAck.class);

        client1.receivedMessages.remove();

        RetrieveMessages message = new RetrieveMessages("Jaco", id2);
        Assert.assertEquals(message.getDuration(), 3);
        Assert.assertEquals(message.author, "Jaco");
        worker2.receive(message);
        while (client2.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client2.receivedMessages.element().getClass(), FoundMessages.class);
        FoundMessages found = (FoundMessages) client2.receivedMessages.remove();
        Assert.assertEquals(found.communicationId.longValue(), id2);
        Assert.assertEquals(1, found.messages.size());
        Assert.assertEquals(found.getDuration(), 1);

        // ------------------- Search for Text
        Publish TextMessage = new Publish(new UserMessage("Coco", "searchy?!"), id2);
        worker2.receive(TextMessage);
        while (client2.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client2.receivedMessages.element().getClass(), OperationAck.class);

        client2.receivedMessages.remove();


        SearchMessages messageSearch = new SearchMessages("searchy?!", id1);
        Assert.assertEquals(messageSearch.getDuration(), 3);

        Assert.assertEquals(messageSearch.searchText, "searchy?!");
        worker1.receive(messageSearch);

        while (client1.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client1.receivedMessages.element().getClass(), FoundMessages.class);
        FoundMessages foundSearch = (FoundMessages) client1.receivedMessages.remove();
        Assert.assertEquals(foundSearch.communicationId.longValue(), id1);
        Assert.assertEquals(1, foundSearch.messages.size());


        // ------------------- Also search for non existingText
        SearchMessages messageNoFinds = new SearchMessages("nix da", id1);
        Assert.assertEquals(messageNoFinds.searchText, "nix da");
        worker1.receive(messageNoFinds);

        while (client1.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client1.receivedMessages.element().getClass(), FoundMessages.class);
        FoundMessages foundNothing = (FoundMessages) client1.receivedMessages.remove();
        Assert.assertEquals(foundNothing.communicationId.longValue(), id1);
        Assert.assertEquals(0, foundNothing.messages.size());

        // ------------------- Also search for author via text
        SearchMessages messageAuthor = new SearchMessages("Coco", id1);
        Assert.assertEquals(messageAuthor.searchText, "Coco");
        worker1.receive(messageAuthor);

        while (client1.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client1.receivedMessages.element().getClass(), FoundMessages.class);
        FoundMessages foundAuthor = (FoundMessages) client1.receivedMessages.remove();
        Assert.assertEquals(foundAuthor.communicationId.longValue(), id1);
        Assert.assertEquals(1, foundAuthor.messages.size());

        // ------------------- Also search for oneself
        SearchMessages messageSelf = new SearchMessages("Jaco", id1);
        Assert.assertEquals(messageSelf.searchText, "Jaco");
        worker1.receive(messageAuthor);

        while (client1.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client1.receivedMessages.element().getClass(), FoundMessages.class);
        FoundMessages foundSelf = (FoundMessages) client1.receivedMessages.remove();
        Assert.assertEquals(foundSelf.communicationId.longValue(), id1);
        Assert.assertEquals(1, foundSelf.messages.size());

        // end the communication
        worker1.tell(new FinishCommunication(id1));
        worker2.tell(new FinishCommunication(id2));
        while (client1.receivedMessages.size() == 0 || client2.receivedMessages.size() == 0)
            system.runFor(1);

        dispatcher.tell(new Stop());
    }

    @Test

    public void testLikeAndDislike() throws UnknownClientException {
        SimulatedActorSystem system = new SimulatedActorSystem();
        Dispatcher dispatcher = new Dispatcher(system, 2);
        system.spawn(dispatcher);
        TestClient client = new TestClient();
        system.spawn(client);

        // send request and run system until a response is received
        // communication id is chosen by clients
        long communicationID = 10;
        dispatcher.tell(new InitCommunication(client, communicationID));
        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Message initAckMessage = client.receivedMessages.remove();
        InitAck initAck = (InitAck) initAckMessage;

        SimulatedActor worker = initAck.worker;

        Publish publishedMessage = new Publish(new UserMessage("Jaco", "Like Test"), communicationID);
        Assert.assertEquals(publishedMessage.getDuration(), 3);
        worker.receive(publishedMessage);

        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Like publishLike = new Like("Jaco", communicationID, publishedMessage.message.getMessageId());
        Assert.assertEquals(publishLike.getDuration(), 1);
        worker.receive(publishLike);
        client.receivedMessages.remove();

        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        //check if 1 like
        Assert.assertEquals(publishedMessage.message.getLikes().size(), 1);

        Dislike publishDislike = new Dislike("Jaco", communicationID, publishedMessage.message.getMessageId());
        Assert.assertEquals(publishDislike.getDuration(), 1);

        worker.receive(publishDislike);
        client.receivedMessages.remove();

        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        //check if 1 dislike
        Assert.assertEquals(publishedMessage.message.getDislikes().size(), 1);

        client.receivedMessages.remove();

        Like publishLikeNonExistingMessage = new Like("Jaco", communicationID, -1);
        worker.receive(publishLikeNonExistingMessage);


        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        //check if 1 like
        Assert.assertEquals(client.receivedMessages.element().getClass(), OperationFailed.class);

        client.receivedMessages.remove();
        Dislike publishDislikeNonExistingMessage = new Dislike("Jaco", communicationID, -1);
        worker.receive(publishDislikeNonExistingMessage);


        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        //check if 1 dislike
        //Assert.assertEquals(client.receivedMessages.element().getClass(), OperationFailed.class);

        client.receivedMessages.remove();

        // end the communication
        worker.tell(new FinishCommunication(communicationID));
        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Message finAckMessage = client.receivedMessages.remove();
        Assert.assertEquals(FinishAck.class, finAckMessage.getClass());
        FinishAck finAck = (FinishAck) finAckMessage;

        Assert.assertEquals(Long.valueOf(10), finAck.communicationId);
        dispatcher.tell(new Stop());
    }

    @Test
    public void testUpdate() throws UnknownClientException {
        SimulatedActorSystem system = new SimulatedActorSystem();
        Dispatcher dispatcher = new Dispatcher(system, 2);
        system.spawn(dispatcher);
        TestClient client = new TestClient();
        system.spawn(client);

        // send request and run system until a response is received
        // communication id is chosen by clients
        long communicationID = 10;
        dispatcher.tell(new InitCommunication(client, communicationID));
        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Message initAckMessage = client.receivedMessages.remove();
        InitAck initAck = (InitAck) initAckMessage;

        SimulatedActor worker = initAck.worker;

        Publish publishedMessage = new Publish(new UserMessage("Jaco", "Message"), communicationID);
        worker.receive(publishedMessage);

        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        client.receivedMessages.remove();

        //post the same
        Publish sameMessage = new Publish(new UserMessage("Jaco", "Message"), communicationID);
        worker.receive(sameMessage);

        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(OperationFailed.class, client.receivedMessages.element().getClass());
        client.receivedMessages.remove();

        //post with other author
        Publish otherAuthor = new Publish(new UserMessage("Coco", "Message"), communicationID);
        worker.receive(otherAuthor);

        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(OperationAck.class, client.receivedMessages.element().getClass());
        client.receivedMessages.remove();

        //post with other message
        Publish otherMessage = new Publish(new UserMessage("Jaco", "Update"), communicationID);
        worker.receive(otherMessage);

        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(OperationAck.class, client.receivedMessages.element().getClass());
        client.receivedMessages.remove();


        // end the communication
        worker.tell(new FinishCommunication(communicationID));
        while (client.receivedMessages.size() == 0)
            system.runFor(1);

        Message finAckMessage = client.receivedMessages.remove();
        Assert.assertEquals(FinishAck.class, finAckMessage.getClass());
        FinishAck finAck = (FinishAck) finAckMessage;
        Assert.assertEquals(finAck.getDuration(), 1);

        Assert.assertEquals(Long.valueOf(10), finAck.communicationId);
        dispatcher.tell(new Stop());
    }

    @Test
    public void testReport() throws UnknownClientException {
        SimulatedActorSystem system = new SimulatedActorSystem();
        Dispatcher dispatcher = new Dispatcher(system, 2);
        system.spawn(dispatcher);
        TestClient client1 = new TestClient();
        long id1 = 1;
        system.spawn(client1);
        TestClient client2 = new TestClient();
        long id2 = 2;
        system.spawn(client2);

        dispatcher.tell(new InitCommunication(client1, id1));
        dispatcher.tell(new InitCommunication(client2, id2));

        while (client1.receivedMessages.size() == 0 || client2.receivedMessages.size() == 0)
            system.runFor(1);

        Message initAckMessage1 = client1.receivedMessages.remove();
        InitAck initAck1 = (InitAck) initAckMessage1;

        Message initAckMessage2 = client2.receivedMessages.remove();
        InitAck initAck2 = (InitAck) initAckMessage2;

        SimulatedActor worker1 = initAck1.worker;
        SimulatedActor worker2 = initAck2.worker;

        Publish publishedMessage = new Publish(new UserMessage("Jaco", "Test"), id1);

        worker1.receive(publishedMessage);
        while (client1.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client1.receivedMessages.element().getClass(), OperationAck.class);

        client1.receivedMessages.remove();

        Report report = new Report("Coco", id2, "Jaco");
        Assert.assertEquals(report.getDuration(), 1);

        worker2.receive(report);
        while (client2.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client2.receivedMessages.element().getClass(), OperationAck.class);
        client2.receivedMessages.remove();

        //report twice
        Report report2 = new Report("Coco", id2, "Jaco");
        worker2.receive(report2);
        while (client2.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client2.receivedMessages.element().getClass(), OperationFailed.class);
        client2.receivedMessages.remove();

        //selfreport
        Report report3 = new Report("Coco", id2, "Coco");
        worker2.receive(report3);
        while (client2.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client2.receivedMessages.element().getClass(), OperationAck.class);
        client2.receivedMessages.remove();

        // end the communication
        worker1.tell(new FinishCommunication(id1));
        worker2.tell(new FinishCommunication(id2));
        while (client1.receivedMessages.size() == 0 || client2.receivedMessages.size() == 0)
            system.runFor(1);

        dispatcher.tell(new Stop());
    }

    @Test
    public void testBan() throws UnknownClientException {
        SimulatedActorSystem system = new SimulatedActorSystem();
        Dispatcher dispatcher = new Dispatcher(system, 2);
        system.spawn(dispatcher);
        TestClient client1 = new TestClient();
        long id1 = 1;
        system.spawn(client1);
        TestClient client2 = new TestClient();
        long id2 = 2;
        system.spawn(client2);

        dispatcher.tell(new InitCommunication(client1, id1));
        dispatcher.tell(new InitCommunication(client2, id2));

        while (client1.receivedMessages.size() == 0 || client2.receivedMessages.size() == 0)
            system.runFor(1);

        Message initAckMessage1 = client1.receivedMessages.remove();
        InitAck initAck1 = (InitAck) initAckMessage1;

        Message initAckMessage2 = client2.receivedMessages.remove();
        InitAck initAck2 = (InitAck) initAckMessage2;

        SimulatedActor worker1 = initAck1.worker;
        SimulatedActor worker2 = initAck2.worker;

        Publish goodMessage = new Publish(new UserMessage("Jaco", "open"), id1);
        worker1.receive(goodMessage);
        while (client1.receivedMessages.size() == 0)
            system.runFor(1);

        Assert.assertEquals(client1.receivedMessages.element().getClass(), OperationAck.class);

        client1.receivedMessages.remove();

        //report 5 times
        for (int i = 0; i <= 5; i++) {
            Report report = new Report(String.valueOf(i),id2, "Jaco");
            worker2.receive(report);
            while (client2.receivedMessages.size() == 0)
                system.runFor(1);

            Assert.assertEquals(client2.receivedMessages.element().getClass(), OperationAck.class);
            client2.receivedMessages.remove();
        }

        //test Publish
        Publish badMessage = new Publish(new UserMessage("Jaco", "blocked"), id1);
        worker1.receive(badMessage);
        while (client1.receivedMessages.size() == 0)
            system.runFor(1);
        Assert.assertEquals(client1.receivedMessages.element().getClass(), UserBanned.class);
        client1.receivedMessages.remove();

        //create TestMessage
        Publish testMessage = new Publish(new UserMessage("Coco", "works"), id2);
        worker2.receive(testMessage);
        while (client2.receivedMessages.size() == 0)
            system.runFor(1);
        Assert.assertEquals(client2.receivedMessages.element().getClass(), OperationAck.class);
        client2.receivedMessages.remove();


        //test report
        Report badReport = new Report("Jaco", id1, "Coco");
        worker1.receive(badReport);
        while (client1.receivedMessages.size() == 0)
            system.runFor(1);
        Assert.assertEquals(client1.receivedMessages.element().getClass(), UserBanned.class);
        client1.receivedMessages.remove();

        //test Like
        Like badLike = new Like("Jaco", id1, testMessage.message.getMessageId());
        worker1.receive(badLike);
        while (client1.receivedMessages.size() == 0)
            system.runFor(1);
        Assert.assertEquals(client1.receivedMessages.element().getClass(), UserBanned.class);
        client1.receivedMessages.remove();

        //test DisLike
        Dislike badDisLike = new Dislike("Jaco", id1, testMessage.message.getMessageId());
        worker1.receive(badDisLike);
        while (client1.receivedMessages.size() == 0)
            system.runFor(1);
        Assert.assertEquals(client1.receivedMessages.element().getClass(), UserBanned.class);
        client1.receivedMessages.remove();


        // end the communication
        worker1.tell(new FinishCommunication(id1));
        worker2.tell(new FinishCommunication(id2));
        while (client1.receivedMessages.size() == 0 || client2.receivedMessages.size() == 0)
            system.runFor(1);

        dispatcher.tell(new Stop());
    }
}