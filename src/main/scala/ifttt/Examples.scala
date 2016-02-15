package ifttt

/**
  * Created by papacharlie on 2016-02-14.
  */
object Examples {

  val beatlesTeam = new Team("The Beatles", Set(
    "paul@mccartney.org",
    "john@lennon.com",
    "george@harrison.org",
    "ringo@starr.org"
  ))

  val quarrymenTeam = new Team("The Quarrymen", Set(
    "john@lennon.org",
    "paul@mccartney.org",
    "stu@sutcliffe.org"
  ))

  val wingsTeam = new Team("Wings", Set(
    "paul@mccartney.org",
    "linda@mccartney.org"
  ))

  val plasticOnoTeam = new Team("Plastic Ono Band", Set(
    "john@lennon.org",
    "yoko@ono.org"
  ))

  val travellingWildurysTeam = new Team("Traveling Wilburys", Set(
    "george@harrison.org",
    "tom@petty.org",
    "roy@orbison.org"
  ))

  val allTeams = List(
    beatlesTeam,
    quarrymenTeam,
    wingsTeam,
    plasticOnoTeam,
    travellingWildurysTeam
  )

}
