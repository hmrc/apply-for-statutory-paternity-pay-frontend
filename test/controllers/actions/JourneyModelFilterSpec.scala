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

package controllers.actions

import base.SpecBase
import generators.ModelGenerators
import models.requests.DataRequest
import models.{CountryOfResidence, Name, NormalMode, PaternityLeaveLengthGbPreApril24OrNi}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.concurrent.ScalaFutures
import pages._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContent
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import uk.gov.hmrc.domain.Nino
import play.api.test.Helpers._

import java.time.LocalDate
import scala.concurrent.Future

class JourneyModelFilterSpec extends SpecBase with ScalaFutures with ModelGenerators {

  "JourneyModelFilter" - {

    lazy val app = new GuiceApplicationBuilder()
      .build()

    lazy val filter = app.injector.instanceOf[JourneyModelFilter]

    "must not filter requests which contain data for a complete journey model" in {

      val answers =
        emptyUserAnswers
          .set(CountryOfResidencePage, CountryOfResidence.England).success.value
          .set(BabyDateOfBirthPage, LocalDate.now).success.value
          .set(BabyDueDatePage, LocalDate.now).success.value
          .set(BabyHasBeenBornPage, true).success.value
          .set(IsAdoptingOrParentalOrderPage, false).success.value
          .set(IsBiologicalFatherPage, true).success.value
          .set(IsCohabitingPage, true).success.value
          .set(IsInQualifyingRelationshipPage, true).success.value
          .set(NamePage, Name("first", "last")).success.value
          .set(NinoPage, arbitrary[Nino].sample.value).success.value
          .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value
          .set(PayStartDateBabyBornPage, LocalDate.now).success.value
          .set(WillHaveCaringResponsibilityPage, true).success.value
          .set(WillTakeTimeToCareForChildPage, true).success.value
          .set(WillTakeTimeToSupportPartnerPage, true).success.value

      val completedDataRequest = DataRequest[AnyContent](FakeRequest(), "id", answers)
      val result = filter.invokeBlock[AnyContent](completedDataRequest, _ => Future.successful(Ok))
      status(result) mustEqual OK
    }

    "must redirect to the first page in the journey which fails" in {

      val completedDataRequest = DataRequest[AnyContent](FakeRequest(), "id", emptyUserAnswers)
      val result = filter.invokeBlock[AnyContent](completedDataRequest, _ => Future.successful(Ok))
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.CountryOfResidenceController.onPageLoad(NormalMode).url
    }
  }
}
