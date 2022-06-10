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

package queries

import pages.{BabyDateOfBirthPage, BabyDueDatePage}
import play.api.libs.json.JsPath

import java.time.LocalDate
import java.time.DayOfWeek.SUNDAY

import java.time.temporal.TemporalAdjusters.next

case class PayStartDateLimits(min: LocalDate, max: LocalDate)

case class DerivePayStartDateLimits(babyBorn: Boolean) extends Derivable[LocalDate, PayStartDateLimits] {

  override val derive: LocalDate => PayStartDateLimits = {
    date =>
      if (babyBorn) PayStartDateLimits(date.plusDays(1), date.plusWeeks(8))
      else          PayStartDateLimits(date.plusDays(1), date.plusWeeks(8).`with`(next(SUNDAY)))
  }

  override def path: JsPath = if (babyBorn) BabyDateOfBirthPage.path else BabyDueDatePage.path
}
