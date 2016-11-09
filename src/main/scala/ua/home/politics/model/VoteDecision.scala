package ua.home.politics.model

/**
  * User: maksymlabazov 
  * Date: 07/11/2016.
  */
object VoteDecision extends Enumeration {
  trait VoteDecision
  case object YES         extends VoteDecision
  case object NO          extends VoteDecision
  case object DIDNT_VOTE  extends VoteDecision
  case object WAS_ABSENT  extends VoteDecision
}
