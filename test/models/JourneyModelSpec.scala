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

import config.Constants
import generators.Generators
import models.CountryOfResidence._
import models.JourneyModel._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

class JourneyModelSpec
  extends AnyFreeSpec
    with Matchers
    with OptionValues
    with TryValues
    with EitherValues
    with Generators
    with ScalaCheckPropertyChecks {

  private val nino = arbitrary[Nino].sample.value
  private val dateBeforeLegislation = LocalDate.of(2000, 1, 1)
  private val dateAfterLegislation = LocalDate.of(2100, 1, 1)
  
  ".from" - {

    val emptyUserAnswers = UserAnswers("id")

    "must return a completed journey model" - {

      "when the user says that a birth child has already been born" - {

        "in Northern Ireland" in {

          forAll(datesBetween(dateBeforeLegislation, dateAfterLegislation)) { dueDate =>

            val birthDate = LocalDate.now
            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, NorthernIreland).success.value
              .set(IsAdoptingOrParentalOrderPage, false).success.value
              .set(IsBiologicalFatherPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, true).success.value
              .set(BabyDateOfBirthPage, birthDate).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = NorthernIreland,
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
              childDetails = BirthParentalOrderChild(dueDate, Some(birthDate)),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales before 7 April 24" in {

          val gen = for {
            date <- datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective.minusDays(1))
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)
          
          forAll(gen) { case (dueDate, country) =>

            val birthDate = LocalDate.now
            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, false).success.value
              .set(IsBiologicalFatherPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, true).success.value
              .set(BabyDateOfBirthPage, birthDate).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
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
              childDetails = BirthParentalOrderChild(dueDate, Some(birthDate)),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales on or after 7 April 24" in {

          val gen = for {
            date <- datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (dueDate, country) =>

            val birthDate = LocalDate.now

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, false).success.value
              .set(IsBiologicalFatherPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, true).success.value
              .set(BabyDateOfBirthPage, birthDate).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.Unsure).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
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
              childDetails = BirthParentalOrderChild(dueDate, Some(birthDate)),
              paternityLeaveDetails = PaternityLeaveGbPostApril24Unsure
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }
      }

      "when the user says that a birth child has not yet been born" - {

        "in Northern Ireland" in {

          forAll(datesBetween(dateBeforeLegislation, dateAfterLegislation)) { dueDate =>

            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, NorthernIreland).success.value
              .set(IsAdoptingOrParentalOrderPage, false).success.value
              .set(IsBiologicalFatherPage, false).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, false).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = NorthernIreland,
              eligibility = JourneyModel.BirthChildEligibility(
                biologicalFather = false,
                inRelationshipWithMother = Some(true),
                livingWithMother = None,
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, None),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales and due before 7 April 2024" in {

          val gen = for {
            date <- datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (dueDate, country) =>

            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, false).success.value
              .set(IsBiologicalFatherPage, false).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, false).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.BirthChildEligibility(
                biologicalFather = false,
                inRelationshipWithMother = Some(true),
                livingWithMother = None,
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, None),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales and due on or after 7 April 2024" in {

          val gen = for {
            date <- datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (dueDate, country) =>

            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, false).success.value
              .set(IsBiologicalFatherPage, false).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, false).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
              .set(PayStartDateGbPostApril24Page, Some(payStartDate)).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.BirthChildEligibility(
                biologicalFather = false,
                inRelationshipWithMother = Some(true),
                livingWithMother = None,
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, None),
              paternityLeaveDetails = PaternityLeaveGbPostApril24OneWeek(Some(payStartDate))
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }
      }

      "when the user says that a parental order child has already been born" - {

        "in Northern Ireland" in {

          forAll(datesBetween(dateBeforeLegislation, dateAfterLegislation)) { dueDate =>

            val birthDate = LocalDate.now.minusDays(1)
            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, NorthernIreland).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, true).success.value
              .set(BabyDateOfBirthPage, birthDate).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = NorthernIreland,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = RelationshipToChild.ParentalOrder,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, Some(birthDate)),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales before 7 April 2024" in {

          val gen = for {
            date <- datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (dueDate, country) =>

            val birthDate = LocalDate.now.minusDays(1)
            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, true).success.value
              .set(BabyDateOfBirthPage, birthDate).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = RelationshipToChild.ParentalOrder,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, Some(birthDate)),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales on or after 7 April 2024" in {

          val gen = for {
            date <- datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (dueDate, country) =>

            val birthDate = LocalDate.now.minusDays(1)
            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, true).success.value
              .set(BabyDateOfBirthPage, birthDate).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value
              .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
              .set(PayStartDateGbPostApril24Page, Some(payStartDate)).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = RelationshipToChild.ParentalOrder,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, Some(birthDate)),
              paternityLeaveDetails = PaternityLeaveGbPostApril24TwoWeeksTogether(Some(payStartDate))
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }
      }

      "when the user says that a parental order child has not yet been born" - {

        "in Northern Ireland" in {

          forAll(datesBetween(dateBeforeLegislation, dateAfterLegislation)) { dueDate =>

            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, NorthernIreland).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, false).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = NorthernIreland,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = RelationshipToChild.ParentalOrder,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, None),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales and due before 7 April 24" in {

          val gen = for {
            date <- datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (dueDate, country) =>

            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, false).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = RelationshipToChild.ParentalOrder,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, None),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales and due after 7 April 24" in {

          val gen = for {
            date <- datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (dueDate, country) =>

            val payStartDate = LocalDate.now.plusMonths(1)

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(BabyHasBeenBornPage, false).success.value
              .set(BabyDueDatePage, dueDate).success.value
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value
              .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value
              .set(PayStartDateWeek1Page, Some(payStartDate)).success.value
              .set(PayStartDateWeek2Page, None).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = RelationshipToChild.ParentalOrder,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = BirthParentalOrderChild(dueDate, None),
              paternityLeaveDetails = PaternityLeaveGbPostApril24TwoWeeksSeparate(Some(payStartDate), None)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }
      }

      "when the user says that a UK adopted child has already been placed" - {

        "in Northern Ireland" in {

          forAll(datesBetween(dateBeforeLegislation, dateAfterLegislation)) { placementDate =>

            val matchedDate = LocalDate.now.minusDays(2)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, NorthernIreland).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateChildWasMatchedPage, matchedDate).success.value
              .set(ChildHasBeenPlacedPage, true).success.value
              .set(ChildPlacementDatePage, placementDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = NorthernIreland,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = relationship,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedUkChild(matchedDate, hasBeenPlaced = true, placementDate),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales placed before 7 April 24" in {

          val gen = for {
            date <- datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (placementDate, country) =>

            val matchedDate = LocalDate.now.minusDays(2)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateChildWasMatchedPage, matchedDate).success.value
              .set(ChildHasBeenPlacedPage, true).success.value
              .set(ChildPlacementDatePage, placementDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = relationship,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedUkChild(matchedDate, hasBeenPlaced = true, placementDate),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales placed on or after 7 April 24" in {

          val gen = for {
            date <- datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (placementDate, country) =>

            val matchedDate = LocalDate.now.minusDays(2)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateChildWasMatchedPage, matchedDate).success.value
              .set(ChildHasBeenPlacedPage, true).success.value
              .set(ChildPlacementDatePage, placementDate).success.value
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
              .set(PayStartDateGbPostApril24Page, None).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = relationship,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedUkChild(matchedDate, hasBeenPlaced = true, placementDate),
              paternityLeaveDetails = PaternityLeaveGbPostApril24OneWeek(None)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }
      }

      "when the user says that a UK adopted child has not yet been placed" - {

        "in Northern Ireland" in {

          forAll(datesBetween(dateBeforeLegislation, dateAfterLegislation)) { expectedPlacementDate =>

            val matchedDate = LocalDate.now.plusDays(1)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, NorthernIreland).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateChildWasMatchedPage, matchedDate).success.value
              .set(ChildHasBeenPlacedPage, false).success.value
              .set(ChildExpectedPlacementDatePage, expectedPlacementDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = NorthernIreland,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = relationship,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedUkChild(matchedDate, hasBeenPlaced = false, expectedPlacementDate),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales with an expected placement date before 7 April 24" in {

          val gen = for {
            date <- datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (expectedPlacementDate, country) =>

            val matchedDate = LocalDate.now.plusDays(1)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateChildWasMatchedPage, matchedDate).success.value
              .set(ChildHasBeenPlacedPage, false).success.value
              .set(ChildExpectedPlacementDatePage, expectedPlacementDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = relationship,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedUkChild(matchedDate, hasBeenPlaced = false, expectedPlacementDate),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales with an expected placement date on or after 7 April 24" in {

          val gen = for {
            date <- datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (expectedPlacementDate, country) =>

            val matchedDate = LocalDate.now.plusDays(1)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateChildWasMatchedPage, matchedDate).success.value
              .set(ChildHasBeenPlacedPage, false).success.value
              .set(ChildExpectedPlacementDatePage, expectedPlacementDate).success.value
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.Unsure).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = false,
                reasonForRequesting = relationship,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedUkChild(matchedDate, hasBeenPlaced = false, expectedPlacementDate),
              paternityLeaveDetails = PaternityLeaveGbPostApril24Unsure
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }
      }

      "when the user says that a child adopted from abroad has entered the UK" - {

        "in Northern Ireland" in {

          forAll(datesBetween(dateBeforeLegislation, dateAfterLegislation)) { enteredUkDate =>

            val notifiedDate = LocalDate.now.minusDays(2)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, NorthernIreland).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, true).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateOfAdoptionNotificationPage, notifiedDate).success.value
              .set(ChildHasEnteredUkPage, true).success.value
              .set(DateChildEnteredUkPage, enteredUkDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = NorthernIreland,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = true,
                reasonForRequesting = relationship,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedAbroadChild(notifiedDate, hasEnteredUk = true, enteredUkDate),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales and they entered the UK before 7 April 24" in {

          val gen = for {
            date <- datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (enteredUkDate, country) =>

            val notifiedDate = LocalDate.now.minusDays(2)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, true).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateOfAdoptionNotificationPage, notifiedDate).success.value
              .set(ChildHasEnteredUkPage, true).success.value
              .set(DateChildEnteredUkPage, enteredUkDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = true,
                reasonForRequesting = relationship,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedAbroadChild(notifiedDate, hasEnteredUk = true, enteredUkDate),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales and they entered the UK on or after 7 April 24" in {

          val gen = for {
            date <- datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (enteredUkDate, country) =>

            val notifiedDate = LocalDate.now.minusDays(2)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, true).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateOfAdoptionNotificationPage, notifiedDate).success.value
              .set(ChildHasEnteredUkPage, true).success.value
              .set(DateChildEnteredUkPage, enteredUkDate).success.value
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value
              .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
              .set(PayStartDateGbPostApril24Page, None).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = true,
                reasonForRequesting = relationship,
                inQualifyingRelationship = true,
                livingWithPartner = None,
                responsibilityForChild = true,
                timeOffToCareForChild = true,
                timeOffToSupportPartner = None
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedAbroadChild(notifiedDate, hasEnteredUk = true, enteredUkDate),
              paternityLeaveDetails = PaternityLeaveGbPostApril24TwoWeeksTogether(None)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }
      }

      "when the user says that a child adopted from abroad has not yet entered the UK" - {

        "in Northern Ireland" in {

          forAll(datesBetween(dateBeforeLegislation, dateAfterLegislation)) { expectedUkEntryDate =>

            val notifiedDate = LocalDate.now.plusDays(1)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, NorthernIreland).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, true).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateOfAdoptionNotificationPage, notifiedDate).success.value
              .set(ChildHasEnteredUkPage, false).success.value
              .set(DateChildExpectedToEnterUkPage, expectedUkEntryDate).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value

            val expected = JourneyModel(
              countryOfResidence = NorthernIreland,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = true,
                reasonForRequesting = relationship,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedAbroadChild(notifiedDate, hasEnteredUk = false, expectedUkEntryDate),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales and they are expected to enter the UK before 7 April 24" in {

          val gen = for {
            date <- datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (expectedUkEntryDate, country) =>

            val notifiedDate = LocalDate.now.plusDays(1)
            val payStartDate = LocalDate.now.plusMonths(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, true).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateOfAdoptionNotificationPage, notifiedDate).success.value
              .set(ChildHasEnteredUkPage, false).success.value
              .set(DateChildExpectedToEnterUkPage, expectedUkEntryDate).success.value
              .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
              .set(PayStartDateGbPreApril24OrNiPage, payStartDate).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = true,
                reasonForRequesting = relationship,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedAbroadChild(notifiedDate, hasEnteredUk = false, expectedUkEntryDate),
              paternityLeaveDetails = PaternityLeaveGbPreApril24OrNi(PaternityLeaveLengthGbPreApril24OrNi.OneWeek, payStartDate)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }

        "in England, Scotland or Wales and they are expected to enter the UK on or after 7 April 24" in {

          val gen = for {
            date <- datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)
            country <- Gen.oneOf(England, Scotland, Wales)
          } yield (date, country)

          forAll(gen) { case (expectedUkEntryDate, country) =>

            val notifiedDate = LocalDate.now.plusDays(1)
            val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

            val answers = emptyUserAnswers
              .set(CountryOfResidencePage, country).success.value
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
              .set(IsAdoptingFromAbroadPage, true).success.value
              .set(ReasonForRequestingPage, relationship).success.value
              .set(IsInQualifyingRelationshipPage, false).success.value
              .set(IsCohabitingPage, true).success.value
              .set(WillHaveCaringResponsibilityPage, true).success.value
              .set(WillTakeTimeToCareForChildPage, false).success.value
              .set(WillTakeTimeToSupportPartnerPage, true).success.value
              .set(NamePage, Name("foo", "bar")).success.value
              .set(NinoPage, nino).success.value
              .set(DateOfAdoptionNotificationPage, notifiedDate).success.value
              .set(ChildHasEnteredUkPage, false).success.value
              .set(DateChildExpectedToEnterUkPage, expectedUkEntryDate).success.value
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
              .set(PayStartDateGbPostApril24Page, None).success.value

            val expected = JourneyModel(
              countryOfResidence = country,
              eligibility = JourneyModel.AdoptionParentalOrderEligibility(
                applyingForStatutoryAdoptionPay = false,
                adoptingFromAbroad = true,
                reasonForRequesting = relationship,
                inQualifyingRelationship = false,
                livingWithPartner = Some(true),
                responsibilityForChild = true,
                timeOffToCareForChild = false,
                timeOffToSupportPartner = Some(true)
              ),
              name = Name("foo", "bar"),
              nino = nino,
              childDetails = AdoptedAbroadChild(notifiedDate, hasEnteredUk = false, expectedUkEntryDate),
              paternityLeaveDetails = PaternityLeaveGbPostApril24OneWeek(None)
            )

            JourneyModel.from(answers).value mustEqual expected
          }
        }
      }
    }

    "must return all pages that have failed" in {

      val errors = JourneyModel.from(emptyUserAnswers).left.value.toChain.toList

      errors.distinct must contain theSameElementsInOrderAs Seq(
        CountryOfResidencePage,
        IsAdoptingOrParentalOrderPage,
        NamePage,
        NinoPage
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
