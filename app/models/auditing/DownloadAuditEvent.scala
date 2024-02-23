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

import models.auditing.DownloadAuditEvent.{ChildDetails, Eligibility, PaternityLeaveDetails}
import models.{CountryOfResidence, JourneyModel, Name, PaternityLeaveLengthGbPreApril24OrNi, RelationshipToChild}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

final case class DownloadAuditEvent(
                                     countryOfResidence: CountryOfResidence,
                                     eligibility: Eligibility,
                                     name: Name,
                                     nino: Nino,
                                     childDetails: ChildDetails,
                                     paternityLeaveDetails: PaternityLeaveDetails
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
    implicit lazy val writes: OWrites[BirthChildEligibility] = Json.writes[BirthChildEligibility]
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
    implicit lazy val writes: OWrites[AdoptionParentalOrderEligibility] = Json.writes[AdoptionParentalOrderEligibility]
  }

  object Eligibility {
    implicit lazy val writes: OWrites[Eligibility] = OWrites[Eligibility] {
      case x: BirthChildEligibility => Json.toJsObject(x)(BirthChildEligibility.writes)
      case x: AdoptionParentalOrderEligibility => Json.toJsObject(x)(AdoptionParentalOrderEligibility.writes)
    }
  }

  sealed trait ChildDetails

  final case class BirthParentalOrderChild(
                                            dueDate: LocalDate,
                                            birthDate: Option[LocalDate]
                                          ) extends ChildDetails

  object BirthParentalOrderChild {
    implicit lazy val writes: OWrites[BirthParentalOrderChild] = Json.writes
  }

  final case class AdoptedUkChild(
                                   matchedDate: LocalDate,
                                   hasBeenPlaced: Boolean,
                                   effectiveDate: LocalDate
                                 ) extends ChildDetails

  object AdoptedUkChild {
    implicit lazy val writes: OWrites[AdoptedUkChild] = Json.writes
  }

  final case class AdoptedAbroadChild(
                                       notifiedDate: LocalDate,
                                       hasEnteredUk: Boolean,
                                       effectiveDate: LocalDate
                                     ) extends ChildDetails

  object AdoptedAbroadChild {
    implicit lazy val writes: OWrites[AdoptedAbroadChild] = Json.writes
  }

  object ChildDetails {
    implicit lazy val writes: OWrites[ChildDetails] = OWrites[ChildDetails] {
      case x: BirthParentalOrderChild => Json.toJsObject(x)(BirthParentalOrderChild.writes)
      case x: AdoptedUkChild => Json.toJsObject(x)(AdoptedUkChild.writes)
      case x:AdoptedAbroadChild => Json.toJsObject(x)(AdoptedAbroadChild.writes)
    }
  }

  sealed trait PaternityLeaveDetails

  final case class PaternityLeaveGbPreApril24OrNi(
                                                   leaveLength: PaternityLeaveLengthGbPreApril24OrNi,
                                                   payStartDate: LocalDate
                                                 ) extends PaternityLeaveDetails

  object PaternityLeaveGbPreApril24OrNi {
    implicit lazy val writes: OWrites[PaternityLeaveGbPreApril24OrNi] = Json.writes
  }

  final case class PaternityLeaveGbPostApril24OneWeek(
                                                       payStartDate: Option[LocalDate]
                                                     ) extends PaternityLeaveDetails

  object PaternityLeaveGbPostApril24OneWeek {

    implicit lazy val writes: OWrites[PaternityLeaveGbPostApril24OneWeek] =
      (
        (__ \ "payStartDate").writeNullable[LocalDate] and
        (__ \ "leaveLength").write(Writes.pure("oneWeek"))
      )(x => (x.payStartDate, JsNull))
  }

  final case class PaternityLeaveGbPostApril24TwoWeeksTogether(
                                                                payStartDate: Option[LocalDate]
                                                              ) extends PaternityLeaveDetails

  object PaternityLeaveGbPostApril24TwoWeeksTogether {

    implicit lazy val writes: OWrites[PaternityLeaveGbPostApril24TwoWeeksTogether] =
      (
        (__ \ "payStartDate").writeNullable[LocalDate] and
        (__ \ "leaveLength").write(Writes.pure("twoWeeks")) and
        (__ \ "takenTogetherOrSeparately").write(Writes.pure("together"))
      )(x => (x.payStartDate, JsNull, JsNull))
  }

  final case class PaternityLeaveGbPostApril24TwoWeeksSeparate(
                                                                week1StartDate: Option[LocalDate],
                                                                week2StartDate: Option[LocalDate]
                                                              ) extends PaternityLeaveDetails

  object PaternityLeaveGbPostApril24TwoWeeksSeparate {

    implicit lazy val writes: OWrites[PaternityLeaveGbPostApril24TwoWeeksSeparate] =
      (
        (__ \ "week1StartDate").writeNullable[LocalDate] and
        (__ \ "week2StartDate").writeNullable[LocalDate] and
        (__ \ "leaveLength").write(Writes.pure("twoWeeks")) and
        (__ \ "takenTogetherOrSeparately").write(Writes.pure("separately"))
      )(x => (x.week1StartDate, x.week2StartDate, JsNull, JsNull))

  }

  case object PaternityLeaveGbPostApril24Unsure extends PaternityLeaveDetails

  object PaternityLeaveDetails {

    implicit lazy val writes: OWrites[PaternityLeaveDetails] = OWrites {
      case x: PaternityLeaveGbPreApril24OrNi => Json.toJsObject(x)(PaternityLeaveGbPreApril24OrNi.writes)
      case x: PaternityLeaveGbPostApril24OneWeek => Json.toJsObject(x)(PaternityLeaveGbPostApril24OneWeek.writes)
      case x: PaternityLeaveGbPostApril24TwoWeeksTogether => Json.toJsObject(x)(PaternityLeaveGbPostApril24TwoWeeksTogether.writes)
      case x: PaternityLeaveGbPostApril24TwoWeeksSeparate => Json.toJsObject(x)(PaternityLeaveGbPostApril24TwoWeeksSeparate.writes)
      case PaternityLeaveGbPostApril24Unsure => Json.obj("leaveLength" -> "unsure")
    }
  }

  def from(model: JourneyModel): DownloadAuditEvent =
    DownloadAuditEvent(
      countryOfResidence = model.countryOfResidence,
      eligibility = getEligibility(model),
      name = model.name,
      nino = model.nino,
      getChildDetails(model.childDetails),
      paternityLeaveDetails = getPaternityDetails(model.paternityLeaveDetails)
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

  private def getPaternityDetails(model: models.JourneyModel.PaternityLeaveDetails): PaternityLeaveDetails =
    model match {
      case x: models.JourneyModel.PaternityLeaveGbPreApril24OrNi =>
        PaternityLeaveGbPreApril24OrNi(x.leaveLength, x.payStartDate)

      case x: models.JourneyModel.PaternityLeaveGbPostApril24OneWeek =>
        PaternityLeaveGbPostApril24OneWeek(x.payStartDate)

      case x: models.JourneyModel.PaternityLeaveGbPostApril24TwoWeeksTogether =>
        PaternityLeaveGbPostApril24TwoWeeksTogether(x.payStartDate)

      case x: models.JourneyModel.PaternityLeaveGbPostApril24TwoWeeksSeparate =>
        PaternityLeaveGbPostApril24TwoWeeksSeparate(x.week1StartDate, x.week2StartDate)

      case models.JourneyModel.PaternityLeaveGbPostApril24Unsure =>
        PaternityLeaveGbPostApril24Unsure
    }

  implicit lazy val writes: OWrites[DownloadAuditEvent] = Json.writes[DownloadAuditEvent]
}