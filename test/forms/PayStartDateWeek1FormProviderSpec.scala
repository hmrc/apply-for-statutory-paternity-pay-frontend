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

import java.time.{LocalDate, ZoneOffset}
import forms.behaviours.DateBehaviours
import models.PayStartDateLimits
import play.api.data.FormError
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessages

class PayStartDateWeek1FormProviderSpec extends DateBehaviours {

  private val limitsGen = for {
    min <- datesBetween(LocalDate.of(2000, 1, 1), LocalDate.of(2050, 1, 1))
    max <- datesBetween(min.plusDays(1), LocalDate.of(2100, 1, 1))
  } yield PayStartDateLimits(min, max)

  val limits = limitsGen.sample.value

  private implicit val messages: Messages = stubMessages()
  private implicit val lang: Lang = Lang("en")

  val form = new PayStartDateWeek1FormProvider()(limits)

  ".value" - {

    val validData = datesBetween(
      min = limits.min,
      max = limits.max
    )

    behave like dateField(form, "value", validData)

    behave like optionalDateField(form, "value")

    behave like dateFieldWithMax(
      form,
      "value",
      limits.max,
      FormError("value", "payStartDateWeek1.error.tooHigh", Seq(limits.max.format(dateTimeFormat)))
    )

    behave like dateFieldWithMin(
      form,
      "value",
      limits.min,
      FormError("value", "payStartDateWeek1.error.tooLow", Seq(limits.min.format(dateTimeFormat)))
    )
  }
}
