import ServerRPC.formatStandard
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import Communication._
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future, duration}


// RPC server Program
object ServerRPC {
  final val formatStandard = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

  def main(args: Array[String]): Unit = {

    // thread count set at 5
    val group: EventLoopGroup = new NioEventLoopGroup(5)
    try {
      val server = apply(ExecutionContext.global)
      server.start()
      // server set to run for 5 minutes until stopping
      group.awaitTermination(5, duration.MINUTES)
    }
    finally {
      group.shutdownGracefully()
    }
  }
  def apply(executionContext: ExecutionContext): ServerRPC = new ServerRPC(executionContext)



}

// server thread
class ServerRPC(executionContext: ExecutionContext) { self =>
  private[this] var server: Server = null
  private def start(): Unit = {
    // building server on port with RPC server
    val mainService = new ReturnMessageService
    server = NettyServerBuilder
      .forPort(Configuration.port)
      .addService(isPresentServiceGrpc.bindService(mainService, executionContext))
      .build
      .start
    println("\nServer up and running.\nAwaiting Connection\n")
    sys.addShutdownHook {
      println("\nShutting Down\n")
    }
  }

  // Implements GRPC service
  class ReturnMessageService extends isPresentServiceGrpc.isPresentService {
    // accepts an request and returns a reply Future
    override def searchRequest(request: isPresentRequest): Future[isPresentReply] = {
      println(request.message)
      try {
        // searching logfile via binary search
        val logString: Array[String] = LogSearcherBinary.logSearchByTime(LocalTime.parse(request.time, formatStandard), request.timeInterval.toInt, new ListBuffer[String])
        println("return Sent")
        // return results as isPresent reply object
        Future.successful(isPresentReply(present = !logString.isEmpty, entries = logString))
      }
      catch {  // exceptions if log not found
        case a: Exception => {
          println(a.toString+"error\n")
          Future.failed(new Exception("Log File not Found\n"))
        }
      }
    }
  }
}