package ua.home.politics

import java.time.{Instant, LocalDate, LocalTime, ZoneId}
import java.time.temporal.TemporalField

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.Actor.Receive
import reactivemongo.api.BSONSerializationPack.IdentityWriter
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoDriver}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, DefaultBSONHandlers}
import reactivemongo.bson._
import ua.home.politics.DataStorageActor.{LoadDeputee, LoadDeputees, LoadVoteProject, SaveDeputee}
import ua.home.politics.configuration.ConfiguredContext
import ua.home.politics.model.{Deputee, VoteProject}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by Maksym on 6/12/2016.
  */
class DataStorageActor extends Actor with ConfiguredContext {
  import reactivemongo.bson._
  private val deputeeCollectionName: String = "deputees"
  private val voteProjectCollectionName: String = "voteProject"

  implicit val deputeeHandler : DeputeeHandler = new DeputeeHandler
  implicit val voteProjecteHandler : VoteProjectHandler = new VoteProjectHandler


  override def receive: Receive = {
    case SaveDeputee(deputee) => saveDeputee(deputee)
    case LoadDeputee(id) => loadDeputee(id)
    case LoadDeputees => sender ! loadDeputees()
    case LoadVoteProject(number) => sender ! loadVoteProject(number)
  }

  def loadVoteProject(number: String) : Future[Option[VoteProject]] = {
    val db: DefaultDB = getConfigured[DefaultDB]
    val result = db[BSONCollection](voteProjectCollectionName).
      find(BSONDocument(
        "number" -> number
      )).
      one[VoteProject]

    result
  }

  def loadDeputee(id: Int): Future[Option[Deputee]] = {
    val db: DefaultDB = getConfigured[DefaultDB]
    val result = db[BSONCollection](deputeeCollectionName).
      find(BSONDocument(
        "id" -> id
      )).
      one[Deputee]

    result
  }

  def loadDeputees(): Future[List[Deputee]] = {
    val db: DefaultDB = getConfigured[DefaultDB]
    val result = db[BSONCollection](deputeeCollectionName).
      find(BSONDocument()).
      cursor[Deputee]().
      collect[List]()


    result
  }

  def saveDeputee(deputee :  Deputee) = {
      val db: DefaultDB = getConfigured[DefaultDB]
      db[BSONCollection](deputeeCollectionName).insert(deputee)
  }

  class VoteProjectHandler extends BSONDocumentWriter[VoteProject] with BSONDocumentReader[VoteProject] {
    override def write(item: VoteProject): BSONDocument =
      BSONDocument("id" -> item.id,
        "name" -> item.name,
        "number" -> item.number,
        "category" -> item.category,
        "registrationDate" -> BSONDateTime(item.registrationDate.atStartOfDay(DataStorageActor.timeZone).toEpochSecond())
      )

    override def read(bson: BSONDocument): VoteProject = {
      val id = bson.get("id").get.asInstanceOf[BSONString].value
      val name = bson.get("name").get.asInstanceOf[BSONString].value
      val number = bson.get("number").get.asInstanceOf[BSONString].value
      val category = bson.get("category").get.asInstanceOf[BSONString].value
      val registrationDateMillis: Long = bson.get("registrationDate").get.asInstanceOf[BSONDateTime].value
      val registrationDate: LocalDate = Instant.ofEpochMilli(registrationDateMillis).atZone(DataStorageActor.timeZone).toLocalDate
      new VoteProject(id, name, number, registrationDate, category)
    }
  }

  class DeputeeHandler extends BSONDocumentWriter[Deputee] with BSONDocumentReader[Deputee] {
    override def write(item: Deputee): BSONDocument =
      BSONDocument("firstName" -> item.firstName,
        "lastName" -> item.lastName,
        "link" -> item.link,
        "id" -> item.id,
        "fraction" -> item.fraction,
        "dateOfBirth" -> BSONDateTime(item.dateOfBirth.atStartOfDay(DataStorageActor.timeZone).toEpochSecond())
      )


    override def read(bson: BSONDocument): Deputee = {
      val firstName: String = bson.get("firstName").get.asInstanceOf[BSONString].value
      val lastName: String = bson.get("lastName").get.asInstanceOf[BSONString].value
      val link: String = bson.get("link").get.asInstanceOf[BSONString].value
      val id: Int = bson.get("id").get.asInstanceOf[BSONInteger].value
      val fraction: String = bson.get("fraction").get.asInstanceOf[BSONString].value
      val dateOfBirthMillis: Long = bson.get("dateOfBirth").get.asInstanceOf[BSONDateTime].value
      val dateOfBirth: LocalDate = Instant.ofEpochMilli(dateOfBirthMillis).atZone(DataStorageActor.timeZone).toLocalDate
      new Deputee(lastName, firstName, id, fraction, dateOfBirth, link)
    }
  }
}

 object DataStorageActor {
   case class SaveDeputee(deputee : Deputee)
   case class LoadDeputee(id: Int)
   case object LoadDeputees
   case class LoadVoteProject(number: String)

   val timeZone = ZoneId.of("Europe/Kiev")
}
