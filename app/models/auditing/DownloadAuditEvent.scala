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

package models.auditing

import models.auditing.DownloadAuditEvent.{BirthDetails, Eligibility}
import models.{JourneyModel, Name, PaternityLeaveLength}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

final case class DownloadAuditEvent(
                                     eligibility: Eligibility,
                                     name: Name,
                                     nino: Nino,
                                     hasTheBabyBeenBorn: Boolean,
                                     dueDate: LocalDate,
                                     birthDetails: BirthDetails,
                                     payStartDate: Option[LocalDate],
                                     howLongWillYouBeOnLeave: PaternityLeaveLength
                                   ) {

}

object DownloadAuditEvent {

  final case class Eligibility(
                                becomingAdoptiveParents: Boolean,
                                biologicalFather: Boolean,
                                inRelationshipWithMother: Option[Boolean],
                                livingWithMother: Option[Boolean],
                                responsibilityForChild: Boolean,
                                timeOffToCareForChild: Boolean,
                                timeOffToSupportMother: Option[Boolean]
                              )

  object Eligibility {
    implicit lazy val writes: Writes[Eligibility] = Json.writes[Eligibility]
  }

  sealed abstract class BirthDetails

  object BirthDetails {

    final case class AlreadyBorn(
                                  birthDate: LocalDate,
                                  payShouldStartFromBirthDay: Boolean
                                ) extends BirthDetails

    final case class Due(
                          payShouldStartFromDueDate: Boolean
                        ) extends BirthDetails

    private[auditing] def from(model: JourneyModel.BirthDetails): BirthDetails =
      model match {
        case JourneyModel.BirthDetails.AlreadyBorn(birthDate, payShouldStartFromBirthDay) =>
          AlreadyBorn(birthDate, payShouldStartFromBirthDay)
        case JourneyModel.BirthDetails.Due(payShouldStartFromDueDate) =>
          Due(payShouldStartFromDueDate)
      }

    implicit lazy val writes: Writes[BirthDetails] = Writes {
      case ab: AlreadyBorn => Json.toJson(ab)(Json.writes)
      case d: Due => Json.toJson(d)(Json.writes)
    }
  }

  def from(model: JourneyModel): DownloadAuditEvent =
    DownloadAuditEvent(
      eligibility = Eligibility(
        becomingAdoptiveParents = model.eligibility.becomingAdoptiveParents,
        biologicalFather = model.eligibility.biologicalFather,
        inRelationshipWithMother = model.eligibility.inRelationshipWithMother,
        livingWithMother = model.eligibility.livingWithMother,
        responsibilityForChild = model.eligibility.responsibilityForChild,
        timeOffToCareForChild = model.eligibility.timeOffToCareForChild,
        timeOffToSupportMother = model.eligibility.timeOffToSupportMother
      ),
      name = model.name,
      nino = model.nino,
      dueDate = model.dueDate,
      hasTheBabyBeenBorn = model.hasTheBabyBeenBorn,
      birthDetails = BirthDetails.from(model.birthDetails),
      payStartDate = model.payStartDate,
      howLongWillYouBeOnLeave = model.howLongWillYouBeOnLeave
    )

  implicit lazy val writes: Writes[DownloadAuditEvent] = Json.writes[DownloadAuditEvent]
}