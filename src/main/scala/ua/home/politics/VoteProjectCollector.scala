package ua.home.politics

import akka.actor.Actor
import akka.actor.Actor.Receive
import ua.home.politics.VoteProjectCollector.ProcessVoteProject
import ua.home.politics.model.VoteProject

import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * User: maksymlabazov 
  * Date: 08/11/2016.
  */
class VoteProjectCollector extends Actor {
  override def receive: Receive = {
    case ProcessVoteProject(url) => processVoteProject(url)
  }


  def processVoteProject(url: String): Unit = {
    val voteProject = loadVoteProject(url)
  }
  def loadVoteProject(url: String) : VoteProject = {
    val request: HttpRequest = Http(url)
    val response: HttpResponse[String] = request.asString
    new VoteProject(null, name = null, number = null, null, category = null, votes = null)
  }
}

object VoteProjectCollector {
  case class ProcessVoteProject(url: String)
}
