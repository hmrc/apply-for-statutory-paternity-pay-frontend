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
import forms.mappings.Mappings
import models.PayStartDateLimits
import play.api.data.Form
import play.api.i18n.Lang

import java.time.LocalDate
import javax.inject.Inject

class PayStartDateBabyDueFormProvider @Inject() extends Mappings {

  def apply(dateLimits: PayStartDateLimits)(implicit lang: Lang): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "payStartDateBabyDue.error.invalid",
        allRequiredKey = "payStartDateBabyDue.error.required.all",
        twoRequiredKey = "payStartDateBabyDue.error.required.two",
        requiredKey    = "payStartDateBabyDue.error.required"
      ).verifying(minDate(dateLimits.min, "payStartDateBabyDue.error.belowMinimum", dateLimits.min.format(dateTimeFormat())))
        .verifying(maxDate(dateLimits.max, "payStartDateBabyDue.error.aboveMaximum", dateLimits.max.format(dateTimeFormat())))
    )
}
