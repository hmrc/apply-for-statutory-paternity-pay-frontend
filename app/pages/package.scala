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

import models.UserAnswers

import scala.util.{Success, Try}

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

package object pages {

  val allQuestionPages: Set[QuestionPage[_]] = Set(
    BabyDateOfBirthPage,
    BabyDueDatePage,
    BabyHasBeenBornPage,
    ChildExpectedPlacementDatePage,
    ChildHasBeenPlacedPage,
    ChildPlacementDatePage,
    CountryOfResidencePage,
    DateChildWasMatchedPage,
    IsAdoptingFromAbroadPage,
    IsAdoptingOrParentalOrderPage,
    IsApplyingForStatutoryAdoptionPayPage,
    IsBiologicalFatherPage,
    IsCohabitingPage,
    IsInQualifyingRelationshipPage,
    NamePage,
    NinoPage,
    PaternityLeaveLengthPage,
    PayStartDateBabyBornPage,
    PayStartDateBabyDuePage,
    ReasonForRequestingPage,
    WillHaveCaringResponsibilityPage,
    WillTakeTimeToCareForChildPage,
    WillTakeTimeToSupportPartnerPage
  )

  def removeRedundantAnswers(userAnswers: UserAnswers, answersToRemove: Set[QuestionPage[_]]): Try[UserAnswers] =
    answersToRemove
      .foldLeft[Try[UserAnswers]](Success(userAnswers))(
        (acc, page) =>
          acc.flatMap(_.remove(page))
      )
}
