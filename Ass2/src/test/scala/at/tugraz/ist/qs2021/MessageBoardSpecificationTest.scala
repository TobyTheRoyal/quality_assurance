package at.tugraz.ist.qs2021

import junit.framework.TestCase
import org.scalacheck.Test
import org.scalacheck.Test.Parameters
import org.scalacheck.util.ConsoleReporter

class MessageBoardSpecificationTest extends TestCase {
  def testMessageBoardModelBased(): Unit = {
    val params: Parameters => Parameters = p => p
      .withTestCallback(ConsoleReporter(2))
      .withMinSuccessfulTests(300)
    val result: Boolean = Test.check(MessageBoardSpecification.property())(params).passed
    org.junit.Assert.assertTrue(result)
  }
}