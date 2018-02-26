package uk.co.telegraph.googleplayreceipt

import net.serenitybdd.cucumber.CucumberWithSerenity
import com.waioeka.sbt.runner.CucumberRunner
import cucumber.api.CucumberOptions
import org.junit.runner.RunWith

@RunWith(classOf[CucumberWithSerenity])
@CucumberOptions(
  features = Array("classpath:features"),
  plugin   = Array(
    "html:target/cucumber",
    "json:target/cucumber/test-report.json",
    "junit:target/cucumber/test-report.xml"
  )
)
class SampleTest extends CucumberRunner {
}