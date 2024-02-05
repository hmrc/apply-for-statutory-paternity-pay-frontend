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
                               dueDate: LocalDate,
                               birthDetails: BirthDetails,
                               payStartDate: Option[LocalDate],
                               howLongWillYouBeOnLeave: PaternityLeaveLength
                             ) {

  def resolvedStartDate: LocalDate =
    birthDetails match {
      case BirthDetails.AlreadyBorn(birthDate, true) => birthDate
      case BirthDetails.Due(true)                    => dueDate
      case _                                         => payStartDate.getOrElse(throw new IllegalStateException("Invalid data given"))
    }
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

  sealed abstract class BirthDetails

  object BirthDetails {

    final case class AlreadyBorn(
                                  birthDate: LocalDate,
                                  payShouldStartFromBirthDay: Boolean
                                ) extends BirthDetails

    final case class Due(
                          payShouldStartFromDueDate: Boolean
                        ) extends BirthDetails
  }

  def from(answers: UserAnswers): EitherNec[QuestionPage[_], JourneyModel] =
    (
      getEligibility(answers),
      answers.getEither(NamePage),
      answers.getEither(NinoPage),
      answers.getEither(BabyDueDatePage),
      for {
        alreadyBorn  <- answers.getEither(BabyHasBeenBornPage)
        birthDetails <- getBirthDetails(answers, alreadyBorn)
        payStartDate <- getPayStartDate(answers, alreadyBorn)
      } yield (alreadyBorn, birthDetails, payStartDate),
      answers.getEither(PaternityLeaveLengthPage),
    ).parMapN { case (eligibility, name, nino, dueDate, (alreadyBorn, birthDetails, payStartDate), paternityLeaveLength) =>
      JourneyModel(eligibility, name, nino, alreadyBorn, dueDate, birthDetails, payStartDate, paternityLeaveLength)
    }

  private def getEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], Eligibility] =
    (
      getIsAdopting(answers),
      getRelationshipEligibility(answers),
      getWillHaveCaringResponsibility(answers),
      getTimeEligibility(answers)
    ).parMapN { case (isAdopting, (isBiologicalFather, isInQualifyingRelationship, isCohabiting), caringResponsibility, (careForChild, supportMother)) =>
      Eligibility(isAdopting, isBiologicalFather, isInQualifyingRelationship, isCohabiting, caringResponsibility, careForChild, supportMother)
    }

  private def getIsAdopting(answers: UserAnswers): EitherNec[QuestionPage[_], Boolean] =
    answers.getEither(IsAdoptingPage).flatMap {
      case true => IsAdoptingPage.leftNec
      case false => Right(false)
    }

  private def getRelationshipEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], (Boolean, Option[Boolean], Option[Boolean])] =
    answers.getEither(IsBiologicalFatherPage).flatMap {
      case false => answers.getEither(IsInQualifyingRelationshipPage).flatMap {
        case false => answers.getEither(IsCohabitingPage).flatMap {
          case false => IsCohabitingPage.leftNec
          case true => Right((false, Some(false), Some(true)))
        }
        case true => Right((false, Some(true), None))
      }
      case true => Right((true, None, None))
    }

  private def getWillHaveCaringResponsibility(answers: UserAnswers): EitherNec[QuestionPage[_], Boolean] =
    answers.getEither(WillHaveCaringResponsibilityPage).flatMap {
      case true => Right(true)
      case false => WillHaveCaringResponsibilityPage.leftNec
    }

  private def getTimeEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], (Boolean, Option[Boolean])] =
    answers.getEither(WillTakeTimeToCareForChildPage).flatMap {
      case false => answers.getEither(WillTakeTimeToSupportMotherPage).flatMap {
        case true => Right((false, Some(true)))
        case false => WillTakeTimeToSupportMotherPage.leftNec
      }
      case true => Right((true, None))
    }

  private def getBirthDetails(answers: UserAnswers, alreadyBorn: Boolean): EitherNec[QuestionPage[_], BirthDetails] =
    if (alreadyBorn) getAlreadyBornBirthDetails(answers) else getDueBirthDetails(answers)

  private def getAlreadyBornBirthDetails(answers: UserAnswers): EitherNec[QuestionPage[_], BirthDetails.AlreadyBorn] =
    (
      answers.getEither(BabyDateOfBirthPage),
      answers.getEither(WantPayToStartOnBirthDatePage)
    ).parMapN { case (dateOfBirth, payShouldStartFromBirthDate) =>
      BirthDetails.AlreadyBorn(dateOfBirth, payShouldStartFromBirthDate)
    }

  private def getDueBirthDetails(answers: UserAnswers): EitherNec[QuestionPage[_], BirthDetails.Due] =
      answers.getEither(WantPayToStartOnDueDatePage).map(BirthDetails.Due)

  private def getPayStartDate(answers: UserAnswers, alreadyBorn: Boolean): EitherNec[QuestionPage[_], Option[LocalDate]] =
    if (alreadyBorn) getPayStartDateBorn(answers) else getPayStartDateDue(answers)

  private def getPayStartDateBorn(answers: UserAnswers): EitherNec[QuestionPage[_], Option[LocalDate]] =
    answers.getEither(WantPayToStartOnBirthDatePage).flatMap {
      case true  => Right(None)
      case false => answers.getEither(PayStartDateBabyBornPage).map(Some(_))
    }

  private def getPayStartDateDue(answers: UserAnswers): EitherNec[QuestionPage[_], Option[LocalDate]] =
    answers.getEither(WantPayToStartOnDueDatePage).flatMap {
      case true  => Right(None)
      case false => answers.getEither(PayStartDateBabyDuePage).map(Some(_))
    }
}
