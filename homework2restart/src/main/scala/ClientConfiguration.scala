import com.typesafe.config.{Config, ConfigFactory}


object ClientConfiguration {
  private val config = ConfigFactory.load()
  config.getConfig("clientConfigurations")

  val time: String = config.getString("clientConfigurations.Time")
  val timeInterval: String = config.getString("clientConfigurations.Interval")
  val port: Int = config.getInt("clientConfigurations.Port")
  val serverIP: String = config.getString("clientConfigurations.ServerIP")
  val word: String = config.getString("clientConfigurations.TestWord")

}
