
name := "tracing"

version := "0.1"

scalaVersion := "2.13.6"

scalacOptions in GlobalScope ++= Seq(
  "-Ypartial-unification",
  "-Xfatal-warnings",
  "-feature",
  "-language:higherKinds",
  "-deprecation"
)

val commonRestApi = (project in file("common-rest-api"))
  .settings(
    name := "common-rest-api",
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.3").cross(CrossVersion.full)),
    libraryDependencies ++= Dependencies.commonInfo
  )

val commonDB = (project in file("common-db"))
  .settings(
    name := "common-db",
    libraryDependencies ++= Dependencies.commonInfo
  )

val companyInfo = (project in file("company-info"))
  .settings(
    name := "company-info",
    libraryDependencies ++= Dependencies.commonInfo,
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.3").cross(CrossVersion.full))
  ).dependsOn(commonRestApi, commonDB)

val companyPrices = (project in file("company-prices"))
  .settings(
    name := "company-prices",
    libraryDependencies ++= Dependencies.commonInfo,
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.3").cross(CrossVersion.full))
  ).dependsOn(commonRestApi, commonDB)

val entryPoint = (project in file("entry-point"))
  .settings(
    name := "entry-point",
    libraryDependencies ++= Dependencies.commonInfo,
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.3").cross(CrossVersion.full))
  ).dependsOn(commonRestApi)