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

import models.{LeaveTakenTogetherOrSeparately, PaternityLeaveLengthGbPostApril24, PaternityLeaveLengthGbPreApril24OrNi, RelationshipToChild}
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class ReasonForRequestingPageSpec extends PageBehaviours {

  "ReasonForRequestingPage" - {

    beRetrievable[RelationshipToChild](ReasonForRequestingPage)

    beSettable[RelationshipToChild](ReasonForRequestingPage)

    beRemovable[RelationshipToChild](ReasonForRequestingPage)

    "must remove adoption and paternity details when set to parental order" in {

      val answers =
        emptyUserAnswers
          .set(BabyHasBeenBornPage, true).success.value
          .set(BabyDueDatePage, LocalDate.now).success.value
          .set(BabyDateOfBirthPage, LocalDate.now).success.value
          .set(DateOfAdoptionNotificationPage, LocalDate.now).success.value
          .set(ChildHasEnteredUkPage, true).success.value
          .set(DateChildEnteredUkPage, LocalDate.now).success.value
          .set(DateChildExpectedToEnterUkPage, LocalDate.now).success.value
          .set(DateChildWasMatchedPage, LocalDate.now).success.value
          .set(ChildHasBeenPlacedPage, true).success.value
          .set(ChildPlacementDatePage, LocalDate.now).success.value
          .set(ChildExpectedPlacementDatePage, LocalDate.now).success.value
          .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
          .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value
          .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
          .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
          .set(PayStartDateGbPostApril24Page, LocalDate.now).success.value
          .set(PayStartDateWeek1Page, LocalDate.now).success.value
          .set(PayStartDateWeek2Page, LocalDate.now).success.value

      val result = answers.set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value

      result.get(BabyHasBeenBornPage) mustBe defined
      result.get(BabyDueDatePage) mustBe defined
      result.get(BabyDateOfBirthPage) mustBe defined

      result.get(DateOfAdoptionNotificationPage) must not be defined
      result.get(ChildHasEnteredUkPage) must not be defined
      result.get(DateChildEnteredUkPage) must not be defined
      result.get(DateChildExpectedToEnterUkPage) must not be defined
      result.get(DateChildWasMatchedPage) must not be defined
      result.get(ChildHasBeenPlacedPage) must not be defined
      result.get(ChildPlacementDatePage) must not be defined
      result.get(ChildExpectedPlacementDatePage) must not be defined
      result.get(PaternityLeaveLengthGbPreApril24OrNiPage) must not be defined
      result.get(PayStartDateGbPreApril24OrNiPage) must not be defined
      result.get(PaternityLeaveLengthGbPostApril24Page) must not be defined
      result.get(LeaveTakenTogetherOrSeparatelyPage) must not be defined
      result.get(PayStartDateGbPostApril24Page) must not be defined
      result.get(PayStartDateWeek1Page) must not be defined
      result.get(PayStartDateWeek2Page) must not be defined
    }

    "must remove birth child / parental order, adopting from abroad, and paternity details when set to adopting or supporting adoption in the UK" in {

      val newReason = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

      val answers =
        emptyUserAnswers
          .set(IsAdoptingFromAbroadPage, false).success.value
          .set(BabyHasBeenBornPage, true).success.value
          .set(BabyDueDatePage, LocalDate.now).success.value
          .set(BabyDateOfBirthPage, LocalDate.now).success.value
          .set(DateOfAdoptionNotificationPage, LocalDate.now).success.value
          .set(ChildHasEnteredUkPage, true).success.value
          .set(DateChildEnteredUkPage, LocalDate.now).success.value
          .set(DateChildExpectedToEnterUkPage, LocalDate.now).success.value
          .set(DateChildWasMatchedPage, LocalDate.now).success.value
          .set(ChildHasBeenPlacedPage, true).success.value
          .set(ChildPlacementDatePage, LocalDate.now).success.value
          .set(ChildExpectedPlacementDatePage, LocalDate.now).success.value
          .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
          .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value
          .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
          .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
          .set(PayStartDateGbPostApril24Page, LocalDate.now).success.value
          .set(PayStartDateWeek1Page, LocalDate.now).success.value
          .set(PayStartDateWeek2Page, LocalDate.now).success.value

      val result = answers.set(ReasonForRequestingPage, newReason).success.value

      result.get(DateChildWasMatchedPage) mustBe defined
      result.get(ChildHasBeenPlacedPage) mustBe defined
      result.get(ChildPlacementDatePage) mustBe defined
      result.get(ChildExpectedPlacementDatePage) mustBe defined

      result.get(BabyHasBeenBornPage) must not be defined
      result.get(BabyDueDatePage) must not be defined
      result.get(BabyDateOfBirthPage) must not be defined
      result.get(DateOfAdoptionNotificationPage) must not be defined
      result.get(ChildHasEnteredUkPage) must not be defined
      result.get(DateChildEnteredUkPage) must not be defined
      result.get(DateChildExpectedToEnterUkPage) must not be defined
      result.get(PaternityLeaveLengthGbPreApril24OrNiPage) must not be defined
      result.get(PayStartDateGbPreApril24OrNiPage) must not be defined
      result.get(PaternityLeaveLengthGbPostApril24Page) must not be defined
      result.get(LeaveTakenTogetherOrSeparatelyPage) must not be defined
      result.get(PayStartDateGbPostApril24Page) must not be defined
      result.get(PayStartDateWeek1Page) must not be defined
      result.get(PayStartDateWeek2Page) must not be defined
    }

    "must remove birth child / parental order, adopting from UK, and paternity details when set to adopting / supporting adoption from abroad" in {

      val newReason = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

      val answers =
        emptyUserAnswers
          .set(IsAdoptingFromAbroadPage, true).success.value
          .set(BabyHasBeenBornPage, true).success.value
          .set(BabyDueDatePage, LocalDate.now).success.value
          .set(BabyDateOfBirthPage, LocalDate.now).success.value
          .set(DateOfAdoptionNotificationPage, LocalDate.now).success.value
          .set(ChildHasEnteredUkPage, true).success.value
          .set(DateChildEnteredUkPage, LocalDate.now).success.value
          .set(DateChildExpectedToEnterUkPage, LocalDate.now).success.value
          .set(DateChildWasMatchedPage, LocalDate.now).success.value
          .set(ChildHasBeenPlacedPage, true).success.value
          .set(ChildPlacementDatePage, LocalDate.now).success.value
          .set(ChildExpectedPlacementDatePage, LocalDate.now).success.value
          .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
          .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value
          .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
          .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
          .set(PayStartDateGbPostApril24Page, LocalDate.now).success.value
          .set(PayStartDateWeek1Page, LocalDate.now).success.value
          .set(PayStartDateWeek2Page, LocalDate.now).success.value

      val result = answers.set(ReasonForRequestingPage, newReason).success.value

      result.get(DateOfAdoptionNotificationPage) mustBe defined
      result.get(ChildHasEnteredUkPage) mustBe defined
      result.get(DateChildEnteredUkPage) mustBe defined
      result.get(DateChildExpectedToEnterUkPage) mustBe defined

      result.get(BabyHasBeenBornPage) must not be defined
      result.get(BabyDueDatePage) must not be defined
      result.get(BabyDateOfBirthPage) must not be defined
      result.get(DateChildWasMatchedPage) must not be defined
      result.get(ChildHasBeenPlacedPage) must not be defined
      result.get(ChildPlacementDatePage) must not be defined
      result.get(ChildExpectedPlacementDatePage) must not be defined
      result.get(PaternityLeaveLengthGbPreApril24OrNiPage) must not be defined
      result.get(PayStartDateGbPreApril24OrNiPage) must not be defined
      result.get(PaternityLeaveLengthGbPostApril24Page) must not be defined
      result.get(LeaveTakenTogetherOrSeparatelyPage) must not be defined
      result.get(PayStartDateGbPostApril24Page) must not be defined
      result.get(PayStartDateWeek1Page) must not be defined
      result.get(PayStartDateWeek2Page) must not be defined
    }
  }
}
