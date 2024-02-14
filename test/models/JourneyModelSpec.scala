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

import generators.ModelGenerators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import pages._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

class JourneyModelSpec extends AnyFreeSpec with Matchers with OptionValues with TryValues with EitherValues with ModelGenerators {

  private val nino = arbitrary[Nino].sample.value
  
  ".from" - {

    val emptyUserAnswers = UserAnswers("id")

    "must return a completed journey model when the user says that the child has already been born" in {

      val dueDate = LocalDate.now.minusDays(2)
      val birthDate = LocalDate.now.minusDays(1)
      val payStartDate = LocalDate.now.plusMonths(1)

      val answers = emptyUserAnswers
        .set(CountryOfResidencePage, CountryOfResidence.England).success.value
        .set(IsAdoptingOrParentalOrderPage, false).success.value
        .set(IsBiologicalFatherPage, true).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, true).success.value
        .set(NamePage, Name("foo", "bar")).success.value
        .set(NinoPage, nino).success.value
        .set(BabyHasBeenBornPage, true).success.value
        .set(BabyDateOfBirthPage, birthDate).success.value
        .set(BabyDueDatePage, dueDate).success.value
        .set(PayStartDateBabyBornPage, payStartDate).success.value
        .set(PaternityLeaveLengthPage, PaternityLeaveLength.Oneweek).success.value

      val expected = JourneyModel(
        countryOfResidence = CountryOfResidence.England,
        eligibility = JourneyModel.BirthChildEligibility(
          biologicalFather = true,
          inRelationshipWithMother = None,
          livingWithMother = None,
          responsibilityForChild = true,
          timeOffToCareForChild = true,
          timeOffToSupportPartner = None
        ),
        name = Name("foo", "bar"),
        nino = nino,
        hasTheBabyBeenBorn = true,
        dueDate = dueDate,
        birthDate = Some(birthDate),
        payStartDate = payStartDate,
        howLongWillYouBeOnLeave = PaternityLeaveLength.Oneweek
      )

      JourneyModel.from(answers).right.value mustEqual expected
    }

    "must return a completed journey model when the user stays that the child has not yet been born" in {

      val babyDueDate = LocalDate.now.plusDays(1)
      val payStartDate = LocalDate.now.plusMonths(1)

      val answers = emptyUserAnswers
        .set(CountryOfResidencePage, CountryOfResidence.England).success.value
        .set(IsAdoptingOrParentalOrderPage, true).success.value
        .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
        .set(IsAdoptingFromAbroadPage, false).success.value
        .set(ReasonForRequestingPage, RelationshipToChild.Adopting).success.value
        .set(IsInQualifyingRelationshipPage, true).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, false).success.value
        .set(WillTakeTimeToSupportPartnerPage, true).success.value
        .set(NamePage, Name("foo", "bar")).success.value
        .set(NinoPage, nino).success.value
        .set(BabyHasBeenBornPage, false).success.value
        .set(BabyDueDatePage, babyDueDate).success.value
        .set(PayStartDateBabyDuePage, payStartDate).success.value
        .set(PaternityLeaveLengthPage, PaternityLeaveLength.Oneweek).success.value

      val expected = JourneyModel(
        countryOfResidence = CountryOfResidence.England,
        eligibility = JourneyModel.AdoptionParentalOrderEligibility(
          applyingForStatutoryAdoptionPay = false,
          adoptingFromAbroad = false,
          reasonForRequesting = RelationshipToChild.Adopting,
          inQualifyingRelationship = true,
          livingWithPartner = None,
          responsibilityForChild = true,
          timeOffToCareForChild = false,
          timeOffToSupportPartner = Some(true)
        ),
        name = Name("foo", "bar"),
        nino = nino,
        hasTheBabyBeenBorn = false,
        dueDate = babyDueDate,
        birthDate = None,
        payStartDate = payStartDate,
        howLongWillYouBeOnLeave = PaternityLeaveLength.Oneweek
      )

      JourneyModel.from(answers).right.value mustEqual expected
    }

    "must return all pages that have failed" in {

      val errors = JourneyModel.from(emptyUserAnswers).left.value.toChain.toList

      errors must contain only(
        CountryOfResidencePage,
        IsAdoptingOrParentalOrderPage,
        NamePage,
        NinoPage,
        BabyDueDatePage,
        BabyHasBeenBornPage,
        PaternityLeaveLengthPage
      )
    }

    "must return the Applying for Statutory Adoption Pay page when the user is applying for SAP" in {

      val answers = emptyUserAnswers
        .set(IsAdoptingOrParentalOrderPage, true).success.value
        .set(IsApplyingForStatutoryAdoptionPayPage, true).success.value

      val errors = JourneyModel.from(answers).left.value.toChain.toList

      errors must contain (IsApplyingForStatutoryAdoptionPayPage)
    }

    "must return the is cohabiting page when the user is adopting or parental order and is not in a relationship or cohabiting with the mother" in {

      val answers = emptyUserAnswers
        .set(IsAdoptingOrParentalOrderPage, true).success.value
        .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
        .set(IsAdoptingFromAbroadPage, false).success.value
        .set(ReasonForRequestingPage, RelationshipToChild.Adopting).success.value
        .set(IsInQualifyingRelationshipPage, false).success.value
        .set(IsCohabitingPage, false).success.value

      val errors = JourneyModel.from(answers).left.value.toChain.toList

      errors must contain(
        IsCohabitingPage
      )
    }

    "must return the is cohabiting page when the user is not in a relationship or cohabiting with the mother" in {

      val answers = emptyUserAnswers
        .set(IsAdoptingOrParentalOrderPage, false).success.value
        .set(IsBiologicalFatherPage, false).success.value
        .set(IsInQualifyingRelationshipPage, false).success.value
        .set(IsCohabitingPage, false).success.value

      val errors = JourneyModel.from(answers).left.value.toChain.toList

      errors must contain(
        IsCohabitingPage
      )
    }

    "must return the will have caring responsibility page when the user is not responsible for caring for the child" in {

      val answers = emptyUserAnswers
        .set(IsAdoptingOrParentalOrderPage, false).success.value
        .set(IsBiologicalFatherPage, true).success.value
        .set(WillHaveCaringResponsibilityPage, false).success.value

      val errors = JourneyModel.from(answers).left.value.toChain.toList

      errors must contain(
        WillHaveCaringResponsibilityPage
      )
    }

    "must return will take time to support mother page when the user is not taking time off to care for either the child or the mother" in {

      val answers = emptyUserAnswers
        .set(IsAdoptingOrParentalOrderPage, false).success.value
        .set(IsBiologicalFatherPage, true).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, false).success.value
        .set(WillTakeTimeToSupportPartnerPage, false).success.value

      val errors = JourneyModel.from(answers).left.value.toChain.toList

      errors must contain(
        WillTakeTimeToSupportPartnerPage
      )
    }
  }
}
