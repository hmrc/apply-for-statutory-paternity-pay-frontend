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

package navigation

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.routes
import pages._
import models._

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    case IsBiologicalFatherPage           => isBiologicalFatherRoute
    case IsInQualifyingRelationshipPage   => isInQualifyingRelationshipRoute
    case IsCohabitingPage                 => isCohabitingRoute
    case WillHaveCaringResponsibilityPage => willHaveCaringResponsibilityRoute
    case WillTakeTimeToCareForChildPage   => willTakeTimeToCareForChildRoute
    case WillTakeTimeToSupportMotherPage  => willTakeTimeToSupportMotherRoute
    case NamePage                         => _ => routes.NinoController.onPageLoad(NormalMode)
    case NinoPage                         => _ => routes.BabyHasBeenBornController.onPageLoad(NormalMode)
    case BabyHasBeenBornPage              => babyHasBeenBornRoute
    case BabyDateOfBirthPage              => _ => routes.WantPayToStartOnBirthDateController.onPageLoad(NormalMode)
    case BabyDueDatePage                  => _ => routes.WantPayToStartOnDueDateController.onPageLoad(NormalMode)
    case WantPayToStartOnBirthDatePage    => wantPayToStartOnBirthDateRoute
    case WantPayToStartOnDueDatePage      => wantPayToStartOnDueDateRoute
    case PaternityLeaveLengthPage         => _ => routes.CheckYourAnswersController.onPageLoad
    case _                                => _ => routes.IndexController.onPageLoad
  }

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

  private def wantPayToStartOnBirthDateRoute(answers: UserAnswers): Call =
    answers.get(WantPayToStartOnBirthDatePage).map {
      case true  => routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
      case false => routes.PayStartDateController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def wantPayToStartOnDueDateRoute(answers: UserAnswers): Call =
    answers.get(WantPayToStartOnDueDatePage).map {
      case true  => routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
      case false => routes.PayStartDateController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ => _ => routes.CheckYourAnswersController.onPageLoad
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
