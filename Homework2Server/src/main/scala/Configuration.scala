import com.typesafe.config.{Config, ConfigFactory}


object Configuration {
  private val config = ConfigFactory.load()
  config.getConfig("serverConfigurations")

  val generatingPattern: String = config.getString("serverConfigurations.Pattern")
  val timeInterval: String = config.getString("serverConfigurations.Interval")
  val port: Int = config.getInt("serverConfigurations.Port")
  val logLocation: String = config.getString("serverConfigurations.LogLocation")

}
