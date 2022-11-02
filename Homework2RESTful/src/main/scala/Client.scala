import Communication.isPresentRequest
import Communication.isPresentReply
import org.json.JSONObject
import com.google.gson.GsonBuilder
import com.sun.org.slf4j.internal.{Logger, LoggerFactory}

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import scala.util.{Failure, Success, Try}

// logger utility
object CreateLogger {
  def apply[T](loggerClass: Class[T]): Logger = {
    val LOGBACKXML = "logback.xml"
    val logger = LoggerFactory.getLogger(loggerClass)
    Try(getClass.getClassLoader.getResourceAsStream(LOGBACKXML)) match {
      case Failure(exception) => logger.error(s"Failed to locate $LOGBACKXML for reason $exception")
      case Success(inStream) => inStream.close()
    }
    logger
  }
}


object Client {

  // endpoint URL
  val endpoint = Configuration.endpoint

  // main calling function
  def main(args: Array[String]): Unit = {
    sendRequest()
  }


  def sendRequest(): Unit = {
    // generating request object from Protobuf
    val presentRequest = isPresentRequest(Configuration.message, Configuration.time, Configuration.timeInterval)
    // object converted to JSON string
    val jsonObject: String = new JSONObject()
      .put("message", presentRequest.message)
      .put("time", presentRequest.time)
      .put("timeInterval", presentRequest.timeInterval).toString()
    println(jsonObject)
    // sending object to lambda via HTTP as JSON in body
    val request = HttpRequest.newBuilder()
      .uri(URI.create(endpoint))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(jsonObject))
      .build();
    println("Contacting: "+endpoint)
    val client = HttpClient.newHttpClient
    println("sending request")
    // waiting for response from Lambda
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    val g = new GsonBuilder().setPrettyPrinting().create()
    // deserializing response from JSON returned by lambda, converting to reply object
    val responseAsObject: isPresentReply = g.fromJson(response.body(), classOf[isPresentReply])
    println("Logs found? "+responseAsObject.present.toString)
    println("Search Responses: "+responseAsObject.entries)
  }
}

