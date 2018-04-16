package uk.co.telegraph.applereceipt.steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers

class SampleSteps extends ScalaDsl with EN with Matchers{

  private var givenCalled = false
  private var whenCalled = false

  Given("""^A SBT project$""") { () =>
    givenCalled = true
  }

  When("""^I run the cucumber goal$""") { () =>
    whenCalled = true
  }

  Then("""^Cucumber is executed against my features and step definitions$""") { () =>
    givenCalled should be (true)
    whenCalled should be (true)
  }

}
