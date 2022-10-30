import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
import io.grpc.{Server, ServerBuilder}
import io.grpc.netty.{NettyServerBuilder, NettyServerProvider}
import Communication.{CommunicationProto, isPresentReply, isPresentRequest, isPresentServiceGrpc}

import scala.concurrent.{ExecutionContext, Future, duration}
import LogSearcher.{formatStandard}
import LogSearcherBinary._
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer



object ServerRPC {

  def main(args: Array[String]): Unit = {

    val group: EventLoopGroup = new NioEventLoopGroup(5)
    try {
      val server = apply(ExecutionContext.global)
      server.start()
      group.awaitTermination(5, duration.MINUTES)
    }
    finally {
      group.shutdownGracefully()
    }
  }

  def apply(executionContext: ExecutionContext): ServerRPC = new ServerRPC(executionContext)

  final val formatStandard = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

}


class ServerRPC(executionContext: ExecutionContext) { self =>
  private[this] var server: Server = null
  private def start(): Unit = {
    val mainService = new ReturnMessageService
    server = NettyServerBuilder
      .forPort(Configuration.port)
      .addService(isPresentServiceGrpc.bindService(mainService, executionContext))
      .build
      .start
    println("\nServer up and running.\nAwaiting Connection\n")
    sys.addShutdownHook {
      println("\nShutting Down\n")
    //  self.stop() old way of doing it
    }
  }

  class ReturnMessageService extends isPresentServiceGrpc.isPresentService {
    override def searchRequest(request: isPresentRequest): Future[isPresentReply] = {
      println(request.message)
      try {
        val logString: Array[String] = LogSearcherBinary.logSearchByTime(LocalTime.parse(request.time, formatStandard), request.timeInterval.toInt, new ListBuffer[String])
        println("return Sent")
        Future.successful(isPresentReply(present = !logString.isEmpty, entries = logString))
      }
      catch {
        case a: Exception => {
          println(a.toString+"error\n")
          Future.failed(new Exception("Log File not Found\n"))
        }
      }
    }
  }
}