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

package navigation

import base.SpecBase
import controllers.routes
import pages._
import models._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad
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

      "must go from Baby Due Date to Want Pay to Start on Due Date" in {

        navigator.nextPage(BabyDueDatePage, NormalMode, emptyUserAnswers) mustEqual routes.WantPayToStartOnDueDateController.onPageLoad(NormalMode)
      }

      "must go from Want Pay To Start on Birth Date" - {

        "to Paternity Leave Length when the answer is yes" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnBirthDatePage, true).success.value
          navigator.nextPage(WantPayToStartOnBirthDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
        }

        "to Pay Start Date when the answer is no" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnBirthDatePage, false).success.value
          navigator.nextPage(WantPayToStartOnBirthDatePage, NormalMode, answers) mustEqual routes.PayStartDateController.onPageLoad(NormalMode)
        }
      }

      "must go from Want Pay To Start on Due Date" - {

        "to Paternity Leave Length when the answer is yes" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnDueDatePage, true).success.value
          navigator.nextPage(WantPayToStartOnDueDatePage, NormalMode, answers) mustEqual routes.PaternityLeaveLengthController.onPageLoad(NormalMode)
        }

        "to Pay Start Date when the answer is no" in {

          val answers = emptyUserAnswers.set(WantPayToStartOnDueDatePage, false).success.value
          navigator.nextPage(WantPayToStartOnDueDatePage, NormalMode, answers) mustEqual routes.PayStartDateController.onPageLoad(NormalMode)
        }
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
    }
  }
}
