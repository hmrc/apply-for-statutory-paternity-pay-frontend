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

package services.auditing

import generators.ModelGenerators
import models.{CountryOfResidence, JourneyModel, Name, PaternityLeaveLengthGbPreApril24OrNi}
import models.auditing.DownloadAuditEvent
import models.auditing.DownloadAuditEvent.BirthParentalOrderChild
import models.auditing.DownloadAuditEvent.PaternityLeaveGbPostApril24Unsure
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global

class AuditServiceSpec extends AnyFreeSpec with Matchers with MockitoSugar with ModelGenerators {

  val nino = arbitrary[Nino].sample.get
  val mockAuditConnector: AuditConnector = mock[AuditConnector]
  val configuration: Configuration = Configuration(
    "auditing.downloadEventName" -> "downloadAuditEvent"
  )
  val service = new AuditService(mockAuditConnector, configuration)

  "auditDownload" - {

    "must call the audit connector with the correct payload" in {

      val birthDate = LocalDate.now
      val dueDate = LocalDate.now.minusDays(1)

      val model: JourneyModel = JourneyModel(
        countryOfResidence = CountryOfResidence.England,
        eligibility = JourneyModel.BirthChildEligibility(
          biologicalFather = true,
          inRelationshipWithMother = None,
          livingWithMother = None,
          responsibilityForChild = true,
          timeOffToCareForChild = true,
          timeOffToSupportPartner = None
        ),
        name = Name("foo", "bar"),
        nino = nino,
        childDetails = JourneyModel.BirthParentalOrderChild(birthDate, Some(dueDate)),
        paternityLeaveDetails = JourneyModel.PaternityLeaveGbPostApril24Unsure
      )

      val expected: DownloadAuditEvent = DownloadAuditEvent(
        countryOfResidence = CountryOfResidence.England,
        eligibility = DownloadAuditEvent.BirthChildEligibility(
          biologicalFather = true,
          inRelationshipWithMother = None,
          livingWithMother = None,
          responsibilityForChild = true,
          timeOffToCareForChild = true,
          timeOffToSupportPartner = None
        ),
        name = Name("foo", "bar"),
        nino = nino,
        childDetails = BirthParentalOrderChild(birthDate, Some(dueDate)),
        paternityLeaveDetails = PaternityLeaveGbPostApril24Unsure
      )

      val hc = HeaderCarrier()
      service.auditDownload(model)(hc)

      verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("downloadAuditEvent"), eqTo(expected))(eqTo(hc), any(), any())
    }
  }
}
