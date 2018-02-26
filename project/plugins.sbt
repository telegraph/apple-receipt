/* Add Here Additional Plugins */
resolvers += "mvn-artifacts" atS3 "s3://mvn-artifacts/release"

addSbtPlugin("com.eed3si9n"       % "sbt-assembly" % "0.14.3")
addSbtPlugin("uk.co.telegraph"    % "sbt-pipeline-plugin" % "1.1.0-b+")


