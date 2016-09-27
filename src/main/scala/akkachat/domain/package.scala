package akkachat

/**
  * Created by tushark on 25/9/16.
  */
package object domain {

  case class Organization(name: String, id: Int)

  case class User(name: String, email: String, displayName: String, id: Option[Int] = None)

  case class OrganizationUser(organization: Organization, user: User)

  case class Channel(name: String, users: List[OrganizationUser])

}
