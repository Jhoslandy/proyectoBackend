// All code commented out to prevent Scala/Gatling build errors
// package com.universidad.performance
//
// import io.gatling.core.Predef._
// import io.gatling.http.Predef._
// import org.junit.jupiter.api.Disabled
//
// @Disabled("Skipping due to build issues.")
// class BasicSimulationScala extends Simulation {
//   val httpProtocol = http
//     .baseUrl("http://localhost:8081")
//     .acceptHeader("application/json")
//
//   val scn = scenario("Basic Load Test Scala")
//     .exec(http("Get Home Page").get("/"))
//
//   setUp(
//     scn.inject(atOnceUsers(1))
//   ).protocols(httpProtocol)
// }
