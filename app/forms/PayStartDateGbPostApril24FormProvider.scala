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

import java.time.LocalDate
import forms.mappings.Mappings
import models.PayStartDateLimits

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms.optional
import play.api.i18n.{Lang, Messages}

class PayStartDateGbPostApril24FormProvider @Inject() extends Mappings {

  def apply(limits: PayStartDateLimits)(implicit messages: Messages): Form[Option[LocalDate]] = {
    implicit val lang: Lang = messages.lang

    Form(
      "value" -> optional(localDate(
        invalidKey = "payStartDateGbPostApril24.error.invalid",
        allRequiredKey = "payStartDateGbPostApril24.error.required.all",
        twoRequiredKey = "payStartDateGbPostApril24.error.required.two",
        requiredKey = "payStartDateGbPostApril24.error.required"
      ).verifying(
        maxDate(limits.max, "payStartDateGbPostApril24.error.tooHigh", limits.max.format(dateTimeFormat())),
        minDate(limits.min, "payStartDateGbPostApril24.error.tooLow", limits.min.format(dateTimeFormat()))
      ))
    )
  }
}