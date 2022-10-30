import java.time.format.DateTimeFormatter
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.matching.Regex
import sys.process._
import java.net.URL
import java.io.File
import scala.language.postfixOps



object LogSearcher {
  var i: Int = 0;
  final val injectedRegexPattern = new Regex(Configuration.generatingPattern)
  final val formatStandard = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

  def fileDownloader(url: String, filename: String):Unit = {
    new URL(url) #> new File(filename) !!
  }


  def logSearchByTime(timeToSearch: LocalTime, interval: Int, output: ListBuffer[String]): Array[String] = {
    val fileName = "logs"+i.toString+".log"
    fileDownloader(Configuration.logLocation, fileName)
    val totalFile: Array[String] = Source.fromFile(fileName).getLines().toArray
    totalFile.foreach { token =>
      val line: Array[String] = token.split(" ")
      val time = LocalTime.parse(line(0), formatStandard)
      if(ChronoUnit.MINUTES.between(time, timeToSearch).abs.toInt <= interval) {
        output += token
      }
    }
    i = i+1
    val returnArray = output.filter(injectedRegexPattern.findFirstIn(_).nonEmpty)
    returnArray.toArray
  }
}
