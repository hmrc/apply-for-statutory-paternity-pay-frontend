package pages

import controllers.routes
import models.{PaternityLeaveLengthPostApril24, Mode}
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object PaternityLeaveLengthPostApril24Page extends QuestionPage[PaternityLeaveLengthPostApril24] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "paternityLeaveLengthPostApril24"

  override def route(mode: Mode): Call = routes.PaternityLeaveLengthPostApril24Controller.onPageLoad(mode)
}
