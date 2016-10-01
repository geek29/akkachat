package akkachat.support

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSelection}
import akka.event.LoggingAdapter
/**
  * Created by tushark on 26/9/16.
  */
trait AsyncSupport {

  import akka.util.Timeout

  import scala.concurrent.ExecutionContext

  object Implicit {
    implicit val ec = ExecutionContext.Implicits.global
    implicit lazy val timeout = Timeout(30, TimeUnit.SECONDS)
  }

  def startTx() = 25

  def commitTx(x : Int) : Unit = {

  }

  def rollback() : Unit = {

  }

  /** Simple function to wrap db code inside TX*/
  def doInTx[T](requester: ActorSelection, req: T, log: LoggingAdapter)(f: (T) => Option[T]): Unit ={
    val db = startTx()
    try {
      val response = f(req)
      commitTx(db)
      requester ! response
    } catch {
      case e: Throwable =>
    } finally {
      //close
    }
  }
}
