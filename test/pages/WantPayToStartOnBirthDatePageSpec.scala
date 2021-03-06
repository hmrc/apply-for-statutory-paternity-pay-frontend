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

package pages

import pages.behaviours.PageBehaviours

import java.time.LocalDate

class WantPayToStartOnBirthDatePageSpec extends PageBehaviours {

  "WantPayToStartOnBirthDatePage" - {

    beRetrievable[Boolean](WantPayToStartOnBirthDatePage)

    beSettable[Boolean](WantPayToStartOnBirthDatePage)

    beRemovable[Boolean](WantPayToStartOnBirthDatePage)

    "must remove Pay Start Date Baby Born when the answer is yes" in {

      val answers = emptyUserAnswers.set(PayStartDateBabyBornPage, LocalDate.now).success.value

      val result = answers.set(WantPayToStartOnBirthDatePage, true).success.value

      result.get(WantPayToStartOnBirthDatePage) must be(defined)
      result.get(PayStartDateBabyBornPage)      must not be defined
    }

    "must not remove Pay Start Date Baby Born when the answer is no" in {

      val answers = emptyUserAnswers.set(PayStartDateBabyBornPage, LocalDate.now).success.value

      val result = answers.set(WantPayToStartOnBirthDatePage, false).success.value

      result.get(WantPayToStartOnBirthDatePage) must be(defined)
      result.get(PayStartDateBabyBornPage)      must be(defined)
    }
  }
}
