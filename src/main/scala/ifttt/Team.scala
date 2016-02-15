package ifttt

import ifttt.Scheduler.makeOrderedPair

/**
  * Data structure representing a team
  */
case class Team(val name: String, val members: Set[String]) {

  /**
    * Add one email/teammember to the team
    *
    * @param email
    * @return
    */
  def +(email: String): Team = {
    new Team(name, members + email)
  }

  /**
    * Remove one email/teammember to the team
    *
    * @param email
    * @return
    */
  def -(email: String): Team = {
    new Team(name, members - email)
  }

  /**
    * Add a series of emails/teammembers to the team
    *
    * @param emails
    * @return
    */
  def ++(emails: Iterable[String]): Team = {
    new Team(name, members ++ emails)
  }

  /**
    * Contains all of the possible 1 + 1 pairs of teammates
    */
  lazy val pairs: Set[(String, String)] = {
    def makePairs(emails: List[String]): List[(String, String)] = {
      emails match {
        case hd :: tl => tl.map(makeOrderedPair(hd, _)) ::: makePairs(tl)
        case Nil => List()
      }
    }
    // By sorting the pairs, we ensure that if two people are both part of two distinct teams, the pairs that contains
    // their emails will be in the same order in both teams.
    makePairs(members.toList).toSet
  }

  def toJson: String = {
    import org.json4s._
    import org.json4s.jackson.Serialization
    import org.json4s.jackson.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)
    write(this)
  }

}