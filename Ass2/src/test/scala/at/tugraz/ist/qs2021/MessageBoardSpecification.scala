package at.tugraz.ist.qs2021

import at.tugraz.ist.qs2021.actorsystem.{Message, SimulatedActor}
import at.tugraz.ist.qs2021.messageboard.MessageStore.USER_BLOCKED_AT_COUNT
import at.tugraz.ist.qs2021.messageboard.UserMessage
import at.tugraz.ist.qs2021.messageboard.Worker.MAX_MESSAGE_LENGTH
import at.tugraz.ist.qs2021.messageboard.clientmessages._
import org.scalacheck.commands.Commands
import org.scalacheck.{Gen, Prop}

import scala.jdk.CollectionConverters._
import scala.util.Try

// Documentation: https://github.com/typelevel/scalacheck/blob/master/doc/UserGuide.md#stateful-testing

object MessageBoardSpecification extends Commands {
  override type State = ModelMessageBoard
  override type Sut = SUTMessageBoard

  override def canCreateNewSut(newState: State, initSuts: Traversable[State], runningSuts: Traversable[Sut]): Boolean = {
    initSuts.isEmpty && runningSuts.isEmpty
  }

  override def newSut(state: State): Sut = new SUTMessageBoard

  override def destroySut(sut: Sut): Unit = ()

  override def initialPreCondition(state: State): Boolean = state.messages.isEmpty && state.reports.isEmpty

  override def genInitialState: Gen[State] = ModelMessageBoard(Nil, Nil, lastCommandSuccessful = false, userBanned = false)

  override def genCommand(state: State): Gen[Command] = Gen.oneOf(genPublish, genLike, genDislike, genReport, genRetrieve, genSearch)

  val genAuthor: Gen[String] = Gen.oneOf("Alice", "Bob")
  val genReporter: Gen[String] = Gen.oneOf("Alice", "Bob", "Lena", "Lukas", "Simone", "Charles", "Gracie", "Patrick", "Laura", "Leon")
  val genMessage: Gen[String] = Gen.oneOf("msg_w_9ch", "msg_w_10ch", "msg_w_11ch_")

  def genPublish: Gen[PublishCommand] = for {
    author <- genAuthor
    message <- genMessage
  } yield PublishCommand(author, message)

  case class PublishCommand(author: String, message: String) extends Command {
    type Result = Message

    def run(sut: Sut): Result = {
      // TODO
      throw new java.lang.UnsupportedOperationException("Not implemented yet.")
    }

    def nextState(state: State): State = {
      // TODO
      state
    }

    override def preCondition(state: State): Boolean = true

    override def postCondition(state: State, result: Try[Message]): Prop = {
      if (result.isSuccess) {
        val reply: Message = result.get
        val newState: State = nextState(state)
        false // TODO
      } else {
        false
      }
    }

    override def toString: String = s"Publish($author, $message)"
  }

  def genLike: Gen[LikeCommand] = for {
    author <- genAuthor
    message <- genMessage
    likeName <- genAuthor
  } yield LikeCommand(author, message, likeName)

  case class LikeCommand(author: String, message: String, likeName: String) extends Command {
    type Result = Message

    def run(sut: Sut): Result = {
      // TODO
      throw new java.lang.UnsupportedOperationException("Not implemented yet.")
    }

    def nextState(state: State): State = {
      // TODO
      state
    }

    override def preCondition(state: State): Boolean = true

    override def postCondition(state: State, result: Try[Message]): Prop = {
      if (result.isSuccess) {
        val reply: Message = result.get
        val newState: State = nextState(state)
        false // TODO
      } else {
        false
      }
    }

    override def toString: String = s"Like($author, $message, $likeName)"
  }

  def genDislike: Gen[DislikeCommand] = for {
    author <- genAuthor
    message <- genMessage
    dislikeName <- genAuthor
  } yield DislikeCommand(author, message, dislikeName)

  case class DislikeCommand(author: String, message: String, dislikeName: String) extends Command {
    type Result = Message

    def run(sut: Sut): Result = {
      // TODO
      throw new java.lang.UnsupportedOperationException("Not implemented yet.")
    }

    def nextState(state: State): State = {
      // TODO
      state
    }

    override def preCondition(state: State): Boolean = true

    override def postCondition(state: State, result: Try[Message]): Prop = {
      if (result.isSuccess) {
        val reply: Message = result.get
        val newState: State = nextState(state)
        false // TODO
      } else {
        false
      }
    }

    override def toString: String = s"Dislike($author, $message, $dislikeName)"
  }

