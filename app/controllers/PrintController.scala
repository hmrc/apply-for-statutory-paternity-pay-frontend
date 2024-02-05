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

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import logging.Logging
import models.{JourneyModel, NormalMode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.FopService
import services.auditing.AuditService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.xml.pdf.PdfView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrintController @Inject() (
                                  override val messagesApi: MessagesApi,
                                  identify: IdentifierAction,
                                  getData: DataRetrievalAction,
                                  requireData: DataRequiredAction,
                                  val controllerComponents: MessagesControllerComponents,
                                  pdfView: PdfView,
                                  auditService: AuditService,
                                  fopService: FopService
                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onDownload: Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      JourneyModel.from(request.userAnswers).fold(
        pages => {
          val message = pages.toChain.toList.mkString(", ")
          logger.warn(s"Failed to generate journey model, missing pages: $message")
          Future.successful(Redirect(pages.head.route(NormalMode)))
        },
        model => {
          fopService.render(pdfView.render(model, implicitly, implicitly).body).map { pdf =>
            auditService.auditDownload(model)
            Ok(pdf)
              .as("application/octet-stream")
              .withHeaders(CONTENT_DISPOSITION -> "attachment; filename=apply-for-statutory-paternity-pay.pdf")
          }
        }
      )
  }
}
