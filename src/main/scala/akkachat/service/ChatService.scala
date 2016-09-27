package akkachat.service

import akka.actor.ActorSystem
import akkachat.actors.ChatActor.{OrgAdded, OrgAlreadyPresent}
import akkachat.actors.OrganizationActor._
import akkachat.client.ChatClient
import akkachat.domain.User
import akkachat.service.ChatService.{CreateOrgPack, UserPack}
import akkachat.support.AsyncSupport
import grizzled.slf4j.Logging
import spray.http.StatusCode
import spray.routing.HttpService

/**
  * Created by tushark on 26/9/16.
  */
trait ChatService extends HttpService with Logging with AsyncSupport with HandlerSupport {

  import Implicit._
  import akkachat.support.JSON4sSupport._
  import spray.http.StatusCodes._

  def actorSystem: ActorSystem

  val routes = pathPrefix("chat") {
    createOrg ~ orgServices
  }



  def createOrg = path("org") {
    put {
      entity(as[CreateOrgPack]) { pack =>
        val chatClient = new ChatClient(actorSystem)
        complete {
          chatClient.createOrg(pack.name).map {
            case k: OrgAdded =>
              (OK, ServiceResponse(ServiceReturnCodes.SUCCESS))
            case k: OrgAlreadyPresent =>
              (OK, ServiceResponse(ServiceReturnCodes.ORG_ALREADY_PRESENT))
          }.recover {
            case k =>
              (InternalServerError, ServiceResponse(ServiceReturnCodes.INTERNAL_SERVER_ERROR, Some(k.getMessage)))
          }.mapTo[(StatusCode,ServiceResponse)]
        }
      }
    }
  }

  def orgServices = pathPrefix("org") {
    createChannel ~ addUser
  }

  def createChannel = path(Segment / "channel") { orgName =>
    put {
      parameter('name) { name =>
        val chatClient = new ChatClient(actorSystem)
        complete {
          chatClient.createChannel(orgName, name).map( {
            wrapHandlers(_) {
              case k: ChannelAdded =>
                Some(OK, ServiceResponse(ServiceReturnCodes.SUCCESS))
              case k: ChannelAlreadyAdded =>
                Some(OK, ServiceResponse(ServiceReturnCodes.CHANNEL_ALREADY_PRESENT))
            }
          } )
        }
      }
    }
  }

  def addUser = path(Segment / "users") { orgName =>
    put {
      entity(as[UserPack]) { user =>
        val chatClient = new ChatClient(actorSystem)
        complete {
          chatClient.addUser(orgName, User(user.name, user.email, user.displayName, None)).map {
            wrapHandlers(_) {
              case k: UserAdded =>
                Some(OK, ServiceResponse(ServiceReturnCodes.SUCCESS))
              case k: UserAlreadyAdded =>
                Some(OK, ServiceResponse(ServiceReturnCodes.USER_ALREADY_PRESENT))
            }
          }
        }
      }
    }
  }

}

object ChatService {
  case class CreateOrgPack(name: String)
  case class UserPack(name: String, email: String, displayName: String)

}
