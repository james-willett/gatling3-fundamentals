package advanced

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import scala.concurrent.duration._

class AdvancedExampleSimulation extends Simulation {

	val httpConf = http.baseURL("http://excilysbank.gatling.cloudbees.net")
	.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
	.acceptEncodingHeader("gzip,deflate")
	.acceptLanguageHeader("fr,en-us;q=0.7,en;q=0.3")
	.userAgentHeader("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.17) Gecko/20110422 Ubuntu/9.10 (karmic) Firefox/3.6.17")
	.disableFollowRedirect

	setUp(
		SomeScenario.scn.inject(ramp(10 users) over (10 seconds)),
		SomeOtherScenario.otherScn.inject(nothingFor(30 seconds), ramp(5 users) over (20 seconds)))
		.protocols(httpConf)
}
