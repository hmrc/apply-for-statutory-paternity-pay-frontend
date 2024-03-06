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
import forms.PayStartDateGbPostApril24FormProvider
import json.OptionalLocalDateReads._
import logging.Logging
import models.Mode
import navigation.Navigator
import pages.PayStartDateGbPostApril24Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.PayStartDateService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PayStartDateGbPostApril24View

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PayStartDateGbPostApril24Controller @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     sessionRepository: SessionRepository,
                                                     navigator: Navigator,
                                                     identify: IdentifierAction,
                                                     getData: DataRetrievalAction,
                                                     requireData: DataRequiredAction,
                                                     formProvider: PayStartDateGbPostApril24FormProvider,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: PayStartDateGbPostApril24View,
                                                     payStartDateService: PayStartDateService
                                      )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport
    with AnswerExtractor
    with Logging {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      getPaternityReason { paternityReason =>

        payStartDateService.gbPostApril24Dates(request.userAnswers).fold(
          pages => {
            val message = pages.toChain.toList.mkString(", ")
            logger.warn(s"Failed to find pay start date limits GB post April 24, missing pages: $message")
            Redirect(routes.JourneyRecoveryController.onPageLoad())
          },
          limits => {
            val form = formProvider(limits)

            val preparedForm = request.userAnswers.get(PayStartDateGbPostApril24Page) match {
              case None => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, mode, paternityReason))
          }
        )
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      getPaternityReasonAsync { paternityReason =>

        payStartDateService.gbPostApril24Dates(request.userAnswers).fold(
          pages => {
            val message = pages.toChain.toList.mkString(", ")
            logger.warn(s"Failed to find pay start date limits GB post April 24, missing pages: $message")
            Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))
          },
          limits => {
            val form = formProvider(limits)

            form.bindFromRequest().fold(
              formWithErrors =>
                Future.successful(BadRequest(view(formWithErrors, mode, paternityReason))),

              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(PayStartDateGbPostApril24Page, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(PayStartDateGbPostApril24Page, mode, updatedAnswers))
            )
          }
        )
    }
  }
}
