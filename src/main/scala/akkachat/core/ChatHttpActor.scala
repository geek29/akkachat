package akkachat.core

import akka.actor.{ActorLogging, ActorSystem}
import akkachat.service.ChatService
import spray.routing.HttpServiceActor

/**
  * Created by tushark on 26/9/16.
  */
class ChatHttpActor extends HttpServiceActor with ActorLogging {

  override def actorRefFactory = context

  var chatService = new ChatService {
    def actorRefFactory = context
    override def actorSystem: ActorSystem = context.system
  }

  def receive = runRoute(chatService.routes)

}
