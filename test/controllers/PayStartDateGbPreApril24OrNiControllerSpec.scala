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

import java.time.{LocalDate, ZoneOffset}
import base.SpecBase
import cats.data.NonEmptyChain
import forms.PayStartDateGbPreApril24OrNiFormProvider
import models.{NormalMode, PayStartDateLimits, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{BabyHasBeenBornPage, IsAdoptingOrParentalOrderPage, PayStartDateGbPreApril24OrNiPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.PayStartDateGbPreApril24OrNiView
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages
import services.PayStartDateService

import scala.concurrent.Future

class PayStartDateGbPreApril24OrNiControllerSpec extends SpecBase with MockitoSugar {

  private implicit val messages: Messages = stubMessages()
  private val minDate = LocalDate.now
  private val maxDate = LocalDate.now.plusDays(1)
  private val payStartDateLimits = PayStartDateLimits(minDate, maxDate)

  val formProvider = new PayStartDateGbPreApril24OrNiFormProvider()
  private def form = formProvider(payStartDateLimits)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val payStartDateGbPreApril24OrNiRoute = routes.PayStartDateGbPreApril24OrNiController.onPageLoad(NormalMode).url

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, payStartDateGbPreApril24OrNiRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, payStartDateGbPreApril24OrNiRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "PayStartDateGbPreApril24OrNi Controller" - {

    "must return OK and the correct view for a GET" - {
      
      "when the birth or parental order child has been born" in {

        val answers = emptyUserAnswers.set(BabyHasBeenBornPage, true).success.value
        val mockPayStartDateService = mock[PayStartDateService]

        when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Right(payStartDateLimits)

        val application =
          applicationBuilder(userAnswers = Some(answers))
            .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
            .build()

        running(application) {
          val result = route(application, getRequest).value

          val view = application.injector.instanceOf[PayStartDateGbPreApril24OrNiView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, NormalMode, showBabyNotBornHint = false, payStartDateLimits)(getRequest, messages(application)).toString
        }
      }
      
      "when a birth or parental order child has not yet been born" in {

        val answers = emptyUserAnswers.set(BabyHasBeenBornPage, false).success.value
        val mockPayStartDateService = mock[PayStartDateService]

        when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Right(payStartDateLimits)

        val application =
          applicationBuilder(userAnswers = Some(answers))
            .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
            .build()

        running(application) {
          val result = route(application, getRequest).value

          val view = application.injector.instanceOf[PayStartDateGbPreApril24OrNiView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, NormalMode, showBabyNotBornHint = true, payStartDateLimits)(getRequest, messages(application)).toString
        }
      }
      
      "when the child is not a birth or parental order child" in {

        val mockPayStartDateService = mock[PayStartDateService]

        when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Right(payStartDateLimits)

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
            .build()

        running(application) {
          val result = route(application, getRequest).value

          val view = application.injector.instanceOf[PayStartDateGbPreApril24OrNiView]

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(form, NormalMode, showBabyNotBornHint = false, payStartDateLimits)(getRequest, messages(application)).toString
        }
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(PayStartDateGbPreApril24OrNiPage, validAnswer).success.value

      val mockPayStartDateService = mock[PayStartDateService]

      when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()

      running(application) {
        val view = application.injector.instanceOf[PayStartDateGbPreApril24OrNiView]

        val result = route(application, getRequest).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode, false, payStartDateLimits)(getRequest, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]
      val mockPayStartDateService = mock[PayStartDateService]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[PayStartDateService].toInstance(mockPayStartDateService)
          )
          .build()

      running(application) {
        val result = route(application, postRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val mockPayStartDateService = mock[PayStartDateService]

      when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()

      val request =
        FakeRequest(POST, payStartDateGbPreApril24OrNiRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      running(application) {
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[PayStartDateGbPreApril24OrNiView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, showBabyNotBornHint = false, payStartDateLimits)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val mockPayStartDateService = mock[PayStartDateService]

      when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()


      running(application) {
        val result = route(application, getRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if pay start date limits cannot be calculated" in {

      val mockPayStartDateService = mock[PayStartDateService]

      when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Left(NonEmptyChain(IsAdoptingOrParentalOrderPage))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()

      running(application) {
        val result = route(application, getRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val mockPayStartDateService = mock[PayStartDateService]

      when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()


      running(application) {
        val result = route(application, postRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if pay start date limits cannot be calculated" in {

      val mockPayStartDateService = mock[PayStartDateService]

      when(mockPayStartDateService.gbPreApril24OrNiDates(any())) thenReturn Left(NonEmptyChain(IsAdoptingOrParentalOrderPage))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()

      running(application) {
        val result = route(application, postRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
