import com.typesafe.config.{Config, ConfigFactory}

// Configuration utility to access application.conf
object Configuration {
  private val config = ConfigFactory.load()
  config.getConfig("serverConfigurations")

  val timeInterval: String = config.getString("serverConfigurations.Interval")
  val time: String = config.getString("serverConfigurations.Time")
  val message: String = config.getString("serverConfigurations.Message")
  val endpoint: String = config.getString("serverConfigurations.Endpoint")

}
