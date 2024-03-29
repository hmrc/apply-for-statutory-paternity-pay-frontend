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

package object pages {

  lazy val filterQuestions: Set[QuestionPage[_]] = Set(
    IsAdoptingOrParentalOrderPage,
    IsApplyingForStatutoryAdoptionPayPage,
    IsAdoptingFromAbroadPage,
    ReasonForRequestingPage,
    IsBiologicalFatherPage,
    IsInQualifyingRelationshipPage,
    IsCohabitingPage,
    WillHaveCaringResponsibilityPage,
    WillTakeTimeToCareForChildPage,
    WillTakeTimeToSupportPartnerPage
  )

  lazy val applicantQuestions: Set[QuestionPage[_]] = Set(
    NamePage,
    NinoPage,
  )

  lazy val birthChildPaternityOrderDateQuestions: Set[QuestionPage[_]] = Set(
    BabyHasBeenBornPage,
    BabyDateOfBirthPage,
    BabyDueDatePage,
  )

  lazy val adoptingAbroadDateQuestions: Set[QuestionPage[_]] = Set(
    DateOfAdoptionNotificationPage,
    ChildHasEnteredUkPage,
    DateChildEnteredUkPage,
    DateChildExpectedToEnterUkPage
  )

  lazy val adoptingUkDateQuestions: Set[QuestionPage[_]] = Set(
    DateChildWasMatchedPage,
    ChildHasBeenPlacedPage,
    ChildExpectedPlacementDatePage,
    ChildPlacementDatePage,
  )

  lazy val paternityPagesGbPreApril24OrNi: Set[QuestionPage[_]] = Set(
    PaternityLeaveLengthGbPreApril24OrNiPage,
    PayStartDateGbPreApril24OrNiPage
  )

  lazy val paternityPagesGbPostApril24: Set[QuestionPage[_]] = Set(
    PaternityLeaveLengthGbPostApril24Page,
    LeaveTakenTogetherOrSeparatelyPage,
    PayStartDateGbPostApril24Page,
    PayStartDateWeek1Page,
    PayStartDateWeek2Page
  )

  lazy val allQuestionPages: Set[QuestionPage[_]] =
    Set(CountryOfResidencePage) ++
      filterQuestions ++
      applicantQuestions ++
      birthChildPaternityOrderDateQuestions ++
      adoptingAbroadDateQuestions ++
      adoptingUkDateQuestions ++
      paternityPagesGbPreApril24OrNi ++
      paternityPagesGbPostApril24

  def removeRedundantAnswers(userAnswers: UserAnswers, answersToRemove: Set[QuestionPage[_]]): Try[UserAnswers] =
    answersToRemove
      .foldLeft[Try[UserAnswers]](Success(userAnswers))(
        (acc, page) =>
          acc.flatMap(_.remove(page))
      )
}
