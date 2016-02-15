package ifttt

import org.specs2.mutable.Specification
import ifttt.Examples._

import scala.util.Random

/**
  * Created by papacharlie on 2016-02-13.
  */
class SchedulerTest extends Specification {

  "Pairs" should {

    "fail when called with invalid teams" in {
      new Scheduler(List()) must throwAn[IllegalArgumentException]
      new Scheduler(List(
        new Team("testTeam1", Set("member1@ifttt.com")),
        new Team("testTeam2", Set("member2@ifttt.com"))
      )) must throwAn[IllegalArgumentException]
    }

    "correctly handle a simple schedule" in {
      val beatles = new Scheduler(List(beatlesTeam))
      val schedules = beatles.makeSchedules(3)
      schedules.flatMap(_.unmatchedEmployees).toSet === Set()
    }

    "create the optimal schedule for a team containing an even number of members" in {
      val gen = new Random(42).alphanumeric
      val members = Set("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
      val schedules = new Scheduler(List(new Team("test", members))).makeSchedules(10)
      schedules.head === schedules.last
      schedules.forall(_.unmatchedEmployees == Set()) === true
    }

    "correctly handle a multi-team schedule" in {
      val teams = new Scheduler(List(plasticOnoTeam, travellingWildurysTeam))
      val schedules = teams.makeSchedules(3)
      schedules.flatMap(_.unmatchedEmployees).sorted === travellingWildurysTeam.members.toList.sorted
    }

    "never match two employees early" in {
      val teams = new Scheduler(allTeams)
      val schedules = teams.makeSchedules(100)
      val testPair = schedules.head.pairs.head
      val relevantPairs = allTeams.find(_.pairs.contains(testPair)).get.pairs.filter {
        case (a, b) => a == testPair._1 || b == testPair._1
      }
      schedules.tail.foldLeft(schedules.head.pairs.toSet) {
        case (previousPairs, sc) if sc.pairs.contains(testPair) =>
          relevantPairs.foreach(previousPairs.contains(_) === true)
          sc.pairs.toSet
        case (previousPairs, sc) => {
          previousPairs ++ sc.pairs
        }
      }
      true === true
    }

    "never sechdule an employee twice in a week" in {
      val teams = new Scheduler(allTeams)
      val allEmployees = allTeams.flatMap(_.pairs)
      val schedules = teams.makeSchedules(100)
      schedules.forall { sc =>
        val employees: List[String] = sc.pairs.flatMap { case (a, b) => List(a, b) } ::: sc.unmatchedEmployees.toList
        employees.toSet.size == employees.size
      } === true
    }

  }

}
