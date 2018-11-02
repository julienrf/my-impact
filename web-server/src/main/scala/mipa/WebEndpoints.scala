package mipa

import endpoints.play.server.{Assets, Endpoints, PlayComponents}
import play.twirl.api.StringInterpolation

class WebEndpoints(val playComponents: PlayComponents)
  extends Endpoints
    with Assets {

  val index = endpoint(get(path), htmlResponse)

  val assets = assetsEndpoint(path / "assets" / assetSegments())

  lazy val digests = AssetsDigests.digests

  val routes = routesFromEndpoints(
    index.implementedBy(_ =>
      html"""<!DOCTYPE html>
            <html>
              <head>
                <title>My Impact</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <script defer src="${assets.call(asset("client-fastopt-bundle.js"))}"></script>
              </head>
              <body><div></div></body>
            </html>"""
    ),
    assets.implementedBy(assetsResources(pathPrefix = Some("/public")))
  )

}
