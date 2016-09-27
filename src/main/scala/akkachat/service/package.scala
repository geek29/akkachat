package akkachat

import spray.http.{StatusCode, StatusCodes}

/**
  * Created by tushark on 26/9/16.
  */
package object service {

  case class ServiceResponse(returnCode: Int, message: Option[String] = None)

  object ServiceReturnCodes {
    val SUCCESS = 0
    val INTERNAL_SERVER_ERROR = -1
    val UNKNOWN_ERROR = -2

    val ORG_ALREADY_PRESENT = 1
    val CHANNEL_ALREADY_PRESENT = 2
    val USER_ALREADY_PRESENT = 3
    val ORG_DOES_NOT_EXIST = 4
  }

}
