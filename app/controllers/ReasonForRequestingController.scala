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
import forms.ReasonForRequestingFormProvider

import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{IsAdoptingFromAbroadPage, ReasonForRequestingPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ReasonForRequestingView

import scala.concurrent.{ExecutionContext, Future}

class ReasonForRequestingController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: ReasonForRequestingFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ReasonForRequestingView
                                     )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport
    with AnswerExtractor {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      getAnswer(IsAdoptingFromAbroadPage) { adoptingFromAbroad =>

        val preparedForm = request.userAnswers.get(ReasonForRequestingPage) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mode, adoptingFromAbroad))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getAnswerAsync(IsAdoptingFromAbroadPage) { adoptingFromAbroad =>

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, adoptingFromAbroad))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ReasonForRequestingPage, value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(ReasonForRequestingPage, mode, updatedAnswers))
        )
      }
  }
}
