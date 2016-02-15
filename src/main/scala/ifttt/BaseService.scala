package ifttt

import java.io.{BufferedWriter, File, FileWriter}

import akka.actor.Actor
import ifttt.BaseService.{loadTeams, writeTeams}
import ifttt.web.html
import spray.http.MediaTypes.`text/html`
import spray.http.StatusCodes
import spray.routing.HttpService
import uk.gov.hmrc.emailaddress.EmailAddress
import scala.collection.mutable.ArrayBuffer

import scala.io.Source


/**
  * Created by papacharlie on 2016-02-14.
  */
class BaseService extends Actor with HttpService {

  private var teams = loadTeams()

  private var schedules: List[Schedule] = List()

  private def makeSchedules(weeks: Int) = {
    schedules = new Scheduler(teams.toList).makeSchedules(weeks)
  }

  private def persist(): Boolean = {
    writeTeams(teams)
    if (schedules.nonEmpty) {
      makeSchedules(schedules.length)
    }
    true
  }

  private def deleteTeam(name: String): Boolean = {
    val temp = teams.filterNot(_.name == name)
    if (temp.size == teams.size) {
      false
    } else {
      teams = temp
      persist()
    }
  }

  private def addTeam(team: Team): Unit = {
    teams += team
    persist()
  }

  private def deleteMember(name: String, member: String): Boolean = {
    teams.indexWhere(_.name == name) match {
      case -1 => false
      case index => {
        teams(index) = teams(index) - member
        persist()
      }
    }
  }

  private def addMember(name: String, member: String) = {
    teams.indexWhere(_.name == name) match {
      case -1 => false
      case index => {
        teams(index) = teams(index) + member
        persist()
      }
    }
  }

  def actorRefFactory = context

  def receive = runRoute {
    path("" ~ (PathEnd | Slash) | "index" | "index.html") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            html.index(teams.toList, schedules).toString()
          }
        }
      }
    } ~ path("schedules" ~ (PathEnd | Slash)) {
      post {
        formField('weeks.as[Int]) { weeks =>
          makeSchedules(weeks)
          redirect("/", StatusCodes.Found)
        }
      }
    } ~ pathPrefix("add") {
      path("team") {
        post {
          formField('team.as[String]) { (team) =>
            addTeam(new Team(team, Set()))
            redirect("/", StatusCodes.Found)
          }
        }
      } ~ path("member" / """.*""".r) { team =>
        post {
          formField('email.as[String]) { (email) =>
            if (!EmailAddress.isValid(email)) {
              complete {
                html.error(s"Invalid email address: $email").toString()
              }
            } else if (!addMember(team, email)) {
              complete {
                html.error("This team does not exist!").toString()
              }
            } else {
              redirect("/", StatusCodes.Found)
            }
          }
        }
      }
    } ~ pathPrefix("delete") {
      path("team" / """.*""".r) { team =>
        post {
          if (!deleteTeam(team)) {
            complete {
              html.error("This team does not exist!").toString()
            }
          } else {
            redirect("/", StatusCodes.Found)
          }
        }
      } ~ path("member" / """.*""".r) { team =>
        formField('members.as[String]) { (member) =>
          if (!deleteMember(team, member)) {
            complete {
              html.error("This team does not exist!").toString()
            }
          } else {
            redirect("/", StatusCodes.Found)
          }
        }
      }
    }

  }

}

object BaseService {

  import org.json4s.NoTypeHints
  import org.json4s.jackson.Serialization
  import org.json4s.jackson.Serialization.{read, write}

  private implicit val formats = Serialization.formats(NoTypeHints)

  val teamFile = new File("teams.json")

  /**
    * Loads the set of teams from disk
    *
    * @return
    */
  def loadTeams(): ArrayBuffer[Team] = {
    val json = Source.fromFile(teamFile).mkString
    ArrayBuffer(read[List[Team]](json): _*)
  }

  /**
    * Writes the set of teams to disk
    *
    * @param teams set of teams to save
    */
  def writeTeams(teams: ArrayBuffer[Team]): Unit = {
    val writer = new BufferedWriter(new FileWriter(teamFile))
    writer.write(write(teams))
    writer.close()
  }

}