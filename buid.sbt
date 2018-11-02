import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val endpointsVersion = "0.7.0"

val commonSettings = Seq(
  scalaVersion := "2.12.7"
)

val shared =
  crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file("shared"))
    .settings(
      commonSettings,
      libraryDependencies ++= Seq(
        "org.julienrf" %%% "endpoints-algebra-playjson" % endpointsVersion
      )
    )

val client =
  project.in(file("web-client"))
    .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalaJSWeb)
    .settings(
      scalaJSUseMainModuleInitializer := true,
      emitSourceMaps := false,
      libraryDependencies ++= Seq(
        "org.julienrf" %%% "scalm" % "1.0.0-RC1+7-ff1789ba+20181028-2149",
        "org.julienrf" %%% "endpoints-xhr-client-faithful" % endpointsVersion
      ),
      version in webpack := "4.23.1",
      npmDependencies in Compile ++= Seq(
        "echarts" -> "4.1.0",
        "materialize-css" -> "1.0.0"
      ),
      npmDevDependencies in Compile ++= Seq(
        "webpack-merge" -> "4.1.4",
        "css-loader" -> "1.0.1",
        "style-loader" -> "0.23.1"
      ),
      webpackConfigFile in fastOptJS := Some(baseDirectory.value / "dev.webpack.config.js"),
      webpackConfigFile in fullOptJS := Some(baseDirectory.value / "prod.webpack.config.js"),
      webpackBundlingMode := BundlingMode.LibraryOnly(),
      scalacOptions += "-P:scalajs:sjsDefinedByDefault"
    )
    .dependsOn(shared.js)

val server =
  project.in(file("web-server"))
    .enablePlugins(WebScalaJSBundlerPlugin, JavaServerAppPackaging)
    .settings(
      WebKeys.packagePrefix in Assets := "public/",
      WebKeys.exportedMappings in Assets := Seq(), // https://github.com/playframework/playframework/issues/5242
      (managedClasspath in Runtime) += (packageBin in Assets).value,
      scalaJSProjects := Seq(client),
      pipelineStages in Assets := Seq(scalaJSPipeline),
      libraryDependencies ++= Seq(
        "org.julienrf" %% "endpoints-play-server" % endpointsVersion,
        "org.slf4j" % "slf4j-simple" % "1.7.25"
      ),
      (sourceGenerators in Compile) += Def.task {
        AssetsTasks.generateDigests(
          baseDirectory = WebKeys.assets.value,
          targetDirectory = (sourceManaged in Compile).value,
          generatedObjectName = "AssetsDigests",
          generatedPackage = Some("mipa"),
          assetsPath = identity
        )
      }.taskValue,
      herokuAppName in Compile := "my-impact"
    )
    .dependsOn(shared.jvm)
