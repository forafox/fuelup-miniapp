package ru.fuelup.loadtest

object Config {
  val baseUrl: String = sys.env.getOrElse("TARGET_URL", "http://localhost:8080")
  val internalApiKey: String = sys.env.getOrElse("INTERNAL_API_KEY", "change_me")

  // stepped injection profile (VKR section 3.1)
  val rampUsers: Seq[(Int, Int)] = Seq(
    (10, 30),
    (50, 60),
    (100, 60),
    (500, 120),
    (1000, 180)
  )
}
