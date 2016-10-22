package ua.home.politics

import java.time.LocalDate

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import akka.actor.Actor.Receive
import akka.event.Logging
import org.htmlcleaner.{HtmlCleaner, TagNode}
import ua.home.politics.DataStorageActor.SaveDeputee
import ua.home.politics.DeputeeHtmlProcessorActor.CollectDeputees
import ua.home.politics.model.Deputee

import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * User: maksymlabazov 
  * Date: 02/10/2016.
  */
object DeputeeHtmlProcessorActor {
  case object CollectDeputees
}

class DeputeeHtmlProcessorActor extends Actor {

  val log = Logging(context.system, this)

  override def receive: Receive = {
    case CollectDeputees => collectDeputees()
  }


  def getDeputee(): (AnyRef) => Deputee = {
    tagNode => {
      val tag = tagNode.asInstanceOf[TagNode]

      val fio = tag.evaluateXPath("/p[2]/a/text()")(0).toString.split(" ")
      val lastName = fio(0)
      val firstName = fio(1)

      val link = tag.evaluateXPath("/p[2]/a/@href")(0).toString

      val splittedLink = link.split("/")
      val id = splittedLink(splittedLink.size - 1).toInt

      val fraction = tag.evaluateXPath("/dl/dd[4]/text()")(0).toString
      val dateOfBirth = LocalDate.now()
      new Deputee(lastName, firstName, id, fraction, dateOfBirth, link)

    }
  }

  def collectDeputees(): Unit = {
    val request: HttpRequest = Http("http://w1.c1.rada.gov.ua/pls/site2/fetch_mps?skl_id=9")
    val response: HttpResponse[String] = request.asString
    val cleanHtml: TagNode = new HtmlCleaner().clean(response.body)

    val items: Array[AnyRef] = cleanHtml.evaluateXPath("/body/ul/li")
    val resultList : Array[Deputee] = items.map(getDeputee())
    if (!resultList.isEmpty) {
      val storageActor: ActorRef = context.actorOf(Props[DataStorageActor])
      for (result <- resultList) {
        log.info("Deputee collected: {}", result.lastName)
        storageActor ! SaveDeputee(result)
      }
    } else {
      log.warning("Didn't manage to get Deputee from html tag.")
    }
  }
}
