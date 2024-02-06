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
    case CountryOfResidencePage                => _ => routes.IsAdoptingController.onPageLoad(NormalMode)
    case IsAdoptingPage                        => isAdoptingRoute
    case IsApplyingForStatutoryAdoptionPayPage => isApplyingForStatutoryAdoptionPayPageRoute
    case IsBiologicalFatherPage                => isBiologicalFatherRoute
    case IsInQualifyingRelationshipPage        => isInQualifyingRelationshipRoute
    case IsCohabitingPage                      => isCohabitingRoute
    case WillHaveCaringResponsibilityPage      => willHaveCaringResponsibilityRoute
    case WillTakeTimeToCareForChildPage        => willTakeTimeToCareForChildRoute
    case WillTakeTimeToSupportMotherPage       => willTakeTimeToSupportMotherRoute
    case NamePage                              => _ => routes.NinoController.onPageLoad(NormalMode)
    case NinoPage                              => _ => routes.BabyHasBeenBornController.onPageLoad(NormalMode)
    case BabyHasBeenBornPage                   => babyHasBeenBornRoute
    case BabyDateOfBirthPage                   => _ => routes.WantPayToStartOnBirthDateController.onPageLoad(NormalMode)
    case BabyDueDatePage                       => babyDueDateRoute
    case WantPayToStartOnBirthDatePage         => wantPayToStartOnBirthDateRoute
    case WantPayToStartOnDueDatePage           => wantPayToStartOnDueDateRoute
    case PayStartDateBabyBornPage              => _ => routes.BabyDueDateController.onPageLoad(NormalMode)
    case PayStartDateBabyDuePage               => _ => routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
    case PaternityLeaveLengthPage              => _ => routes.CheckYourAnswersController.onPageLoad
    case _                                     => _ => routes.IndexController.onPageLoad
  }

  private def isAdoptingRoute(answers: UserAnswers): Call =
    answers.get(IsAdoptingPage).map {
      case true  => routes.IsApplyingForStatutoryAdoptionPayController.onPageLoad(NormalMode)
      case false => routes.IsBiologicalFatherController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def isApplyingForStatutoryAdoptionPayPageRoute(answers: UserAnswers): Call =
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
      case false => routes.WillTakeTimeToSupportMotherController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def willTakeTimeToSupportMotherRoute(answers: UserAnswers): Call =
    answers.get(WillTakeTimeToSupportMotherPage).map {
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
      case true  => routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
      case false => routes.WantPayToStartOnDueDateController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def wantPayToStartOnBirthDateRoute(answers: UserAnswers): Call =
    answers.get(WantPayToStartOnBirthDatePage).map {
      case true  => routes.BabyDueDateController.onPageLoad(NormalMode)
      case false => routes.PayStartDateBabyBornController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def wantPayToStartOnDueDateRoute(answers: UserAnswers): Call =
    answers.get(WantPayToStartOnDueDatePage).map {
      case true  => routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
      case false => routes.PayStartDateBabyDueController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private val checkRouteMap: Page => UserAnswers => Call = {
    case IsAdoptingPage                   => isAdoptingCheckRoute
    case IsBiologicalFatherPage           => isBiologicalFatherCheckRoute
    case IsInQualifyingRelationshipPage   => isInQualifyingRelationshipCheckRoute
    case IsCohabitingPage                 => isCohabitingCheckRoute
    case WillHaveCaringResponsibilityPage => willHaveCaringResponsibilityCheckRoute
    case WillTakeTimeToCareForChildPage   => willTakeTimeToCareForChildCheckRoute
    case WillTakeTimeToSupportMotherPage  => willTakeTimeToSupportMotherCheckRoute
    case BabyHasBeenBornPage              => babyHasBeenBornCheckRoute
    case BabyDateOfBirthPage              => babyDateOfBirthCheckRoute
    case BabyDueDatePage                  => babyDueDateCheckRoute
    case WantPayToStartOnDueDatePage      => wantPayToStartOnDueDateCheckRoute
    case WantPayToStartOnBirthDatePage    => wantPayToStartOnBirthDateCheckRoute
    case _                                => _ => routes.CheckYourAnswersController.onPageLoad
  }

  private def isAdoptingCheckRoute(answers: UserAnswers): Call =
    answers.get(IsAdoptingPage).map {
      case true  => routes.CannotApplyAdoptingController.onPageLoad()
      case false => routes.CheckYourAnswersController.onPageLoad
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

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
    (answers.get(WillTakeTimeToCareForChildPage), answers.get(WillTakeTimeToSupportMotherPage)) match {
      case (Some(false), None) => routes.WillTakeTimeToSupportMotherController.onPageLoad(CheckMode)
      case _                   => routes.CheckYourAnswersController.onPageLoad
    }

  private def willTakeTimeToSupportMotherCheckRoute(answers: UserAnswers): Call =
    answers.get(WillTakeTimeToSupportMotherPage).map {
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
        answers.get(WantPayToStartOnDueDatePage)
          .map(_ => routes.CheckYourAnswersController.onPageLoad)
          .getOrElse(routes.WantPayToStartOnDueDateController.onPageLoad(CheckMode))
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def babyDateOfBirthCheckRoute(answers: UserAnswers): Call =
    answers.get(WantPayToStartOnBirthDatePage)
      .map(_ => routes.CheckYourAnswersController.onPageLoad)
      .getOrElse(routes.WantPayToStartOnBirthDateController.onPageLoad(CheckMode))

  private def babyDueDateCheckRoute(answers: UserAnswers): Call =
    answers.get(WantPayToStartOnDueDatePage)
      .map(_ => routes.CheckYourAnswersController.onPageLoad)
      .getOrElse(routes.WantPayToStartOnDueDateController.onPageLoad(CheckMode))

  private def wantPayToStartOnBirthDateCheckRoute(answers: UserAnswers): Call =
    (answers.get(WantPayToStartOnBirthDatePage), answers.get(PayStartDateBabyBornPage)) match {
      case (Some(false), None) => routes.PayStartDateBabyBornController.onPageLoad(CheckMode)
      case _                   => routes.CheckYourAnswersController.onPageLoad
    }

  private def wantPayToStartOnDueDateCheckRoute(answers: UserAnswers): Call =
    (answers.get(WantPayToStartOnDueDatePage), answers.get(PayStartDateBabyBornPage)) match {
      case (Some(false), None) => routes.PayStartDateBabyDueController.onPageLoad(CheckMode)
      case _                   => routes.CheckYourAnswersController.onPageLoad
    }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
