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
import models.{LeaveTakenTogetherOrSeparately, Mode, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object LeaveTakenTogetherOrSeparatelyPage extends QuestionPage[LeaveTakenTogetherOrSeparately] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "leaveTakenTogetherOrSeparately"

  override def route(mode: Mode): Call = routes.LeaveTakenTogetherOrSeparatelyController.onPageLoad(mode)

  override def cleanup(value: Option[LeaveTakenTogetherOrSeparately], userAnswers: UserAnswers): Try[UserAnswers] =
    value.map {
      case LeaveTakenTogetherOrSeparately.Together =>
        userAnswers
          .remove(PayStartDateWeek1Page)
          .flatMap(_.remove(PayStartDateWeek2Page))

      case LeaveTakenTogetherOrSeparately.Separately =>
        userAnswers.remove(PayStartDateGbPostApril24Page)

    }.getOrElse(super.cleanup(value, userAnswers))
}
