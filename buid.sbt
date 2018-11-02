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
      npmDependencies in Compile += "echarts" -> "4.1.0",
      scalacOptions += "-P:scalajs:sjsDefinedByDefault",
      WebKeys.exportedMappings in Assets := Seq() // https://github.com/playframework/playframework/issues/5242
    )
    .dependsOn(shared.js)

val server =
  project.in(file("web-server"))
    .enablePlugins(WebScalaJSBundlerPlugin)
    .settings(
      WebKeys.packagePrefix in Assets := "public/",
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
      }.taskValue
    )
    .dependsOn(shared.jvm)
