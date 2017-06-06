package main.scala.service

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{RatedData, StatusSearch, Tweet}
import com.danielasfregola.twitter4s.entities.enums.ResultType
import com.typesafe.config.ConfigFactory
import rest.utils.FileSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by lucian on 6/6/17.
  */
// scalastyle:off
object TwitterService extends App with FileSupport {

  val restClient: TwitterRestClient = TwitterRestClient()
  val maxItems: Int                 = 5

  def getTweets(query: String, max_id: Option[Long] = None): Future[Seq[Tweet]] = {
    def extractNextMaxId(params: Option[String]): Option[Long] =
      //example: "?max_id=658200158442790911&q=%23scala&include_entities=1&result_type=mixed"
      params.getOrElse("").split("&").find(_.contains("max_id")).map(_.split("=")(1).toLong)

    restClient
      .searchTweet(query, count = maxItems, result_type = ResultType.Recent, max_id = max_id)
      .flatMap { ratedData =>
        val result    = ratedData.data
        val nextMaxId = extractNextMaxId(result.search_metadata.next_results)

        val tweets = result.statuses
        if (tweets.nonEmpty) {
          getTweets(query, nextMaxId).map(_ ++ tweets)
        } else {
          Future(tweets.sortBy(_.created_at))
        }
      } recover { case x: Any => println(x.toString); Seq.empty }
  }

  val filename = {
    val config = ConfigFactory.load()
    config.getString("tweets.file")
  }

  val result = getTweets("#scalax", Some(658200158442790911L)).map { tweets =>
    val t: Future[RatedData[StatusSearch]] = restClient.searchTweet("#scalaz", count = maxItems)
    t.map(ts => ts.data.statuses.map(tw => println(tw.toString)))
    t.map(ts => toFileAsJson("test", ts.data))

    println(s"Downloaded ${tweets.size} tweets")
    toFileAsJson(filename, tweets)
    println(s"Tweets saved to file $filename")
  }

}
// scalastyle:on
