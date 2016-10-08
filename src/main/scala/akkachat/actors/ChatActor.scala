package akkachat.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import akkachat.domain.Organization
import akkachat.actors.ChatActor.{AddOrganization, OrgAdded, OrgAlreadyPresent}
import akkachat.actors.OrganizationActor._

/**
  * Created by tushark on 25/9/16.
  *
  * This is root actor in actor system
  *
  * Actor Hierarchy
  * Chat
  *   Organization
  *     Channel
  *     User
  *
  * Right now its all in-memory
  *
  */
class ChatActor extends Actor with ActorLogging {

  var organizations : List[Organization] = List[Organization]()

  override def receive: Receive = {
    case k: AddOrganization =>
      addOrganization(k, sender())
    case m: OrgActorCommands =>
      forwardToOrg(m)

  }

  def forwardToOrg(m: OrgActorCommands) = {
    val name = m match {
      case k: CreateChannel => k.orgName
      case k: AddUser => k.orgName
      case k: RequestInvite => k.orgName
      case k: AcceptInvite => k.orgName
      case k: ListInvites => k.orgName
    }

    organizations.find(p => p.name == name) match {
      case Some(organization) =>
        context.actorSelection(s"org-$name") forward(m)
      case None =>
        sender() ! OrgNotFound(name)
    }
  }

  def addOrganization(k: AddOrganization, sender: ActorRef) = {
    //TODO : Validation for same org name already existing
    organizations.find( p => p.name == k.name) match {
      case Some(org) =>
        sender ! OrgAlreadyPresent(k.name)
      case None =>
        val list = organizations.sortWith(_.id > _.id).take(1)
        val newId = if(!list.isEmpty) {
          list(0).id + 1
        } else 0

        val organization = Organization(k.name, newId)
        val actor = context.actorOf(OrganizationActor.props(organization), s"org-${k.name}")
        context.watch(actor)
        organizations = organization :: organizations
        sender ! OrgAdded(k.name)
    }
  }

}

object ChatActor {
  sealed trait ChatActorMessages
  case class AddOrganization(name: String)
  case class OrgAdded(name: String) extends ChatActorMessages
  case class OrgAlreadyPresent(name: String) extends ChatActorMessages
}
