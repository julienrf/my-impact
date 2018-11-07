package mipa

import endpoints.play.server.{DefaultPlayComponents, HttpServer}
import play.core.server.ServerConfig

object Main {

  def main(args: Array[String]): Unit = {
    val config = ServerConfig(port = sys.props.get("http.port").map(_.toInt).orElse(Some(9000)))
    val playComponents = new DefaultPlayComponents(config)
    val webEndpoints = new WebEndpoints(playComponents)
    HttpServer(config, playComponents, webEndpoints.routes)
    ()
  }

}
