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

import models.{CountryOfResidence, Name, PaternityLeaveLengthGbPreApril24OrNi, RelationshipToChild}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

class IsAdoptingFromAbroadPageSpec extends PageBehaviours {

  "IsAdoptingFromAbroadPage" - {

    beRetrievable[Boolean](IsAdoptingFromAbroadPage)

    beSettable[Boolean](IsAdoptingFromAbroadPage)

    beRemovable[Boolean](IsAdoptingFromAbroadPage)

    val fullAnswersAdopting =
      emptyUserAnswers
        .set(ReasonForRequestingPage, Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value).success.value
        .set(IsAdoptingOrParentalOrderPage, true).success.value
        .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
        .set(IsAdoptingFromAbroadPage, true).success.value
        .set(CountryOfResidencePage, CountryOfResidence.England).success.value
        .set(IsInQualifyingRelationshipPage, false).success.value
        .set(IsCohabitingPage, true).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, true).success.value
        .set(WillTakeTimeToSupportPartnerPage, true).success.value
        .set(ChildHasBeenPlacedPage, true).success.value
        .set(ChildExpectedPlacementDatePage, LocalDate.now).success.value
        .set(ChildPlacementDatePage, LocalDate.now).success.value
        .set(DateChildWasMatchedPage, LocalDate.now).success.value
        .set(DateOfAdoptionNotificationPage, LocalDate.now).success.value
        .set(ChildHasEnteredUkPage, true).success.value
        .set(DateChildEnteredUkPage, LocalDate.now).success.value
        .set(DateChildExpectedToEnterUkPage, LocalDate.now).success.value
        .set(NamePage, Name("first", "last")).success.value
        .set(NinoPage, arbitrary[Nino].sample.value).success.value
        .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value
        .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value

    val fullAnswersParentalOrder =
      emptyUserAnswers
        .set(CountryOfResidencePage, CountryOfResidence.England).success.value
        .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
        .set(IsAdoptingOrParentalOrderPage, true).success.value
        .set(IsAdoptingFromAbroadPage, true).success.value
        .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value
        .set(IsInQualifyingRelationshipPage, false).success.value
        .set(IsCohabitingPage, true).success.value
        .set(WillHaveCaringResponsibilityPage, true).success.value
        .set(WillTakeTimeToCareForChildPage, true).success.value
        .set(WillTakeTimeToSupportPartnerPage, true).success.value
        .set(NamePage, Name("first", "last")).success.value
        .set(NinoPage, arbitrary[Nino].sample.value).success.value
        .set(BabyDateOfBirthPage, LocalDate.now).success.value
        .set(BabyDueDatePage, LocalDate.now).success.value
        .set(BabyHasBeenBornPage, true).success.value
        .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value
        .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value

    "must remove redundant answers when the answer is true and Reason for Requesting is Parental Order" in {

      val result = fullAnswersParentalOrder.set(IsAdoptingFromAbroadPage, true).success.value

      result.get(IsAdoptingFromAbroadPage).value mustEqual true
      result.get(CountryOfResidencePage) mustBe defined
      result.get(IsAdoptingOrParentalOrderPage) mustBe defined
      result.get(IsApplyingForStatutoryAdoptionPayPage) mustBe defined

      result.get(BabyDateOfBirthPage) must not be defined
      result.get(BabyDueDatePage) must not be defined
      result.get(BabyHasBeenBornPage) must not be defined
      result.get(ChildExpectedPlacementDatePage) must not be defined
      result.get(ChildHasBeenPlacedPage) must not be defined
      result.get(ChildPlacementDatePage) must not be defined
      result.get(DateChildWasMatchedPage) must not be defined
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
      result.get(DateOfAdoptionNotificationPage) must not be defined
      result.get(ChildHasEnteredUkPage) must not be defined
      result.get(DateChildEnteredUkPage) must not be defined
      result.get(DateChildExpectedToEnterUkPage) must not be defined
    }

    "must not remove any answers" - {

      "when the answer is false" in {

        val result = fullAnswersParentalOrder.set(IsAdoptingFromAbroadPage, false).success.value

        result.get(IsAdoptingFromAbroadPage).value mustEqual false
        result.get(CountryOfResidencePage) mustBe defined
        result.get(IsAdoptingOrParentalOrderPage) mustBe defined
        result.get(IsApplyingForStatutoryAdoptionPayPage) mustBe defined
        result.get(BabyDateOfBirthPage) mustBe defined
        result.get(BabyDueDatePage) mustBe defined
        result.get(BabyHasBeenBornPage) mustBe defined
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
      }

      "when the answer is true and Reason for Requesting is Adopting or Supporting Adoption" in {

        val result = fullAnswersAdopting.set(IsAdoptingFromAbroadPage, true).success.value

        result.get(IsAdoptingFromAbroadPage).value mustEqual true
        result.get(CountryOfResidencePage) mustBe defined
        result.get(IsAdoptingOrParentalOrderPage) mustBe defined
        result.get(IsApplyingForStatutoryAdoptionPayPage) mustBe defined
        result.get(ChildExpectedPlacementDatePage) mustBe defined
        result.get(ChildHasBeenPlacedPage) mustBe defined
        result.get(ChildPlacementDatePage) mustBe defined
        result.get(DateChildWasMatchedPage) mustBe defined
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
        result.get(DateOfAdoptionNotificationPage) mustBe defined
        result.get(ChildHasEnteredUkPage) mustBe defined
        result.get(DateChildEnteredUkPage) mustBe defined
        result.get(DateChildExpectedToEnterUkPage) mustBe defined
      }
    }
  }
}