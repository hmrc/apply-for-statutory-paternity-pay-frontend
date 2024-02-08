package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait PaternityLeaveLengthPostApril24

object PaternityLeaveLengthPostApril24 extends Enumerable.Implicits {

  case object Oneweek extends WithName("oneweek") with PaternityLeaveLengthPostApril24
  case object Twoweek extends WithName("twoweek") with PaternityLeaveLengthPostApril24

  val values: Seq[PaternityLeaveLengthPostApril24] = Seq(
    Oneweek, Twoweek
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map {
    case (value, index) =>
      RadioItem(
        content = Text(messages(s"paternityLeaveLengthPostApril24.${value.toString}")),
        value   = Some(value.toString),
        id      = Some(s"value_$index")
      )
  }

  implicit val enumerable: Enumerable[PaternityLeaveLengthPostApril24] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
