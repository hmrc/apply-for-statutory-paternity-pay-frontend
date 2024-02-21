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

import models.auditing.DownloadAuditEvent.{ChildDetails, Eligibility}
import models.{CountryOfResidence, JourneyModel, Name, PaternityLeaveLengthGbPreApril24OrNi, RelationshipToChild}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

final case class DownloadAuditEvent(
                                     countryOfResidence: CountryOfResidence,
                                     eligibility: Eligibility,
                                     name: Name,
                                     nino: Nino,
                                     childDetails: ChildDetails,
                                     payStartDate: LocalDate,
                                     howLongWillYouBeOnLeave: PaternityLeaveLengthGbPreApril24OrNi
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

  sealed trait ChildDetails

  final case class BirthParentalOrderChild(
                                            dueDate: LocalDate,
                                            birthDate: Option[LocalDate]
                                          ) extends ChildDetails

  object BirthParentalOrderChild {
    implicit lazy val writes: Writes[BirthParentalOrderChild] = Json.writes
  }

  final case class AdoptedUkChild(
                                   matchedDate: LocalDate,
                                   hasBeenPlaced: Boolean,
                                   effectiveDate: LocalDate
                                 ) extends ChildDetails

  object AdoptedUkChild {
    implicit lazy val writes: Writes[AdoptedUkChild] = Json.writes
  }

  final case class AdoptedAbroadChild(
                                       notifiedDate: LocalDate,
                                       hasEnteredUk: Boolean,
                                       effectiveDate: LocalDate
                                     ) extends ChildDetails

  object AdoptedAbroadChild {
    implicit lazy val writes: Writes[AdoptedAbroadChild] = Json.writes
  }
  object ChildDetails {
    implicit lazy val writes: Writes[ChildDetails] = Writes[ChildDetails] {
      case x: BirthParentalOrderChild => Json.toJson(x)(BirthParentalOrderChild.writes)
      case x: AdoptedUkChild => Json.toJson(x)(AdoptedUkChild.writes)
      case x:AdoptedAbroadChild => Json.toJson(x)(AdoptedAbroadChild.writes)
    }
  }

  def from(model: JourneyModel): DownloadAuditEvent =
    DownloadAuditEvent(
      countryOfResidence = model.countryOfResidence,
      eligibility = getEligibility(model),
      name = model.name,
      nino = model.nino,
      getChildDetails(model.childDetails),
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

  private def getChildDetails(model: models.JourneyModel.ChildDetails): ChildDetails =
    model match {
      case x: models.JourneyModel.BirthParentalOrderChild =>
        BirthParentalOrderChild(x.dueDate, x.birthDate)

      case x: models.JourneyModel.AdoptedUkChild =>
        AdoptedUkChild(x.matchedDate, x.hasBeenPlaced, x.effectiveDate)

      case x: models.JourneyModel.AdoptedAbroadChild =>
        AdoptedAbroadChild(x.notifiedDate, x.hasEnteredUk, x.effectiveDate)
    }

  implicit lazy val writes: Writes[DownloadAuditEvent] = Json.writes[DownloadAuditEvent]
}