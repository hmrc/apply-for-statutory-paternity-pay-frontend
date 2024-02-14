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

package models.auditing

import models.auditing.DownloadAuditEvent.Eligibility
import models.{CountryOfResidence, JourneyModel, Name, PaternityLeaveLength, RelationshipToChild}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

final case class DownloadAuditEvent(
                                     countryOfResidence: CountryOfResidence,
                                     eligibility: Eligibility,
                                     name: Name,
                                     nino: Nino,
                                     hasTheBabyBeenBorn: Boolean,
                                     dueDate: LocalDate,
                                     birthDate: Option[LocalDate],
                                     payStartDate: LocalDate,
                                     howLongWillYouBeOnLeave: PaternityLeaveLength
                                   ) {

}

object DownloadAuditEvent {
  sealed trait Eligibility

  final case class BirthChildEligibility(
                                          biologicalFather: Boolean,
                                          inRelationshipWithMother: Option[Boolean],
                                          livingWithMother: Option[Boolean],
                                          responsibilityForChild: Boolean,
                                          timeOffToCareForChild: Boolean,
                                          timeOffToSupportPartner: Option[Boolean]
                                        ) extends Eligibility

  object BirthChildEligibility {
    implicit lazy val writes: Writes[BirthChildEligibility] = Json.writes[BirthChildEligibility]
  }

  final case class AdoptionParentalOrderEligibility(
                                                     applyingForStatutoryAdoptionPay: Boolean,
                                                     adoptingFromAbroad: Boolean,
                                                     reasonForRequesting: RelationshipToChild,
                                                     inQualifyingRelationship: Boolean,
                                                     livingWithPartner: Option[Boolean],
                                                     responsibilityForChild: Boolean,
                                                     timeOffToCareForChild: Boolean,
                                                     timeOffToSupportPartner: Option[Boolean]
                                                   ) extends Eligibility

  object AdoptionParentalOrderEligibility {
    implicit lazy val writes: Writes[AdoptionParentalOrderEligibility] = Json.writes[AdoptionParentalOrderEligibility]
  }

  object Eligibility {
    implicit lazy val writes: Writes[Eligibility] = Writes[Eligibility] {
      case x: BirthChildEligibility => Json.toJson(x)(BirthChildEligibility.writes)
      case x: AdoptionParentalOrderEligibility => Json.toJson(x)(AdoptionParentalOrderEligibility.writes)
    }
  }

  def from(model: JourneyModel): DownloadAuditEvent =
    DownloadAuditEvent(
      countryOfResidence = model.countryOfResidence,
      eligibility = getEligibility(model),
      name = model.name,
      nino = model.nino,
      dueDate = model.dueDate,
      hasTheBabyBeenBorn = model.hasTheBabyBeenBorn,
      birthDate = model.birthDate,
      payStartDate = model.payStartDate,
      howLongWillYouBeOnLeave = model.howLongWillYouBeOnLeave
    )

  private def getEligibility(model: JourneyModel): Eligibility =
    model.eligibility match {
      case e: models.JourneyModel.BirthChildEligibility =>
        BirthChildEligibility(
          biologicalFather = e.biologicalFather,
          inRelationshipWithMother = e.inRelationshipWithMother,
          livingWithMother = e.livingWithMother,
          responsibilityForChild = e.responsibilityForChild,
          timeOffToCareForChild = e.timeOffToCareForChild,
          timeOffToSupportPartner = e.timeOffToSupportPartner
        )

      case e: models.JourneyModel.AdoptionParentalOrderEligibility =>
        AdoptionParentalOrderEligibility(
          applyingForStatutoryAdoptionPay = e.applyingForStatutoryAdoptionPay,
          adoptingFromAbroad = e.adoptingFromAbroad,
          reasonForRequesting = e.reasonForRequesting,
          inQualifyingRelationship = e.inQualifyingRelationship,
          livingWithPartner = e.livingWithPartner,
          responsibilityForChild = e.responsibilityForChild,
          timeOffToCareForChild = e.timeOffToCareForChild,
          timeOffToSupportPartner = e.timeOffToSupportPartner
        )
    }

  implicit lazy val writes: Writes[DownloadAuditEvent] = Json.writes[DownloadAuditEvent]
}