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

import models.{LeaveTakenTogetherOrSeparately, PaternityLeaveLengthGbPostApril24}
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class PaternityLeaveLengthGbPostApril24PageSpec extends PageBehaviours {

  "PaternityLeaveLengthGbPostApril24Page" - {

    beRetrievable[PaternityLeaveLengthGbPostApril24](PaternityLeaveLengthGbPostApril24Page)

    beSettable[PaternityLeaveLengthGbPostApril24](PaternityLeaveLengthGbPostApril24Page)

    beRemovable[PaternityLeaveLengthGbPostApril24](PaternityLeaveLengthGbPostApril24Page)

    "must remove Pay Start Date Week 1, Week 2, and Leave Taken Together or Separately when the answer is one week" in {

      val answers =
        emptyUserAnswers
          .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
          .set(PayStartDateWeek1Page, LocalDate.now).success.value
          .set(PayStartDateWeek2Page, LocalDate.now).success.value
          .set(PayStartDateGbPostApril24Page, LocalDate.now).success.value

      val result = answers.set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value

      result.isDefined(PayStartDateGbPostApril24Page) mustEqual true

      result.isDefined(LeaveTakenTogetherOrSeparatelyPage) mustEqual false
      result.isDefined(PayStartDateWeek1Page)              mustEqual false
      result.isDefined(PayStartDateWeek2Page)              mustEqual false
    }

    "must not remove details when the answer is two weeks" in {

      val answers =
        emptyUserAnswers
          .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
          .set(PayStartDateWeek1Page, LocalDate.now).success.value
          .set(PayStartDateWeek2Page, LocalDate.now).success.value
          .set(PayStartDateGbPostApril24Page, LocalDate.now).success.value

      val result = answers.set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value

      result.isDefined(PayStartDateGbPostApril24Page)      mustEqual true
      result.isDefined(LeaveTakenTogetherOrSeparatelyPage) mustEqual true
      result.isDefined(PayStartDateWeek1Page)              mustEqual true
      result.isDefined(PayStartDateWeek2Page)              mustEqual true
    }

    "must remove all Pay details when the answer is Unsure" in {

      val answers =
        emptyUserAnswers
          .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value
          .set(PayStartDateWeek1Page, LocalDate.now).success.value
          .set(PayStartDateWeek2Page, LocalDate.now).success.value
          .set(PayStartDateGbPostApril24Page, LocalDate.now).success.value

      val result = answers.set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.Unsure).success.value

      result.isDefined(PayStartDateGbPostApril24Page)      mustEqual false
      result.isDefined(LeaveTakenTogetherOrSeparatelyPage) mustEqual false
      result.isDefined(PayStartDateWeek1Page)              mustEqual false
      result.isDefined(PayStartDateWeek2Page)              mustEqual false
    }
  }
}
