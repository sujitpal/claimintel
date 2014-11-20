name := "claimintel"

organization := "com.mycompany"

version := "0.1"

scalaVersion := "2.10.2"

sbtVersion := "0.13.1"

jetty()

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.apache.solr" % "solr-core" % "4.9.1",
  "org.springframework" % "spring-webmvc" % "4.0.0.RELEASE",
  "jfree" % "jfreechart" % "1.0.13",
  "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "container, compile",
  "org.eclipse.jetty" % "jetty-jsp" % "9.1.0.v20131115" % "container",
  "org.apache.commons" % "commons-lang3" % "3.0",
  "net.sourceforge.collections" % "collections-generic" % "4.01",
  "commons-beanutils" % "commons-beanutils" % "1.8.3",
  "commons-io" % "commons-io" % "2.4",
  "log4j" % "log4j" % "1.2.14",
  "com.novocode" % "junit-interface" % "0.8" % "test"
)
