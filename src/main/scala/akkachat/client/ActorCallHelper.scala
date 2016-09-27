package akkachat.client

import akka.actor.Actor._
import akka.actor.{ActorSystem, PoisonPill, Props}
import akkachat.support.AsyncSupport

import scala.concurrent.{Future, Promise}

/**
  * Created by tushark on 26/9/16.
  */
trait ActorCallHelper extends AsyncSupport {

  import Implicit._


  val system: ActorSystem

  /**
    * This function is written to reduce the boilerplate code for calling upstream actor
    * Using this function you can pass partial functions for targetActor and receive block for handling
    * the response
    *
    * @param props : PartialFunction which takes promise and Receive and returns Actor Props
    * @param f Function returns Receive block with promise passed
    * @tparam T Type of message returned by the target actor
    * @return
    */
  def ask[T](props: (Promise[T]) => Receive => Props)(f: ((Promise[T]) => Receive)) : Future[T] = {
    val promise = Promise[T]()
    val receive = f(promise)
    val propsCooked = props(promise)(receive)
    callActor(promise, propsCooked)
  }

  /**
    * This is modification of ask with simplistic receive block which just completes the promise
    *
    * @param props
    * @tparam T
    * @return
    */
  def askNoReceive[T](props: (Promise[T]) => Receive => Props) : Future[T] = {
    val promise = Promise[T]()
    val receive: Receive = {
      case k =>
        promise.success(k.asInstanceOf[T])
    }
    val propsCooked = props(promise)(receive)
    callActor(promise, propsCooked)
  }

  def callActor[T](promise: Promise[T], propsCooked: Props)= {
    val actor = system.actorOf(propsCooked)
    promise.future.onComplete { p =>
      actor ! PoisonPill
    }
    promise.future
  }


}
