package akkachat.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akkachat.actors.ChannelActor._
import akkachat.actors.OrganizationActor._
import akkachat.domain._
import akkachat.support.AsyncSupport

/**
  * Created by tushark on 25/9/16.
  */
class OrganizationActor(organization: Organization) extends Actor with ActorLogging  with AsyncSupport {

  var users: List[OrganizationUser] = List[OrganizationUser]()
  var channels: List[Channel] = List[Channel]()
  var invites: List[OrganizationInvite] = List[OrganizationInvite]()

  override def receive: Receive = {
    case k: AddUser =>
      addUser(k.user, sender())
    case k: CreateChannel =>
      createChannel(k.name, sender())
    case k: RequestInvite =>
      addInvite(k.user, sender())
    case k: AcceptInvite =>
      acceptInvite(k.email, sender())
    case k: ListInvites =>
      listInvites(sender())
    case k: ChannelActorCommands =>
      forwardToChannel(k)
  }

  def forwardToChannel(m: ChannelActorCommands) = {
    val name = m match {
      case k: AddUserToChannel => k.cname
      case k: ListChannelUsers => k.cname
      case k: ChatMessageCmd => k.cname
    }

    channels.find(p => p.name == name) match {
      case Some(channel) =>
        context.actorSelection(s"channel-$name") forward(m)
      case None =>
        sender() ! ChannelNotFound(name)
    }
  }


  def addInvite(user: User, ref: ActorRef): Unit = {
    invites.find( p => p.user.email == user.email) match {
      case Some(k) =>
        sender ! UserAlreadyAdded(user.email)
      case None =>
        val resp = users.find( p => p.user.email == user.email) match {
          case Some(k) =>
            UserAlreadyAdded(user.email)
          case None =>
            invites = OrganizationInvite(user) :: invites
            InviteAdded(user.email)
        }
        ref ! resp
    }
  }

  def acceptInvite(email: String, ref: ActorRef): Unit = {
    invites.find( p => p.user.email == email) match {
      case Some(k) =>
        invites = invites.filterNot( p => p.user.email == email)
        addUser(k.user, ref)
      case None =>
        sender ! InviteMissing(email)
    }
  }

  def listInvites(ref: ActorRef): Unit = {
    ref ! InviteList(invites)
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
    channels.find( p => p.name == name) match {
      case Some(k) =>
        sender ! ChannelAlreadyAdded(k.name)
      case None =>
        log.debug(s"Adding nwe channel with name $name")
        val channel = Channel(name, List())
        channels = channel :: channels
        val actor = context.actorOf(ChannelActor.props(channel), s"channel-${name}")
        context.watch(actor)
        sender ! ChannelAdded(name)
    }
  }

}

object OrganizationActor {
  sealed trait OrgActorCommands
  case class AddUser(orgName: String, user: User) extends OrgActorCommands
  case class CreateChannel(orgName: String, name: String) extends OrgActorCommands
  case class RequestInvite(orgName: String, user: User) extends OrgActorCommands
  case class AcceptInvite(orgName: String, email: String) extends OrgActorCommands
  case class ListInvites(orgName: String) extends OrgActorCommands

  sealed trait OrgActorMessages
  case class UserAlreadyAdded(email: String) extends  OrgActorMessages
  case class UserAdded(email: String) extends  OrgActorMessages

  case class InviteAdded(email: String) extends OrgActorMessages
  case class InviteMissing(email: String) extends OrgActorMessages
  case class InviteAccepted(email: String) extends OrgActorMessages

  case class InviteList(list: List[OrganizationInvite]) extends OrgActorMessages

  case class ChannelAlreadyAdded(name: String) extends  OrgActorMessages
  case class ChannelAdded(name: String) extends  OrgActorMessages
  case class OrgNotFound(name: String) extends  OrgActorMessages


  def props(organization: Organization): Props = Props(new OrganizationActor(organization))

}
