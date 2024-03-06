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
import forms.PayStartDateWeek1FormProvider

import javax.inject.Inject
import json.OptionalLocalDateReads._
import models.Mode
import navigation.Navigator
import pages.PayStartDateWeek1Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.PayStartDateService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PayStartDateWeek1View

import scala.concurrent.{ExecutionContext, Future}

class PayStartDateWeek1Controller @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: PayStartDateWeek1FormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: PayStartDateWeek1View,
                                        payStartDateService: PayStartDateService
                                      )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport
    with AnswerExtractor {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      getPaternityReason { paternityReason =>
        val form = formProvider()

        val preparedForm = request.userAnswers.get(PayStartDateWeek1Page) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mode, paternityReason))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getPaternityReasonAsync { paternityReason =>
        val form = formProvider()

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, paternityReason))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PayStartDateWeek1Page, value))
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(PayStartDateWeek1Page, mode, updatedAnswers))
        )
      }
  }
}
