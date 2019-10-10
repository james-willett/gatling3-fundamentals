package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class BasicLoadSimulation extends Simulation {

  val httpConf = http
    .baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
 //   .proxy(Proxy("localhost", 8866).httpsPort(8866))

  def getAllVideoGames() = {
    exec(
      http("Get All Video Games - 1st call")
        .get("videogames")
        .check(status.is(200)))
  }

  def getSpecificVideoGame() = {
    exec(http("Get Specific Video Game")
      .get("videogames/2")
      .check(status.is(200)))
  }

  val scn = scenario("Video Game DB")
    .exec(getAllVideoGames())
    .pause(5)
    .exec(getSpecificVideoGame())
    .pause(5)
    .exec(getAllVideoGames())

  // Load Simulation 1:  basic Load Simulation
  setUp(
    scn.inject(
      nothingFor(5 seconds), // do nothing for 5 seconds
      atOnceUsers(5), // inject 5 users at once
      rampUsers(10) during (10 seconds) // inject 10 users over a period of 10 seconds
    ).protocols(httpConf.inferHtmlResources()) // inferHtmlResources will fetch everything on the page (JS, CSS, images etc.)
  )
}
