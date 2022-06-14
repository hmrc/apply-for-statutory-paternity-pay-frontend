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

import models.PayStartDateLimits
import pages.BabyDateOfBirthPage
import play.api.libs.json.JsPath

import java.time.LocalDate

case object DerivePayStartDateLimitsBabyBorn extends Derivable[LocalDate, PayStartDateLimits] {

  override val derive: LocalDate => PayStartDateLimits =
    date => PayStartDateLimits(date.plusDays(1), date.plusWeeks(8))

  override def path: JsPath = BabyDateOfBirthPage.path
}
