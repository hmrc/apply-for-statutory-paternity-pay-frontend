/*
 * Copyright 2022 HM Revenue & Customs
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

import com.dmanchester.playfop.sapi.PlayFop
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import logging.Logging
import models.JourneyModel
import org.apache.fop.apps.FOUserAgent
import org.apache.xmlgraphics.util.MimeConstants
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.auditing.AuditService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.xml.pdf.PdfView

import javax.inject.{Inject, Singleton}

@Singleton
class PrintController @Inject() (
                                  override val messagesApi: MessagesApi,
                                  identify: IdentifierAction,
                                  getData: DataRetrievalAction,
                                  requireData: DataRequiredAction,
                                  val controllerComponents: MessagesControllerComponents,
                                  pdfView: PdfView,
                                  auditService: AuditService,
                                  fop: PlayFop
                                ) extends FrontendBaseController with I18nSupport with Logging {

  private val userAgentBlock: FOUserAgent => Unit = { foUserAgent: FOUserAgent =>
    foUserAgent.setAccessibility(true)
    foUserAgent.setPdfUAEnabled(true)
    foUserAgent.setAuthor("HMRC forms service")
    foUserAgent.setProducer("HMRC forms services")
    foUserAgent.setCreator("HMRC forms services")
    foUserAgent.setSubject("Apply for statutory paternity pay form")
    foUserAgent.setTitle("Apply for statutory paternity pay form")
  }

  def onDownload: Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      JourneyModel.from(request.userAnswers).fold(
        pages => {
          val message = pages.toNonEmptyList.toList.mkString(", ")
          logger.warn(s"Failed to generate journey model, missing pages: $message")
          Redirect(routes.JourneyRecoveryController.onPageLoad())
        },
        model => {
          auditService.auditDownload(model)
          val pdf = fop.processTwirlXml(pdfView(model), MimeConstants.MIME_PDF, foUserAgentBlock = userAgentBlock)
          Ok(pdf)
            .as("application/octet-stream")
            .withHeaders(CONTENT_DISPOSITION -> "attachment; filename=apply-for-statutory-paternity-pay-sc3.pdf")
        }
      )
  }
}
