import Communication.{isPresentReply, isPresentRequest}
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import com.google.gson.{Gson, GsonBuilder}
import org.json.JSONObject

import scala.language.postfixOps

class RestfulLambda extends RequestHandler[String, String] {

  override def handleRequest(event: String, context: Context): String = {
    println(event)
    println(event)
    println(event)
    println(context.toString)
    val g = new GsonBuilder().setPrettyPrinting().create()
    val request: isPresentRequest = g.fromJson(event, isPresentRequest.getClass)
    println(request.message)
    val response: isPresentReply = isPresentReply(true)
    val jsonObject: String = new JSONObject()
      .put("present", response.present).toString
    jsonObject
  }
}
