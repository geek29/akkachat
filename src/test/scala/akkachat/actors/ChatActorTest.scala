package akkachat.actors

import akka.testkit.TestActorRef
import akkachat.actors.ChatActor.{AddOrganization, OrgAdded, OrgAlreadyPresent}

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
  }
}