  def genReport: Gen[ReportCommand] = for {
    reporter <- genReporter
    reported <- genAuthor
  } yield ReportCommand(reporter, reported)

  case class ReportCommand(reporter: String, reported: String) extends Command {
    type Result = Message

    def run(sut: Sut): Result = {
      sut.getDispatcher.tell(new InitCommunication(sut.getClient, sut.getCommId))
      while (sut.getClient.receivedMessages.isEmpty)
        sut.getSystem.runFor(1)
      val initAck = sut.getClient.receivedMessages.remove.asInstanceOf[InitAck]
      val worker: SimulatedActor = initAck.worker

      worker.tell(new Report(reporter, sut.getCommId, reported))
      while (sut.getClient.receivedMessages.isEmpty)
        sut.getSystem.runFor(1)
      val result = sut.getClient.receivedMessages.remove()

      worker.tell(new FinishCommunication(sut.getCommId))
      while (sut.getClient.receivedMessages.isEmpty)
        sut.getSystem.runFor(1)
      sut.getClient.receivedMessages.remove()

      result
    }

    def nextState(state: State): State = {
      // R8 If a user has been reported at least USER BLOCKED AT COUNT (= 6) times,
      // he/she cannot send any further Publish, Like, Dislike or Report messages.

      if (state.reports.count(r => r.reportedClientName == reporter) >= USER_BLOCKED_AT_COUNT) {
        return state.copy(
          lastCommandSuccessful = false,
          userBanned = true
        )
      }

      // R7 A user may report another user only if he has not previously reported the user in question.

      if (state.reports.exists(report => report.clientName == reporter && report.reportedClientName == reported)) {
        return state.copy(
          lastCommandSuccessful = false,
          userBanned = false
        )
      }

      state.copy(
        lastCommandSuccessful = true,
        userBanned = false,
        reports = ModelReport(reporter, reported) :: state.reports
      )
    }

    override def preCondition(state: State): Boolean = true

    override def postCondition(state: State, result: Try[Message]): Prop = {
      if (result.isSuccess) {
        val reply: Message = result.get
        val newState: State = nextState(state)
        (reply.isInstanceOf[UserBanned] == newState.userBanned) && (reply.isInstanceOf[OperationAck] == newState.lastCommandSuccessful)
      } else {
        false
      }
    }

    override def toString: String = s"Report($reporter, $reported)"
  }

  def genRetrieve: Gen[RetrieveCommand] = for {
    author <- genAuthor
  } yield RetrieveCommand(author)

  // just a suggestion, change it according to your needs.
  case class RetrieveCommandResult(success: Boolean, messages: List[String])

  case class RetrieveCommand(author: String) extends Command {
    type Result = RetrieveCommandResult

    def run(sut: Sut): Result = {
      // TODO
      throw new java.lang.UnsupportedOperationException("Not implemented yet.")
    }

    def nextState(state: State): State = {
      // TODO
      state
    }

    override def preCondition(state: State): Boolean = true

    override def postCondition(state: State, result: Try[Result]): Prop = {
      if (result.isSuccess) {
        val reply: Result = result.get
        false // TODO
      } else {
        false
      }
    }

    override def toString: String = s"Retrieve($author)"
  }

  def genSearch: Gen[SearchCommand] = for {
    searchText <- genMessage
  } yield SearchCommand(searchText)

  case class SearchCommandResult(success: Boolean, messages: List[String])

  case class SearchCommand(searchText: String) extends Command {
    type Result = SearchCommandResult

    def run(sut: Sut): Result = {
      // TODO
      throw new java.lang.UnsupportedOperationException("Not implemented yet.")
    }

    def nextState(state: State): State = {
      // TODO
      state
    }

    override def preCondition(state: State): Boolean = true

    override def postCondition(state: State, result: Try[Result]): Prop = {
      if (result.isSuccess) {
        val reply: Result = result.get
        false // TODO
      } else {
        false
      }
    }

    override def toString: String = s"Search($searchText)"
  }

}