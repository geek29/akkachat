package akkachat.core

import com.typesafe.config.ConfigFactory

/**
  * Created by tushark on 26/9/16.
  */
object ChatConfig {

  private val config = ConfigFactory.load()

  object HttpConfig {
    private val httpConfig = config.getConfig("chatRestAPI")
    lazy val interface = httpConfig.getString("host")
    lazy val port = httpConfig.getInt("port")
  }


}
