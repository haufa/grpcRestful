import com.typesafe.config.{Config, ConfigFactory}


// configuration settings
object Configuration {
  private val config = ConfigFactory.load()
  config.getConfig("serverConfigurations")

  val generatingPattern: String = config.getString("serverConfigurations.Pattern")
  val logLocation: String = config.getString("serverConfigurations.LogLocation")
  val bucket: String = config.getString("serverConfigurations.Bucket")

}
