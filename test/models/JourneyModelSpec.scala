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

package models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import pages._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

class JourneyModelSpec extends AnyFreeSpec with Matchers with OptionValues with TryValues with EitherValues {

  ".from" - {

    val emptyUserAnswers = UserAnswers("id")

    "must return a completed journey model when the user stays that the child has already been born" - {

      val birthDate = LocalDate.now.minusDays(1)

      val answers = emptyUserAnswers
        .set(IsAdoptingPage, false).success.value
        .set(IsBiologicalFatherPage, true).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, true).success.value
        .set(NamePage, Name("foo", "bar")).success.value
        .set(NinoPage, Nino("AA123456A")).success.value
        .set(BabyHasBeenBornPage, true).success.value
        .set(BabyDateOfBirthPage, birthDate).success.value
        .set(WantPayToStartOnBirthDatePage, true).success.value
        .set(PaternityLeaveLengthPage, PaternityLeaveLength.Oneweek).success.value

      val expected = JourneyModel(
        eligibility = JourneyModel.Eligibility(
          becomingAdoptiveParents = false,
          biologicalFather = true,
          inRelationshipWithMother = None,
          livingWithMother = None,
          responsibilityForChild = true,
          timeOffToCareForChild = true,
          timeOffToSupportMother = None
        ),
        name = Name("foo", "bar"),
        nino = Nino("AA123456A"),
        hasTheBabyBeenBorn = true,
        birthDetails = JourneyModel.BirthDetails.AlreadyBorn(
          birthDate = birthDate,
          payShouldStartFromBirthDay = true,
          payStartDate = None
        ),
        howLongWillYouBeOnLeave = PaternityLeaveLength.Oneweek
      )

      "and the user wants payment to start from the child's birth date" in {
        JourneyModel.from(answers).right.value mustEqual expected
      }

      "and the user does not want payment to start from the child's birth date" in {

        val payStartDate = birthDate.plusDays(1)

        val answersWithPayDate = answers
          .set(WantPayToStartOnBirthDatePage, false).success.value
          .set(PayStartDateBabyBornPage, payStartDate).success.value

        val expectedWithPayStartDate = expected.copy(
          birthDetails = JourneyModel.BirthDetails.AlreadyBorn(
            birthDate = birthDate,
            payShouldStartFromBirthDay = false,
            payStartDate = Some(payStartDate)
          )
        )

        JourneyModel.from(answersWithPayDate).right.value mustEqual expectedWithPayStartDate
      }
    }

    "must return a completed journey model when the user stays that the child has not yet been born" - {

      val babyDueDate = LocalDate.now.plusDays(1)

      val answers = emptyUserAnswers
        .set(IsAdoptingPage, false).success.value
        .set(IsBiologicalFatherPage, false).success.value
        .set(IsInQualifyingRelationshipPage, true).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, false).success.value
        .set(WillTakeTimeToSupportMotherPage, true).success.value
        .set(NamePage, Name("foo", "bar")).success.value
        .set(NinoPage, Nino("AA123456A")).success.value
        .set(BabyHasBeenBornPage, false).success.value
        .set(BabyDueDatePage, babyDueDate).success.value
        .set(WantPayToStartOnDueDatePage, true).success.value
        .set(PaternityLeaveLengthPage, PaternityLeaveLength.Oneweek).success.value

      val expected = JourneyModel(
        eligibility = JourneyModel.Eligibility(
          becomingAdoptiveParents = false,
          biologicalFather = false,
          inRelationshipWithMother = Some(true),
          livingWithMother = None,
          responsibilityForChild = true,
          timeOffToCareForChild = false,
          timeOffToSupportMother = Some(true)
        ),
        name = Name("foo", "bar"),
        nino = Nino("AA123456A"),
        hasTheBabyBeenBorn = false,
        birthDetails = JourneyModel.BirthDetails.Due(
          dueDate = babyDueDate,
          payShouldStartFromDueDate = true,
          payStartDate = None
        ),
        howLongWillYouBeOnLeave = PaternityLeaveLength.Oneweek
      )

      "and the user wants payment to start from the child's due date" in {
        JourneyModel.from(answers).right.value mustEqual expected
      }

      "and the user does not want payment to start from the child's due date" in {

        val payStartDate = babyDueDate.plusDays(1)

        val answersWithPayDate = answers
          .set(WantPayToStartOnDueDatePage, false).success.value
          .set(PayStartDateBabyDuePage, payStartDate).success.value

        val expectedWithPayStartDate = expected.copy(
          birthDetails = JourneyModel.BirthDetails.Due(
            dueDate = babyDueDate,
            payShouldStartFromDueDate = false,
            payStartDate = Some(payStartDate)
          )
        )

        JourneyModel.from(answersWithPayDate).right.value mustEqual expectedWithPayStartDate
      }
    }

    "must return all pages that have failed" in {

      val errors = JourneyModel.from(emptyUserAnswers).left.value.toChain.toList

      errors must contain only(
        IsAdoptingPage,
        IsBiologicalFatherPage,
        WillHaveCaringResponsibilityPage,
        WillTakeTimeToCareForChildPage,
        NamePage,
        NinoPage,
        BabyHasBeenBornPage,
        PaternityLeaveLengthPage
      )
    }

    "must return the cannot apply when adopting page when the user says they are adopting" in {

      val answers = emptyUserAnswers
        .set(IsAdoptingPage, true).success.value

      val errors = JourneyModel.from(answers).left.value.toChain.toList

      errors must contain (
        CannotApplyAdoptingPage
      )
    }

    "must return the cannot apply page" - {

      "when the user is not in a relationship or cohabiting with the mother" in {

        val answers = emptyUserAnswers
          .set(IsAdoptingPage, false).success.value
          .set(IsBiologicalFatherPage, false).success.value
          .set(IsInQualifyingRelationshipPage, false).success.value
          .set(IsCohabitingPage, false).success.value

        val errors = JourneyModel.from(answers).left.value.toChain.toList

        errors must contain (
          CannotApplyPage
        )
      }

      "when the user is not responsible for caring for the child" in {

        val answers = emptyUserAnswers
          .set(IsAdoptingPage, false).success.value
          .set(IsBiologicalFatherPage, true).success.value
          .set(WillHaveCaringResponsibilityPage, false).success.value

        val errors = JourneyModel.from(answers).left.value.toChain.toList

        errors must contain (
          CannotApplyPage
        )
      }

      "when the user is not taking time off to care for either the child or the mother" in {

        val answers = emptyUserAnswers
          .set(IsAdoptingPage, false).success.value
          .set(IsBiologicalFatherPage, true).success.value
          .set(WillHaveCaringResponsibilityPage, true).success.value
          .set(WillTakeTimeToCareForChildPage, false).success.value
          .set(WillTakeTimeToSupportMotherPage, false).success.value

        val errors = JourneyModel.from(answers).left.value.toChain.toList

        errors must contain (
          CannotApplyPage
        )
      }
    }
  }
}
