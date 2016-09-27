package akkachat.client

import akka.actor.Actor._
import akka.actor.{Actor, ActorLogging, ActorSelection, ActorSystem, Props}
import akkachat.actors.ChatActor.{AddOrganization, ChatActorMessages}
import akkachat.actors.OrganizationActor.{AddUser, CreateChannel, OrgActorMessages, OrgNotFound}
import akkachat.domain.User

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
  * Created by tushark on 26/9/16.
  *
  * Client is wrapping up the actor call to various actors to give easy API
  * This uses Temporary actor to receive messages from targetActor to avoid use
  * of ASK pattern. It Returns futures
  *
  */
class ChatClient(_system: ActorSystem)  extends ActorCallHelper {

  override val system: ActorSystem = _system

  val chatActor = system.actorSelection(s"/user/chat")
  def orgActor(name: String) = system.actorSelection(s"/user/chat/org-$name")

  def serviceChatActorProps[T](msg: Any) = ActorCallServiceActor.props[T](chatActor, msg) _
  def serviceOrgActorProps[T](orgName: String, msg: Any) = ActorCallServiceActor.props[T](orgActor(orgName), msg) _


  def createOrg(name: String) = {
    askNoReceive(serviceChatActorProps[ChatActorMessages](AddOrganization(name)))
  }

  def createChannel(orgName: String, name: String) = {
    askNoReceive(serviceChatActorProps[OrgActorMessages](CreateChannel(orgName, name)))
  }

  def addUser(orgName: String, u: User) = {
    askNoReceive(serviceChatActorProps[OrgActorMessages](AddUser(orgName, u)))
  }

}

class ActorCallServiceActor[T](targetRef: ActorSelection, sendMsg: Any, f: Receive, promise: Promise[T]) extends Actor with ActorLogging {

  targetRef ! sendMsg

  override def receive = f

  override def postStop(): Unit = {
    super.postStop()
    println("Shutting down myself")
  }

}

object ActorCallServiceActor {

  def props[T](targetRef: ActorSelection, sendMsg: Any, f: Receive, promise: Promise[T]) : Props = Props(new ActorCallServiceActor(targetRef, sendMsg, f, promise))

  def props[T](targetRef: ActorSelection, sendMsg: Any)(promise: Promise[T])(f: Receive) : Props = Props(new ActorCallServiceActor(targetRef, sendMsg, f, promise))

}
