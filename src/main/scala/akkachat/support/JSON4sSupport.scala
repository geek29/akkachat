package akkachat.support

import org.json4s.{Extraction, Formats, JValue, NoTypeHints, jackson}
import spray.httpx.Json4sJacksonSupport

/**
  * Created by tushark on 26/9/16.
  */
object JSON4sSupport extends Json4sJacksonSupport {

  implicit def json4sJacksonFormats: Formats = jackson.Serialization.formats(NoTypeHints)

  //so you don't need to import
  //jackson everywhere
  val jsonMethods = org.json4s.jackson.JsonMethods


  def toJValue[T](value: T): JValue = {
    Extraction.decompose(value)
  }

}
