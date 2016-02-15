package ifttt

import scala.collection.mutable
import Scheduler.makeOrderedPair

/**
  * 1 + 1 scheduler implementation.
  */
class Scheduler(teams: List[Team]) {

  require(teams.nonEmpty, "Employee list may not be empty.")
  require(teams.forall(_.members.size > 1), "All teams must have more than one member.")

  private val employees: Set[String] = teams.flatMap(_.members).toSet

  private val baseMap: Map[String, Set[String]] = {
    val remaining: mutable.Map[String, Set[String]] =
      mutable.Map() ++ employees.map(_ -> Set[String]()).toMap
    (teams.flatMap(_.pairs).toSet: Set[(String, String)]).foreach { case (member1, member2) =>
      remaining(member1) += member2
      remaining(member2) += member1
    }
    Map() ++ remaining
  }

  /**
    * Generates n schedules for n weeks
    *
    * @param weeks number of schedules to generate
    * @return
    */
  def makeSchedules(weeks: Int): List[Schedule] = {
    val remainingTeamMates: mutable.Map[String, Set[String]] = mutable.Map() ++ baseMap
    for (week <- 1 to weeks) yield {
      val unbooked = mutable.Set() ++ employees
      var pairs: List[(String, String)] = List()
      var break = false
      while (!break && unbooked.nonEmpty) {
        val possiblePairs = remainingTeamMates.flatMap { case (member, teammates) if unbooked.contains(member) =>
          val possibleMates = teammates.intersect(unbooked).filter(remainingTeamMates(_).contains(member))
          if (possibleMates.nonEmpty) {
            Some((member, possibleMates))
          } else {
            None
          }
        case _ => None
        }
        if (possiblePairs.nonEmpty) {
          val (one, mates) = possiblePairs.minBy(_._2.size)
          val plusOne = mates.minBy(possiblePairs(_).size)
          remainingTeamMates(one) -= plusOne
          remainingTeamMates(plusOne) -= one
          unbooked -= one
          unbooked -= plusOne
          pairs = makeOrderedPair(one, plusOne) :: pairs
        } else {
          break = true
        }
      }
      remainingTeamMates.foreach {
        case (member, mates) if mates.isEmpty => remainingTeamMates(member) = baseMap(member)
        case _ =>
      }
      new Schedule(pairs, unbooked.toSet)
    }
  }.toList

}

object Scheduler {
  /**
    * Ensures that all pairs are ordered, so Sets don't contain duplicates
    *
    * @param member1 first element of the pair
    * @param member2 second element of the pair
    * @return
    */
  def makeOrderedPair(member1: String, member2: String): (String, String) = {
    if (member1 > member2) {
      (member2, member1)
    } else {
      (member1, member2)
    }
  }
}

case class Schedule(pairs: List[(String, String)], unmatchedEmployees: Set[String])