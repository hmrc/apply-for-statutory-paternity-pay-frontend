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
import forms.PaternityLeaveLengthGbPostApril24FormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.PaternityLeaveLengthGbPostApril24Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PaternityLeaveLengthGbPostApril24View

import scala.concurrent.{ExecutionContext, Future}

class PaternityLeaveLengthGbPostApril24Controller @Inject()(
                                       override val messagesApi: MessagesApi,
                                       sessionRepository: SessionRepository,
                                       navigator: Navigator,
                                       identify: IdentifierAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: PaternityLeaveLengthGbPostApril24FormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: PaternityLeaveLengthGbPostApril24View
                                     )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport
    with AnswerExtractor {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      getPaternityReason { reason =>

        val preparedForm = request.userAnswers.get(PaternityLeaveLengthGbPostApril24Page) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mode, reason))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getPaternityReasonAsync { reason =>

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, reason))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PaternityLeaveLengthGbPostApril24Page, value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(PaternityLeaveLengthGbPostApril24Page, mode, updatedAnswers))
        )
      }
  }
}
