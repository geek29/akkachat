package akkachat.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akkachat.actors.OrganizationActor._
import akkachat.domain.{Channel, Organization, OrganizationUser, User}
import akkachat.support.AsyncSupport

/**
  * Created by tushark on 25/9/16.
  */
class OrganizationActor(organization: Organization) extends Actor with ActorLogging  with AsyncSupport {

  var users: List[OrganizationUser] = List[OrganizationUser]()
  var channels: List[Channel] = List[Channel]()



  override def receive: Receive = {
    case k: AddUser =>
      addUser(k.user, sender())
    case k: CreateChannel =>
      createChannel(k.name, sender())
  }


  def addUser(u: User, sender : ActorRef) = {
    //validate that users is not added before
    users.find( p => p.user.email == u.email) match {
      case Some(k) =>
        sender ! UserAlreadyAdded(u.email)
      case None =>
        val list = users.sortWith(_.user.id.get > _.user.id.get).take(1)
        val newId = if(!list.isEmpty) {
          list(0).user.id.get + 1
        } else 0
        println(s"Adding nwe user with email ${u.email} id = ${newId}")
        users = OrganizationUser(organization, u.copy(id = Some(newId))) :: users
        sender ! UserAdded(u.email)
    }
  }



  def createChannel(name: String, ref: ActorRef): Unit = {
    //validate that users is not added before
    channels.find( p => p.name == p.name) match {
      case Some(k) =>
        sender ! ChannelAlreadyAdded(k.name)
      case None =>
        log.debug(s"Adding nwe channel with name $name")
        channels = Channel(name, List()) :: channels
        sender ! ChannelAdded(name)
    }
  }

}

object OrganizationActor {
  sealed trait OrgActorCommands
  case class AddUser(name: String, user: User) extends OrgActorCommands
  case class CreateChannel(orgName: String, name: String) extends OrgActorCommands

  sealed trait OrgActorMessages
  case class UserAlreadyAdded(email: String) extends  OrgActorMessages
  case class UserAdded(email: String) extends  OrgActorMessages

  case class ChannelAlreadyAdded(name: String) extends  OrgActorMessages
  case class ChannelAdded(name: String) extends  OrgActorMessages

  case class OrgNotFound(name: String) extends  OrgActorMessages
  //RequestInvite
  //

  def props(organization: Organization): Props = Props(new OrganizationActor(organization))

}
