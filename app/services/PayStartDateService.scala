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

package services

import cats.data.EitherNec
import cats.implicits._
import models.{PaternityLeaveLengthGbPreApril24OrNi, PayStartDateLimits, RelationshipToChild, UserAnswers}
import pages._

import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.next
import javax.inject.Inject

class PayStartDateService @Inject() {

  def gbPreApril24OrNiDates(answers: UserAnswers): EitherNec[QuestionPage[_], PayStartDateLimits] = {

    def maxWeeksAllowed(leaveLength: PaternityLeaveLengthGbPreApril24OrNi): Int =
      leaveLength match {
        case PaternityLeaveLengthGbPreApril24OrNi.OneWeek  => 7
        case PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks => 6
      }

    def getLimitsBasedOnDate(datePage: QuestionPage[LocalDate], answers: UserAnswers, extendToEndOfWeek: Boolean): EitherNec[QuestionPage[_], PayStartDateLimits] =
      (
        answers.getEither(datePage),
        answers.getEither(PaternityLeaveLengthGbPreApril24OrNiPage).map(maxWeeksAllowed)
      ).parMapN { case (date, maxWeeks) =>
        val endDate = if (extendToEndOfWeek) date.plusWeeks(maxWeeks).`with`(next(SUNDAY)) else date.plusWeeks(maxWeeks)
        PayStartDateLimits(date.plusDays(1), endDate)
      }

    def getBirthParentalOrderDates(answers: UserAnswers): EitherNec[QuestionPage[_], PayStartDateLimits] =
      answers.getEither(BabyHasBeenBornPage).ifM(
        ifTrue  = getLimitsBasedOnDate(BabyDateOfBirthPage, answers, extendToEndOfWeek = false),
        ifFalse = getLimitsBasedOnDate(BabyDueDatePage, answers, extendToEndOfWeek = true)
      )

    answers.getEither(IsAdoptingOrParentalOrderPage).ifM(
      ifTrue = answers.getEither(ReasonForRequestingPage).flatMap {
        case RelationshipToChild.ParentalOrder =>
          getBirthParentalOrderDates(answers)

        case _ =>
          answers.getEither(IsAdoptingFromAbroadPage).ifM(
            ifTrue  = answers.getEither(ChildHasEnteredUkPage).ifM(
              ifTrue  = getLimitsBasedOnDate(DateChildEnteredUkPage, answers, extendToEndOfWeek = true),
              ifFalse = getLimitsBasedOnDate(DateChildExpectedToEnterUkPage, answers, extendToEndOfWeek = true)
            ),
            ifFalse = answers.getEither(ChildHasBeenPlacedPage).ifM(
              ifTrue  = getLimitsBasedOnDate(ChildPlacementDatePage, answers, extendToEndOfWeek = true),
              ifFalse = getLimitsBasedOnDate(ChildExpectedPlacementDatePage, answers, extendToEndOfWeek = true)
            )
          )
      },
      ifFalse = getBirthParentalOrderDates(answers)
    )
  }
}