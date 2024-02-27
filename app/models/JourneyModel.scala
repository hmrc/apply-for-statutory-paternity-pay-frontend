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
import config.Constants
import json.OptionalLocalDateReads._
import models.JourneyModel.{ChildDetails, Eligibility, PaternityLeaveDetails}
import pages._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

final case class JourneyModel(
                               countryOfResidence: CountryOfResidence,
                               eligibility: Eligibility,
                               name: Name,
                               nino: Nino,
                               childDetails: ChildDetails,
                               paternityLeaveDetails: PaternityLeaveDetails
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

  sealed trait ChildDetails

  final case class BirthParentalOrderChild(
                                            dueDate: LocalDate,
                                            birthDate: Option[LocalDate]
                                          ) extends ChildDetails

  final case class AdoptedUkChild(
                                   matchedDate: LocalDate,
                                   hasBeenPlaced: Boolean,
                                   effectiveDate: LocalDate
                                 ) extends ChildDetails

  final case class AdoptedAbroadChild(
                                       notifiedDate: LocalDate,
                                       hasEnteredUk: Boolean,
                                       effectiveDate: LocalDate
                                     ) extends ChildDetails

  sealed trait PaternityLeaveDetails

  final case class PaternityLeaveGbPreApril24OrNi(
                                                   leaveLength: PaternityLeaveLengthGbPreApril24OrNi,
                                                   payStartDate: LocalDate
                                                 ) extends PaternityLeaveDetails

  final case class PaternityLeaveGbPostApril24OneWeek(
                                                       payStartDate: Option[LocalDate]
                                                     ) extends PaternityLeaveDetails

  final case class PaternityLeaveGbPostApril24TwoWeeksTogether(
                                                                payStartDate: Option[LocalDate]
                                                              ) extends PaternityLeaveDetails

  final case class PaternityLeaveGbPostApril24TwoWeeksSeparate(
                                                                week1StartDate: Option[LocalDate],
                                                                week2StartDate: Option[LocalDate]
                                                              ) extends PaternityLeaveDetails

  case object PaternityLeaveGbPostApril24Unsure extends PaternityLeaveDetails

  def from(answers: UserAnswers): EitherNec[QuestionPage[_], JourneyModel] =
    (
      answers.getEither(CountryOfResidencePage),
      getEligibility(answers),
      answers.getEither(NamePage),
      answers.getEither(NinoPage),
      getChildDetails(answers),
      getPaternityDetails(answers),
    ).parMapN { case (country, eligibility, name, nino, childDetails, paternityDetails) =>
      JourneyModel(country, eligibility, name, nino, childDetails, paternityDetails)
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
    answers.getEither(IsApplyingForStatutoryAdoptionPayPage).ifM(
      ifTrue  = IsApplyingForStatutoryAdoptionPayPage.leftNec,
      ifFalse = Right(false)
  )

  private def getAdoptionRelationshipEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], (Boolean, Option[Boolean])] =
    answers.getEither(IsInQualifyingRelationshipPage).ifM(
      ifTrue  = Right(true, None),
      ifFalse = answers.getEither(IsCohabitingPage).ifM(
        ifTrue  = Right(false, Some(true)),
        ifFalse = IsCohabitingPage.leftNec
      )
    )

  private def getBirthChildRelationshipEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], (Boolean, Option[Boolean], Option[Boolean])] =
    answers.getEither(IsBiologicalFatherPage).ifM(
      ifTrue  = Right((true, None, None)),
      ifFalse = answers.getEither(IsInQualifyingRelationshipPage).ifM(
        ifTrue  = Right((false, Some(true), None)),
        ifFalse = answers.getEither(IsCohabitingPage).ifM(
          ifTrue  = Right((false, Some(false), Some(true))),
          ifFalse = IsCohabitingPage.leftNec
        )
      )
    )

  private def getWillHaveCaringResponsibility(answers: UserAnswers): EitherNec[QuestionPage[_], Boolean] =
    answers.getEither(WillHaveCaringResponsibilityPage).ifM(
      ifTrue  = Right(true),
      ifFalse = WillHaveCaringResponsibilityPage.leftNec
    )

  private def getTimeEligibility(answers: UserAnswers): EitherNec[QuestionPage[_], (Boolean, Option[Boolean])] =
    answers.getEither(WillTakeTimeToCareForChildPage).ifM(
      ifTrue  = Right((true, None)),
      ifFalse = answers.getEither(WillTakeTimeToSupportPartnerPage).ifM(
        ifTrue  = Right((false, Some(true))),
        ifFalse = WillTakeTimeToSupportPartnerPage.leftNec
      )
    )

  private def getBirthDate(answers: UserAnswers): EitherNec[QuestionPage[_], Option[LocalDate]] =
    answers.getEither(BabyHasBeenBornPage).ifM(
      ifTrue  = answers.getEither(BabyDateOfBirthPage).map(Some(_)),
      ifFalse = Right(None)
    )

  private def getChildDetails(answers: UserAnswers): EitherNec[QuestionPage[_], ChildDetails] =
    answers.getEither(IsAdoptingOrParentalOrderPage).ifM(
      ifTrue =
        answers.getEither(ReasonForRequestingPage).flatMap {
          case RelationshipToChild.ParentalOrder =>
            getBirthParentalOrderChildDetails(answers)

          case _ =>
            answers.getEither(IsAdoptingFromAbroadPage).ifM(
              ifTrue  = getAdoptedAbroadChildDetails(answers),
              ifFalse = getAdoptedUkChildDetails(answers)
          )
        },
      ifFalse = getBirthParentalOrderChildDetails(answers)
  )

  private def getBirthParentalOrderChildDetails(answers: UserAnswers): EitherNec[QuestionPage[_], BirthParentalOrderChild] = {
    (
      answers.getEither(BabyDueDatePage),
      getBirthDate(answers)
    ).parMapN { case (dueDate, birthDate) =>
      BirthParentalOrderChild(dueDate, birthDate)
    }
  }

  private def getAdoptedUkChildDetails(answers: UserAnswers): EitherNec[QuestionPage[_], AdoptedUkChild] =
    (
      answers.getEither(DateChildWasMatchedPage),
      answers.getEither(ChildHasBeenPlacedPage),
      answers.getEither(ChildHasBeenPlacedPage).ifM(
        ifTrue  = answers.getEither(ChildPlacementDatePage),
        ifFalse = answers.getEither(ChildExpectedPlacementDatePage)
      )
    ).parMapN { case (dateMatched, hasBeenPlaced, effectiveDate) =>
      AdoptedUkChild(dateMatched, hasBeenPlaced, effectiveDate)
    }

  private def getAdoptedAbroadChildDetails(answers: UserAnswers): EitherNec[QuestionPage[_], AdoptedAbroadChild] =
    (
      answers.getEither(DateOfAdoptionNotificationPage),
      answers.getEither(ChildHasEnteredUkPage),
      answers.getEither(ChildHasEnteredUkPage).ifM(
        ifTrue  = answers.getEither(DateChildEnteredUkPage),
        ifFalse = answers.getEither(DateChildExpectedToEnterUkPage)
      )
    ).parMapN { case (notifiedDate, hasEnteredUk, effectiveDate) =>
      AdoptedAbroadChild(notifiedDate, hasEnteredUk, effectiveDate)
    }

  private def getPaternityDetails(answers: UserAnswers): EitherNec[QuestionPage[_], PaternityLeaveDetails] = {

    def getDetailsBasedOnDate(datePage: QuestionPage[LocalDate], answers: UserAnswers): EitherNec[QuestionPage[_], PaternityLeaveDetails] =
      answers.getEither(datePage).flatMap {
        case d if d.isBefore(Constants.april24LegislationEffective) =>
          getGbPreApril24OrNiPaternityDetails(answers)

        case _ =>
          getGBPostApril24PaternityDetails(answers)
      }

    answers.getEither(CountryOfResidencePage).flatMap {
      case CountryOfResidence.NorthernIreland =>
        getGbPreApril24OrNiPaternityDetails(answers)

      case _ =>
        answers.getEither(IsAdoptingOrParentalOrderPage).ifM(
          ifTrue =
            answers.getEither(IsAdoptingFromAbroadPage).ifM(
              ifTrue =
                answers.getEither(ChildHasEnteredUkPage).ifM(
                  ifTrue  = getDetailsBasedOnDate(DateChildEnteredUkPage, answers),
                  ifFalse = getDetailsBasedOnDate(DateChildExpectedToEnterUkPage, answers)
                ),
              ifFalse =
                answers.getEither(ReasonForRequestingPage).flatMap {
                  case RelationshipToChild.ParentalOrder =>
                    getDetailsBasedOnDate(BabyDueDatePage, answers)

                  case _ =>
                    answers.getEither(ChildHasBeenPlacedPage).ifM(
                      ifTrue  = getDetailsBasedOnDate(ChildPlacementDatePage, answers),
                      ifFalse = getDetailsBasedOnDate(ChildExpectedPlacementDatePage, answers)
                    )
                }
            ),
          ifFalse = getDetailsBasedOnDate(BabyDueDatePage, answers)
      )
    }
  }

  private def getGbPreApril24OrNiPaternityDetails(answers: UserAnswers): EitherNec[QuestionPage[_], PaternityLeaveDetails] =
    (
      answers.getEither(PaternityLeaveLengthGbPreApril24OrNiPage),
      answers.getEither(PayStartDateGbPreApril24OrNiPage)
    ).parMapN { case (leaveLength, payStartDate) =>
      PaternityLeaveGbPreApril24OrNi(leaveLength, payStartDate)
    }

  private def getGBPostApril24PaternityDetails(answers: UserAnswers): EitherNec[QuestionPage[_], PaternityLeaveDetails] =
    answers.getEither(PaternityLeaveLengthGbPostApril24Page).flatMap {
      case PaternityLeaveLengthGbPostApril24.OneWeek =>
        answers
          .getEither(PayStartDateGbPostApril24Page)
          .map(PaternityLeaveGbPostApril24OneWeek)

      case PaternityLeaveLengthGbPostApril24.TwoWeeks =>
        answers.getEither(LeaveTakenTogetherOrSeparatelyPage).flatMap {
          case LeaveTakenTogetherOrSeparately.Together =>
            answers
              .getEither(PayStartDateGbPostApril24Page)
              .map(PaternityLeaveGbPostApril24TwoWeeksTogether)

          case LeaveTakenTogetherOrSeparately.Separately =>
            (
              answers.getEither(PayStartDateWeek1Page),
              answers.getEither(PayStartDateWeek2Page)
            ).parMapN { case (week1Date, week2Date) =>
              PaternityLeaveGbPostApril24TwoWeeksSeparate(Some(week1Date), Some(week2Date))
            }
        }

      case PaternityLeaveLengthGbPostApril24.Unsure =>
        Right(PaternityLeaveGbPostApril24Unsure)
    }
}
