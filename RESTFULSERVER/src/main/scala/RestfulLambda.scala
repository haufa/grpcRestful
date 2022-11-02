import Communication.{isPresentReply, isPresentRequest}
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.amazonaws.services.lambda.runtime.events.{APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse}
import com.google.gson.{Gson, GsonBuilder}
import org.json.JSONObject

import java.time.LocalTime
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import LogSearcherBinary.formatStandard

class RestfulLambda extends RequestHandler[APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse] {

  override def handleRequest(event: APIGatewayV2HTTPEvent, context: Context): APIGatewayV2HTTPResponse = {
    println(event)
    val body = event.getBody
    val g = new GsonBuilder().setPrettyPrinting().create()
    val request: isPresentRequest = g.fromJson(body, classOf[isPresentRequest])
    val responseArray: Array[String] = LogSearcherBinary.logSearchByTime(LocalTime.parse(request.time, formatStandard),
      request.timeInterval.toInt, new ListBuffer[String])
    val response = isPresentReply(present = responseArray.nonEmpty, entries = responseArray.mkString("\n"))
    println(response.present.toString+" "+response.entries)
    val jsonObject: String = new JSONObject()
      .put("present", response.present)
      .put("entries", response.entries).toString()
    APIGatewayV2HTTPResponse.builder()
      .withStatusCode(200)
      .withBody(jsonObject)
      .build()
  }
}
