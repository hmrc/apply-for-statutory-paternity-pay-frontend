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

package pages

import config.Constants
import models.{CountryOfResidence, Mode, UserAnswers}

import java.time.LocalDate
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object BabyDueDatePage extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "babyDueDate"

  override def route(mode: Mode): Call = controllers.routes.BabyDueDateController.onPageLoad(mode)

  override def cleanup(value: Option[LocalDate], userAnswers: UserAnswers): Try[UserAnswers] =
    value.map {
      case d if d.isBefore(Constants.april24LegislationEffective) =>
        removeRedundantAnswers(userAnswers, paternityPagesGbPostApril24)

      case _ =>
        userAnswers.get(CountryOfResidencePage).map {
          case CountryOfResidence.NorthernIreland =>
            removeRedundantAnswers(userAnswers, paternityPagesGbPostApril24)

          case _ =>
            removeRedundantAnswers(userAnswers, paternityPagesGbPreApril24OrNi)
        }.getOrElse(super.cleanup(value, userAnswers))
    }.getOrElse(super.cleanup(value, userAnswers))
}
