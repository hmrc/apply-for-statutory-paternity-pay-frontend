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

import java.time.{Clock, LocalDate, ZoneId, ZoneOffset}
import forms.behaviours.DateBehaviours
import play.api.data.FormError

class BabyDueDateFormProviderSpec extends DateBehaviours {

  private val today        = LocalDate.now
  private val fixedInstant = today.atStartOfDay(ZoneId.systemDefault).toInstant
  private val clock        = Clock.fixed(fixedInstant, ZoneId.systemDefault)

  val form = new BabyDueDateFormProvider(clock)()

  ".value" - {

    val validData = datesBetween(
      min = today,
      max = today.plusDays(10) // TODO: Change when we understand rules around dates
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "babyDueDate.error.required.all")

    behave like dateFieldWithMin(form, "value", today, FormError("value", "babyDueDate.error.past"))
  }
}
