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
import controllers.routes
import pages._
import models._
import java.time.LocalDate

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad
      }

      "must go from Country of Residence to Is Adopting" in {

        navigator.nextPage(CountryOfResidencePage, NormalMode, emptyUserAnswers) mustEqual routes.IsAdoptingController.onPageLoad(NormalMode)
      }

      "must go from Is Adopting" - {

        "to Applying for Statutory Adoption Pay and Leave when the answer is yes" in {

          val answers = emptyUserAnswers.set(IsAdoptingPage, true).success.value
          navigator.nextPage(IsAdoptingPage, NormalMode, answers) mustEqual routes.IsApplyingForStatutoryAdoptionPayController.onPageLoad(NormalMode)
        }

        "to Is Biological Father when the answer is no" in {

          val answers = emptyUserAnswers.set(IsAdoptingPage, false).success.value
          navigator.nextPage(IsAdoptingPage, NormalMode, answers) mustEqual routes.IsBiologicalFatherController.onPageLoad(NormalMode)
        }
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
          navigator.nextPage(WillTakeTimeToCareForChildPage, NormalMode, answers) mustEqual routes.WillTakeTimeToSupportMotherController.onPageLoad(NormalMode)
        }
      }

      "must go from Will Take Time to Support Mother" - {

        "to Name when the answer is yes" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToSupportMotherPage, true).success.value
          navigator.nextPage(WillTakeTimeToSupportMotherPage, NormalMode, answers) mustEqual routes.NameController.onPageLoad(NormalMode)
        }

        "to Cannot Apply when the answer is no" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToSupportMotherPage, false).success.value
          navigator.nextPage(WillTakeTimeToSupportMotherPage, NormalMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
        }
      }

      "must go from Name" - {

        "to Nino" in {

          navigator.nextPage(NamePage, NormalMode, emptyUserAnswers) mustEqual routes.NinoController.onPageLoad(NormalMode)
        }
      }

      "must go from Nino" - {

        "to Baby Has Been Born" in {

          navigator.nextPage(NinoPage, NormalMode, emptyUserAnswers) mustEqual routes.BabyHasBeenBornController.onPageLoad(NormalMode)
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

      "must go from Baby Date of Birth to Want Pay to Start on Birth Date" in {

        navigator.nextPage(BabyDateOfBirthPage, NormalMode, emptyUserAnswers) mustEqual routes.WantPayToStartOnBirthDateController.onPageLoad(NormalMode)
      }

      "must go from Baby Due Date" - {

        "to Want Pay to Start on Due Date when the child has not already been born" in {

          val answers = emptyUserAnswers.set(BabyHasBeenBornPage, false).success.value
          navigator.nextPage(BabyDueDatePage, NormalMode, answers) mustEqual routes.WantPayToStartOnDueDateController.onPageLoad(NormalMode)
        }

        "to Paternity Leave Length when the child has already been born" in {

          val answers = emptyUserAnswers.set(BabyHasBeenBornPage, true).success.value
          navigator.nextPage(BabyDueDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
        }
      }

      "must go from Want Pay To Start on Birth Date" - {

        "to Baby Due Date page when the answer is yes" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnBirthDatePage, true).success.value
          navigator.nextPage(WantPayToStartOnBirthDatePage, NormalMode, answers) mustEqual routes.BabyDueDateController.onPageLoad(NormalMode)
        }

        "to Pay Start Date Baby Born when the answer is no" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnBirthDatePage, false).success.value
          navigator.nextPage(WantPayToStartOnBirthDatePage, NormalMode, answers) mustEqual routes.PayStartDateBabyBornController.onPageLoad(NormalMode)
        }
      }

      "must go from Want Pay To Start on Due Date" - {

        "to Paternity Leave Length when the answer is yes" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnDueDatePage, true).success.value
          navigator.nextPage(WantPayToStartOnDueDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
        }

        "to Pay Start Date Baby Due when the answer is no" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnDueDatePage, false).success.value
          navigator.nextPage(WantPayToStartOnDueDatePage, NormalMode, answers) mustEqual routes.PayStartDateBabyDueController.onPageLoad(NormalMode)
        }
      }

      "must go from Pay Start Date Baby Born to Baby Due Date" in {

        navigator.nextPage(PayStartDateBabyBornPage, NormalMode, emptyUserAnswers) mustEqual routes.BabyDueDateController.onPageLoad(NormalMode)
      }

      "must go from Pay Start Date Baby Due to Paternity Leave Length" in {

        navigator.nextPage(PayStartDateBabyDuePage, NormalMode, emptyUserAnswers) mustEqual routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
      }

      "must go from Paternity Leave Length to Check Answers" in {

        navigator.nextPage(PaternityLeaveLengthPage, NormalMode, emptyUserAnswers) mustEqual routes.CheckYourAnswersController.onPageLoad
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad
      }

      "must go from Is Adopting" - {

        "to Cannot Apply Adopting when the answer is yes" in {

          val answers = emptyUserAnswers.set(IsAdoptingPage, true).success.value
          navigator.nextPage(IsAdoptingPage, CheckMode, answers) mustEqual routes.CannotApplyAdoptingController.onPageLoad()
        }

        "to Check Answers when the answer is no" in {

          val answers = emptyUserAnswers.set(IsAdoptingPage, false).success.value
          navigator.nextPage(IsAdoptingPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
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
                .set(WillTakeTimeToSupportMotherPage, true).success.value

            navigator.nextPage(WillTakeTimeToCareForChildPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Will Take Time to Support Mother when that question has not been answered" in {

            val answers = emptyUserAnswers.set(WillTakeTimeToCareForChildPage, false).success.value
            navigator.nextPage(WillTakeTimeToCareForChildPage, CheckMode, answers) mustEqual routes.WillTakeTimeToSupportMotherController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Will Take Time to Support Mother" - {

        "to Check Answers when the answer is yes" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToSupportMotherPage, true).success.value
          navigator.nextPage(WillTakeTimeToSupportMotherPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Cannot Apply when the answer is no" in {

          val answers = emptyUserAnswers.set(WillTakeTimeToSupportMotherPage, false).success.value
          navigator.nextPage(WillTakeTimeToSupportMotherPage, CheckMode, answers) mustEqual routes.CannotApplyController.onPageLoad()
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

          "to Check Answers when Want To Start on Due Date has been answered" in {

            val answers = emptyUserAnswers
              .set(BabyHasBeenBornPage, false).success.value
              .set(WantPayToStartOnDueDatePage, true).success.value
            navigator.nextPage(BabyHasBeenBornPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Want Pay to Start on Due Date when it has not been answered" in {

            val answers = emptyUserAnswers.set(BabyHasBeenBornPage, false).success.value
            navigator.nextPage(BabyHasBeenBornPage, CheckMode, answers) mustEqual routes.WantPayToStartOnDueDateController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Baby Date of Birth" - {

        "to Check Answers when Want Pay to Start on Birth Date has been answered" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnBirthDatePage, true).success.value
          navigator.nextPage(BabyDateOfBirthPage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Want Pay to Start on Birth Date when it has not been answered" in {

          navigator.nextPage(BabyDateOfBirthPage, CheckMode, emptyUserAnswers) mustEqual routes.WantPayToStartOnBirthDateController.onPageLoad(CheckMode)
        }
      }

      "must go from Baby Due Date" - {

        "to Check Answers when Want Pay to Start on Due Date has been answered" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnDueDatePage, true).success.value
          navigator.nextPage(BabyDueDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "to Want Pay to Start on Birth Date when it has not been answered" in {

          navigator.nextPage(BabyDueDatePage, CheckMode, emptyUserAnswers) mustEqual routes.WantPayToStartOnDueDateController.onPageLoad(CheckMode)
        }
      }

      "must go from Want Pay to Start on Birth Date" - {

        "to Check Answers when the answer is yes" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnBirthDatePage, true).success.value
          navigator.nextPage(WantPayToStartOnBirthDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "when the answer is no" - {

          "to Check Answers when Pay Start Date Baby Born has been answered" in {

            val answers =
              emptyUserAnswers
                .set(WantPayToStartOnBirthDatePage, true).success.value
                .set(PayStartDateBabyBornPage, LocalDate.now).success.value

            navigator.nextPage(WantPayToStartOnBirthDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Pay Start Date when it has not already been answered" in {

            val answers = emptyUserAnswers.set(WantPayToStartOnBirthDatePage, false).success.value
            navigator.nextPage(WantPayToStartOnBirthDatePage, CheckMode, answers) mustEqual routes.PayStartDateBabyBornController.onPageLoad(CheckMode)
          }
        }
      }

      "must go from Want Pay to Start on Due Date" - {

        "to Check Answers when the answer is yes" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnDueDatePage, true).success.value
          navigator.nextPage(WantPayToStartOnDueDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
        }

        "when the answer is no" - {

          "to Check Answers when Pay Start Date Baby Due has been answered" in {

            val answers =
              emptyUserAnswers
                .set(WantPayToStartOnDueDatePage, true).success.value
                .set(PayStartDateBabyDuePage, LocalDate.now).success.value

            navigator.nextPage(WantPayToStartOnDueDatePage, CheckMode, answers) mustEqual routes.CheckYourAnswersController.onPageLoad
          }

          "to Pay Start Date Baby Due when it has not already been answered" in {

            val answers = emptyUserAnswers.set(WantPayToStartOnDueDatePage, false).success.value
            navigator.nextPage(WantPayToStartOnDueDatePage, CheckMode, answers) mustEqual routes.PayStartDateBabyDueController.onPageLoad(CheckMode)
          }
        }
      }
    }
  }
}
