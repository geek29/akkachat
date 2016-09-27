package akkachat.service

import akkachat.actors.OrganizationActor.OrgNotFound
import spray.http.StatusCode
import spray.http.StatusCodes._

import scala.concurrent.Future

/**
  * To make service code easy so that all possible errors are handled correctly
  * and service layer responds with proper errors concept of handler pipeline
  * is introduced.
  *
  * Here handlers are executed one after other till handler handles the value
  * and returns msg. By having set of such explicit handlers all situations
  * are handled and service definition only has to specify handling of only
  * selected messages and call wrapHandler function which will take care of
  * other cases
  *
  * In following call, cases like any exception or OrgNotFound error is handled
  * inside wrapHandlers method
  *
  * {{{
  *   complete {
  *        chatClient.addUser(orgName, User(user.name, user.email, user.displayName, None)).map {
  *          wrapHandlers(_) {
  *            case k: UserAdded =>
  *              Some(OK, ServiceResponse(ServiceReturnCodes.SUCCESS))
  *            case k: UserAlreadyAdded =>
  *              Some(OK, ServiceResponse(ServiceReturnCodes.USER_ALREADY_PRESENT))
  *          }
  *        }
  *      }
  * }}}
  *
  * Same can be achieved by using but then you wont get result only Handler which you
  * will need to apply again so basically Handler logic is called twice. Further it wont
  * stop calling other handlers
  *
  * {{{
  *   val flist = list.dropWhile( p =>
  *    p(targetMsg).isDefined
  *   )
  *   flist(0)(targetMsg)
  * }}}
  *
  * Created by tushark on 26/9/16.
  */
trait HandlerSupport {

  type Handler = (Any) => Option[(StatusCode, ServiceResponse)]

  val catchAllErrorHandler: Handler = {
    case _ =>
      Some(BadRequest, ServiceResponse(ServiceReturnCodes.UNKNOWN_ERROR))
  }

  val orgNotFoundHandler: Handler = {
    case m: OrgNotFound =>
      Some(OK, ServiceResponse(ServiceReturnCodes.ORG_DOES_NOT_EXIST))
  }

  def applyHandlers(list: List[Handler], targetMsg: Any) = {
    list.reduceLeft { (filter1, filter2) =>
      filter1(targetMsg) match {
        case k: Some[(StatusCode, ServiceResponse)] =>
        { targetMsg =>
          k
        }
        case None =>
          filter2
      }
    }(targetMsg).get
  }

  def wrapHandlers(msg: Any)(custom: Handler) = {
    applyHandlers(List(custom, orgNotFoundHandler, catchAllErrorHandler), msg)
  }

}
