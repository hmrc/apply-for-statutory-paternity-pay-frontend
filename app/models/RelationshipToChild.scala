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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait RelationshipToChild

object RelationshipToChild extends Enumerable.Implicits {

  case object BirthChild extends WithName("birthChild") with RelationshipToChild
  case object Adopting extends WithName("adopting") with RelationshipToChild
  case object SupportingAdoption extends WithName("supportingAdoption") with RelationshipToChild
  case object ParentalOrder extends WithName("parentalOrder") with RelationshipToChild

  val values: Seq[RelationshipToChild] = Seq(
    BirthChild, Adopting, SupportingAdoption, ParentalOrder
  )

  def options(adoptingFromAbroad: Boolean)(implicit messages: Messages): Seq[RadioItem] =
    if (adoptingFromAbroad) adoptingAbroadOptions else notAdoptingAbroadOptions

  private def adoptingAbroadOptions(implicit messages: Messages): Seq[RadioItem] =
    Seq(
      RadioItem(
        content = Text(messages(s"reasonForRequesting.${Adopting.toString}")),
        value   = Some(Adopting.toString),
        id      = Some(s"value_0")
      ),
      RadioItem(
        content = Text(messages(s"reasonForRequesting.${SupportingAdoption.toString}")),
        value   = Some(SupportingAdoption.toString),
        id      = Some(s"value_1")
      )
    )

  private def notAdoptingAbroadOptions(implicit messages: Messages): Seq[RadioItem] =
    Seq(
      RadioItem(
        content = Text(messages(s"reasonForRequesting.${Adopting.toString}")),
        value   = Some(Adopting.toString),
        id      = Some(s"value_0")
      ),
      RadioItem(
        content = Text(messages(s"reasonForRequesting.${SupportingAdoption.toString}")),
        value   = Some(SupportingAdoption.toString),
        id      = Some(s"value_1")
      ),
      RadioItem(
        content = Text(messages(s"reasonForRequesting.${ParentalOrder.toString}")),
        value   = Some(ParentalOrder.toString),
        id      = Some(s"value_2")
      )
    )

  implicit val enumerable: Enumerable[RelationshipToChild] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
