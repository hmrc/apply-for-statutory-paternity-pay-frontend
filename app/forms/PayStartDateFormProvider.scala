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

import java.time.LocalDate
import forms.mappings.Mappings

import javax.inject.Inject
import play.api.data.Form
import queries.PayStartDateLimits

class PayStartDateFormProvider @Inject() extends Mappings {

  def apply(dateLimits: PayStartDateLimits): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "payStartDate.error.invalid",
        allRequiredKey = "payStartDate.error.required.all",
        twoRequiredKey = "payStartDate.error.required.two",
        requiredKey    = "payStartDate.error.required"
      ).verifying(minDate(dateLimits.min, "payStartDate.error.belowMinimum", dateLimits.min.format(dateTimeFormat)))
        .verifying(maxDate(dateLimits.max, "payStartDate.error.aboveMaximum", dateLimits.max.format(dateTimeFormat)))
    )
}
