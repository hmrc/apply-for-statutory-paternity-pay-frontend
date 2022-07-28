/*
 * Copyright 2022 HM Revenue & Customs
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
import com.dmanchester.playfop.sapi.PlayFop
import models.{JourneyModel, Name, NormalMode, PaternityLeaveLength}
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.EitherValues
import org.scalatestplus.mockito.MockitoSugar
import pages.{BabyDateOfBirthPage, BabyDueDatePage, BabyHasBeenBornPage, IsAdoptingPage, IsBiologicalFatherPage, NamePage, NinoPage, PaternityLeaveLengthPage, WantPayToStartOnBirthDatePage, WillHaveCaringResponsibilityPage, WillTakeTimeToCareForChildPage}
import play.api.http.HeaderNames
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.auditing.AuditService
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

class PrintControllerSpec extends SpecBase with EitherValues with MockitoSugar {

  val birthDate = LocalDate.now.minusDays(1)
  val dueDate = LocalDate.now.minusDays(2)

  val answers = emptyUserAnswers
    .set(IsAdoptingPage, false).success.value
    .set(IsBiologicalFatherPage, true).success.value
    .set(WillHaveCaringResponsibilityPage, true).success.value
    .set(WillTakeTimeToCareForChildPage, true).success.value
    .set(NamePage, Name("foo", "bar")).success.value
    .set(NinoPage, Nino("AA123456A")).success.value
    .set(BabyHasBeenBornPage, true).success.value
    .set(BabyDueDatePage, dueDate).success.value
    .set(BabyDateOfBirthPage, birthDate).success.value
    .set(WantPayToStartOnBirthDatePage, true).success.value
    .set(PaternityLeaveLengthPage, PaternityLeaveLength.Oneweek).success.value

  val model = JourneyModel.from(answers).right.value

  "print form" - {

    "must return OK and the correct view" in {
      val mockAuditService = mock[AuditService]
      val mockPlayFop = mock[PlayFop]
      val application = applicationBuilder(userAnswers = Some(answers))
        .overrides(
          bind[AuditService].toInstance(mockAuditService),
          bind[PlayFop].toInstance(mockPlayFop)
        )
        .build()
      when(mockPlayFop.processTwirlXml(any(), any(), any(), any())).thenReturn("hello".getBytes)
      running(application) {
        val request = FakeRequest(GET, routes.PrintController.onDownload.url)
        val result = route(application, request).value
        verify(mockAuditService, times(1)).auditDownload(eqTo(model))(any())
        status(result) mustEqual OK
        contentAsString(result) mustEqual "hello"
        header(HeaderNames.CONTENT_DISPOSITION, result).value mustEqual "attachment; filename=apply-for-statutory-paternity-pay-sc3.pdf"
      }
    }

    "must redirect to the page with the first missing piece of information that is required when the user has incomplete answers" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
      running(application) {
        val request = FakeRequest(GET, routes.PrintController.onDownload.url)
        val result = route(application, request).value
        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.IsAdoptingController.onPageLoad(NormalMode).url
      }
    }
  }
}