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

class ChildHasEnteredUkPageSpec extends PageBehaviours {

  "ChildHasEnteredUkPage" - {

    beRetrievable[Boolean](ChildHasEnteredUkPage)

    beSettable[Boolean](ChildHasEnteredUkPage)

    beRemovable[Boolean](ChildHasEnteredUkPage)

    "must remove Date Child Expected to Enter UK when the answer is yes" in {

      val answers =
        emptyUserAnswers
          .set(ChildHasEnteredUkPage, false).success.value
          .set(DateChildEnteredUkPage, LocalDate.now).success.value
          .set(DateChildExpectedToEnterUkPage, LocalDate.now).success.value

      val result = answers.set(ChildHasEnteredUkPage, true).success.value

      result.get(DateChildExpectedToEnterUkPage) must not be defined
      result.get(DateChildEnteredUkPage) mustBe defined
    }

    "must remove Date Child Entered UK when the answer is no" in {

      val answers =
        emptyUserAnswers
          .set(ChildHasEnteredUkPage, true).success.value
          .set(DateChildEnteredUkPage, LocalDate.now).success.value
          .set(DateChildExpectedToEnterUkPage, LocalDate.now).success.value

      val result = answers.set(ChildHasEnteredUkPage, false).success.value

      result.get(DateChildEnteredUkPage) must not be defined
      result.get(DateChildExpectedToEnterUkPage) mustBe defined
    }
  }
}
