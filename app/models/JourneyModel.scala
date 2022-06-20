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

package models

import cats.data.EitherNec
import cats.implicits._
import models.JourneyModel.{BirthDetails, Eligibility}
import pages._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

final case class JourneyModel(
                               eligibility: Eligibility,
                               name: Name,
                               nino: Nino,
                               hasTheBabyBeenBorn: Boolean,
                               birthDetails: BirthDetails,
                               howLongWillYouBeOnLeave: PaternityLeaveLength
                             ) {

}

object JourneyModel {

  final case class Eligibility(
                                becomingAdoptiveParents: Boolean,
                                biologicalFather: Boolean,
                                inRelationshipWithMother: Option[Boolean],
                                livingWithMother: Option[Boolean],
                                responsibilityForChild: Boolean,
                                timeOffToCareForChild: Boolean,
                                timeOffToSupportMother: Option[Boolean]
                              )

  sealed abstract class BirthDetails {
    def payStartDate: Option[LocalDate]
    def resolvedStartDate: LocalDate
  }

  object BirthDetails {

    final case class AlreadyBorn(
                                  birthDate: LocalDate,
                                  payShouldStartFromBirthDay: Boolean,
                                  payStartDate: Option[LocalDate]
                                ) extends BirthDetails {

      override def resolvedStartDate: LocalDate =
        if (payShouldStartFromBirthDay) birthDate else payStartDate.getOrElse(throw new IllegalStateException("Invalid data given"))
    }

    final case class Due(
                          dueDate: LocalDate,
                          payShouldStartFromDueDate: Boolean,
                          payStartDate: Option[LocalDate]
                        ) extends BirthDetails {

      override def resolvedStartDate: LocalDate =
        if (payShouldStartFromDueDate) dueDate else payStartDate.getOrElse(throw new IllegalStateException("Invalid data given"))
    }
  }

  def from(answers: UserAnswers): EitherNec[Page, JourneyModel] =
    (
      getEligibility(answers),
      answers.getEither(NamePage),
      answers.getEither(NinoPage),
      for {
        alreadyBorn  <- answers.getEither(BabyHasBeenBornPage)
        birthDetails <- getBirthDetails(answers, alreadyBorn)
      } yield (alreadyBorn, birthDetails),
      answers.getEither(PaternityLeaveLengthPage)
    ).parMapN { case (eligibility, name, nino, (alreadyBorn, birthDetails), paternityLeaveLength) =>
      JourneyModel(eligibility, name, nino, alreadyBorn, birthDetails, paternityLeaveLength)
    }

  private def getEligibility(answers: UserAnswers): EitherNec[Page, Eligibility] =
    (
      getIsAdopting(answers),
      getRelationshipEligibility(answers),
      getWillHaveCaringResponsibility(answers),
      getTimeEligibility(answers)
    ).parMapN { case (isAdopting, (isBiologicalFather, isInQualifyingRelationship, isCohabiting), caringResponsibility, (careForChild, supportMother)) =>
      Eligibility(isAdopting, isBiologicalFather, isInQualifyingRelationship, isCohabiting, caringResponsibility, careForChild, supportMother)
    }

  private def getIsAdopting(answers: UserAnswers): EitherNec[Page, Boolean] =
    answers.getEither(IsAdoptingPage).flatMap {
      case true => CannotApplyAdoptingPage.leftNec
      case false => Right(false)
    }

  private def getRelationshipEligibility(answers: UserAnswers): EitherNec[Page, (Boolean, Option[Boolean], Option[Boolean])] =
    answers.getEither(IsBiologicalFatherPage).flatMap {
      case false => answers.getEither(IsInQualifyingRelationshipPage).flatMap {
        case false => answers.getEither(IsCohabitingPage).flatMap {
          case false => CannotApplyPage.leftNec
          case true => Right((false, Some(false), Some(true)))
        }
        case true => Right((false, Some(true), None))
      }
      case true => Right((true, None, None))
    }

  private def getWillHaveCaringResponsibility(answers: UserAnswers): EitherNec[Page, Boolean] =
    answers.getEither(WillHaveCaringResponsibilityPage).flatMap {
      case true => Right(true)
      case false => CannotApplyPage.leftNec
    }

  private def getTimeEligibility(answers: UserAnswers): EitherNec[Page, (Boolean, Option[Boolean])] =
    answers.getEither(WillTakeTimeToCareForChildPage).flatMap {
      case false => answers.getEither(WillTakeTimeToSupportMotherPage).flatMap {
        case true => Right((false, Some(true)))
        case false => CannotApplyPage.leftNec
      }
      case true => Right((true, None))
    }

  private def getBirthDetails(answers: UserAnswers, alreadyBorn: Boolean): EitherNec[Page, BirthDetails] =
    if (alreadyBorn) getAlreadyBornBirthDetails(answers) else getDueBirthDetails(answers)

  private def getAlreadyBornBirthDetails(answers: UserAnswers): EitherNec[Page, BirthDetails.AlreadyBorn] =
    (
      answers.getEither(BabyDateOfBirthPage),
      for {
        payShouldStartFromBirthDate <- answers.getEither(WantPayToStartOnBirthDatePage)
        paymentStartDate            <- if (payShouldStartFromBirthDate) Right(None) else answers.getEither(PayStartDateBabyBornPage).map(Some(_))
      } yield (payShouldStartFromBirthDate, paymentStartDate)
    ).parMapN { case (dateOfBirth, (payShouldStartFromBirthDate, paymentStartDate)) =>
      BirthDetails.AlreadyBorn(dateOfBirth, payShouldStartFromBirthDate, paymentStartDate)
    }

  private def getDueBirthDetails(answers: UserAnswers): EitherNec[Page, BirthDetails.Due] =
    (
      answers.getEither(BabyDueDatePage),
      for {
        payShouldStartFromDueDate <- answers.getEither(WantPayToStartOnDueDatePage)
        paymentStartDate          <- if (payShouldStartFromDueDate) Right(None) else answers.getEither(PayStartDateBabyDuePage).map(Some(_))
      } yield (payShouldStartFromDueDate, paymentStartDate)
    ).parMapN { case (dueDate, (payShouldStartFromDueDate, paymentStartDate)) =>
      BirthDetails.Due(dueDate, payShouldStartFromDueDate, paymentStartDate)
    }
}
