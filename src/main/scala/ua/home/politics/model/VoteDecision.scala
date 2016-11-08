package ua.home.politics.model

/**
  * User: maksymlabazov 
  * Date: 07/11/2016.
  */
object VoteDecision extends Enumeration {
  val YES = Value("Yes")
  val NO = Value("No")
  val DIDNT_VOTE = Value("Didn't vote")
  val WAS_ABSENT = Value("Was absent")
}
