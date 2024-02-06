package pages

import controllers.routes
import models.Mode
import java.time.LocalDate
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object $className$Page extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "$className;format="decap"$"

  override def route(mode: Mode): Call = routes.$className$Controller.onPageLoad(mode)
}
