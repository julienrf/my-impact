package mipa

import endpoints.play.server.{Assets, Endpoints, PlayComponents}

class WebEndpoints(val playComponents: PlayComponents)
  extends Endpoints
    with Assets
    with Pages {

  val index = endpoint(get(path), htmlResponse)

  val assets = assetsEndpoint(path / "assets" / assetSegments())

  lazy val digests = AssetsDigests.digests

  val routes = routesFromEndpoints(
    index.implementedBy(_ => indexHtml),
    assets.implementedBy(assetsResources(pathPrefix = Some("/public")))
  )

}
