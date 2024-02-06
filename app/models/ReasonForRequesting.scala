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

sealed trait ReasonForRequesting

object ReasonForRequesting extends Enumerable.Implicits {

  case object Adopting extends WithName("adopting") with ReasonForRequesting
  case object SupportingAdoption extends WithName("supportingAdoption") with ReasonForRequesting
  case object ParentalOrder extends WithName("parentalOrder") with ReasonForRequesting

  val values: Seq[ReasonForRequesting] = Seq(
    Adopting, SupportingAdoption, ParentalOrder
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

  private def notAdoptingAbroadOptions(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"reasonForRequesting.${value.toString}")),
        value   = Some(value.toString),
        id      = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[ReasonForRequesting] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
