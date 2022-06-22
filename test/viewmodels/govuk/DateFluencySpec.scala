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
import play.api.i18n.Messages
import play.api.test.Helpers.{stubMessages, stubMessagesApi}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.date._
import viewmodels.govuk.fieldset._

import java.time.LocalDate

class DateFluencySpec
  extends AnyFreeSpec
    with Matchers
    with Mappings
    with OptionValues {

  private val legend = LegendViewModel(Text("foo"))

  private val testForm: Form[LocalDate] =
    Form(
      "value" -> localDate("invalid", "allRequired", "twoRequired", "oneRequired")
    )

  private implicit val msgs: Messages = stubMessages(stubMessagesApi())
  private val errorClass = "govuk-input--error"

  ".apply" - {

    "must include error styling on the day field when there is an error on the `day` field" in {

      val boundForm = testForm.bind(Map("value.day" -> "", "value.month" -> "1", "value.year" -> "1"))

      val dateInput = DateViewModel(boundForm("value"), legend)
      dateInput.items.find(_.id == "value.day").value.classes must include(errorClass)
      dateInput.items.find(_.id == "value.month").value.classes must not include errorClass
      dateInput.items.find(_.id == "value.year").value.classes must not include errorClass
    }

    "must include error styling on the month field when there is an error on the `month` field" in {

      val boundForm = testForm.bind(Map("value.day" -> "1", "value.month" -> "", "value.year" -> "1"))

      val dateInput = DateViewModel(boundForm("value"), legend)
      dateInput.items.find(_.id == "value.day").value.classes must not include errorClass
      dateInput.items.find(_.id == "value.month").value.classes must include(errorClass)
      dateInput.items.find(_.id == "value.year").value.classes must not include errorClass
    }

    "must include error styling on the year field when there is an error on the `year` field" in {

      val boundForm = testForm.bind(Map("value.day" -> "1", "value.month" -> "1", "value.year" -> ""))

      val dateInput = DateViewModel(boundForm("value"), legend)
      dateInput.items.find(_.id == "value.day").value.classes must not include errorClass
      dateInput.items.find(_.id == "value.month").value.classes must not include errorClass
      dateInput.items.find(_.id == "value.year").value.classes must include(errorClass)
    }

    "must include error styling on all fields when there is an error with all fields" in {

      val boundForm = testForm.bind(Map("value.day" -> "", "value.month" -> "", "value.year" -> ""))

      val dateInput = DateViewModel(boundForm("value"), legend)
      dateInput.items.find(_.id == "value.day").value.classes must include(errorClass)
      dateInput.items.find(_.id == "value.month").value.classes must include(errorClass)
      dateInput.items.find(_.id == "value.year").value.classes must include(errorClass)
    }
  }
}