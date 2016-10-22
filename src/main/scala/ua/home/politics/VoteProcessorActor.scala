package ua.home.politics

import akka.actor.Actor
import akka.actor.Actor.Receive
import ua.home.politics.VoteProcessorActor.VoteProjectLink

import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * User: maksymlabazov 
  * Date: 10/10/2016.
  */
class VoteProcessorActor extends Actor {
  override def receive: Receive = {
    case VoteProjectLink(link) => processLink(link)
  }

  def processLink(link: String) = {
    val request: HttpRequest = Http(link)
    val response: HttpResponse[String] = request.asString
  }
}

object VoteProcessorActor {
  case class VoteProjectLink(link: String)
}
