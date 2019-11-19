package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  val csvFeeder = csv("data/gameCsvFile.csv").circular

  def getSpecificVideoGame() = {
    repeat(10) {
      feed(csvFeeder)
        .exec(http("Get specific video game")
        .get("videogames/${gameId}")
        .check(jsonPath("$.name").is("${gameName}"))
        .check(status.is(200)))
        .pause(1)
    }
  }

  val scn = scenario("Csv Feeder test")
      .exec(getSpecificVideoGame())



  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
