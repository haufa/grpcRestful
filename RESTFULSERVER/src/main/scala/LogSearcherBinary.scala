import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder

import java.io.File
import java.net.URL
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.language.postfixOps
import scala.sys.process._
import scala.util.matching.Regex


object LogSearcherBinary {
  final val injectedRegexPattern = new Regex(Configuration.generatingPattern)
  final val formatStandard = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")




  def logSearchByTime(timeToSearch: LocalTime, interval: Int, output: ListBuffer[String]): Array[String] = {
    val awsClient = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build()
    val totalFile: Array[String] = awsClient.getObjectAsString(Configuration.bucket, Configuration.logLocation).split('\n')
    val size = totalFile.length
    val low = 0;
    val high = (size -1)
    val mid = (high-low)/2
    val startTime = LocalTime.parse(totalFile(0).split(" ")(0), formatStandard)
    val endTime = LocalTime.parse(totalFile.last.split(" ")(0), formatStandard)
    val searchTimeLow: LocalTime = timeToSearch.minus(interval, ChronoUnit.MINUTES)
    val searchTimeHigh:LocalTime = timeToSearch.plus(interval, ChronoUnit.MINUTES)
    if(timeToSearch.isAfter(startTime) && timeToSearch.isBefore(endTime)) {
      val firstInstance = logSearchHelper(searchTimeHigh, searchTimeLow, low, mid, high, totalFile)
      logAdder(firstInstance, true, searchTimeHigh, totalFile, output)
      logAdder(firstInstance, false, searchTimeLow, totalFile, output)
      output.toArray
      val returnArray = output.filter(injectedRegexPattern.findFirstIn(_).nonEmpty)
      returnArray.toArray
    }
    else {
      output.toArray
    }
  }

  def logSearchHelper(timeToSearchHigh: LocalTime, timeToSearchLow: LocalTime, low: Int, mid: Int, high: Int, Source: Array[String]): Int = {
    val timeCode = LocalTime.parse(Source(mid).split(" ")(0), formatStandard)
    // within range
    if(timeToSearchLow.isBefore(timeCode) && timeToSearchHigh.isAfter(timeCode)) {
      mid
    }
      // search up
    else if(timeToSearchLow.isAfter(timeCode)) {
      val newHigh = high
      val newLow = mid
      val newMid = (high - mid)/2 + mid
      logSearchHelper(timeToSearchHigh, timeToSearchLow, newLow, newMid, newHigh, Source)
    }
      // search low
    else if(timeToSearchHigh.isBefore(timeCode)) {
      val newLow = low
      val newHigh = mid
      val newMid = (mid-low)/2 + low
      logSearchHelper(timeToSearchHigh, timeToSearchLow, newLow, newMid, newHigh, Source)
    }
    else throw new Exception("error log search fail")
  }

  def logAdder(StartLog: Int, UpOrDown: Boolean, TimeValue: LocalTime, Source: Array[String], Output: ListBuffer[String]): Unit = {
    var logEntry = StartLog
    if(UpOrDown) {
      while(logEntry < Source.length && LocalTime.parse(Source(logEntry).split(" ")(0), formatStandard).isBefore(TimeValue)) {
        Output += Source(logEntry)
        logEntry = logEntry+1
      }
    }
    else {
      logEntry = logEntry-1
      while(logEntry >= 0 && LocalTime.parse(Source(logEntry).split(" ")(0), formatStandard).isAfter(TimeValue)) {
        Output += Source(logEntry)
        logEntry = logEntry-1
      }
    }
  }
}
