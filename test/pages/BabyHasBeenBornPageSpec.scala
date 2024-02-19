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

import pages.behaviours.PageBehaviours

import java.time.LocalDate

class BabyHasBeenBornPageSpec extends PageBehaviours {

  "BabyHasBeenBornPage" - {

    beRetrievable[Boolean](BabyHasBeenBornPage)

    beSettable[Boolean](BabyHasBeenBornPage)

    beRemovable[Boolean](BabyHasBeenBornPage)

    "when the answer is yes" - {

      "must remove Pay Start Date Baby Due" in {

        val answers =
          emptyUserAnswers
            .set(BabyDateOfBirthPage, LocalDate.now).success.value
            .set(BabyDueDatePage, LocalDate.now).success.value

        val result = answers.set(BabyHasBeenBornPage, true).success.value

        result.get(BabyDueDatePage)     must be(defined)
        result.get(BabyDateOfBirthPage) must be(defined)
      }
    }

    "when the answer is no" - {

      "must remove Baby Date of Birth" in {

        val answers =
          emptyUserAnswers
            .set(BabyDateOfBirthPage, LocalDate.now).success.value
            .set(BabyDueDatePage, LocalDate.now).success.value

        val result = answers.set(BabyHasBeenBornPage, false).success.value

        result.get(BabyDueDatePage)     must be(defined)
        result.get(BabyDateOfBirthPage) must not be defined
      }
    }
  }
}
