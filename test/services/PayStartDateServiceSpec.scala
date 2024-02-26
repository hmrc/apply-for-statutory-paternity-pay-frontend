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

import generators.Generators
import models.RelationshipToChild._
import models.{PaternityLeaveLengthGbPreApril24OrNi, PayStartDateLimits, RelationshipToChild, UserAnswers}
import org.scalacheck.Gen
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.next

class PayStartDateServiceSpec
  extends AnyFreeSpec
    with Matchers
    with OptionValues
    with TryValues
    with EitherValues
    with ScalaCheckPropertyChecks
    with Generators {

  private val service = new PayStartDateService()

  ".gbPreApril24OrNiDates" - {

    val lowDate = LocalDate.of(2000, 1, 1)
    val highDate = LocalDate.of(2100, 1, 1)

    "when a birth child has been born" - {

      "and the user is taking one week" - {

        "must give limits of one day after the DOB to 7 weeks after the DOB" in {

          forAll(datesBetween(lowDate, highDate)) { date =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, false).success.value
                .set(BabyHasBeenBornPage, true).success.value
                .set(BabyDateOfBirthPage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(7))
          }
        }
      }

      "and the user is taking two weeks" - {

        "must give limits of one day after the DOB to 6 weeks after the DOB" in {

          forAll(datesBetween(lowDate, highDate)) { date =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, false).success.value
                .set(BabyHasBeenBornPage, true).success.value
                .set(BabyDateOfBirthPage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(6))
          }
        }
      }
    }

    "when a birth child is due" - {

      "and the user is taking one week" - {

        "must give limits of one day after the due date to the Sunday after 7 weeks after the due date" in {

          forAll(datesBetween(lowDate, highDate)) { date =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, false).success.value
                .set(BabyHasBeenBornPage, false).success.value
                .set(BabyDueDatePage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(7).`with`(next(SUNDAY)))
          }
        }
      }

      "and the user is taking two weeks" - {

        "must give limits of one day after the due date to the Sunday after 6 weeks after the due date" in {

          forAll(datesBetween(lowDate, highDate)) { date =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, false).success.value
                .set(BabyHasBeenBornPage, false).success.value
                .set(BabyDueDatePage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(6).`with`(next(SUNDAY)))
          }
        }
      }
    }

    "when a parental order child has been born" - {

      "and the user is taking one week" - {

        "must give limits of one day after the DOB to 7 weeks after the DOB" in {

          forAll(datesBetween(lowDate, highDate)) { date =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(ReasonForRequestingPage, ParentalOrder).success.value
                .set(BabyHasBeenBornPage, true).success.value
                .set(BabyDateOfBirthPage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(7))
          }
        }
      }

      "and the user is taking two weeks" - {

        "must give limits of one day after the DOB to 6 weeks after the DOB" in {

          forAll(datesBetween(lowDate, highDate)) { date =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(ReasonForRequestingPage, ParentalOrder).success.value
                .set(BabyHasBeenBornPage, true).success.value
                .set(BabyDateOfBirthPage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(6))
          }
        }
      }
    }

    "when a parental order child is due" - {

      "and the user is taking one week" - {

        "must give limits of one day after the due date to the Sunday after 7 weeks after the due date" in {

          forAll(datesBetween(lowDate, highDate)) { date =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(ReasonForRequestingPage, ParentalOrder).success.value
                .set(BabyHasBeenBornPage, false).success.value
                .set(BabyDueDatePage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(7).`with`(next(SUNDAY)))
          }
        }
      }

      "and the user is taking two weeks" - {

        "must give limits of one day after the due date to the Sunday after 6 weeks after the due date" in {

          forAll(datesBetween(lowDate, highDate)) { date =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(ReasonForRequestingPage, ParentalOrder).success.value
                .set(BabyHasBeenBornPage, false).success.value
                .set(BabyDueDatePage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(6).`with`(next(SUNDAY)))
          }
        }
      }
    }

    "when a child being adopted in the UK has been placed" - {

      "and the user is taking one week" - {

        "must give limits of one day after the placement date to the Sunday after 7 weeks after the placement date" in {
          
          forAll(datesBetween(lowDate, highDate), Gen.oneOf(Adopting, SupportingAdoption)) { case (date, relationship) =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, false).success.value
                .set(ReasonForRequestingPage, relationship).success.value
                .set(ChildHasBeenPlacedPage, true).success.value
                .set(ChildPlacementDatePage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(7).`with`(next(SUNDAY)))
          }
        }
      }

      "and the user is taking two weeks" - {

        "must give limits of one day after the placement date to the Sunday after 6 weeks after the placement date" in {

          forAll(datesBetween(lowDate, highDate), Gen.oneOf(Adopting, SupportingAdoption)) { case (date, relationship) =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, false).success.value
                .set(ReasonForRequestingPage, relationship).success.value
                .set(ChildHasBeenPlacedPage, true).success.value
                .set(ChildPlacementDatePage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(6).`with`(next(SUNDAY)))
          }
        }
      }
    }

    "when a child being adopted in the UK has not been placed" - {

      "and the user is taking one week" - {

        "must give limits of one day after the expected placement date to the Sunday after 7 weeks after the placement date" in {

          forAll(datesBetween(lowDate, highDate), Gen.oneOf(Adopting, SupportingAdoption)) { case (date, relationship) =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, false).success.value
                .set(ReasonForRequestingPage, relationship).success.value
                .set(ChildHasBeenPlacedPage, false).success.value
                .set(ChildExpectedPlacementDatePage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(7).`with`(next(SUNDAY)))
          }
        }
      }

      "and the user is taking two weeks" - {

        "must give limits of one day after the expected placement date to the Sunday after 6 weeks after the placement date" in {

          forAll(datesBetween(lowDate, highDate), Gen.oneOf(Adopting, SupportingAdoption)) { case (date, relationship) =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, false).success.value
                .set(ReasonForRequestingPage, relationship).success.value
                .set(ChildHasBeenPlacedPage, false).success.value
                .set(ChildExpectedPlacementDatePage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(6).`with`(next(SUNDAY)))
          }
        }
      }
    }

    "when a child being adopted from abroad has entered the UK" - {

      "and the user is taking one week" - {

        "must give limits of one day after the entry date to the Sunday after 7 weeks after the entry date" in {

          forAll(datesBetween(lowDate, highDate), Gen.oneOf(Adopting, SupportingAdoption)) { case (date, relationship) =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, true).success.value
                .set(ReasonForRequestingPage, relationship).success.value
                .set(ChildHasEnteredUkPage, true).success.value
                .set(DateChildEnteredUkPage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(7).`with`(next(SUNDAY)))
          }
        }
      }

      "and the user is taking two weeks" - {

        "must give limits of one day after the entry date to the Sunday after 6 weeks after the entry date" in {

          forAll(datesBetween(lowDate, highDate), Gen.oneOf(Adopting, SupportingAdoption)) { case (date, relationship) =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, true).success.value
                .set(ReasonForRequestingPage, relationship).success.value
                .set(ChildHasEnteredUkPage, true).success.value
                .set(DateChildEnteredUkPage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(6).`with`(next(SUNDAY)))
          }
        }
      }
    }

    "when a child being adopted from abroad has not yet entered the UK" - {

      "and the user is taking one week" - {

        "must give limits of one day after the expected entry date to the Sunday after 7 weeks after the expected entry date" in {

          forAll(datesBetween(lowDate, highDate), Gen.oneOf(Adopting, SupportingAdoption)) { case (date, relationship) =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, true).success.value
                .set(ReasonForRequestingPage, relationship).success.value
                .set(ChildHasEnteredUkPage, false).success.value
                .set(DateChildExpectedToEnterUkPage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(7).`with`(next(SUNDAY)))
          }
        }
      }

      "and the user is taking two weeks" - {

        "must give limits of one day after the expected entry date to the Sunday after 6 weeks after the expected entry date" in {

          forAll(datesBetween(lowDate, highDate), Gen.oneOf(Adopting, SupportingAdoption)) { case (date, relationship) =>
            val answers =
              UserAnswers("id")
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, true).success.value
                .set(ReasonForRequestingPage, relationship).success.value
                .set(ChildHasEnteredUkPage, false).success.value
                .set(DateChildExpectedToEnterUkPage, date).success.value
                .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value

            val result = service.gbPreApril24OrNiDates(answers).value

            result mustEqual PayStartDateLimits(date.plusDays(1), date.plusWeeks(6).`with`(next(SUNDAY)))
          }
        }
      }
    }
  }
}
