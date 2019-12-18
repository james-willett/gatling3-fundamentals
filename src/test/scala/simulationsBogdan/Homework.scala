package simulationsBogdan

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random


class Homework extends Simulation {

  /*** get runtime parameters properties ***/
  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  /*** define runtime parameters ***/
  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "20").toInt

  /*** print useful infos about the test ***/
  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration: ${testDuration} seconds")
  }

  /*** define http conf details ***/
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  /*** define local variables ***/
  var idNumbers = (200 to 300).iterator
  val rnd = new Random()
  val now = LocalDate.now()
  val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  /*** define generators ***/
  def getRandomDate(now: LocalDate, rnd: Random) = {
    now.minusDays(rnd.nextInt(30)).format(dateFormat)
  }

  def randomString(length: Int): Any = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  /*** define custom feeder for creating entries with new id any time ***/
  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game" + randomString(5)),
    "releaseDate" -> getRandomDate(now, rnd),
    "reviewScore" -> rnd.nextInt(25),
    "category" -> ("Category" + randomString(4)),
    "rating" -> ("Rating" + randomString(2))
    ))



  /*** define API methods ***/
  def getAllVideoGames() = {
    exec(
      http("Get all video games")
        .get("videogames")
        .check(status.is(200))
    )
  }

  def createNewGame() = {
    feed(customFeeder)
      .exec(http("Create a new game via POST")
        .post("videogames/")
          .body(ElFileBody("bodies/NewGameTemplate.json")).asJson
        .check(status.is(200))
      )
  }


  def getSpecificGame() = {
    exec(
      http("Get Specific Game")
        .get("videogames/${gameId}")
        .check(status.is(200))
    )
  }

  def deleteGame() = {
    exec(
      http("Delete a specific Game")
        .delete("videogames/${gameId}")
        .check(status.is(200))
    )
  }

  /*** define test scenario ***/
  val loadScenario = scenario("Real load test is running...")
      .forever() {
        exec(getAllVideoGames())
          .pause(3)
          .exec(createNewGame())
          .pause(3)
          .exec(getSpecificGame)
          .pause(3)
          .exec(deleteGame()
        )
      }

  /*** execute test scenario ***/
  setUp(
    loadScenario.inject(
      nothingFor(2 seconds),
      rampUsers(userCount) during (rampDuration second)
    )
  ).protocols(httpConf)
    .maxDuration(testDuration seconds)

  /*** After ***/
  after {
    println("Load test completed")
  }
}
