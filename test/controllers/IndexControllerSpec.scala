/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import base.SpecBase
import config.Constants
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.{IndexView, TransitionalIndexView}

import java.time.{Clock, Instant, LocalDate, ZoneId}

class IndexControllerSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Index Controller" - {

    "must return OK and the correct view for a GET" - {

      "when the current date is before 7th April 2024" in {

        forAll(datesBetween(LocalDate.of(2000, 1, 1), Constants.april24LegislationEffective.minusDays(1))) { date =>

          val clock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault).toInstant, ZoneId.systemDefault)

          val application =
            applicationBuilder(userAnswers = None)
              .overrides(bind[Clock].toInstance(clock))
              .build()

          running(application) {
            val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[TransitionalIndexView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual view()(request, messages(application)).toString
          }
        }
      }

      "when the current date is on or after 7th April 2024" in {

        forAll(datesBetween(Constants.april24LegislationEffective, LocalDate.of(2100, 1, 1))) { date =>

          val clock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault).toInstant, ZoneId.systemDefault)

          val application =
            applicationBuilder(userAnswers = None)
              .overrides(bind[Clock].toInstance(clock))
              .build()

          running(application) {
            val request = FakeRequest(GET, routes.IndexController.onPageLoad.url)

            val result = route(application, request).value

            val view = application.injector.instanceOf[IndexView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual view()(request, messages(application)).toString
          }
        }
      }
    }
  }
}
