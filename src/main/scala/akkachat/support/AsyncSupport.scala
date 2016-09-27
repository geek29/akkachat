package akkachat.support

import java.util.concurrent.TimeUnit
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

}
