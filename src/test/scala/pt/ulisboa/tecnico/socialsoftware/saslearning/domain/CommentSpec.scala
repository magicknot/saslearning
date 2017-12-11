package pt.ulisboa.tecnico.socialsoftware.saslearning.domain

import eu.timepit.refined.auto._
import org.scalatest.{EitherValues, Matchers, WordSpec}

class CommentSpec extends WordSpec
  with Matchers
  with EitherValues {

  val user = User(None, username = "jdoe", email = "john.doe@example.org", displayName = "John Doe")

  "A comment" should {
    "not have empty content" in {
      val comment = Comment.fromUnsafe("", user)
      comment should be ('left)
    }
    "have content" in {
      val content = "Can you explain this?"
      val comment = Comment.fromUnsafe(content, user)
      comment should be ('right)
      comment.right.value.content.value should be (content)
      comment.right.value.author should be (user)
    }
  }

}
