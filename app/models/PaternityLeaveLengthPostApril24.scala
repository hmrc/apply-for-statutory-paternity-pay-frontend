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
import uk.gov.hmrc.govukfrontend.views.Aliases.{Hint, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait PaternityLeaveLengthPostApril24 {
  val hint: Option[String] = None
}

object PaternityLeaveLengthPostApril24 extends Enumerable.Implicits {

  case object Oneweek extends WithName("oneweek") with PaternityLeaveLengthPostApril24
  case object Twoweek extends WithName("twoweek") with PaternityLeaveLengthPostApril24
  case object Notsure extends WithName("notsure") with PaternityLeaveLengthPostApril24 {
    override val hint: Option[String] = Some("notsure.hint")
  }

  val values: Seq[PaternityLeaveLengthPostApril24] = Seq(
    Oneweek, Twoweek, Notsure
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"paternityLeaveLengthPostApril24.${value.toString}")),
        value   = Some(value.toString),
        id      = Some(s"value_$index"),
        hint    = value.hint.map(key => Hint(content = Text(messages(s"paternityLeaveLengthPostApril24.$key"))))
      )
  }

  implicit val enumerable: Enumerable[PaternityLeaveLengthPostApril24] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
