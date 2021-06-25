name := "something-awesome"

version := "0.1"

scalaVersion := "2.13.6"

scalacOptions in GlobalScope ++= Seq(
  "-Ypartial-unification",
  "-Xfatal-warnings",
  "-feature",
  "-language:higherKinds",
  "-deprecation"
)

val core = (project in file("module1"))
  .settings(
    name := "module1",
    libraryDependencies ++= Dependencies.module1,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(("org.typelevel" %% "kind-projector" % "0.11.3").cross(CrossVersion.full))
  )