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
import forms.PayStartDateGbPostApril24FormProvider
import models.{NormalMode, PaternityReason, PayStartDateLimits, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.eclipse.jetty.util.resource.PathResource
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{IsAdoptingOrParentalOrderPage, PayStartDateGbPostApril24Page}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.PayStartDateGbPostApril24View
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages
import services.PayStartDateService

import scala.concurrent.Future

class PayStartDateGbPostApril24ControllerSpec extends SpecBase with MockitoSugar {

  private implicit val messages: Messages = stubMessages()
  private val minDate = LocalDate.now
  private val maxDate = LocalDate.now.plusDays(1)
  private val payStartDateLimits = PayStartDateLimits(minDate, maxDate)

  val formProvider = new PayStartDateGbPostApril24FormProvider()
  private def form = formProvider(payStartDateLimits)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = Some(LocalDate.now(ZoneOffset.UTC))

  lazy val payStartDateGbPostApril24Route = routes.PayStartDateGbPostApril24Controller.onPageLoad(NormalMode).url

  val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, payStartDateGbPostApril24Route)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, payStartDateGbPostApril24Route)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.value.getDayOfMonth.toString,
        "value.month" -> validAnswer.value.getMonthValue.toString,
        "value.year"  -> validAnswer.value.getYear.toString
      )

  "PayStartDateGbPostApril24 Controller" - {

    "must return OK and the correct view for a GET" in {

      val mockPayStartDateService = mock[PayStartDateService]

      when(mockPayStartDateService.gbPostApril24Dates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()

      running(application) {
        val result = route(application, getRequest).value

        val view = application.injector.instanceOf[PayStartDateGbPostApril24View]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, PaternityReason.PaternityFromBirth)(getRequest, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = answers.set(PayStartDateGbPostApril24Page, validAnswer).success.value

      val mockPayStartDateService = mock[PayStartDateService]

      when(mockPayStartDateService.gbPostApril24Dates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()

      running(application) {
        val view = application.injector.instanceOf[PayStartDateGbPostApril24View]

        val result = route(application, getRequest).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode, PaternityReason.PaternityFromBirth)(getRequest, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]
      val mockPayStartDateService = mock[PayStartDateService]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockPayStartDateService.gbPostApril24Dates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = Some(answers))
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

      when(mockPayStartDateService.gbPostApril24Dates(any())) thenReturn Right(payStartDateLimits)

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(bind[PayStartDateService].toInstance(mockPayStartDateService))
          .build()

      val request =
        FakeRequest(POST, payStartDateGbPostApril24Route)
          .withFormUrlEncodedBody(("value", "invalid value"))

      running(application) {
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[PayStartDateGbPostApril24View]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, PaternityReason.PaternityFromBirth)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = route(application, getRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val result = route(application, postRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
