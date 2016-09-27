name := "akkachat"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.scalatest"          %% "scalatest"             % "2.2.4" % "test"

libraryDependencies += "com.typesafe"           %  "config"                % "1.2.1"

libraryDependencies += "io.spray"               %%  "spray-can"            % "1.3.3"

libraryDependencies += "io.spray"               %%  "spray-routing"        % "1.3.3"

libraryDependencies += "io.spray"               %%  "spray-testkit"        % "1.3.3"  % "test"

libraryDependencies += "io.spray"               %%  "spray-json"           % "1.3.1"

libraryDependencies += "io.spray"               %%  "spray-client"         % "1.3.3"

libraryDependencies += "io.spray"               %% "spray-httpx"           % "1.3.3"

libraryDependencies += "com.typesafe.akka"      %%  "akka-actor"           % "2.4.2"

libraryDependencies += "com.typesafe.akka"      %%  "akka-slf4j"           % "2.4.2"

libraryDependencies += "com.typesafe.akka"      %%  "akka-testkit"         % "2.4.2"   % "test"

libraryDependencies += "ch.qos.logback"         % "logback-classic"        % "1.1.3"

libraryDependencies += "org.specs2"             %% "specs2-core"           % "3.6.5" % "test"

libraryDependencies += "org.clapper"            %  "grizzled-slf4j_2.10"   % "1.0.2"

libraryDependencies += "org.json4s"             %%  "json4s-jackson"       % "3.2.9"

libraryDependencies += "org.apache.commons"     %  "commons-io"            % "1.3.2"



    