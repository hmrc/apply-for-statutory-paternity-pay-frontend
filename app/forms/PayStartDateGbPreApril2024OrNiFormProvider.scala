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

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.Messages

class PayStartDateGbPreApril2024OrNiFormProvider @Inject() extends Mappings {

  def apply()(implicit messages: Messages): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "payStartDateGbPreApril2024OrNi.error.invalid",
        allRequiredKey = "payStartDateGbPreApril2024OrNi.error.required.all",
        twoRequiredKey = "payStartDateGbPreApril2024OrNi.error.required.two",
        requiredKey    = "payStartDateGbPreApril2024OrNi.error.required"
      )
    )
}
