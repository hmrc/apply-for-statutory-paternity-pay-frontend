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

package controllers.actions

import models.requests.DataRequest
import models.{JourneyModel, NormalMode}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyModelFilterImpl @Inject() ()(implicit override val executionContext: ExecutionContext) extends JourneyModelFilter {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {
    Future.successful {
      JourneyModel.from(request.userAnswers).fold(
        pages => Some(Redirect(pages.head.route(NormalMode))),
        _ => None
      )
    }
  }
}

trait JourneyModelFilter extends ActionFilter[DataRequest]