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

import models.RelationshipToChild
import models.requests.DataRequest
import pages.{IsAdoptingOrParentalOrderPage, ReasonForRequestingPage}
import play.api.libs.json.Reads
import play.api.mvc.{AnyContent, Result}
import play.api.mvc.Results.Redirect
import queries.{Derivable, Gettable}

import scala.concurrent.{ExecutionContext, Future}

trait AnswerExtractor {

  def getAnswer[A](query: Gettable[A])
                  (block: A => Result)
                  (implicit request: DataRequest[AnyContent], ev: Reads[A]): Result =
    request.userAnswers
      .get(query)
      .map(block(_))
      .getOrElse(Redirect(routes.JourneyRecoveryController.onPageLoad()))

  def getAnswerAsync[A](query: Gettable[A])
                       (block: A => Future[Result])
                       (implicit request: DataRequest[AnyContent], ec: ExecutionContext, ev: Reads[A]): Future[Result] =
    request.userAnswers
      .get(query)
      .map(block(_))
      .getOrElse(Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad())))

  def getAnswer[A, B](query: Derivable[A, B])
                     (block: B => Result)
                     (implicit request: DataRequest[AnyContent], ev: Reads[A]): Result =
    request.userAnswers
      .get(query)
      .map(block(_))
      .getOrElse(Redirect(routes.JourneyRecoveryController.onPageLoad()))

  def getAnswerAsync[A, B](query: Derivable[A, B])
                          (block: B => Future[Result])
                          (implicit request: DataRequest[AnyContent], ec: ExecutionContext, ev: Reads[A]): Future[Result] =
    request.userAnswers
      .get(query)
      .map(block(_))
      .getOrElse(Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad())))

  def getRelationshipToChild(block: RelationshipToChild => Result)(implicit request: DataRequest[AnyContent]): Result =
    request.userAnswers
      .get(IsAdoptingOrParentalOrderPage).map {
        case true =>
          request.userAnswers
            .get(ReasonForRequestingPage)
            .map(block(_))
            .getOrElse(Redirect(routes.JourneyRecoveryController.onPageLoad()))

        case false =>
          block(RelationshipToChild.BirthChild)
      }.getOrElse(Redirect(routes.JourneyRecoveryController.onPageLoad()))

  def getRelationshipToChildAsync(block: RelationshipToChild => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(IsAdoptingOrParentalOrderPage).map {
        case true =>
          request.userAnswers
            .get(ReasonForRequestingPage)
            .map(block(_))
            .getOrElse(Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad())))

        case false =>
          block(RelationshipToChild.BirthChild)
      }.getOrElse(Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad())))
}
