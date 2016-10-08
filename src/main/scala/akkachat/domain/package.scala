package akkachat

/**
  * Created by tushark on 25/9/16.
  */
package object domain {

  case class Organization(name: String, id: Int)

  case class User(name: String, email: String, displayName: String, id: Option[Int] = None)

  case class OrganizationUser(organization: Organization, user: User)

  case class Channel(name: String, users: List[OrganizationUser])

  case class OrganizationInvite(user: User)

  sealed trait ChatMessage

  case class TextChatMessage(from: User, txt: String)

  case class MarkDownMessage(from: User, txt: String)

  case class ChannelChatMessage(user: User, ts: Long, msg: ChatMessage)

  case class P2PChatMessage(from: User, to : User, ts: Long, msg: ChatMessage)

}
