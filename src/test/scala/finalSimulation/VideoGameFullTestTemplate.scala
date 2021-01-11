package finalSimulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class VideoGameFullTestTemplate extends Simulation {

  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
    .proxy(Proxy("localhost", 8888))

  /*** HTTP CALLS ***/
  def getAllVideoGames() = {
    exec(
      http("Get all video games")
        .get("videogames")
        .check(status.is(200))
    )
  }

  // add other calls here

  /** SCENARIO DESIGN */

  // using the http call, create a scenario that does the following:
  // 1. Get all games
  // 2. Create new Game
  // 3. Get details of that single
  // 4. Delete the game

  /** SETUP LOAD SIMULATION */

  // create a scenario that has runtime parameters for:
  // 1. Users
  // 2. Ramp up time
  // 3. Test duration

  /** Custom Feeder */

  // to generate the date for the Create new Game JSON

  /** Helper methods */

  // for the custom feeder, or the defaults for the runtime parameters... and anything

  /** Variables */

  // for the helper methods

  /** Before & After */
  // to print out message at the start and end of the test

}
