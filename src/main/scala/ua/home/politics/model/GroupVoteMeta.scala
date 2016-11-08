package ua.home.politics.model

import java.time.LocalTime

/**
  * User: maksymlabazov 
  * Date: 07/11/2016.
  */
class GroupVoteMeta(val id: String, val datetime: LocalTime, val votesYes: Int, val votesNo: Int, val didntVote: Int) {

}
