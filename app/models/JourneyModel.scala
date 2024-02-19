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
import models.JourneyModel.Eligibility
import pages._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

final case class JourneyModel(
                               countryOfResidence: CountryOfResidence,
                               eligibility: Eligibility,
                               name: Name,
                               nino: Nino,
                               hasTheBabyBeenBorn: Boolean,
                               dueDate: LocalDate,
                               birthDate: Option[LocalDate],
                               payStartDate: LocalDate,
                               howLongWillYouBeOnLeave: PaternityLeaveLengthGbPreApril24OrNi
                             )

object JourneyModel {

  sealed trait Eligibility

  final case class BirthChildEligibility(
                                          biologicalFather: Boolean,
                                          inRelationshipWithMother: Option[Boolean],
                                          livingWithMother: Option[Boolean],
                                          responsibilityForChild: Boolean,
                                          timeOffToCareForChild: Boolean,
                                          timeOffToSupportPartner: Option[Boolean]
                                        ) extends Eligibility

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

  def from(answers: UserAnswers): EitherNec[QuestionPage[_], JourneyModel] =
    (
      answers.getEither(CountryOfResidencePage),
      getEligibility(answers),
      answers.getEither(NamePage),
      answers.getEither(NinoPage),
      answers.getEither(BabyHasBeenBornPage),
      answers.getEither(BabyDueDatePage),
      getBirthDate(answers),
      answers.getEither(PayStartDateGbPreApril24OrNiPage),
      answers.getEither(PaternityLeaveLengthGbPreApril24OrNiPage)
    ).parMapN { case (country, eligibility, name, nino, alreadyBorn, dueDate, birthDate, payStartDate, paternityLeaveLength) =>
      JourneyModel(country, eligibility, name, nino, alreadyBorn, dueDate, birthDate, payStartDate, paternityLeaveLength)
    }

  private def getEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], Eligibility] =
    answers.getEither(IsAdoptingOrParentalOrderPage).flatMap {
      case true => getAdoptionParentalOrderEligibility(answers)
      case false => getBirthChildEligibility(answers)
    }

  private def getBirthChildEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], BirthChildEligibility] =
    (
      getBirthChildRelationshipEligibility(answers),
      getWillHaveCaringResponsibility(answers),
      getTimeEligibility(answers)
    ).parMapN { case ((isBiologicalFather, isInQualifyingRelationship, isCohabiting), caringResponsibility, (careForChild, supportMother)) =>
      BirthChildEligibility(isBiologicalFather, isInQualifyingRelationship, isCohabiting, caringResponsibility, careForChild, supportMother)
    }

  private def getAdoptionParentalOrderEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], AdoptionParentalOrderEligibility] = (
    getApplyingForStatutoryAdoptionPay(answers),
    answers.getEither(IsAdoptingFromAbroadPage),
    answers.getEither(ReasonForRequestingPage),
    getAdoptionRelationshipEligibility(answers),
    getWillHaveCaringResponsibility(answers),
    getTimeEligibility(answers)
  ).parMapN { case (applyingForStatutoryAdoptionPay, adoptingFromAbroad, reasonForRequesting, (inQualifyingRelationship, cohabiting), caringResponsibility, (careForChild, supportPartner)) =>
    AdoptionParentalOrderEligibility(applyingForStatutoryAdoptionPay, adoptingFromAbroad, reasonForRequesting, inQualifyingRelationship, cohabiting, caringResponsibility, careForChild, supportPartner)
  }

  private def getApplyingForStatutoryAdoptionPay(answers: UserAnswers): EitherNec[QuestionPage[_], Boolean] =
    answers.getEither(IsApplyingForStatutoryAdoptionPayPage).flatMap {
      case true => IsApplyingForStatutoryAdoptionPayPage.leftNec
      case false => Right(false)
    }

  private def getAdoptionRelationshipEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], (Boolean, Option[Boolean])] =
    answers.getEither(IsInQualifyingRelationshipPage).flatMap {
      case true => Right(true, None)
      case false => answers.getEither(IsCohabitingPage).flatMap {
        case true => Right(false, Some(true))
        case false => IsCohabitingPage.leftNec
      }
    }

  private def getBirthChildRelationshipEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], (Boolean, Option[Boolean], Option[Boolean])] =
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
      case false => answers.getEither(WillTakeTimeToSupportPartnerPage).flatMap {
        case true => Right((false, Some(true)))
        case false => WillTakeTimeToSupportPartnerPage.leftNec
      }
      case true => Right((true, None))
    }

  private def getBirthDate(answers: UserAnswers): EitherNec[QuestionPage[_], Option[LocalDate]] =
    answers.getEither(BabyHasBeenBornPage).flatMap {
      case true => answers.getEither(BabyDateOfBirthPage).map(Some(_))
      case false => Right(None)
    }
}
