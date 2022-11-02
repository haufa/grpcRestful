import Communication.{isPresentReply, isPresentRequest}
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.lambda.runtime.events.{APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse}
import com.google.gson.{GsonBuilder}
import org.json.JSONObject
import java.time.LocalTime
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import LogSearcherBinary.formatStandard
import com.sun.org.slf4j.internal.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

// create logger
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

// HTTP EVENT used as handled values and return value
class RestfulLambda extends RequestHandler[APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse] {

  // primary lambda HTTP request handler
  override def handleRequest(event: APIGatewayV2HTTPEvent, context: Context): APIGatewayV2HTTPResponse = {
    // fetching JSON string in HTTP body
    val body = event.getBody
    val g = new GsonBuilder().setPrettyPrinting().create()
    // deserializing JSON string to isPresentRequest object
    val request: isPresentRequest = g.fromJson(body, classOf[isPresentRequest])
    // running binary search based on request parameters
    val responseArray: Array[String] = LogSearcherBinary.logSearchByTime(LocalTime.parse(request.time, formatStandard),
      request.timeInterval.toInt, new ListBuffer[String])
    // converting response Array of Strings into a single string and creating isPresentReply object
    val response = isPresentReply(present = responseArray.nonEmpty, entries = responseArray.mkString("\n"))
    println(response.present.toString+" "+response.entries)
    // serializing the isPresentReply object
    val jsonObject: String = new JSONObject()
      .put("present", response.present)
      .put("entries", response.entries).toString()
    // sending response
    APIGatewayV2HTTPResponse.builder()
      .withStatusCode(200)
      .withBody(jsonObject)
      .build()
  }
}
