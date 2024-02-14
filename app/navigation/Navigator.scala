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

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.routes
import pages._
import models._

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    case CountryOfResidencePage                => _ => routes.IsAdoptingOrParentalOrderController.onPageLoad(NormalMode)
    case IsAdoptingOrParentalOrderPage         => isAdoptingOrParentalOrderRoute
    case IsApplyingForStatutoryAdoptionPayPage => isApplyingForStatutoryAdoptionPayRoute
    case IsAdoptingFromAbroadPage              => _ => routes.ReasonForRequestingController.onPageLoad(NormalMode)
    case ReasonForRequestingPage               => _ => routes.IsInQualifyingRelationshipController.onPageLoad(NormalMode)
    case IsBiologicalFatherPage                => isBiologicalFatherRoute
    case IsInQualifyingRelationshipPage        => isInQualifyingRelationshipRoute
    case IsCohabitingPage                      => isCohabitingRoute
    case WillHaveCaringResponsibilityPage      => willHaveCaringResponsibilityRoute
    case WillTakeTimeToCareForChildPage        => willTakeTimeToCareForChildRoute
    case WillTakeTimeToSupportPartnerPage       => willTakeTimeToSupportPartnerRoute
    case NamePage                              => _ => routes.NinoController.onPageLoad(NormalMode)
    case NinoPage                              => _ => routes.BabyHasBeenBornController.onPageLoad(NormalMode)
    case BabyHasBeenBornPage                   => babyHasBeenBornRoute
    case BabyDateOfBirthPage                   => _ => routes.BabyDueDateController.onPageLoad(NormalMode)
    case BabyDueDatePage                       => babyDueDateRoute
    case PayStartDateBabyBornPage              => _ => routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
    case PayStartDateBabyDuePage               => _ => routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
    case PaternityLeaveLengthPage              => _ => routes.CheckYourAnswersController.onPageLoad
    case _                                     => _ => routes.IndexController.onPageLoad
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

  private def babyHasBeenBornRoute(answers: UserAnswers): Call =
    answers.get(BabyHasBeenBornPage).map {
      case true  => routes.BabyDateOfBirthController.onPageLoad(NormalMode)
      case false => routes.BabyDueDateController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def babyDueDateRoute(answers: UserAnswers): Call =
    answers.get(BabyHasBeenBornPage).map {
      case true  => routes.PayStartDateBabyBornController.onPageLoad(NormalMode)
      case false => routes.PayStartDateBabyDueController.onPageLoad(NormalMode)
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
    case BabyHasBeenBornPage                   => babyHasBeenBornCheckRoute
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

  private def babyHasBeenBornCheckRoute(answers: UserAnswers): Call =
    answers.get(BabyHasBeenBornPage).map {
      case true =>
        answers.get(BabyDateOfBirthPage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.BabyDateOfBirthController.onPageLoad(CheckMode))
      case false =>
        routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
