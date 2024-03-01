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

package pages

import json.OptionalLocalDateReads._
import models.{CountryOfResidence, LeaveTakenTogetherOrSeparately, Name, PaternityLeaveLengthGbPostApril24, PaternityLeaveLengthGbPreApril24OrNi, RelationshipToChild}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

class IsAdoptingOrParentalOrderPageSpec extends PageBehaviours {

  "IsAdoptingOrParentalOrderPage" - {

    beRetrievable[Boolean](IsAdoptingOrParentalOrderPage)

    beSettable[Boolean](IsAdoptingOrParentalOrderPage)

    beRemovable[Boolean](IsAdoptingOrParentalOrderPage)

    def fullAnswersNotAdopting =
      emptyUserAnswers
        .set(IsAdoptingOrParentalOrderPage, false).success.value
        .set(BabyDateOfBirthPage, LocalDate.now).success.value
        .set(BabyDueDatePage, LocalDate.now).success.value
        .set(BabyHasBeenBornPage, true).success.value
        .set(CountryOfResidencePage, CountryOfResidence.England).success.value
        .set(IsBiologicalFatherPage, false).success.value
        .set(IsCohabitingPage, true).success.value
        .set(IsInQualifyingRelationshipPage, false).success.value
        .set(NamePage, Name("first", "last")).success.value
        .set(NinoPage, arbitrary[Nino].sample.value).success.value
        .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
        .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, true).success.value
        .set(WillTakeTimeToSupportPartnerPage, true).success.value
        .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
        .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
        .set(PayStartDateGbPostApril24Page, Some(LocalDate.now)).success.value
        .set(PayStartDateWeek1Page, Some(LocalDate.now)).success.value
        .set(PayStartDateWeek2Page, Some(LocalDate.now)).success.value

