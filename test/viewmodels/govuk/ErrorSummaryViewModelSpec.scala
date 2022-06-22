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

package viewmodels.govuk

import forms.mappings.Mappings
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.test.Helpers.{stubMessages, stubMessagesApi}
import viewmodels.govuk.errorsummary._

import java.time.LocalDate

class ErrorSummaryViewModelSpec
  extends AnyFreeSpec
    with Matchers
    with Mappings
    with OptionValues {

  case class Data(date: LocalDate, string: String)

  private val testForm: Form[Data] =
    Form(
      mapping(
        "date"   -> localDate("invalid", "allRequired", "twoRequired", "oneRequired"),
        "string" -> text()
      )(Data.apply)(Data.unapply)
    )

  private implicit val msgs: Messages = stubMessages(stubMessagesApi())

  ".apply" - {

    "must append `.day` to the error key when `day` is an argument on the error" in {

      val boundForm = testForm.bind(Map("date.month" -> "1", "date.year" -> "1"))

      val errorSummary = ErrorSummaryViewModel(boundForm)
      errorSummary.errorList.head.href.value mustEqual "#date.day"
      errorSummary.errorList(1).href.value mustEqual "#string"
    }

    "must append `.month` to the error key when `month` is an argument on the error" in {

      val boundForm = testForm.bind(Map("date.day" -> "1", "date.year" -> "1"))

      val errorSummary = ErrorSummaryViewModel(boundForm)
      errorSummary.errorList.head.href.value mustEqual "#date.month"
      errorSummary.errorList(1).href.value mustEqual "#string"
    }

    "must append `.year` to the error key when `year` is an argument on the error" in {

      val boundForm = testForm.bind(Map("date.day" -> "1", "date.month" -> "1"))

      val errorSummary = ErrorSummaryViewModel(boundForm)
      errorSummary.errorList.head.href.value mustEqual "#date.year"
      errorSummary.errorList(1).href.value mustEqual "#string"
    }

    "must not append anything to the error key when neither day, month or year is an argument on the error" in {

      val boundForm = testForm.bind(Map.empty[String, String])

      val errorSummary = ErrorSummaryViewModel(boundForm)
      errorSummary.errorList.head.href.value mustEqual "#date"
      errorSummary.errorList(1).href.value mustEqual "#string"
    }
  }
}