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

package viewmodels.checkAnswers

import config.Formats.dateTimeFormat
import models.{LeaveTakenTogetherOrSeparately, PaternityLeaveLengthGbPostApril24, UserAnswers}
import org.scalatest.{OptionValues, TryValues}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import pages.{LeaveTakenTogetherOrSeparatelyPage, PaternityLeaveLengthGbPostApril24Page, PayStartDateGbPostApril24Page}
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessages

import java.time.LocalDate

class PayStartDateGbPostApril24SummarySpec extends AnyFreeSpec with Matchers with TryValues with OptionValues {

  ".row" - {

    implicit val messages: Messages = stubMessages()
    implicit val lang: Lang = messages.lang

    "must return a row when the user has answered this question" in {

      val now = LocalDate.now
      val answers = UserAnswers("id").set(PayStartDateGbPostApril24Page, Some(now)).success.value

      val result = PayStartDateGbPostApril24Summary.row(answers)

      result mustBe defined
      result.value.value.content.asHtml.toString mustEqual now.format(dateTimeFormat())
    }

    "must return a `not answered` row when the user is taking one week of leave and this question is not answered" in {

      val answers = UserAnswers("id").set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value

      val result = PayStartDateGbPostApril24Summary.row(answers)

      result mustBe defined
      result.value.value.content.asHtml.toString mustEqual messages("site.notProvided")
    }

    "must return a `not answered` row when the user is taking two weeks of leave together and this question is not answered" in {

      val answers =
        UserAnswers("id")
          .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value
          .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Together).success.value

      val result = PayStartDateGbPostApril24Summary.row(answers)

      result mustBe defined
      result.value.value.content.asHtml.toString mustEqual messages("site.notProvided")
    }

    "must return None when the user is taking two weeks of leave separately and this question is not answered" in {

      val answers =
        UserAnswers("id")
          .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.TwoWeeks).success.value
          .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value

      val result = PayStartDateGbPostApril24Summary.row(answers)

      result must not be defined
    }

    "must return None when the user is unsure how many weeks of leave they are taking" in {

      val answers = UserAnswers("id").set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.Unsure).success.value

      val result = PayStartDateGbPostApril24Summary.row(answers)

      result must not be defined
    }

    "must return None when the user has not answered how many weeks of leave they are taking" in {

      val result = PayStartDateWeek2Summary.row(UserAnswers("id"))

      result must not be defined
    }
  }
}
