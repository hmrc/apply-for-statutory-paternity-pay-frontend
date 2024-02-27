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
import models.LeaveTakenTogetherOrSeparately
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class LeaveTakenTogetherOrSeparatelyPageSpec extends PageBehaviours {

  "LeaveTakenTogetherOrSeparatelyPage" - {

    beRetrievable[LeaveTakenTogetherOrSeparately](LeaveTakenTogetherOrSeparatelyPage)

    beSettable[LeaveTakenTogetherOrSeparately](LeaveTakenTogetherOrSeparatelyPage)

    beRemovable[LeaveTakenTogetherOrSeparately](LeaveTakenTogetherOrSeparatelyPage)

    "must remove Pay Start Date Week 1 and 2 when the answer is Together" in {

      val answers =
        emptyUserAnswers
          .set(PayStartDateGbPostApril24Page, Some(LocalDate.now)).success.value
          .set(PayStartDateWeek1Page, LocalDate.now).success.value
          .set(PayStartDateWeek2Page, LocalDate.now).success.value

      val result = answers.set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value

      result.isDefined(PayStartDateGbPostApril24Page) mustEqual true

      result.isDefined(PayStartDateWeek1Page) mustEqual false
      result.isDefined(PayStartDateWeek2Page) mustEqual false
    }

    "must remove Pay Start Date GB Post April 24 when the answer is Separate" in {

      val answers =
        emptyUserAnswers
          .set(PayStartDateGbPostApril24Page, Some(LocalDate.now)).success.value
          .set(PayStartDateWeek1Page, LocalDate.now).success.value
          .set(PayStartDateWeek2Page, LocalDate.now).success.value

      val result = answers.set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value

      result.isDefined(PayStartDateWeek1Page) mustEqual true
      result.isDefined(PayStartDateWeek2Page) mustEqual true

      result.isDefined(PayStartDateGbPostApril24Page) mustEqual false
    }
  }
}
