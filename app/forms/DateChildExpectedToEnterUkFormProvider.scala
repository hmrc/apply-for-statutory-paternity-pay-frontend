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

package forms

import config.Formats.dateTimeFormat

import java.time.{Clock, LocalDate}
import forms.mappings.Mappings

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{Lang, Messages}

class DateChildExpectedToEnterUkFormProvider @Inject()(clock: Clock) extends Mappings {

  def apply()(implicit messages: Messages): Form[LocalDate] = {
    implicit val lang: Lang = messages.lang

    val maximumDate = LocalDate.now(clock).plusWeeks(40)
    val minimumDate = LocalDate.now(clock)

    Form(
      "value" -> localDate(
        invalidKey = "dateChildExpectedToEnterUk.error.invalid",
        allRequiredKey = "dateChildExpectedToEnterUk.error.required.all",
        twoRequiredKey = "dateChildExpectedToEnterUk.error.required.two",
        requiredKey = "dateChildExpectedToEnterUk.error.required"
      ).verifying(
        maxDate(maximumDate, "dateChildExpectedToEnterUk.error.tooHigh", maximumDate.format(dateTimeFormat())),
        minDate(minimumDate, "dateChildExpectedToEnterUk.error.tooLow", minimumDate.format(dateTimeFormat()))
      )
    )
  }
}
