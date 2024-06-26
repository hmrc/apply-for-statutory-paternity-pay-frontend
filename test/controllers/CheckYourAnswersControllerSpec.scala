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
import generators.Generators
import models.{CountryOfResidence, Name, PaternityLeaveLengthGbPostApril24}
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.domain.Nino
import viewmodels.checkAnswers._
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

import java.time.LocalDate

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency with Generators {

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

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
          .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
          .set(PayStartDateGbPostApril24Page, Some(LocalDate.now)).success.value
          .set(WillHaveCaringResponsibilityPage, true).success.value
          .set(WillTakeTimeToCareForChildPage, true).success.value
          .set(WillTakeTimeToSupportPartnerPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        
        implicit val msgs: Messages = messages(application)

        val applicationDetails = SummaryListViewModel(
          rows = Seq(
            CountryOfResidenceSummary.row(answers),
            IsAdoptingOrParentalOrderSummary.row(answers)
          ).flatten
        )

        val relationshipDetails = SummaryListViewModel(
          rows = Seq(
            IsBiologicalFatherSummary.row(answers),
            IsInQualifyingRelationshipSummary.row(answers),
            IsCohabitingSummary.row(answers),
            WillHaveCaringResponsibilitySummary.row(answers),
            WillTakeTimeToCareForChildSummary.row(answers),
            WillTakeTimeToSupportPartnerSummary.row(answers)
          ).flatten
        )

        val personalDetails = SummaryListViewModel(
          rows = Seq(

            NameSummary.row(answers),
            NinoSummary.row(answers)
          ).flatten
        )

        val babyDetails = SummaryListViewModel(
          rows = Seq(
            BabyHasBeenBornSummary.row(answers),
            BabyDateOfBirthSummary.row(answers),
            BabyDueDateSummary.row(answers)
          ).flatten
        )

        val paternityDetails = SummaryListViewModel(
          rows = Seq(
            PaternityLeaveLengthGbPostApril24Summary.row(answers),
            PayStartDateGbPostApril24Summary.row(answers)
          ).flatten
        )

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(applicationDetails, relationshipDetails, personalDetails, babyDetails, paternityDetails)(request, implicitly).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
