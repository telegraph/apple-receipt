/* Add Here Additional Plugins */
resolvers += "mvn-artifacts" atS3 "s3://mvn-artifacts/release"
resolvers += DefaultMavenRepository

addSbtPlugin("com.frugalmechanic" % "fm-sbt-s3-resolver"   % "0.9.0")
addSbtPlugin("com.eed3si9n"       % "sbt-assembly" % "0.14.3")
addSbtPlugin("uk.co.telegraph"    % "sbt-pipeline-plugin" % "1.1.0-b+")


