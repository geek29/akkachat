package akkachat

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.actor.ActorDSL._
import akka.io.IO
import akka.io.Tcp._
import akkachat.actors.ChatActor
import akkachat.core.{ChatConfig, ChatHttpActor}
import spray.can.Http

/**
  * Created by tushark on 26/9/16.
  */
object AkkaChat extends App {

  val actorSystemName = "AkkaChat"
  implicit val system = ActorSystem(actorSystemName)

  val service = system.actorOf(Props[ChatHttpActor], "AkkaChatRestService")

  val chatActor = system.actorOf(Props[ChatActor], "chat")

  val ioListener = actor("ioListener")(new Act with ActorLogging {
    become {
      case b@Bound(connection) => log.info(b.toString)
    }
  })

  IO(Http).tell(Http.Bind(
    service,
    ChatConfig.HttpConfig.interface,
    ChatConfig.HttpConfig.port
  ), ioListener)


}
