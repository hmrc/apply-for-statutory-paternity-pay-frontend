/*
 * Copyright 2023 HM Revenue & Customs
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

import java.time.{Clock, LocalDate, ZoneId, ZoneOffset}
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class BabyDateOfBirthFormProviderSpec extends DateBehaviours {

  private val today        = LocalDate.now
  private val minimumDate  = today.minusWeeks(7)
  private val fixedInstant = today.atStartOfDay(ZoneId.systemDefault).toInstant
  private val clock        = Clock.fixed(fixedInstant, ZoneId.systemDefault)

  private val form = new BabyDateOfBirthFormProvider(clock)()

  ".value" - {

    val validData = datesBetween(
      min = minimumDate,
      max = today
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "babyDateOfBirth.error.required.all")

    behave like dateFieldWithMax(form, "value", today, FormError("value", "babyDateOfBirth.error.future"))

    behave like dateFieldWithMin(
      form,
      "value",
      minimumDate,
      FormError("value", "babyDateOfBirth.error.beforeMinimum", Seq(minimumDate.format(dateTimeFormat)))
    )
  }
}
