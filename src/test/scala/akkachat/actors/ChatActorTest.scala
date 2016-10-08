package akkachat.actors

import akka.testkit.TestActorRef
import akkachat.actors.ChatActor.{AddOrganization, OrgAdded, OrgAlreadyPresent}
import akkachat.actors.OrganizationActor._
import akkachat.domain.{OrganizationInvite, User}

/**
  * Created by tushark on 25/9/16.
  */
class ChatActorTest extends AbstractActorTest {

  override def beforeAll() = {

  }

  override def afterAll(): Unit ={

  }

  val actorRef = TestActorRef[ChatActor]

  "ChatActor " when {
    "Passed with AddOrganization" must {
      "create Organization return success" in {
        val orgName = "Org1"
        actorRef ! AddOrganization(orgName)
        expectMsg(OrgAdded(orgName))
      }
    }

    "Passed with AddOrganization for an existing organization" must {
      "return OrgAlreadyPresent error" in {
        val orgName = "Org1"
        actorRef ! AddOrganization(orgName)
        expectMsg(OrgAlreadyPresent(orgName))
      }
    }

    "Passed with AddUser" must {
      "add user and return success" in {
        actorRef ! AddUser("Org1", User("u", "u@email.com", "du"))
        expectMsg(UserAdded("u@email.com"))
      }
    }

    "Passed with AddUser for an existing user" must {
      "return UserAlreadyAdded error" in {
        actorRef ! AddUser("Org1", User("u", "u@email.com", "du"))
        expectMsg(UserAlreadyAdded("u@email.com"))
      }
    }

    "passed with user invites" must {
      "process invites correctly" in {
        val user = User("u1", "u1@email.com", "du1")
        actorRef ! RequestInvite("Org1", user)
        expectMsg(InviteAdded("u1@email.com"))
        actorRef ! ListInvites("Org1")
        expectMsg(InviteList(List(OrganizationInvite(user))))
        actorRef ! AcceptInvite("Org1", user.email)
        expectMsg(UserAdded(user.email))
        actorRef ! AcceptInvite("Org1", "wrongemail")
        expectMsg(InviteMissing("wrongemail"))
      }
    }

    "Passed message with wrong org name" must {
      "return OrgNotFound" in {
        val orgName = "Orgxxx"
        actorRef ! AddUser(orgName, User("u", "u@email.com", "du"))
        expectMsg(OrgNotFound(orgName))
      }
    }

  }
}
