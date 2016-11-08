package ua.home.politics

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

import akka.actor.Actor
import akka.event.Logging
import org.htmlcleaner.{HtmlCleaner, TagNode}
import ua.home.politics.VoteDayCollector.{CollectTodaysVotes, CollectVotesByDate}

import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * User: maksymlabazov
  * Date: 08/10/2016.
  */
class VoteDayCollector extends Actor {

  val log = Logging(context.system, this)
  val dayLinkXpath = "/body/div[@id='content-all']/div/div/div/div[4]/div/div/div/ul[1]/li/h5[text()=\"{month}\"]/../table/tbody/tr/td/a[text()=\"{day}\"]/@href"
  val voteLinkXpath = "/body/div[@id='content-all']/div[1]/div/div/div/div/div/table/tbody/tr/td/p/span/span/a/@href"

  val months = Map(1 -> "Січень",
                  2 -> "Лютий",
                  3 -> "Березень",
                  4 -> "Квітень",
                  5 -> "Травень",
                  6 -> "Червень",
                  7 -> "Липень",
                  8 -> "Серпень",
                  9 -> "Вересень",
                  10 -> "Жовтень",
                  11 -> "Листопад",
                  12 -> "Грудень"
  )

  var voteProjectsOfDay : List[String] = List[String]()


  override def receive: Receive = {
    case CollectTodaysVotes => collectLawProjectLink()
    case CollectVotesByDate(date) => collectVotesByDate(date)
  }

  def collectVotesByDate(date: LocalDate): Unit = {
    val dayLink : Option[String] = getDayLink(date)
    dayLink.foreach(link => {
      val collectedVoteProjects = getVoteLaws(link)
      val notProcessedProjects = for {
        project <- collectedVoteProjects
        if !voteProjectsOfDay.contains(project)
      } yield project

      voteProjectsOfDay = collectedVoteProjects
    })
  }

  def collectLawProjectLink(): Unit = {
    collectVotesByDate(LocalDate.now())
  }
  def getVoteLaws(link: String) : List[String] = {
    val request: HttpRequest = Http("http://iportal.rada.gov.ua/" + link)
    val response: HttpResponse[String] = request.asString
    val body: String = cutContentOff(response)

    val cleanHtml: TagNode = new HtmlCleaner().clean(body)

    val ref: Array[AnyRef] = cleanHtml.evaluateXPath(voteLinkXpath)
    if (ref.length != 0 ) {
      log.info("Link with vote was found: " + ref(0).toString)
    } else {
      log.info("there is no voting today")
    }
    ref.toList.map(_.toString)
  }
  def cutContentOff(response: HttpResponse[String]): String = {
    val body: String = response.body
    body.substring(body.indexOf("<div id=\"content-all\">"), body.indexOf("<div class=\"clear\">"))
  }

  def getDayLink(date: LocalDate): Option[String] = {
    val request: HttpRequest = Http("http://iportal.rada.gov.ua/meeting/awt/65")
    val response: HttpResponse[String] = request.asString
    val body = cutContentOff(response)
    val cleanHtml: TagNode = new HtmlCleaner().clean(body)

    val ref: Array[AnyRef] = cleanHtml.evaluateXPath(
      dayLinkXpath
        .replace("{month}", months(date.getMonthValue))
        .replace("{day}", date.getDayOfMonth.toString)
    )
    if (ref.length != 0 ) {
      log.info("Link for todays day was found: " + ref(0).toString)
      Some(ref(0).toString)
    } else {
      log.info("there is no voting today")
      None
    }
  }
}

object VoteDayCollector {
  case object CollectTodaysVotes
  case class CollectVotesByDate(date: LocalDate)
}
