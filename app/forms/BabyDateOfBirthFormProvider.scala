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
import java.time.{Clock, LocalDate}
import forms.mappings.Mappings

import javax.inject.Inject
import play.api.data.Form

class BabyDateOfBirthFormProvider @Inject()(clock: Clock) extends Mappings {

  def apply(): Form[LocalDate] = {

    val minimumDate = LocalDate.now(clock).minusWeeks(7)

    Form(
      "value" -> localDate(
        invalidKey = "babyDateOfBirth.error.invalid",
        allRequiredKey = "babyDateOfBirth.error.required.all",
        twoRequiredKey = "babyDateOfBirth.error.required.two",
        requiredKey = "babyDateOfBirth.error.required"
      ).verifying(maxDate(LocalDate.now(clock), "babyDateOfBirth.error.future"))
        .verifying(minDate(minimumDate, "babyDateOfBirth.error.beforeMinimum", minimumDate.format(dateTimeFormat)))
    )
  }
}
