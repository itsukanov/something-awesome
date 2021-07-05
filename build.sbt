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

val companyInfo = (project in file("company-info"))
  .settings(
    name := "company-info",
    libraryDependencies ++= Dependencies.commonInfo,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.3").cross(CrossVersion.full))
  )