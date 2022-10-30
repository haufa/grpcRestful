//package homework2restart

import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
import Communication.{CommunicationProto, isPresentReply, isPresentRequest, isPresentServiceGrpc}
import io.grpc.netty.NettyChannelBuilder


object Client {

  def apply(host: String, port: Int): Client = {
    val clientConn = NettyChannelBuilder.forAddress(host, port).usePlaintext().build
    val blockingStub = isPresentServiceGrpc.blockingStub(clientConn)
    new Client(clientConn, blockingStub)
  }

  def main(args: Array[String]): Unit = {
    val client = apply(ClientConfiguration.serverIP, ClientConfiguration.port)
    try {
      client.makeIsPresentRequest()
    } catch {
      case a: Any => println(a.toString+"\nData response bad")
    } finally {
      client.shutdown()

    }
  }

}



  class Client private(val clientConn: ManagedChannel, val blockingStub: isPresentServiceGrpc.isPresentServiceBlockingStub) {
      def makeIsPresentRequest(): Unit = {
        val request = isPresentRequest(ClientConfiguration.word, ClientConfiguration.time, ClientConfiguration.timeInterval)
        try {
          val response = blockingStub.searchRequest(request)
          val entries: Array[Any] = response.entries.toArray
          println("Logs found: "+response.present.toString)
          entries.foreach(println)
        }
        catch {
          case a: Any => println(a.toString+"\nNo connection available\n")
        }
      }
    def shutdown(): Unit = {
      clientConn.shutdownNow()
      println("Client has shutdown")
    }
}