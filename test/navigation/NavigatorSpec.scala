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

package navigation

import base.SpecBase
import config.Constants
import controllers.routes
import generators.Generators
import models._
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

import java.time.LocalDate

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val navigator = new Navigator

  private val dateBeforeLegistation    = LocalDate.of(2000, 1, 1)
  private val dateAfterLegislation     = LocalDate.of(2100, 1, 1)

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad
      }

      "must go from Country of Residence to Is Adopting Or Parental Order" in {

        navigator.nextPage(CountryOfResidencePage, NormalMode, emptyUserAnswers) mustEqual routes.IsAdoptingOrParentalOrderController.onPageLoad(NormalMode)
      }

      "must go from Is Adopting Or Parental Order" - {

        "to Is Applying for Statutory Adoption Pay and Leave when the answer is yes" in {

          val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, true).success.value
          navigator.nextPage(IsAdoptingOrParentalOrderPage, NormalMode, answers) mustEqual routes.IsApplyingForStatutoryAdoptionPayController.onPageLoad(NormalMode)
        }

        "to Is Biological Father when the answer is no" in {

          val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value
          navigator.nextPage(IsAdoptingOrParentalOrderPage, NormalMode, answers) mustEqual routes.IsBiologicalFatherController.onPageLoad(NormalMode)
        }
      }

      "must go from Is Applying for Statutory Adoption Pay and Leave"  -{

        "to Not Eligible when the answer is yes" in {

          val answers = emptyUserAnswers.set(IsApplyingForStatutoryAdoptionPayPage, true).success.value
          navigator.nextPage(IsApplyingForStatutoryAdoptionPayPage, NormalMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
        }

        "to Is Adopting From Abroad when the answer is no" in {

          val answers = emptyUserAnswers.set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
          navigator.nextPage(IsApplyingForStatutoryAdoptionPayPage, NormalMode, answers) mustEqual routes.IsAdoptingFromAbroadController.onPageLoad(NormalMode)
        }
      }

      "must go from Is Adopting From Abroad to Reason For Requesting" in {

        navigator.nextPage(IsAdoptingFromAbroadPage, NormalMode, emptyUserAnswers) mustEqual routes.ReasonForRequestingController.onPageLoad(NormalMode)
      }

      "must go from Reason For Requesting to Is In Qualifying Relationship" in {

        navigator.nextPage(ReasonForRequestingPage, NormalMode, emptyUserAnswers) mustEqual routes.IsInQualifyingRelationshipController.onPageLoad(NormalMode)
      }

      "must go from Is Biological Father" - {

        "to Will Have Caring Responsibility when the answer is yes" in {

          val answers = emptyUserAnswers.set(IsBiologicalFatherPage, true).success.value
          navigator.nextPage(IsBiologicalFatherPage, NormalMode, answers) mustEqual routes.WillHaveCaringResponsibilityController.onPageLoad(NormalMode)
        }

        "to Is In Qualifying Relationship when the answer is no" in {

          val answers = emptyUserAnswers.set(IsBiologicalFatherPage, false).success.value
          navigator.nextPage(IsBiologicalFatherPage, NormalMode, answers) mustEqual routes.IsInQualifyingRelationshipController.onPageLoad(NormalMode)
        }
      }

      "must go from Is In Qualifying Relationship" - {

        "to Will Have Caring Responsibility  when the answer is yes" in {

          val answers = emptyUserAnswers.set(IsInQualifyingRelationshipPage, true).success.value
          navigator.nextPage(IsInQualifyingRelationshipPage, NormalMode, answers) mustEqual routes.WillHaveCaringResponsibilityController.onPageLoad(NormalMode)
        }

        "to Is Cohabiting when the answer is no" in {

          val answers = emptyUserAnswers.set(IsInQualifyingRelationshipPage, false).success.value
          navigator.nextPage(IsInQualifyingRelationshipPage, NormalMode, answers) mustEqual routes.IsCohabitingController.onPageLoad(NormalMode)
        }
      }

      "must go from Is Cohabiting" - {

        "to Will Have Caring Responsibility when the answer is yes" in {

          val answers = emptyUserAnswers.set(IsCohabitingPage, true).success.value
          navigator.nextPage(IsCohabitingPage, NormalMode, answers) mustEqual routes.WillHaveCaringResponsibilityController.onPageLoad(NormalMode)
        }

        "to Cannot Apply when the answer is no" in {

          val answers = emptyUserAnswers.set(IsCohabitingPage, false).success.value
          navigator.nextPage(IsCohabitingPage, NormalMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
        }
      }

      "must go from Will Have Caring Responsibility" - {

        "to Will Take Time to Care For Child when the answer is yes" in {

          val answers = emptyUserAnswers.set(WillHaveCaringResponsibilityPage, true).success.value
          navigator.nextPage(WillHaveCaringResponsibilityPage, NormalMode, answers) mustEqual routes.WillTakeTimeToCareForChildController.onPageLoad(NormalMode)
        }

        "to Cannot Apply when the answer is no" in {

          val answers = emptyUserAnswers.set(WillHaveCaringResponsibilityPage, false).success.value
          navigator.nextPage(WillHaveCaringResponsibilityPage, NormalMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
        }
      }

      "must go from Will Take Time to Care for Child" - {

        "to Name when the answer is yes" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToCareForChildPage, true).success.value
          navigator.nextPage(WillTakeTimeToCareForChildPage, NormalMode, answers) mustEqual routes.NameController.onPageLoad(NormalMode)
        }

        "to Will Take Time to Support Mother when the answer is no" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToCareForChildPage, false).success.value
          navigator.nextPage(WillTakeTimeToCareForChildPage, NormalMode, answers) mustEqual routes.WillTakeTimeToSupportPartnerController.onPageLoad(NormalMode)
        }
      }

      "must go from Will Take Time to Support Mother" - {

        "to Name when the answer is yes" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToSupportPartnerPage, true).success.value
          navigator.nextPage(WillTakeTimeToSupportPartnerPage, NormalMode, answers) mustEqual routes.NameController.onPageLoad(NormalMode)
        }

        "to Cannot Apply when the answer is no" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToSupportPartnerPage, false).success.value
          navigator.nextPage(WillTakeTimeToSupportPartnerPage, NormalMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
        }
      }

      "must go from Name" - {

        "to Nino" in {

          navigator.nextPage(NamePage, NormalMode, emptyUserAnswers) mustEqual routes.NinoController.onPageLoad(NormalMode)
        }
      }

      "must go from Nino" - {

        "to Baby Has Been Born when the user is not adopting or a parental order parent" in {

          val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value
          navigator.nextPage(NinoPage, NormalMode, answers) mustEqual routes.BabyHasBeenBornController.onPageLoad(NormalMode)
        }

        "to Baby Has Been Born when the user is a parental order parent" in {

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value

          navigator.nextPage(NinoPage, NormalMode, answers) mustEqual routes.BabyHasBeenBornController.onPageLoad(NormalMode)
        }

        "to Date Child Was Matched when the user is adopting, but not from abroad" in {

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.Adopting).success.value

          navigator.nextPage(NinoPage, NormalMode, answers) mustEqual routes.DateChildWasMatchedController.onPageLoad(NormalMode)
        }

        "to Date Child Was Matched when the user is supporting adoption, but not from abroad" in {

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsAdoptingFromAbroadPage, false).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.SupportingAdoption).success.value

          navigator.nextPage(NinoPage, NormalMode, answers) mustEqual routes.DateChildWasMatchedController.onPageLoad(NormalMode)
        }

        "to Date of Adoption Notification when the user is adopting from abroad" in {

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsAdoptingFromAbroadPage, true).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.Adopting).success.value

          navigator.nextPage(NinoPage, NormalMode, answers) mustEqual routes.DateOfAdoptionNotificationController.onPageLoad(NormalMode)
        }

        "to Date of Adoption Notification when the user is supporting adoption from abroad" in {

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsAdoptingFromAbroadPage, true).success.value
              .set(ReasonForRequestingPage, RelationshipToChild.SupportingAdoption).success.value

          navigator.nextPage(NinoPage, NormalMode, answers) mustEqual routes.DateOfAdoptionNotificationController.onPageLoad(NormalMode)
        }
      }

      "must go from Baby Has Been Born" - {

        "to Baby Date of Birth when the answer is yes" in {

          val answers = emptyUserAnswers.set(BabyHasBeenBornPage, true).success.value
          navigator.nextPage(BabyHasBeenBornPage, NormalMode, answers) mustEqual routes.BabyDateOfBirthController.onPageLoad(NormalMode)
        }

        "to Baby Due Date when the answer is no" in {

          val answers = emptyUserAnswers.set(BabyHasBeenBornPage, false).success.value
          navigator.nextPage(BabyHasBeenBornPage, NormalMode, answers) mustEqual routes.BabyDueDateController.onPageLoad(NormalMode)
        }
      }

      "must go from Baby Date of Birth to Baby Due Date" in {

        navigator.nextPage(BabyDateOfBirthPage, NormalMode, emptyUserAnswers) mustEqual routes.BabyDueDateController.onPageLoad(NormalMode)
      }

      "must go from Baby Due Date" - {

        "to Paternity Leave Length GB Pre April 24 or NI when the due date is on or before 6th April" in {

          forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

            val answers = emptyUserAnswers.set(BabyDueDatePage, date).success.value
            navigator.nextPage(BabyDueDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
          }
        }

        "to Paternity Leave Length GB Pre April 24 or NI when the user is in Northern Ireland, regardless of the date" in {

          forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

            val answers =
              emptyUserAnswers
                .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value
                .set(BabyDueDatePage, date).success.value

            navigator.nextPage(BabyDueDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
          }
        }

        "to Paternity Leave length GB Post April 24 when the user is not in NI and the due date is after 6th April" in {

          import models.CountryOfResidence._

          forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

            val answers =
              emptyUserAnswers
                .set(CountryOfResidencePage, country).success.value
                .set(BabyDueDatePage, date).success.value

            navigator.nextPage(BabyDueDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(NormalMode)
          }
        }
      }

      "must go from Date Child Was Matched to Child Has Been Placed" in {

        navigator.nextPage(DateChildWasMatchedPage, NormalMode, emptyUserAnswers) mustEqual routes.ChildHasBeenPlacedController.onPageLoad(NormalMode)
      }

      "must go from Child Has Been Placed" - {

        "to Child Placement Date when the answer is yes" in {

          val answers = emptyUserAnswers.set(ChildHasBeenPlacedPage, true).success.value
          navigator.nextPage(ChildHasBeenPlacedPage, NormalMode, answers) mustEqual routes.ChildPlacementDateController.onPageLoad(NormalMode)
        }

        "to Child Expected Placement Date when the answer is no" in {

          val answers = emptyUserAnswers.set(ChildHasBeenPlacedPage, false).success.value
          navigator.nextPage(ChildHasBeenPlacedPage, NormalMode, answers) mustEqual routes.ChildExpectedPlacementDateController.onPageLoad(NormalMode)
        }
      }

      "must go from Child Placement Date" - {

        "to Paternity Leave Length GB Pre April 24 or NI when the date is on or before 6th April" in {

          forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

            val answers = emptyUserAnswers.set(ChildPlacementDatePage, date).success.value
            navigator.nextPage(ChildPlacementDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
          }
        }

        "to Paternity Leave Length GB Pre April 24 or NI when the user is in Northern Ireland, regardless of the date" in {

          forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

            val answers =
              emptyUserAnswers
                .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value
                .set(ChildPlacementDatePage, date).success.value

            navigator.nextPage(ChildPlacementDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
          }
        }

        "to Paternity Leave length GB Post April 24 when the user is not in NI and the date is after 6th April" in {

          import models.CountryOfResidence._

          forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

            val answers =
              emptyUserAnswers
                .set(CountryOfResidencePage, country).success.value
                .set(ChildPlacementDatePage, date).success.value

            navigator.nextPage(ChildPlacementDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(NormalMode)
          }
        }
      }

      "must go from Child Expected Placement Date" - {

        "to Paternity Leave Length GB Pre April 24 or NI when the date is on or before 6th April" in {

          forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

            val answers = emptyUserAnswers.set(ChildExpectedPlacementDatePage, date).success.value
            navigator.nextPage(ChildExpectedPlacementDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
          }
        }

        "to Paternity Leave Length GB Pre April 24 or NI when the user is in Northern Ireland, regardless of the date" in {

          forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

            val answers =
              emptyUserAnswers
                .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value
                .set(ChildExpectedPlacementDatePage, date).success.value

            navigator.nextPage(ChildExpectedPlacementDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
          }
        }

        "to Paternity Leave length GB Post April 24 when the user is not in NI and the date is after 6th April" in {

          import models.CountryOfResidence._

          forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

            val answers =
              emptyUserAnswers
                .set(CountryOfResidencePage, country).success.value
                .set(ChildExpectedPlacementDatePage, date).success.value

            navigator.nextPage(ChildExpectedPlacementDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(NormalMode)
          }
        }
      }

      "must go from Date of Adoption Notification to Child Has Entered UK" in {

        navigator.nextPage(DateOfAdoptionNotificationPage, NormalMode, emptyUserAnswers) mustEqual routes.ChildHasEnteredUkController.onPageLoad(NormalMode)
      }

      "must go from Child Has Entered UK" - {

        "to Date Child Entered UK when the answer is yes" in {

          val answers = emptyUserAnswers.set(ChildHasEnteredUkPage, true).success.value
          navigator.nextPage(ChildHasEnteredUkPage, NormalMode, answers) mustEqual routes.DateChildEnteredUkController.onPageLoad(NormalMode)
        }

        "to Date Child Expected in UK when the answer is no" in {

          val answers = emptyUserAnswers.set(ChildHasEnteredUkPage, false).success.value
          navigator.nextPage(ChildHasEnteredUkPage, NormalMode, answers) mustEqual routes.DateChildExpectedToEnterUkController.onPageLoad(NormalMode)
        }
      }


      "must go from Date Child Entered UK" - {

        "to Paternity Leave Length GB Pre April 24 or NI when the date is on or before 6th April" in {

          forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

            val answers = emptyUserAnswers.set(DateChildEnteredUkPage, date).success.value
            navigator.nextPage(DateChildEnteredUkPage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
          }
        }

        "to Paternity Leave Length GB Pre April 24 or NI when the user is in Northern Ireland, regardless of the date" in {

          forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

            val answers =
              emptyUserAnswers
                .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value
                .set(DateChildEnteredUkPage, date).success.value

            navigator.nextPage(DateChildEnteredUkPage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
          }
        }

        "to Paternity Leave length GB Post April 24 when the user is not in NI and the date is after 6th April" in {

          import models.CountryOfResidence._

          forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

            val answers =
              emptyUserAnswers
                .set(CountryOfResidencePage, country).success.value
                .set(DateChildEnteredUkPage, date).success.value

            navigator.nextPage(DateChildEnteredUkPage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(NormalMode)
          }
        }
      }

      "must go from Date Child Expected to Enter UK to Paternity Leave Length" in {

        navigator.nextPage(DateChildExpectedToEnterUkPage, NormalMode, emptyUserAnswers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(NormalMode)
      }

      "must go from Paternity Leave Length GB Pre April 24 or NI to Pay Start Date GB Pre April 24 or NI" in {

          navigator.nextPage(PaternityLeaveLengthGbPreApril24OrNiPage, NormalMode, emptyUserAnswers) mustEqual routes.PayStartDateGbPreApril24OrNiController.onPageLoad(NormalMode)
      }

      "must go from Paternity Leave Length GB Post April 24" - {

        "to Pay Start Date GB Post April 24 when the answer is 1 week" in {

          val answers = emptyUserAnswers.set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
          navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, NormalMode, answers) mustEqual routes.PayStartDateGbPostApril24Controller.onPageLoad(NormalMode)
        }

        "to Leave Taken Together or Separately when the answer is 2 weeks" in {

          val answers = emptyUserAnswers.set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value
          navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, NormalMode, answers) mustEqual routes.LeaveTakenTogetherOrSeparatelyController.onPageLoad(NormalMode)
        }

        "to CYA when the answer is Unsure" in {

          val answers = emptyUserAnswers.set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.Unsure).success.value
          navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, NormalMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }
      }

      "must go from Pay Start Date GB Post April 24 to CYA" in {

        navigator.nextPage(PayStartDateGbPostApril24Page, NormalMode, emptyUserAnswers) mustEqual routes.CheckYourAnswersController.onPageLoad
      }

      "must go from Leave Taken Together or Separately" - {

        "to Pay Start Date GB Post April 24  to CYA when the answer is Together" in {

          val answers = emptyUserAnswers.set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
          navigator.nextPage(LeaveTakenTogetherOrSeparatelyPage, NormalMode, answers) mustEqual routes.PayStartDateGbPostApril24Controller.onPageLoad(NormalMode)
        }

        "to Pay Start Date Week 1 when the answer is Separately" in {

          val answers = emptyUserAnswers.set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value
          navigator.nextPage(LeaveTakenTogetherOrSeparatelyPage, NormalMode, answers) mustEqual routes.PayStartDateWeek1Controller.onPageLoad(NormalMode)
        }
      }

      "must go from Pay Start Date Week 1 to Pay Start Date Week 2" in {

        navigator.nextPage(PayStartDateWeek1Page, NormalMode, emptyUserAnswers) mustEqual routes.PayStartDateWeek2Controller.onPageLoad(NormalMode)
      }

      "must go from Pay Start Date Week 2 to CYA" in {

        navigator.nextPage(PayStartDateWeek2Page, NormalMode, emptyUserAnswers) mustEqual routes.CheckYourAnswersController.onPageLoad
      }

      "must go from Pay Start Date GB Pre April 24 or NI to CYA" in {

        navigator.nextPage(PayStartDateGbPreApril24OrNiPage, NormalMode, emptyUserAnswers) mustEqual routes.CheckYourAnswersController.onPageLoad
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from Is Adopting Or Parental Order" - {

        "to Is Applying for SAP when the answer is yes and that question has not been answered" in {

          val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, true).success.value
          navigator.nextPage(IsAdoptingOrParentalOrderPage, CheckMode, answers) mustEqual routes.IsApplyingForStatutoryAdoptionPayController.onPageLoad(CheckMode)
        }

        "to Check Your Answers when the answer is yes and Is Applying for SAP has been answered" in {

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(IsApplyingForStatutoryAdoptionPayPage, false).success.value

          navigator.nextPage(IsAdoptingOrParentalOrderPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Check Answers when the answer is no and Is Biological Father has been answered" in {

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, false).success.value
              .set(IsBiologicalFatherPage, true).success.value

          navigator.nextPage(IsAdoptingOrParentalOrderPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Is Biological Father when the answer is no and that question has not been answered" in {

          val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value
          navigator.nextPage(IsAdoptingOrParentalOrderPage, CheckMode, answers) mustEqual routes.IsBiologicalFatherController.onPageLoad(CheckMode)
        }
      }

      "must go from Is Applying for SAP" - {

        "to Cannot Apply if the answer is yes" in {

          val answers = emptyUserAnswers.set(IsApplyingForStatutoryAdoptionPayPage, true).success.value
          navigator.nextPage(IsApplyingForStatutoryAdoptionPayPage, CheckMode, answers) mustEqual routes.CannotApplyController.onPageLoad
        }

        "to Check Your Answers if the answer is no" in {

          val answers = emptyUserAnswers.set(IsApplyingForStatutoryAdoptionPayPage, false).success.value
          navigator.nextPage(IsApplyingForStatutoryAdoptionPayPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }
      }

      "must go from Is Adopting From Abroad" - {

        "to CYA when Reason for Requesting has been answered" in {

          val answers = emptyUserAnswers.set(ReasonForRequestingPage, RelationshipToChild.Adopting).success.value
          navigator.nextPage(IsAdoptingFromAbroadPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Reason for Requesting in Normal Mode when that question has not been answered" in {

          navigator.nextPage(IsAdoptingFromAbroadPage, CheckMode, emptyUserAnswers) mustEqual routes.ReasonForRequestingController.onPageLoad(NormalMode)
        }
      }

      "must go from Reason for Requesting" - {

        "to CYA is Is in Qualifying Relationship has been answered" in {

          val answers = emptyUserAnswers.set(IsInQualifyingRelationshipPage, true).success.value
          navigator.nextPage(ReasonForRequestingPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Is In Qualifying Relationship if that question had not been answered" in {

          navigator.nextPage(ReasonForRequestingPage, CheckMode, emptyUserAnswers) mustEqual routes.IsInQualifyingRelationshipController.onPageLoad(CheckMode)
        }
      }

      "must go from Is Biological Father" - {

        "when the answer is yes" - {

          "to Check Answers" in {

            val answers = emptyUserAnswers.set(IsBiologicalFatherPage, true).success.value
            navigator.nextPage(IsBiologicalFatherPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }
        }

        "when the answer is no" - {

          "to Check Answers when Is In Qualifying Relationship has been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsBiologicalFatherPage, false).success.value
                .set(IsInQualifyingRelationshipPage, true).success.value

            navigator.nextPage(IsBiologicalFatherPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Is In Qualifying Relationship when that has not already been answered" in {

            val answers = emptyUserAnswers.set(IsBiologicalFatherPage, false).success.value
            navigator.nextPage(IsBiologicalFatherPage, CheckMode, answers) mustEqual routes.IsInQualifyingRelationshipController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Is In Qualifying Relationship" - {

        "when the answer is yes" - {

          "to Check Answers" in {

            val answers = emptyUserAnswers.set(IsInQualifyingRelationshipPage, true).success.value
            navigator.nextPage(IsInQualifyingRelationshipPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }
        }

        "when the answer is no" - {

          "to Check Answers when Is Cohabiting has been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsInQualifyingRelationshipPage, false).success.value
                .set(IsCohabitingPage, true).success.value

            navigator.nextPage(IsInQualifyingRelationshipPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Is Cohabiting when that has not already been answered" in {

            val answers = emptyUserAnswers.set(IsInQualifyingRelationshipPage, false).success.value
            navigator.nextPage(IsInQualifyingRelationshipPage, CheckMode, answers) mustEqual routes.IsCohabitingController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Is Cohabiting" - {

        "to Check Answers when the answer is yes" in {

          val answers = emptyUserAnswers.set(IsCohabitingPage, true).success.value
          navigator.nextPage(IsCohabitingPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Cannot Apply when the answer is no" in {

          val answers = emptyUserAnswers.set(IsCohabitingPage, false).success.value
          navigator.nextPage(IsCohabitingPage, CheckMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
        }
      }

      "must go from Will Have Caring Responsibility" - {

        "to Check Answers when the answer is yes" in {

          val answers = emptyUserAnswers.set(WillHaveCaringResponsibilityPage, true).success.value
          navigator.nextPage(WillHaveCaringResponsibilityPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Cannot Apply when the answer is no" in {

          val answers = emptyUserAnswers.set(WillHaveCaringResponsibilityPage, false).success.value
          navigator.nextPage(WillHaveCaringResponsibilityPage, CheckMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
        }
      }

      "must go from Will Take Time to Care for Child" - {

        "when the answer is yes" - {

          "to Check Answers" in {

            val answers = emptyUserAnswers.set(WillTakeTimeToCareForChildPage, true).success.value
            navigator.nextPage(WillTakeTimeToCareForChildPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }
        }

        "when the answer is no" - {

          "to Check Answers when Will Take Time to Support Mother has been answered" in {

            val answers =
              emptyUserAnswers
                .set(WillTakeTimeToCareForChildPage, false).success.value
                .set(WillTakeTimeToSupportPartnerPage, true).success.value

            navigator.nextPage(WillTakeTimeToCareForChildPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Will Take Time to Support Mother when that question has not been answered" in {

            val answers = emptyUserAnswers.set(WillTakeTimeToCareForChildPage, false).success.value
            navigator.nextPage(WillTakeTimeToCareForChildPage, CheckMode, answers) mustEqual routes.WillTakeTimeToSupportPartnerController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Will Take Time to Support Mother" - {

        "to Check Answers when the answer is yes" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToSupportPartnerPage, true).success.value
          navigator.nextPage(WillTakeTimeToSupportPartnerPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Cannot Apply when the answer is no" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToSupportPartnerPage, false).success.value
          navigator.nextPage(WillTakeTimeToSupportPartnerPage, CheckMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
        }
      }

      "must go from NINO" - {

        "when the user is not adopting or parental order" - {

          "to CYA when Has the Baby Been Born has been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsAdoptingOrParentalOrderPage, false).success.value
                .set(BabyHasBeenBornPage, true).success.value

            navigator.nextPage(NinoPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Has the Baby Been Born when it has not been answered" in {

            val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value

            navigator.nextPage(NinoPage, CheckMode, answers) mustEqual routes.BabyHasBeenBornController.onPageLoad(CheckMode)
          }
        }

        "when the user is adopting or supporting adoption in the UK" - {

          val reason = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

          "to CYA when Date Child Matched has been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, false).success.value
                .set(ReasonForRequestingPage, reason).success.value
                .set(DateChildWasMatchedPage, LocalDate.now).success.value

            navigator.nextPage(NinoPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Date Child Matched when it has not been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, false).success.value
                .set(ReasonForRequestingPage, reason).success.value

            navigator.nextPage(NinoPage, CheckMode, answers) mustEqual routes.DateChildWasMatchedController.onPageLoad(CheckMode)
          }
        }

        "when the user is adopting or supporting adoption from abroad" - {

          val reason = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value

          "to CYA when Date of Adoption Notification has been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, true).success.value
                .set(ReasonForRequestingPage, reason).success.value
                .set(DateOfAdoptionNotificationPage, LocalDate.now).success.value

            navigator.nextPage(NinoPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Date of Adoption Notification when it has not been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, true).success.value
                .set(ReasonForRequestingPage, reason).success.value

            navigator.nextPage(NinoPage, CheckMode, answers) mustEqual routes.DateOfAdoptionNotificationController.onPageLoad(CheckMode)
          }
        }

        "when the user is a parental order parent" - {

          "to CYA when Has the Baby Been Born has been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, true).success.value
                .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value
                .set(BabyHasBeenBornPage, true).success.value

            navigator.nextPage(NinoPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Has the Baby Been Born when it has not been answered" in {

            val answers =
              emptyUserAnswers
                .set(IsAdoptingOrParentalOrderPage, true).success.value
                .set(IsAdoptingFromAbroadPage, true).success.value
                .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value

            navigator.nextPage(NinoPage, CheckMode, answers) mustEqual routes.BabyHasBeenBornController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Baby Has Been Born" - {

        "when the answer is yes" - {

          "to Check Answers when Baby Date of Birth has been answered" in {

            val answers = emptyUserAnswers
              .set(BabyHasBeenBornPage, true).success.value
              .set(BabyDateOfBirthPage, LocalDate.now).success.value
            navigator.nextPage(BabyHasBeenBornPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Baby Date of Birth when it has not been answered" in {

            val answers = emptyUserAnswers.set(BabyHasBeenBornPage, true).success.value
            navigator.nextPage(BabyHasBeenBornPage, CheckMode, answers) mustEqual routes.BabyDateOfBirthController.onPageLoad(CheckMode)
          }
        }

        "when the answer is no" - {

          "to Check Answers" in {

            val answers = emptyUserAnswers.set(BabyHasBeenBornPage, false).success.value
            navigator.nextPage(BabyHasBeenBornPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }
        }
      }

      "must go from Has Child Entered UK" - {

        "when the answer is yes" - {

          "to CYA when Date Child Entered UK has been answered" in {

            val answers =
              emptyUserAnswers
                .set(ChildHasEnteredUkPage, true).success.value
                .set(DateChildEnteredUkPage, LocalDate.now).success.value

            navigator.nextPage(ChildHasEnteredUkPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Date Child Entered UK when it has not been answered" in {

            val answers = emptyUserAnswers.set(ChildHasEnteredUkPage, true).success.value

            navigator.nextPage(ChildHasEnteredUkPage, CheckMode, answers) mustEqual routes.DateChildEnteredUkController.onPageLoad(CheckMode)
          }
        }

        "when the answer is no" - {

          "to CYA when Date Child Expected to Enter UK has been answered" in {

            val answers =
              emptyUserAnswers
                .set(ChildHasEnteredUkPage, false).success.value
                .set(DateChildExpectedToEnterUkPage, LocalDate.now).success.value

            navigator.nextPage(ChildHasEnteredUkPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Date Child Expected to Enter UK when it has not been answered" in {

            val answers = emptyUserAnswers.set(ChildHasEnteredUkPage, false).success.value

            navigator.nextPage(ChildHasEnteredUkPage, CheckMode, answers) mustEqual routes.DateChildExpectedToEnterUkController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Date Child Entered UK" - {

        "to CYA" - {

          "when the date is before 7 April 24 and Paternity Leave Length Pre April 24 has been answered" in {

            forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

              val answers =
                emptyUserAnswers
                  .set(DateChildEnteredUkPage, date).success.value
                  .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value

              navigator.nextPage(DateChildEnteredUkPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }

          "when the user is NI and Paternity Leave Length Pre April 24 has been answered" in {

            forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

              val answers =
                emptyUserAnswers
                  .set(DateChildEnteredUkPage, date).success.value
                  .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value
                  .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value

              navigator.nextPage(DateChildEnteredUkPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }

          "when the user is not in NI, the date is on or after 7 April 24, and Paternity Length Post April 24 has been answered" in {

            import models.CountryOfResidence._

            forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

              val answers =
                emptyUserAnswers
                  .set(DateChildEnteredUkPage, date).success.value
                  .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
                  .set(CountryOfResidencePage, country).success.value

              navigator.nextPage(DateChildEnteredUkPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }
        }

        "to Paternity Leave Length Pre April 24" - {

          "when the date is before 7 April 24 and Paternity Leave Length Pre April 24 has not been answered" in {

            forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

              val answers = emptyUserAnswers.set(DateChildEnteredUkPage, date).success.value

              navigator.nextPage(DateChildEnteredUkPage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode)
            }
          }

          "when the user is in NI and Paternity Leave Length Pre April 24 has not been answered" in {

            forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

              val answers =
                emptyUserAnswers
                  .set(DateChildEnteredUkPage, date).success.value
                  .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value

              navigator.nextPage(DateChildEnteredUkPage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode)
            }
          }
        }

        "to Paternity Leave Length Post April 24" - {

          "when the user is not in NI, the date is on or after 7 April 24, and Paternity Length Post April 24 has not been answered" in {

            import models.CountryOfResidence._

            forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

              val answers =
                emptyUserAnswers
                  .set(DateChildEnteredUkPage, date).success.value
                  .set(CountryOfResidencePage, country).success.value

              navigator.nextPage(DateChildEnteredUkPage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(CheckMode)
            }
          }
        }
      }

      "must go from Child Has Been Placed" - {

        "when the answer is yes" - {

          "to CYA when Child Placement date has been answered" in {

            val answers =
              emptyUserAnswers
                .set(ChildHasBeenPlacedPage, true).success.value
                .set(ChildPlacementDatePage, LocalDate.now).success.value

            navigator.nextPage(ChildHasBeenPlacedPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Date Child Placement Date when it has not been answered" in {

            val answers = emptyUserAnswers.set(ChildHasBeenPlacedPage, true).success.value

            navigator.nextPage(ChildHasBeenPlacedPage, CheckMode, answers) mustEqual routes.ChildPlacementDateController.onPageLoad(CheckMode)
          }
        }

        "when the answer is no" - {

          "to CYA when Child Expected Placement Date has been answered" in {

            val answers =
              emptyUserAnswers
                .set(ChildHasBeenPlacedPage, false).success.value
                .set(ChildExpectedPlacementDatePage, LocalDate.now).success.value

            navigator.nextPage(ChildHasBeenPlacedPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Child Expected Placement Date when it has not been answered" in {

            val answers = emptyUserAnswers.set(ChildHasBeenPlacedPage, false).success.value

            navigator.nextPage(ChildHasBeenPlacedPage, CheckMode, answers) mustEqual routes.ChildExpectedPlacementDateController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Baby Date of Birth" - {

        "to Check Answers when it has not been answered" in {

          navigator.nextPage(BabyDateOfBirthPage, CheckMode, emptyUserAnswers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }
      }

      "must go from Baby Due Date" - {

        "to CYA" - {

          "when the due date is before 7 April 24 and Paternity Leave Length Pre April 24 has been answered" in {

            forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

              val answers =
                emptyUserAnswers
                  .set(BabyDueDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value

              navigator.nextPage(BabyDueDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }

          "when the user is NI and Paternity Leave Length Pre April 24 has been answered" in {

            forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

              val answers =
                emptyUserAnswers
                  .set(BabyDueDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value
                  .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value

              navigator.nextPage(BabyDueDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }

          "when the user is not in NI, the date is on or after 7 April 24, and Paternity Length Post April 24 has been answered" in {

            import models.CountryOfResidence._

            forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

              val answers =
                emptyUserAnswers
                  .set(BabyDueDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
                  .set(CountryOfResidencePage, country).success.value

              navigator.nextPage(BabyDueDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }
        }

        "to Paternity Leave Length Pre April 24" - {

          "when the due date is before 7 April 24 and Paternity Leave Length Pre April 24 has not been answered" in {

            forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

              val answers = emptyUserAnswers.set(BabyDueDatePage, date).success.value

              navigator.nextPage(BabyDueDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode)
            }
          }

          "when the user is in NI and Paternity Leave Length Pre April 24 has not been answered" in {

            forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

              val answers =
                emptyUserAnswers
                  .set(BabyDueDatePage, date).success.value
                  .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value

              navigator.nextPage(BabyDueDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode)
            }
          }
        }

        "to Paternity Leave Length Post April 24" - {

          "when the user is not in NI, the birth is on or after 7 April 24, and Paternity Length Post April 24 has not been answered" in {

            import models.CountryOfResidence._

            forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

              val answers =
                emptyUserAnswers
                  .set(BabyDueDatePage, date).success.value
                  .set(CountryOfResidencePage, country).success.value

              navigator.nextPage(BabyDueDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(CheckMode)
            }
          }
        }
      }

      "must go from Child Placement Date" - {

        "to CYA" - {

          "when the date is before 7 April 24 and Paternity Leave Length Pre April 24 has been answered" in {

            forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

              val answers =
                emptyUserAnswers
                  .set(ChildPlacementDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value

              navigator.nextPage(ChildPlacementDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }

          "when the user is NI and Paternity Leave Length Pre April 24 has been answered" in {

            forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

              val answers =
                emptyUserAnswers
                  .set(ChildPlacementDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value
                  .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value

              navigator.nextPage(ChildPlacementDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }

          "when the user is not in NI, the date is on or after 7 April 24, and Paternity Length Post April 24 has been answered" in {

            import models.CountryOfResidence._

            forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

              val answers =
                emptyUserAnswers
                  .set(ChildPlacementDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
                  .set(CountryOfResidencePage, country).success.value

              navigator.nextPage(ChildPlacementDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }
        }

        "to Paternity Leave Length Pre April 24" - {

          "when the date is before 7 April 24 and Paternity Leave Length Pre April 24 has not been answered" in {

            forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

              val answers = emptyUserAnswers.set(ChildPlacementDatePage, date).success.value

              navigator.nextPage(ChildPlacementDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode)
            }
          }

          "when the user is in NI and Paternity Leave Length Pre April 24 has not been answered" in {

            forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

              val answers =
                emptyUserAnswers
                  .set(ChildPlacementDatePage, date).success.value
                  .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value

              navigator.nextPage(ChildPlacementDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode)
            }
          }
        }

        "to Paternity Leave Length Post April 24" - {

          "when the user is not in NI, the date is on or after 7 April 24, and Paternity Length Post April 24 has not been answered" in {

            import models.CountryOfResidence._

            forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

              val answers =
                emptyUserAnswers
                  .set(ChildPlacementDatePage, date).success.value
                  .set(CountryOfResidencePage, country).success.value

              navigator.nextPage(ChildPlacementDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(CheckMode)
            }
          }
        }
      }

      "must go from Child Expected Placement Date" - {

        "to CYA" - {

          "when the date is before 7 April 24 and Paternity Leave Length Pre April 24 has been answered" in {

            forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

              val answers =
                emptyUserAnswers
                  .set(ChildExpectedPlacementDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value

              navigator.nextPage(ChildExpectedPlacementDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }

          "when the user is NI and Paternity Leave Length Pre April 24 has been answered" in {

            forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

              val answers =
                emptyUserAnswers
                  .set(ChildExpectedPlacementDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.Oneweek).success.value
                  .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value

              navigator.nextPage(ChildExpectedPlacementDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }

          "when the user is not in NI, the date is on or after 7 April 24, and Paternity Length Post April 24 has been answered" in {

            import models.CountryOfResidence._

            forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

              val answers =
                emptyUserAnswers
                  .set(ChildExpectedPlacementDatePage, date).success.value
                  .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
                  .set(CountryOfResidencePage, country).success.value

              navigator.nextPage(ChildExpectedPlacementDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
            }
          }
        }

        "to Paternity Leave Length Pre April 24" - {

          "when the date is before 7 April 24 and Paternity Leave Length Pre April 24 has not been answered" in {

            forAll(datesBetween(dateBeforeLegistation, Constants.april24LegislationEffective.minusDays(1))) { date =>

              val answers = emptyUserAnswers.set(ChildExpectedPlacementDatePage, date).success.value

              navigator.nextPage(ChildExpectedPlacementDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode)
            }
          }

          "when the user is in NI and Paternity Leave Length Pre April 24 has not been answered" in {

            forAll(datesBetween(dateBeforeLegistation, dateAfterLegislation)) { date =>

              val answers =
                emptyUserAnswers
                  .set(ChildExpectedPlacementDatePage, date).success.value
                  .set(CountryOfResidencePage, CountryOfResidence.NorthernIreland).success.value

              navigator.nextPage(ChildExpectedPlacementDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPreApril24OrNiController.onPageLoad(CheckMode)
            }
          }
        }

        "to Paternity Leave Length Post April 24" - {

          "when the user is not in NI, the date is on or after 7 April 24, and Paternity Length Post April 24 has not been answered" in {

            import models.CountryOfResidence._

            forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

              val answers =
                emptyUserAnswers
                  .set(ChildExpectedPlacementDatePage, date).success.value
                  .set(CountryOfResidencePage, country).success.value

              navigator.nextPage(ChildExpectedPlacementDatePage, CheckMode, answers) mustEqual routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(CheckMode)
            }
          }
        }
      }

      "must go from Paternity Leave Length GB Post April 24" - {

        "to CYA" - {

          "when the answer is One Week and Pay Start Date GB Post April 24 has been answered" in {

            val answers =
              emptyUserAnswers
                .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
                .set(PayStartDateGbPostApril24Page, LocalDate.now).success.value

            navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "when the answer is Two Weeks and Leave Taken Together or Separately has been answered" in {

            val answers =
              emptyUserAnswers
                .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value
                .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value

            navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "when the answer is Unsure" in {

            val answers = emptyUserAnswers.set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.Unsure).success.value
            navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }
        }

        "to Pay Start Date GB Post April 24 when the answer is One Week and that question hasn't been answered" in {

          val answers =
            emptyUserAnswers
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value

          navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, CheckMode, answers) mustEqual routes.PayStartDateGbPostApril24Controller.onPageLoad(CheckMode)
        }

        "to Leave Taken Together or Separately when the answer is Two Weeks and that question hasn't been answered" in {

          val answers =
            emptyUserAnswers
              .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value

          navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, CheckMode, answers) mustEqual routes.LeaveTakenTogetherOrSeparatelyController.onPageLoad(CheckMode)
        }
      }

      "must go from Leave Taken Together or Separately" - {

        "to CYA" - {

          "when the answer is Together and Pay Start Date GB Post April 24 has been answered" in {

            val answers =
              emptyUserAnswers
                .set(PayStartDateGbPostApril24Page, LocalDate.now).success.value
                .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value

            navigator.nextPage(LeaveTakenTogetherOrSeparatelyPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "when the answer is Separately and Pay Start Date Week 1 has been answered" in {

            val answers =
              emptyUserAnswers
                .set(PayStartDateWeek1Page, LocalDate.now).success.value
                .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value

            navigator.nextPage(LeaveTakenTogetherOrSeparatelyPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }
        }

        "to Pay Start Date GB Post April 24 when the answer is Together and that question has not been answered" in {

          val answers =
            emptyUserAnswers
              .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value

          navigator.nextPage(LeaveTakenTogetherOrSeparatelyPage, CheckMode, answers) mustEqual routes.PayStartDateGbPostApril24Controller.onPageLoad(CheckMode)
        }

        "to Pay Start Date Week 1 when the answer is Separately and that question has not been answered" in {

          val answers =
            emptyUserAnswers
              .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value

          navigator.nextPage(LeaveTakenTogetherOrSeparatelyPage, CheckMode, answers) mustEqual routes.PayStartDateWeek1Controller.onPageLoad(CheckMode)
        }
      }

      "must go from Pay Start Date Week 1" - {

        "to Pay Start Date Week 2 when it has not been answered" in {

          navigator.nextPage(PayStartDateWeek1Page, CheckMode, emptyUserAnswers) mustEqual routes.PayStartDateWeek2Controller.onPageLoad(CheckMode)
        }

        "to CYA when Pay Start Date Week 2 has been answered" in {

          val answers = emptyUserAnswers.set(PayStartDateWeek2Page, LocalDate.now).success.value
          navigator.nextPage(PayStartDateWeek1Page, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }
      }
    }
  }
}
