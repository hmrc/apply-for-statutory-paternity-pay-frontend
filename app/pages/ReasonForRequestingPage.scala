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
import models.RelationshipToChild.{Adopting, ParentalOrder, SupportingAdoption}
import models.{Mode, RelationshipToChild, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object ReasonForRequestingPage extends QuestionPage[RelationshipToChild] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "reasonForRequesting"

  override def route(mode: Mode): Call = routes.ReasonForRequestingController.onPageLoad(mode)

  override def cleanup(value: Option[RelationshipToChild], userAnswers: UserAnswers): Try[UserAnswers] =
    value.map {
      case ParentalOrder =>
        val answersToRemove = adoptingUkDateQuestions ++ adoptingAbroadDateQuestions ++ paternityPages
        removeRedundantAnswers(userAnswers, answersToRemove)

      case Adopting | SupportingAdoption =>
        userAnswers.get(IsAdoptingFromAbroadPage).map {
          case true =>
            val answersToRemove = birthChildPaternityOrderDateQuestions ++ adoptingUkDateQuestions ++ paternityPages
            removeRedundantAnswers(userAnswers, answersToRemove)

          case false =>
            val answersToRemove = birthChildPaternityOrderDateQuestions ++ adoptingAbroadDateQuestions ++ paternityPages
            removeRedundantAnswers(userAnswers, answersToRemove)
        }.getOrElse(super.cleanup(value, userAnswers))

      case _ =>
        super.cleanup(value, userAnswers)
    }.getOrElse(super.cleanup(value, userAnswers))
}
