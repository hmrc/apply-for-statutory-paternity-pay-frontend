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

import controllers.routes
import models.PaternityLeaveLengthGbPostApril24.{OneWeek, TwoWeeks, Unsure}
import models.{Mode, PaternityLeaveLengthGbPostApril24, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object PaternityLeaveLengthGbPostApril24Page extends QuestionPage[PaternityLeaveLengthGbPostApril24] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "paternityLeaveLengthGbPostApril24"

  override def route(mode: Mode): Call = routes.PaternityLeaveLengthGbPostApril24Controller.onPageLoad(mode)

  override def cleanup(value: Option[PaternityLeaveLengthGbPostApril24], userAnswers: UserAnswers): Try[UserAnswers] =
    value.map {
      case OneWeek =>
        userAnswers
          .remove(LeaveTakenTogetherOrSeparatelyPage)
          .flatMap(_.remove(PayStartDateWeek1Page))
          .flatMap(_.remove(PayStartDateWeek2Page))

      case TwoWeeks =>
        super.cleanup(value, userAnswers)

      case Unsure =>
        userAnswers
          .remove(LeaveTakenTogetherOrSeparatelyPage)
          .flatMap(_.remove(PayStartDateWeek1Page))
          .flatMap(_.remove(PayStartDateWeek2Page))
          .flatMap(_.remove(PayStartDateGbPostApril24Page))


    }.getOrElse(super.cleanup(value, userAnswers))
}
