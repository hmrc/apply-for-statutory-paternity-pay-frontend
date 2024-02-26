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

import java.time.{Clock, LocalDate, ZoneId, ZoneOffset}
import forms.behaviours.DateBehaviours
import play.api.data.FormError
import play.api.i18n.{Lang, Messages}
import play.api.test.Helpers.stubMessages

class DateChildWasMatchedFormProviderSpec extends DateBehaviours {

  private val today        = LocalDate.now
  private val maximumDate  = today
  private val minimumDate  = today.minusYears(2)
  private val fixedInstant = today.atStartOfDay(ZoneId.systemDefault).toInstant
  private val clock        = Clock.fixed(fixedInstant, ZoneId.systemDefault)
  private implicit val messages: Messages = stubMessages()
  private implicit val lang: Lang = Lang("en")

  val form = new DateChildWasMatchedFormProvider(clock)()

  ".value" - {

    val validData = datesBetween(
      min = minimumDate,
      max = maximumDate
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "dateChildWasMatched.error.required.all")

    behave like dateFieldWithMax(
      form,
      "value",
      maximumDate,
      FormError("value", "dateChildWasMatched.error.tooHigh", Seq(maximumDate.format(dateTimeFormat)))
    )

    behave like dateFieldWithMin(
      form,
      "value",
      minimumDate,
      FormError("value", "dateChildWasMatched.error.tooLow", Seq(minimumDate.format(dateTimeFormat)))
    )
  }
}
