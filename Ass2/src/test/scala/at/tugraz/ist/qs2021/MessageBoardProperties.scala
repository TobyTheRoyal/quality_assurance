package at.tugraz.ist.qs2021

import at.tugraz.ist.qs2021.actorsystem.{Message, SimulatedActor}
import at.tugraz.ist.qs2021.messageboard.MessageStore.USER_BLOCKED_AT_COUNT
import at.tugraz.ist.qs2021.messageboard.UserMessage
import at.tugraz.ist.qs2021.messageboard.Worker.MAX_MESSAGE_LENGTH
import at.tugraz.ist.qs2021.messageboard.clientmessages._
import org.junit.runner.RunWith
import org.scalacheck.Prop.{classify, forAll}
import org.scalacheck.{Gen, Properties}

import scala.jdk.CollectionConverters._

@RunWith(classOf[ScalaCheckJUnitPropertiesRunner])
class MessageBoardProperties extends Properties("MessageBoardProperties") {

  val validMessageGen: Gen[String] = Gen.asciiPrintableStr.map(s =>
    if (s.length <= MAX_MESSAGE_LENGTH) s else s.substring(0, MAX_MESSAGE_LENGTH)
  )
  property("message length: Publish + Ack [R1]") = forAll { (author: String, message: String) =>
    // arrange-  initialize the message board
    val sut = new SUTMessageBoard
    sut.getDispatcher.tell(new InitCommunication(sut.getClient, sut.getCommId))
    while (sut.getClient.receivedMessages.isEmpty)
      sut.getSystem.runFor(1)
    val initAck = sut.getClient.receivedMessages.remove.asInstanceOf[InitAck]
    val worker: SimulatedActor = initAck.worker

    // act - send and receive the messages
    worker.tell(new Publish(new UserMessage(author, message), sut.getCommId))
    while (sut.getClient.receivedMessages.isEmpty)
      sut.getSystem.runFor(1)
    val reply = sut.getClient.receivedMessages.remove()

    worker.tell(new FinishCommunication(sut.getCommId))
    while (sut.getClient.receivedMessages.isEmpty)
      sut.getSystem.runFor(1)
    sut.getClient.receivedMessages.remove.asInstanceOf[FinishAck]

    // assert - define your property and check against it
    // The following classify is optional, it prints stats on the generated values.
    // But the check inside is required.
    classify(message.length <= MAX_MESSAGE_LENGTH, "valid message length", "invalid message length") {
      // if operationAck is received, the message length should be smaller or equal to 10
      reply.isInstanceOf[OperationAck] == message.length <= MAX_MESSAGE_LENGTH
    }
  }
  // todo: add another property for R1

  property("example property with generators") =
    forAll(Gen.alphaStr, Gen.nonEmptyListOf(validMessageGen)) { (author: String, messages: List[String]) =>
      val sut = new SUTMessageBoard
      sut.getDispatcher.tell(new InitCommunication(sut.getClient, sut.getCommId))
      while (sut.getClient.receivedMessages.isEmpty)
        sut.getSystem.runFor(1)
      val initAck = sut.getClient.receivedMessages.remove.asInstanceOf[InitAck]
      val worker: SimulatedActor = initAck.worker

      // here would be a worker.tell, e.g. in a loop

      worker.tell(new FinishCommunication(sut.getCommId))
      while (sut.getClient.receivedMessages.isEmpty)
        sut.getSystem.runFor(1)
      sut.getClient.receivedMessages.remove()

      // here would be a check
      false
    }

}
