package service

import main.scala.service.TwitterService
import org.scalatest._

import scala.concurrent.Future
//noinspection ScalaStyle
class TwitterServiceSpec extends WordSpec with Matchers {

  "The twitter service" should {

    "return latest trends" in {

      val r: Future[Unit] = TwitterService.result

    }
  }
}
