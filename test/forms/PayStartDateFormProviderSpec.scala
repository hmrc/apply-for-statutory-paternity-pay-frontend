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

package forms

import config.Formats.dateTimeFormat
import forms.behaviours.DateBehaviours
import play.api.data.FormError
import queries.PayStartDateLimits

import java.time.LocalDate

class PayStartDateFormProviderSpec extends DateBehaviours {

  private val today = LocalDate.now()
  private val formProvider = new PayStartDateFormProvider()
  private val minDate = LocalDate.of(2000, 1, 1)
  private val maxDate = LocalDate.of(2100, 1, 1)
  private val dateLimits = PayStartDateLimits(minDate, maxDate)

  private val form = formProvider(dateLimits)

  ".value" - {

    val validData = datesBetween(
      min = minDate,
      max = maxDate
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "payStartDate.error.required.all")

    behave like dateFieldWithMin(
      form,
      "value",
      minDate,
      FormError("value", "payStartDate.error.belowMinimum", Seq(minDate.format(dateTimeFormat)))
    )

    behave like dateFieldWithMax(
      form,
      "value",
      maxDate,
      FormError("value", "payStartDate.error.aboveMaximum", Seq(maxDate.format(dateTimeFormat)))
    )
  }
}
