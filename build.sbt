
name := "tracing"

version := "0.1"

ThisBuild / scalaVersion := "2.13.6"

scalacOptions in GlobalScope ++= Seq(
  "-Xfatal-warnings",
  "-feature",
  "-language:higherKinds",
  "-deprecation"
)

val baseApp = (project in file("base-app"))
  .settings(
    name := "base-app",
    libraryDependencies ++= Dependencies.commonDeps,
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.0").cross(CrossVersion.full))
  )

val companyInfo = (project in file("company-info"))
  .settings(
    name := "company-info",
    mainClass := Some("com.itsukanov.company.info.CompanyInfoIOApp"),
    libraryDependencies ++= Dependencies.dbDeps,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.0").cross(CrossVersion.full))
  ).dependsOn(baseApp)

val companyPrices = (project in file("company-prices"))
  .settings(
    name := "company-prices",
    mainClass := Some("com.itsukanov.company.prices.CompanyPricesApp"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.0").cross(CrossVersion.full))
  ).dependsOn(baseApp)

val entryPoint = (project in file("entry-point"))
  .settings(
    name := "entry-point",
    mainClass := Some("com.itsukanov.entrypoint.EntryPointApp"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.0").cross(CrossVersion.full))
  ).dependsOn(baseApp)