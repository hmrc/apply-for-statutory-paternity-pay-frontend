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

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object IsAdoptingPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isAdopting"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    if (value contains true) {
      userAnswers
        .remove(BabyDateOfBirthPage)
        .flatMap(_.remove(BabyDueDatePage))
        .flatMap(_.remove(BabyHasBeenBornPage))
        .flatMap(_.remove(IsBiologicalFatherPage))
        .flatMap(_.remove(IsCohabitingPage))
        .flatMap(_.remove(IsInQualifyingRelationshipPage))
        .flatMap(_.remove(NamePage))
        .flatMap(_.remove(NinoPage))
        .flatMap(_.remove(PaternityLeaveLengthPage))
        .flatMap(_.remove(PayStartDatePage))
        .flatMap(_.remove(WantPayToStartOnBirthDatePage))
        .flatMap(_.remove(WantPayToStartOnDueDatePage))
        .flatMap(_.remove(WillHaveCaringResponsibilityPage))
        .flatMap(_.remove(WillTakeTimeToCareForChildPage))
        .flatMap(_.remove(WillTakeTimeToSupportMotherPage))
    }
    else {
      super.cleanup(value, userAnswers)
    }
}
