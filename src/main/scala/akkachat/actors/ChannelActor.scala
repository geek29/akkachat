package akkachat.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akkachat.actors.ChannelActor._
import akkachat.domain._
import akkachat.support.AsyncSupport

/**
  * Created by tushark on 1/10/16.
  */
class ChannelActor(channel: Channel) extends Actor with ActorLogging  with AsyncSupport {

  var users: List[User] = List[User]()

  override def receive = {
    case k: AddUserToChannel =>
      addUser(k.user.user, sender)
    case k: ListChannelUsers =>
      sender ! ChannelUserList(users)
    case k: ChatMessageCmd =>
      val msg = ChannelChatMessage(k.user, System.currentTimeMillis(), k.msg)
      users.foreach { u =>
        context.actorSelection(s"../cu-${u.displayName}") ! msg
      }
  }

  def addUser(u: User, sender : ActorRef) = {
    //validate that users is not added before
    users.find( p => p.email == u.email) match {
      case Some(k) =>
        sender ! CUserAlreadyAdded(u.email)
      case None =>
        println(s"Adding nwe user with email ${u.email}")
        users = u :: users
        //tell the user that you have been added to channel
        context.actorSelection(s"../cu-${u.displayName}") ! CUserAdded(channel.name, u.email)
        sender ! CUserAdded(channel.name, u.email)
    }
  }

}

object ChannelActor {
  sealed trait ChannelActorCommands
  case class AddUserToChannel(orgName: String, cname: String, user: OrganizationUser) extends ChannelActorCommands
  case class ListChannelUsers(orgName: String, cname: String) extends ChannelActorCommands
  case class ChatMessageCmd(orgName: String, cname: String, user: User, msg: ChatMessage) extends ChannelActorCommands

  sealed trait ChannelActorMessages
  case class CUserAlreadyAdded(email: String) extends  ChannelActorMessages
  case class CUserAdded(cname: String, email: String) extends  ChannelActorMessages
  case class ChannelUserList(list: List[User]) extends  ChannelActorMessages
  case class ChannelNotFound(name: String) extends  ChannelActorMessages

  def props(channel: Channel): Props = Props(new ChannelActor(channel))
}
