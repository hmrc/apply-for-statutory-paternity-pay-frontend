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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(ReasonForRequestingPage.type, JsValue)] ::
    arbitrary[(IsApplyingForStatutoryAdoptionPayPage.type, JsValue)] ::
    arbitrary[(IsAdoptingFromAbroadPage.type, JsValue)] ::
    arbitrary[(DateChildWasMatchedPage.type, JsValue)] ::
    arbitrary[(CountryOfResidencePage.type, JsValue)] ::
    arbitrary[(ChildPlacementDatePage.type, JsValue)] ::
    arbitrary[(ChildHasBeenPlacedPage.type, JsValue)] ::
    arbitrary[(ChildExpectedPlacementDatePage.type, JsValue)] ::
    arbitrary[(IsAdoptingOrParentalOrderPage.type, JsValue)] ::
    arbitrary[(NinoPage.type, JsValue)] ::
    arbitrary[(WantPayToStartOnDueDatePage.type, JsValue)] ::
    arbitrary[(WillTakeTimeToSupportPartnerPage.type, JsValue)] ::
    arbitrary[(WillTakeTimeToCareForChildPage.type, JsValue)] ::
    arbitrary[(WillHaveCaringResponsibilityPage.type, JsValue)] ::
    arbitrary[(WantPayToStartOnBirthDatePage.type, JsValue)] ::
    arbitrary[(PayStartDateBabyBornPage.type, JsValue)] ::
    arbitrary[(PayStartDateBabyDuePage.type, JsValue)] ::
    arbitrary[(PaternityLeaveLengthPage.type, JsValue)] ::
    arbitrary[(NamePage.type, JsValue)] ::
    arbitrary[(IsInQualifyingRelationshipPage.type, JsValue)] ::
    arbitrary[(IsCohabitingPage.type, JsValue)] ::
    arbitrary[(IsBiologicalFatherPage.type, JsValue)] ::
    arbitrary[(BabyHasBeenBornPage.type, JsValue)] ::
    arbitrary[(BabyDueDatePage.type, JsValue)] ::
    arbitrary[(BabyDateOfBirthPage.type, JsValue)] ::
    Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id      <- nonEmptyString
        data    <- generators match {
          case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
          case _   => Gen.mapOf(oneOf(generators))
        }
      } yield UserAnswers (
        id = id,
        data = data.foldLeft(Json.obj()) {
          case (obj, (path, value)) =>
            obj.setObject(path.path, value).get
        }
      )
    }
  }
}
