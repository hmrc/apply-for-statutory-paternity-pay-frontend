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
import generators.ModelGenerators
import models.{CountryOfResidence, LeaveTakenTogetherOrSeparately, Name, PaternityLeaveLengthGbPostApril24, PaternityLeaveLengthGbPreApril24OrNi}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.domain.Nino
import views.html.ConfirmationView
import org.scalacheck.Arbitrary.arbitrary

import java.time.LocalDate
import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBase with MockitoSugar with ModelGenerators {

  "Confirmation Controller" - {

    "must return OK and the correct view for a GET" - {

      "for paternity leave pre-April 24" in {
        val birthDate = LocalDate.of(2024, 3, 31)

        val answers =
          emptyUserAnswers
            .set(CountryOfResidencePage, CountryOfResidence.England).success.value
            .set(BabyDateOfBirthPage, birthDate).success.value
            .set(BabyDueDatePage, birthDate).success.value
            .set(BabyHasBeenBornPage, true).success.value
            .set(IsAdoptingOrParentalOrderPage, false).success.value
            .set(IsBiologicalFatherPage, true).success.value
            .set(IsCohabitingPage, true).success.value
            .set(IsInQualifyingRelationshipPage, true).success.value
            .set(NamePage, Name("first", "last")).success.value
            .set(NinoPage, arbitrary[Nino].sample.value).success.value
            .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
            .set(PayStartDateGbPreApril24OrNiPage, birthDate).success.value
            .set(WillHaveCaringResponsibilityPage, true).success.value
            .set(WillTakeTimeToCareForChildPage, true).success.value
            .set(WillTakeTimeToSupportPartnerPage, true).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        running(application) {
          val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[ConfirmationView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(preApril24OrNorthernIreland = true)(request, messages(application)).toString
        }
      }

      "for paternity leave post-April 24" in {
        val answers =
          emptyUserAnswers
            .set(CountryOfResidencePage, CountryOfResidence.England).success.value
            .set(BabyDateOfBirthPage, LocalDate.now).success.value
            .set(BabyDueDatePage, LocalDate.now).success.value
            .set(BabyHasBeenBornPage, false).success.value
            .set(IsAdoptingOrParentalOrderPage, false).success.value
            .set(IsBiologicalFatherPage, true).success.value
            .set(IsCohabitingPage, true).success.value
            .set(IsInQualifyingRelationshipPage, true).success.value
            .set(NamePage, Name("first", "last")).success.value
            .set(NinoPage, arbitrary[Nino].sample.value).success.value
            .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value
            .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
            .set(PayStartDateGbPostApril24Page, Some(LocalDate.now())).success.value
            .set(WillHaveCaringResponsibilityPage, true).success.value
            .set(WillTakeTimeToCareForChildPage, true).success.value
            .set(WillTakeTimeToSupportPartnerPage, true).success.value

        val application = applicationBuilder(userAnswers = Some(answers)).build()

        running(application) {
          val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad().url)

          val result = route(application, request).value

          val view = application.injector.instanceOf[ConfirmationView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(preApril24OrNorthernIreland = false)(request, messages(application)).toString
        }
      }
    }

    "start again must clear user answers and redirect to Index" in {

      val mockSessionRepository = mock[SessionRepository]
      when(mockSessionRepository.clear(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(None)
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

      running(application) {

        val request = FakeRequest(GET, routes.ConfirmationController.startAgain().url)

        val result = route(application, request).value

        val expectedRedirectUrl = routes.IndexController.onPageLoad.url

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual expectedRedirectUrl
        verify(mockSessionRepository, times(1)).clear(eqTo(userAnswersId))
      }
    }
  }
}
