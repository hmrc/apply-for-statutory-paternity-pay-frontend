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

package controllers

import controllers.actions._
import forms.PaternityLeaveLengthGbPreApril24OrNiFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.PaternityLeaveLengthGbPreApril24OrNiPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PaternityLeaveLengthGbPreApril24OrNiView

import scala.concurrent.{ExecutionContext, Future}

class PaternityLeaveLengthGbPreApril24OrNiController @Inject()(
                                                                override val messagesApi: MessagesApi,
                                                                sessionRepository: SessionRepository,
                                                                navigator: Navigator,
                                                                identify: IdentifierAction,
                                                                getData: DataRetrievalAction,
                                                                requireData: DataRequiredAction,
                                                                formProvider: PaternityLeaveLengthGbPreApril24OrNiFormProvider,
                                                                val controllerComponents: MessagesControllerComponents,
                                                                view: PaternityLeaveLengthGbPreApril24OrNiView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(PaternityLeaveLengthGbPreApril24OrNiPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(PaternityLeaveLengthGbPreApril24OrNiPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(PaternityLeaveLengthGbPreApril24OrNiPage, mode, updatedAnswers))
      )
  }
}
