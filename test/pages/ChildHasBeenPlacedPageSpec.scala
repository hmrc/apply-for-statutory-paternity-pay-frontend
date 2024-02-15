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

class ChildHasBeenPlacedPageSpec extends PageBehaviours {

  "ChildHasBeenPlacedPage" - {

    beRetrievable[Boolean](ChildHasBeenPlacedPage)

    beSettable[Boolean](ChildHasBeenPlacedPage)

    beRemovable[Boolean](ChildHasBeenPlacedPage)

    "must remove Child Expected Placement Date when the answer is yes" in {

      val answers =
        emptyUserAnswers
          .set(ChildHasBeenPlacedPage, false).success.value
          .set(ChildExpectedPlacementDatePage, LocalDate.now).success.value
          .set(ChildPlacementDatePage, LocalDate.now).success.value

      val result = answers.set(ChildHasBeenPlacedPage, true).success.value

      result.get(ChildExpectedPlacementDatePage) must not be defined
      result.get(ChildPlacementDatePage) mustBe defined
    }

    "must remove Child Placement Date when the answer is no" in {

      val answers =
        emptyUserAnswers
          .set(ChildHasBeenPlacedPage, true).success.value
          .set(ChildExpectedPlacementDatePage, LocalDate.now).success.value
          .set(ChildPlacementDatePage, LocalDate.now).success.value

      val result = answers.set(ChildHasBeenPlacedPage, false).success.value

      result.get(ChildPlacementDatePage) must not be defined
      result.get(ChildExpectedPlacementDatePage) mustBe defined
    }
  }
}
