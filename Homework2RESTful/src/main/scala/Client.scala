import Communication.isPresentRequest
import Communication.isPresentReply

import org.json
import org.json.JSONObject
import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}


object Client {
  val endpoint = "https://zfmd838gs5.execute-api.us-east-1.amazonaws.com/default/RestfulLambda"

  def main(args: Array[String]): Unit = {
    sendRequest()
  }

  def sendRequest(): Unit = {
    val presentRequest = isPresentRequest("the", "15:55:24.333", "25")
    val jsonObject: String = new JSONObject()
      .put("message", presentRequest.message)
      .put("time", presentRequest.time)
      .put("timeInterval", presentRequest.timeInterval).toString()
    var request = HttpRequest.newBuilder()
      .uri(URI.create(endpoint))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(jsonObject))
      .build();
    val client = HttpClient.newHttpClient
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
   // val responseAsObject: isPresentReply = response.asInstanceOf[isPresentReply]
    println(jsonObject)
    println(response.body())
  }
}

