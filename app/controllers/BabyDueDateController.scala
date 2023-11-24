/*
 * Copyright 2023 HM Revenue & Customs
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

import config.Formats.dateTimeHintFormat
import controllers.actions._
import forms.BabyDueDateFormProvider

import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{BabyDueDatePage, BabyHasBeenBornPage}
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.BabyDueDateView

import java.time.{Clock, LocalDate}
import scala.concurrent.{ExecutionContext, Future}

class BabyDueDateController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: BabyDueDateFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: BabyDueDateView,
                                        clock: Clock
                                      )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport
    with AnswerExtractor {

  def form(implicit lang: Lang) = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>
      implicit val lang: Lang = request.lang(messagesApi)

      getAnswer(BabyHasBeenBornPage) { babyHasBeenBorn =>

        val dateHint = LocalDate.now(clock).plusWeeks(15).format(dateTimeHintFormat)

        val preparedForm = request.userAnswers.get(BabyDueDatePage) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mode, babyHasBeenBorn, dateHint))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      implicit val lang: Lang = request.lang(messagesApi)

      getAnswerAsync(BabyHasBeenBornPage) { babyHasBeenBorn =>

        val dateHint = LocalDate.now(clock).plusWeeks(15).format(dateTimeHintFormat)

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, babyHasBeenBorn, dateHint))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(BabyDueDatePage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(BabyDueDatePage, mode, updatedAnswers))
        )
      }
  }
}
