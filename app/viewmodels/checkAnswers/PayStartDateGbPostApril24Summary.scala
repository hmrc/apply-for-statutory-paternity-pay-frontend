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
import controllers.routes
import json.OptionalLocalDateReads._
import models.{CheckMode, LeaveTakenTogetherOrSeparately, PaternityLeaveLengthGbPostApril24, UserAnswers}
import pages.{LeaveTakenTogetherOrSeparatelyPage, PaternityLeaveLengthGbPostApril24Page, PayStartDateGbPostApril24Page}
import play.api.i18n.{Lang, Messages}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryListRow, Value}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object PayStartDateGbPostApril24Summary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PayStartDateGbPostApril24Page).map {
      _.map {
        answer =>
          implicit val lang: Lang = messages.lang

          viewModel(ValueViewModel(answer.format(dateTimeFormat())))
      }
    }.getOrElse {
      answers.get(PaternityLeaveLengthGbPostApril24Page).flatMap {
        case PaternityLeaveLengthGbPostApril24.OneWeek =>
          Some(viewModel(ValueViewModel(messages("site.notAnswered"))))

        case PaternityLeaveLengthGbPostApril24.TwoWeeks =>
          answers.get(LeaveTakenTogetherOrSeparatelyPage).flatMap {
            case LeaveTakenTogetherOrSeparately.Together =>
              Some(viewModel(ValueViewModel(messages("site.notAnswered"))))

            case _ =>
              None
          }

        case PaternityLeaveLengthGbPostApril24.Unsure =>
          None
      }
    }

  private def viewModel(value: Value)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "payStartDateGbPostApril24.checkYourAnswersLabel",
      value = value,
      actions = Seq(
        ActionItemViewModel("site.change", routes.PayStartDateGbPostApril24Controller.onPageLoad(CheckMode).url)
          .withVisuallyHiddenText(messages("payStartDateGbPostApril24.change.hidden"))
      )
    )
}