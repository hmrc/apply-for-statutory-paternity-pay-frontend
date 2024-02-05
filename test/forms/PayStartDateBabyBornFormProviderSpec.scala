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
import forms.behaviours.DateBehaviours
import models.PayStartDateLimits
import play.api.data.FormError
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessages

import java.time.LocalDate

class PayStartDateBabyBornFormProviderSpec extends DateBehaviours {

  private val formProvider = new PayStartDateBabyBornFormProvider()
  private val minDate = LocalDate.of(2000, 1, 1)
  private val maxDate = LocalDate.of(2100, 1, 1)
  private val dateLimits = PayStartDateLimits(minDate, maxDate)
  private implicit val messages: Messages = stubMessages()
  private implicit val lang: Lang = Lang("en")

  private val form = formProvider(dateLimits)

  ".value" - {

    val validData = datesBetween(
      min = minDate,
      max = maxDate
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "payStartDateBabyBorn.error.required.all")

    behave like dateFieldWithMin(
      form,
      "value",
      minDate,
      FormError("value", "payStartDateBabyBorn.error.belowMinimum", Seq(minDate.format(dateTimeFormat)))
    )

    behave like dateFieldWithMax(
      form,
      "value",
      maxDate,
      FormError("value", "payStartDateBabyBorn.error.aboveMaximum", Seq(maxDate.format(dateTimeFormat)))
    )
  }
}
