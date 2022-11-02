import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.matching.Regex

// binary search function
object LogSearcherBinary {
  // fetching time parser and regex pattern from application.conf
  final val injectedRegexPattern = new Regex(Configuration.generatingPattern)
  final val formatStandard = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")




  def logSearchByTime(timeToSearch: LocalTime, interval: Int, output: ListBuffer[String]): Array[String] = {
    // fetching log file from AWS s3 bucket as string
    val awsClient = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build()
    // converting to Array of Strings by splitting string at newline
    val totalFile: Array[String] = awsClient.getObjectAsString(Configuration.bucket, Configuration.logLocation).split('\n')
    // determining high, low and midpoint in array
    val size = totalFile.length
    val low = 0;
    val high = (size -1)
    val mid = (high-low)/2
    // determining start and end time of log file
    val startTime = LocalTime.parse(totalFile(0).split(" ")(0), formatStandard)
    val endTime = LocalTime.parse(totalFile.last.split(" ")(0), formatStandard)
    // determining search time frame based on search time and time interval
    val searchTimeLow: LocalTime = timeToSearch.minus(interval, ChronoUnit.MINUTES)
    val searchTimeHigh:LocalTime = timeToSearch.plus(interval, ChronoUnit.MINUTES)
    // checking to determine whether search time is in range of log file
    if(timeToSearch.isAfter(startTime) && timeToSearch.isBefore(endTime)) {
      // start of recursive binary search call
      val firstInstance = logSearchHelper(searchTimeHigh, searchTimeLow, low, mid, high, totalFile)
      // once index in range of search period has been found, add to return array by searching up and down the log file
      logAdder(firstInstance, true, searchTimeHigh, totalFile, output)
      logAdder(firstInstance, false, searchTimeLow, totalFile, output)
      output.toArray
      // filter array for regex matches only
      val returnArray = output.filter(injectedRegexPattern.findFirstIn(_).nonEmpty)
      returnArray.toArray
    }
    else { // if search time is not in range return blank array
      output.toArray
    }
  }

  // primary recursive search function
  def logSearchHelper(timeToSearchHigh: LocalTime, timeToSearchLow: LocalTime, low: Int, mid: Int, high: Int, Source: Array[String]): Int = {
    // search mid point,  if in time period range return
    val timeCode = LocalTime.parse(Source(mid).split(" ")(0), formatStandard)
    // within range
    if(timeToSearchLow.isBefore(timeCode) && timeToSearchHigh.isAfter(timeCode)) {
      mid
    }
      // else search up
    else if(timeToSearchLow.isAfter(timeCode)) {
      val newHigh = high
      val newLow = mid
      val newMid = (high - mid)/2 + mid
      // make next recursive call
      logSearchHelper(timeToSearchHigh, timeToSearchLow, newLow, newMid, newHigh, Source)
    }
      // or search down search low
    else if(timeToSearchHigh.isBefore(timeCode)) {
      val newLow = low
      val newHigh = mid
      val newMid = (mid-low)/2 + low
      // make next recurisive call
      logSearchHelper(timeToSearchHigh, timeToSearchLow, newLow, newMid, newHigh, Source)
    }
    else throw new Exception("error log search fail") // throw exception if failure
  }

  // function adds valid log entries to return array
  def logAdder(StartLog: Int, UpOrDown: Boolean, TimeValue: LocalTime, Source: Array[String], Output: ListBuffer[String]): Unit = {
    var logEntry = StartLog
    if(UpOrDown) { // searches further along the array from index value
      while(logEntry < Source.length && LocalTime.parse(Source(logEntry).split(" ")(0), formatStandard).isBefore(TimeValue)) {
        Output += Source(logEntry)
        logEntry = logEntry+1
      }
    }
    else { // searches further down the array from index value
      logEntry = logEntry-1
      while(logEntry >= 0 && LocalTime.parse(Source(logEntry).split(" ")(0), formatStandard).isAfter(TimeValue)) {
        Output += Source(logEntry)
        logEntry = logEntry-1
      }
    }
  }
}
