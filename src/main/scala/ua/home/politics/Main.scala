package ua.home.politics

import java.time.LocalDate

import akka.actor.{ActorSystem, Props}
import reactivemongo.api.MongoDriver
import reactivemongo.core.nodeset.Authenticate
import ua.home.politics.DataStorageActor.SaveDeputee
import ua.home.politics.DeputeeHtmlProcessorActor.CollectDeputees
import ua.home.politics.VoteDayCollector.{CollectTodaysVotes, CollectVotesByDate}
import ua.home.politics.configuration.Configurator
import ua.home.politics.model.Deputee

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

  val system = ActorSystem("dataCollectorSystem")

//  class Application extends Configurator {
//    configure {
//      val driver = new MongoDriver
//      val dbName = "ua-politics"
//      val userName = "admin"
//      val password = "admin"
//      val servers = Seq("192.168.99.100:27017")
//
//            val connection = driver.connection(servers)
//
////      val credentials = Seq(Authenticate(dbName, userName, password))
////      val connection = driver.connection(servers, authentications = credentials)
//
//      val db = connection.database(dbName)
//      Await.result(db, 10 seconds)
//    }
//  }
//
//  new Application


  system.actorOf(Props[VoteDayCollector]) ! CollectVotesByDate(LocalDate.of(2016,11,3))


}