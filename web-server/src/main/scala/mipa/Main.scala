package mipa

import endpoints.play.server.{DefaultPlayComponents, HttpServer}
import play.core.server.ServerConfig

object Main {

  def main(args: Array[String]): Unit = {
    val config = ServerConfig()
    val playComponents = new DefaultPlayComponents(config)
    val webEndpoints = new WebEndpoints(playComponents)
    HttpServer(config, playComponents, webEndpoints.routes)
    ()
  }

}
