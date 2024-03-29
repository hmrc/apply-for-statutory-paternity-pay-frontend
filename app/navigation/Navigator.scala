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

package navigation

import config.Constants
import controllers.routes
import json.OptionalLocalDateReads._
import models._
import pages._
import play.api.mvc.Call

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    case CountryOfResidencePage                   => _ => routes.IsAdoptingOrParentalOrderController.onPageLoad(NormalMode)
    case IsAdoptingOrParentalOrderPage            => isAdoptingOrParentalOrderRoute
    case IsApplyingForStatutoryAdoptionPayPage    => isApplyingForStatutoryAdoptionPayRoute
    case IsAdoptingFromAbroadPage                 => _ => routes.ReasonForRequestingController.onPageLoad(NormalMode)
    case ReasonForRequestingPage                  => _ => routes.IsInQualifyingRelationshipController.onPageLoad(NormalMode)
    case IsBiologicalFatherPage                   => isBiologicalFatherRoute
    case IsInQualifyingRelationshipPage           => isInQualifyingRelationshipRoute
    case IsCohabitingPage                         => isCohabitingRoute
    case WillHaveCaringResponsibilityPage         => willHaveCaringResponsibilityRoute
    case WillTakeTimeToCareForChildPage           => willTakeTimeToCareForChildRoute
    case WillTakeTimeToSupportPartnerPage         => willTakeTimeToSupportPartnerRoute
    case NamePage                                 => _ => routes.NinoController.onPageLoad(NormalMode)
    case NinoPage                                 => ninoRoute
    case BabyHasBeenBornPage                      => babyHasBeenBornRoute
    case BabyDateOfBirthPage                      => _ => routes.BabyDueDateController.onPageLoad(NormalMode)
    case BabyDueDatePage                          => babyDueDateRoute
    case DateChildWasMatchedPage                  => _ => routes.ChildHasBeenPlacedController.onPageLoad(NormalMode)
    case ChildHasBeenPlacedPage                   => childHasBeenPlacedRoute
    case ChildPlacementDatePage                   => childPlacementDateRoute
    case ChildExpectedPlacementDatePage           => childExpectedPlacementDateRoute
    case DateOfAdoptionNotificationPage           => _ => routes.ChildHasEnteredUkController.onPageLoad(NormalMode)
    case ChildHasEnteredUkPage                    => childHasEnteredUkRoute
    case DateChildEnteredUkPage                   => dateChildEnteredUkRoute
    case DateChildExpectedToEnterUkPage           => dateChildExpectedToEnterUkRoute
    case PaternityLeaveLengthGbPreApril24OrNiPage => _ => routes.PayStartDateGbPreApril24OrNiController.onPageLoad(NormalMode)
    case PayStartDateGbPreApril24OrNiPage         => _ => routes.CheckYourAnswersController.onPageLoad
    case PaternityLeaveLengthGbPostApril24Page    => paternityLeaveLengthGbPostApril24Route
    case PayStartDateGbPostApril24Page            => _ => routes.CheckYourAnswersController.onPageLoad
    case LeaveTakenTogetherOrSeparatelyPage       => leaveTakenTogetherOrSeparatelyRoute
    case PayStartDateWeek1Page                    => _ => routes.PayStartDateWeek2Controller.onPageLoad(NormalMode)
    case PayStartDateWeek2Page                    => _ => routes.CheckYourAnswersController.onPageLoad
    case _                                        => _ => routes.IndexController.onPageLoad
  }

  private def isAdoptingOrParentalOrderRoute(answers: UserAnswers): Call =
    answers.get(IsAdoptingOrParentalOrderPage).map {
      case true  => routes.IsApplyingForStatutoryAdoptionPayController.onPageLoad(NormalMode)
      case false => routes.IsBiologicalFatherController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def isApplyingForStatutoryAdoptionPayRoute(answers: UserAnswers): Call =
    answers.get(IsApplyingForStatutoryAdoptionPayPage).map {
      case true  => routes.CannotApplyController.onPageLoad()
      case false => routes.IsAdoptingFromAbroadController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def isBiologicalFatherRoute(answers: UserAnswers): Call =
    answers.get(IsBiologicalFatherPage).map {
      case true  => routes.WillHaveCaringResponsibilityController.onPageLoad(NormalMode)
      case false => routes.IsInQualifyingRelationshipController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def isInQualifyingRelationshipRoute(answers: UserAnswers): Call =
    answers.get(IsInQualifyingRelationshipPage).map {
      case true  => routes.WillHaveCaringResponsibilityController.onPageLoad(NormalMode)
      case false => routes.IsCohabitingController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def isCohabitingRoute(answers: UserAnswers): Call =
    answers.get(IsCohabitingPage).map {
      case true  => routes.WillHaveCaringResponsibilityController.onPageLoad(NormalMode)
      case false => routes.CannotApplyController.onPageLoad()
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def willHaveCaringResponsibilityRoute(answers: UserAnswers): Call =
    answers.get(WillHaveCaringResponsibilityPage).map {
      case true  => routes.WillTakeTimeToCareForChildController.onPageLoad(NormalMode)
      case false => routes.CannotApplyController.onPageLoad()
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def willTakeTimeToCareForChildRoute(answers: UserAnswers): Call =
    answers.get(WillTakeTimeToCareForChildPage).map {
      case true  => routes.NameController.onPageLoad(NormalMode)
      case false => routes.WillTakeTimeToSupportPartnerController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def willTakeTimeToSupportPartnerRoute(answers: UserAnswers): Call =
    answers.get(WillTakeTimeToSupportPartnerPage).map {
      case true  => routes.NameController.onPageLoad(NormalMode)
      case false => routes.CannotApplyController.onPageLoad()
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def ninoRoute(answers: UserAnswers): Call =
    answers.get(IsAdoptingOrParentalOrderPage).map {
      case true =>
        answers.get(ReasonForRequestingPage).map {
          case RelationshipToChild.Adopting | RelationshipToChild.SupportingAdoption =>
            answers.get(IsAdoptingFromAbroadPage).map {
              case true => routes.DateOfAdoptionNotificationController.onPageLoad(NormalMode)
              case false => routes.DateChildWasMatchedController.onPageLoad(NormalMode)
            }.getOrElse(routes.JourneyRecoveryController.onPageLoad())
          case _ =>
            routes.BabyHasBeenBornController.onPageLoad(NormalMode)
        }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

      case false =>
        routes.BabyHasBeenBornController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def babyHasBeenBornRoute(answers: UserAnswers): Call =
    answers.get(BabyHasBeenBornPage).map {
      case true  => routes.BabyDateOfBirthController.onPageLoad(NormalMode)
      case false => routes.BabyDueDateController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def babyDueDateRoute(answers: UserAnswers): Call =
    childToPaternityRoute(answers, BabyDueDatePage)

  private def childPlacementDateRoute(answers: UserAnswers): Call =
    childToPaternityRoute(answers, ChildPlacementDatePage)

  private def childExpectedPlacementDateRoute(answers: UserAnswers): Call =
    childToPaternityRoute(answers, ChildExpectedPlacementDatePage)

  private def dateChildEnteredUkRoute(answers: UserAnswers): Call =
    childToPaternityRoute(answers, DateChildEnteredUkPage)

  private def dateChildExpectedToEnterUkRoute(answers: UserAnswers): Call =
    childToPaternityRoute(answers, DateChildExpectedToEnterUkPage)

  private def childToPaternityRoute(answers: UserAnswers, datePage: QuestionPage[LocalDate]): Call =
    answers.get(datePage).map {
      case d if d.isBefore(Constants.april24LegislationEffective) =>
        routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)

      case _ =>
        answers.get(CountryOfResidencePage).map {
          case CountryOfResidence.NorthernIreland =>
            routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)

          case _ =>
            routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(NormalMode)
        }.getOrElse(routes.JourneyRecoveryController.onPageLoad())
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def childHasBeenPlacedRoute(answers: UserAnswers): Call =
    answers.get(ChildHasBeenPlacedPage).map {
      case true  => routes.ChildPlacementDateController.onPageLoad(NormalMode)
      case false => routes.ChildExpectedPlacementDateController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def childHasEnteredUkRoute(answers: UserAnswers): Call =
    answers.get(ChildHasEnteredUkPage).map {
      case true  => routes.DateChildEnteredUkController.onPageLoad(NormalMode)
      case false => routes.DateChildExpectedToEnterUkController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def paternityLeaveLengthGbPostApril24Route(answers: UserAnswers): Call =
    answers.get(PaternityLeaveLengthGbPostApril24Page).map {
      case PaternityLeaveLengthGbPostApril24.OneWeek  => routes.PayStartDateGbPostApril24Controller.onPageLoad(NormalMode)
      case PaternityLeaveLengthGbPostApril24.TwoWeeks => routes.LeaveTakenTogetherOrSeparatelyController.onPageLoad(NormalMode)
      case PaternityLeaveLengthGbPostApril24.Unsure   => routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def leaveTakenTogetherOrSeparatelyRoute(answers: UserAnswers): Call =
    answers.get(LeaveTakenTogetherOrSeparatelyPage).map {
      case LeaveTakenTogetherOrSeparately.Together   => routes.PayStartDateGbPostApril24Controller.onPageLoad(NormalMode)
      case LeaveTakenTogetherOrSeparately.Separately => routes.PayStartDateWeek1Controller.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private val checkRouteMap: Page => UserAnswers => Call = {
    case IsAdoptingOrParentalOrderPage         => isAdoptingOrParentalOrderCheckRoute
    case IsApplyingForStatutoryAdoptionPayPage => isApplyingForStatutoryAdoptionPayCheckRoute
    case IsAdoptingFromAbroadPage              => isAdoptingFromAbroadCheckRoute
    case ReasonForRequestingPage               => reasonForRequestingCheckRoute
    case IsBiologicalFatherPage                => isBiologicalFatherCheckRoute
    case IsInQualifyingRelationshipPage        => isInQualifyingRelationshipCheckRoute
    case IsCohabitingPage                      => isCohabitingCheckRoute
    case WillHaveCaringResponsibilityPage      => willHaveCaringResponsibilityCheckRoute
    case WillTakeTimeToCareForChildPage        => willTakeTimeToCareForChildCheckRoute
    case WillTakeTimeToSupportPartnerPage      => willTakeTimeToSupportPartnerCheckRoute
    case NinoPage                              => ninoCheckRoute
    case BabyHasBeenBornPage                   => babyHasBeenBornCheckRoute
    case BabyDueDatePage                       => babyDueDateCheckRoute
    case ChildPlacementDatePage                => childPlacementDateCheckRoute
    case ChildExpectedPlacementDatePage        => childExpectedPlacementDateCheckRoute
    case ChildHasBeenPlacedPage                => childHasBeenPlacedCheckRoute
    case ChildHasEnteredUkPage                 => childHasEnteredUkCheckRoute
    case DateChildEnteredUkPage                => dateChildEnteredUkCheckRoute
    case DateChildExpectedToEnterUkPage        => dateChildExpectedToEnterUkCheckRoute
    case PaternityLeaveLengthGbPostApril24Page => paternityLeaveLengthGbPostApril24CheckRoute
    case LeaveTakenTogetherOrSeparatelyPage    => leaveTakenTogetherOrSeparatelyCheckRoute
    case PayStartDateWeek1Page                 => payStartDateWeek1CheckRoute
    case _                                     => _ => routes.CheckYourAnswersController.onPageLoad
  }

  private def isAdoptingOrParentalOrderCheckRoute(answers: UserAnswers): Call =
    answers.get(IsAdoptingOrParentalOrderPage).map {
      case true  =>
        if (answers.isDefined(IsApplyingForStatutoryAdoptionPayPage)) {
          routes.CheckYourAnswersController.onPageLoad
        } else {
          routes.IsApplyingForStatutoryAdoptionPayController.onPageLoad(CheckMode)
        }

      case false =>
        if (answers.isDefined(IsBiologicalFatherPage)) {
          routes.CheckYourAnswersController.onPageLoad
        } else {
          routes.IsBiologicalFatherController.onPageLoad(CheckMode)
        }
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def isApplyingForStatutoryAdoptionPayCheckRoute(answers: UserAnswers): Call =
    answers.get(IsApplyingForStatutoryAdoptionPayPage).map {
      case true  => routes.CannotApplyController.onPageLoad()
      case false => routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def isAdoptingFromAbroadCheckRoute(answers: UserAnswers): Call =
    if (answers.isDefined(ReasonForRequestingPage)) routes.CheckYourAnswersController.onPageLoad
    else                                            routes.ReasonForRequestingController.onPageLoad(NormalMode)

  private def reasonForRequestingCheckRoute(answers: UserAnswers): Call =
    if (answers.isDefined(IsInQualifyingRelationshipPage)) routes.CheckYourAnswersController.onPageLoad
    else                                                   routes.IsInQualifyingRelationshipController.onPageLoad(CheckMode)

  private def isBiologicalFatherCheckRoute(answers: UserAnswers): Call =
    (answers.get(IsBiologicalFatherPage), answers.get(IsInQualifyingRelationshipPage)) match {
      case (Some(false), None) => routes.IsInQualifyingRelationshipController.onPageLoad(CheckMode)
      case _                   => routes.CheckYourAnswersController.onPageLoad
    }

  private def isInQualifyingRelationshipCheckRoute(answers: UserAnswers): Call =
    (answers.get(IsInQualifyingRelationshipPage), answers.get(IsCohabitingPage)) match {
      case (Some(false), None) => routes.IsCohabitingController.onPageLoad(CheckMode)
      case _                   => routes.CheckYourAnswersController.onPageLoad
    }

  private def isCohabitingCheckRoute(answers: UserAnswers): Call =
    answers.get(IsCohabitingPage).map {
      case true  => routes.CheckYourAnswersController.onPageLoad
      case false => routes.CannotApplyController.onPageLoad()
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def willHaveCaringResponsibilityCheckRoute(answers: UserAnswers): Call =
    answers.get(WillHaveCaringResponsibilityPage).map {
      case true  => routes.CheckYourAnswersController.onPageLoad
      case false => routes.CannotApplyController.onPageLoad()
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def willTakeTimeToCareForChildCheckRoute(answers: UserAnswers): Call =
    (answers.get(WillTakeTimeToCareForChildPage), answers.get(WillTakeTimeToSupportPartnerPage)) match {
      case (Some(false), None) => routes.WillTakeTimeToSupportPartnerController.onPageLoad(CheckMode)
      case _                   => routes.CheckYourAnswersController.onPageLoad
    }

  private def willTakeTimeToSupportPartnerCheckRoute(answers: UserAnswers): Call =
    answers.get(WillTakeTimeToSupportPartnerPage).map {
      case true  => routes.CheckYourAnswersController.onPageLoad
      case false => routes.CannotApplyController.onPageLoad()
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def ninoCheckRoute(answers: UserAnswers): Call =
    answers.get(IsAdoptingOrParentalOrderPage).map {
      case true =>
        answers.get(ReasonForRequestingPage).map {
          case RelationshipToChild.ParentalOrder =>
            answers.get(BabyHasBeenBornPage)
              .map(_ => routes.CheckYourAnswersController.onPageLoad)
              .getOrElse(routes.BabyHasBeenBornController.onPageLoad(CheckMode))

          case RelationshipToChild.Adopting | RelationshipToChild.SupportingAdoption =>
            answers.get(IsAdoptingFromAbroadPage).map {
              case true =>
                answers.get(DateOfAdoptionNotificationPage)
                  .map(_ => routes.CheckYourAnswersController.onPageLoad)
                  .getOrElse(routes.DateOfAdoptionNotificationController.onPageLoad(CheckMode))

              case false =>
                answers.get(DateChildWasMatchedPage)
                .map(_ => routes.CheckYourAnswersController.onPageLoad)
                .getOrElse(routes.DateChildWasMatchedController.onPageLoad(CheckMode))
            }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

          case _ =>
            routes.CheckYourAnswersController.onPageLoad
        }.getOrElse(routes.JourneyRecoveryController.onPageLoad())
      case false =>
        answers.get(BabyHasBeenBornPage)
        .map(_ => routes.CheckYourAnswersController.onPageLoad)
        .getOrElse(routes.BabyHasBeenBornController.onPageLoad(CheckMode))
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def babyHasBeenBornCheckRoute(answers: UserAnswers): Call =
    answers.get(BabyHasBeenBornPage).map {
      case true =>
        answers.get(BabyDateOfBirthPage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.BabyDateOfBirthController.onPageLoad(CheckMode))
      case false =>
        routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def babyDueDateCheckRoute(answers: UserAnswers): Call =
    childToPaternityCheckRoute(answers, BabyDueDatePage)

  private def childPlacementDateCheckRoute(answers: UserAnswers): Call =
    childToPaternityCheckRoute(answers, ChildPlacementDatePage)

  private def childExpectedPlacementDateCheckRoute(answers: UserAnswers): Call =
    childToPaternityCheckRoute(answers, ChildExpectedPlacementDatePage)

  private def dateChildEnteredUkCheckRoute(answers: UserAnswers): Call =
    childToPaternityCheckRoute(answers, DateChildEnteredUkPage)

  private def dateChildExpectedToEnterUkCheckRoute(answers: UserAnswers): Call =
    childToPaternityCheckRoute(answers, DateChildExpectedToEnterUkPage)

  private def childToPaternityCheckRoute(answers: UserAnswers, datePage: QuestionPage[LocalDate]): Call =
    answers.get(datePage).map {
      case d if d.isBefore(Constants.april24LegislationEffective) =>
        answers.get(PaternityLeaveLengthGbPreApril24OrNiPage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode))

      case _ =>
        answers.get(CountryOfResidencePage).map {
          case CountryOfResidence.NorthernIreland =>
            answers.get(PaternityLeaveLengthGbPreApril24OrNiPage)
              .map(_ => routes.CheckYourAnswersController.onPageLoad)
              .getOrElse(routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode))

          case _ =>
            answers.get(PaternityLeaveLengthGbPostApril24Page)
            .map (_ => routes.CheckYourAnswersController.onPageLoad)
            .getOrElse(routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(CheckMode))

        }.getOrElse(routes.JourneyRecoveryController.onPageLoad())
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def childHasBeenPlacedCheckRoute(answers: UserAnswers): Call =
    answers.get(ChildHasBeenPlacedPage).map {
      case true =>
        answers.get(ChildPlacementDatePage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.ChildPlacementDateController.onPageLoad(CheckMode))
      case false =>
        answers.get(ChildExpectedPlacementDatePage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.ChildExpectedPlacementDateController.onPageLoad(CheckMode))
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def childHasEnteredUkCheckRoute(answers: UserAnswers): Call =
    answers.get(ChildHasEnteredUkPage).map {
      case true =>
        answers.get(DateChildEnteredUkPage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.DateChildEnteredUkController.onPageLoad(CheckMode))
      case false =>
        answers.get(DateChildExpectedToEnterUkPage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.DateChildExpectedToEnterUkController.onPageLoad(CheckMode))
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def paternityLeaveLengthGbPostApril24CheckRoute(answers: UserAnswers): Call =
    answers.get(PaternityLeaveLengthGbPostApril24Page).map {
      case PaternityLeaveLengthGbPostApril24.OneWeek =>
        answers.get(PayStartDateGbPostApril24Page)
          .map(_.map { _ =>
              routes.CheckYourAnswersController.onPageLoad
            }.getOrElse(routes.PayStartDateGbPostApril24Controller.onPageLoad(CheckMode))
          ).getOrElse(routes.PayStartDateGbPostApril24Controller.onPageLoad(CheckMode))

      case PaternityLeaveLengthGbPostApril24.TwoWeeks =>
        answers.get(LeaveTakenTogetherOrSeparatelyPage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.LeaveTakenTogetherOrSeparatelyController.onPageLoad(CheckMode))

      case PaternityLeaveLengthGbPostApril24.Unsure =>
        routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def leaveTakenTogetherOrSeparatelyCheckRoute(answers: UserAnswers): Call =
    answers.get(LeaveTakenTogetherOrSeparatelyPage).map {
      case LeaveTakenTogetherOrSeparately.Together =>
        answers.get(PayStartDateGbPostApril24Page)
          .map(_.map { _ =>
              routes.CheckYourAnswersController.onPageLoad
            }.getOrElse(routes.PayStartDateGbPostApril24Controller.onPageLoad(CheckMode))
          ).getOrElse(routes.PayStartDateGbPostApril24Controller.onPageLoad(CheckMode))

      case LeaveTakenTogetherOrSeparately.Separately =>
        answers.get(PayStartDateWeek1Page)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.PayStartDateWeek1Controller.onPageLoad(CheckMode))
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def payStartDateWeek1CheckRoute(answers: UserAnswers): Call =
    answers.get(PayStartDateWeek2Page)
      .map(_ => routes.CheckYourAnswersController.onPageLoad)
      .getOrElse(routes.PayStartDateWeek2Controller.onPageLoad(CheckMode))

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