    def fullAnswersAdopting =
      emptyUserAnswers
        .set(IsAdoptingOrParentalOrderPage, true).success.value
        .set(ReasonForRequestingPage, RelationshipToChild.Adopting).success.value
        .set(BabyDateOfBirthPage, LocalDate.now).success.value
        .set(BabyDueDatePage, LocalDate.now).success.value
        .set(BabyHasBeenBornPage, true).success.value
        .set(ChildHasBeenPlacedPage, true).success.value
        .set(ChildExpectedPlacementDatePage, LocalDate.now).success.value
        .set(ChildPlacementDatePage, LocalDate.now).success.value
        .set(CountryOfResidencePage, CountryOfResidence.England).success.value
        .set(DateChildWasMatchedPage, LocalDate.now).success.value
        .set(IsAdoptingFromAbroadPage, true).success.value
        .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
        .set(IsCohabitingPage, true).success.value
        .set(IsInQualifyingRelationshipPage, false).success.value
        .set(NamePage, Name("first", "last")).success.value
        .set(NinoPage, arbitrary[Nino].sample.value).success.value
        .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
        .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, true).success.value
        .set(WillTakeTimeToSupportPartnerPage, true).success.value
        .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
        .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
        .set(PayStartDateGbPostApril24Page, Some(LocalDate.now)).success.value
        .set(PayStartDateWeek1Page, Some(LocalDate.now)).success.value
        .set(PayStartDateWeek2Page, Some(LocalDate.now)).success.value

    "must remove all answers except this and Country of Residence" - {

      "when the answer changes from true to false" in {

        val answers = fullAnswersAdopting

        val result = answers.set(IsAdoptingOrParentalOrderPage, false).success.value

        result.get(IsAdoptingOrParentalOrderPage).value mustEqual false
        result.get(CountryOfResidencePage) mustBe defined

        result.get(BabyDateOfBirthPage) must not be defined
        result.get(BabyDueDatePage) must not be defined
        result.get(BabyHasBeenBornPage) must not be defined
        result.get(ChildExpectedPlacementDatePage) must not be defined
        result.get(ChildHasBeenPlacedPage) must not be defined
        result.get(ChildPlacementDatePage) must not be defined
        result.get(DateChildWasMatchedPage) must not be defined
        result.get(IsAdoptingFromAbroadPage) must not be defined
        result.get(IsApplyingForStatutoryAdoptionPayPage) must not be defined
        result.get(IsCohabitingPage) must not be defined
        result.get(IsInQualifyingRelationshipPage) must not be defined
        result.get(NamePage) must not be defined
        result.get(NinoPage) must not be defined
        result.get(PaternityLeaveLengthGbPreApril24OrNiPage) must not be defined
        result.get(PayStartDateGbPreApril24OrNiPage) must not be defined
        result.get(ReasonForRequestingPage) must not be defined
        result.get(WillHaveCaringResponsibilityPage) must not be defined
        result.get(WillTakeTimeToCareForChildPage) must not be defined
        result.get(WillTakeTimeToSupportPartnerPage) must not be defined
        result.get(PaternityLeaveLengthGbPostApril24Page) must not be defined
        result.get(LeaveTakenTogetherOrSeparatelyPage) must not be defined
        result.get(PayStartDateGbPostApril24Page) must not be defined
        result.get(PayStartDateWeek1Page) must not be defined
        result.get(PayStartDateWeek2Page) must not be defined
      }

      "when the answer changes from false to true" in {

        val answers = fullAnswersNotAdopting

        val result = answers.set(IsAdoptingOrParentalOrderPage, true).success.value

        result.get(IsAdoptingOrParentalOrderPage).value mustEqual true
        result.get(CountryOfResidencePage) mustBe defined

        result.get(BabyDateOfBirthPage) must not be defined
        result.get(BabyDueDatePage) must not be defined
        result.get(BabyHasBeenBornPage) must not be defined
        result.get(ChildExpectedPlacementDatePage) must not be defined
        result.get(ChildHasBeenPlacedPage) must not be defined
        result.get(ChildPlacementDatePage) must not be defined
        result.get(DateChildWasMatchedPage) must not be defined
        result.get(IsAdoptingFromAbroadPage) must not be defined
        result.get(IsApplyingForStatutoryAdoptionPayPage) must not be defined
        result.get(IsBiologicalFatherPage) must not be defined
        result.get(IsCohabitingPage) must not be defined
        result.get(IsInQualifyingRelationshipPage) must not be defined
        result.get(NamePage) must not be defined
        result.get(NinoPage) must not be defined
        result.get(PaternityLeaveLengthGbPreApril24OrNiPage) must not be defined
        result.get(PayStartDateGbPreApril24OrNiPage) must not be defined
        result.get(ReasonForRequestingPage) must not be defined
        result.get(WillHaveCaringResponsibilityPage) must not be defined
        result.get(WillTakeTimeToCareForChildPage) must not be defined
        result.get(WillTakeTimeToSupportPartnerPage) must not be defined
        result.get(PaternityLeaveLengthGbPostApril24Page) must not be defined
        result.get(LeaveTakenTogetherOrSeparatelyPage) must not be defined
        result.get(PayStartDateGbPostApril24Page) must not be defined
        result.get(PayStartDateWeek1Page) must not be defined
        result.get(PayStartDateWeek2Page) must not be defined
      }
    }

    "must not remove any answers when the answer does not change from true" in {

      val answers = fullAnswersAdopting

      val result = answers.set(IsAdoptingOrParentalOrderPage, true).success.value

      result.get(IsAdoptingOrParentalOrderPage) mustBe defined
      result.get(CountryOfResidencePage) mustBe defined

      result.get(BabyDateOfBirthPage) mustBe defined
      result.get(BabyDueDatePage) mustBe defined
      result.get(BabyHasBeenBornPage) mustBe defined
      result.get(ChildExpectedPlacementDatePage) mustBe defined
      result.get(ChildHasBeenPlacedPage) mustBe defined
      result.get(ChildPlacementDatePage) mustBe defined
      result.get(DateChildWasMatchedPage) mustBe defined
      result.get(IsAdoptingFromAbroadPage) mustBe defined
      result.get(IsApplyingForStatutoryAdoptionPayPage) mustBe defined
      result.get(IsCohabitingPage) mustBe defined
      result.get(IsInQualifyingRelationshipPage) mustBe defined
      result.get(NamePage) mustBe defined
      result.get(NinoPage) mustBe defined
      result.get(PaternityLeaveLengthGbPreApril24OrNiPage) mustBe defined
      result.get(PayStartDateGbPreApril24OrNiPage) mustBe defined
      result.get(ReasonForRequestingPage) mustBe defined
      result.get(WillHaveCaringResponsibilityPage) mustBe defined
      result.get(WillTakeTimeToCareForChildPage) mustBe defined
      result.get(WillTakeTimeToSupportPartnerPage) mustBe defined
      result.get(PaternityLeaveLengthGbPostApril24Page) mustBe defined
      result.get(LeaveTakenTogetherOrSeparatelyPage) mustBe defined
      result.get(PayStartDateGbPostApril24Page) mustBe defined
      result.get(PayStartDateWeek1Page) mustBe defined
      result.get(PayStartDateWeek2Page) mustBe defined
    }

    "must not remove any answers when the answer does not change from false" in {

      val answers = fullAnswersNotAdopting

      val result = answers.set(IsAdoptingOrParentalOrderPage, false).success.value

      result.get(IsAdoptingOrParentalOrderPage) mustBe defined
      result.get(CountryOfResidencePage) mustBe defined

      result.get(BabyDateOfBirthPage) mustBe defined
      result.get(BabyDueDatePage) mustBe defined
      result.get(BabyHasBeenBornPage) mustBe defined
      result.get(IsBiologicalFatherPage) mustBe defined
      result.get(IsCohabitingPage) mustBe defined
      result.get(IsInQualifyingRelationshipPage) mustBe defined
      result.get(NamePage) mustBe defined
      result.get(NinoPage) mustBe defined
      result.get(PaternityLeaveLengthGbPreApril24OrNiPage) mustBe defined
      result.get(PayStartDateGbPreApril24OrNiPage) mustBe defined
      result.get(WillHaveCaringResponsibilityPage) mustBe defined
      result.get(WillTakeTimeToCareForChildPage) mustBe defined
      result.get(WillTakeTimeToSupportPartnerPage) mustBe defined
      result.get(PaternityLeaveLengthGbPostApril24Page) mustBe defined
      result.get(LeaveTakenTogetherOrSeparatelyPage) mustBe defined
      result.get(PayStartDateGbPostApril24Page) mustBe defined
      result.get(PayStartDateWeek1Page) mustBe defined
      result.get(PayStartDateWeek2Page) mustBe defined
    }
  }
}
