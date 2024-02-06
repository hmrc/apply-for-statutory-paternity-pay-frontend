package pages

import controllers.routes
import models.Mode
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object $className$Page extends QuestionPage[Int] {
  
  override def path: JsPath = JsPath \ toString
  
  override def toString: String = "$className;format="decap"$"

  override def route(mode: Mode): Call = routes.$className$Controller.onPageLoad(mode)
}
