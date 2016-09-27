package akkachat.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

/**
  * Created by tushark on 25/9/16.
  */
abstract class AbstractActorTest extends TestKit(ActorSystem("testSystem")) with ImplicitSender
  with WordSpecLike with MustMatchers with BeforeAndAfterAll{

  /*
  override def beforeAll() = {

  }

  override def afterAll() = {

  }*/

}
