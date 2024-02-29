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

import base.SpecBase
import models.RelationshipToChild._
import models.{Mode, PaternityReason, RelationshipToChild, UserAnswers}
import models.requests.DataRequest
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.{IsAdoptingFromAbroadPage, IsAdoptingOrParentalOrderPage, QuestionPage, ReasonForRequestingPage}
import play.api.libs.json.{JsPath, JsString, Json}
import play.api.mvc.Results.{Ok, Redirect}
import play.api.mvc.{AnyContent, Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import queries.Gettable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AnswerExtractorSpec extends SpecBase with ScalaCheckPropertyChecks {

  private object TestPage extends QuestionPage[Int] {
    override def path: JsPath = JsPath \ "test"
    override def route(mode: Mode): Call = Call("GET", "/")
  }

  private def buildRequest(answers: UserAnswers): DataRequest[AnyContent] =
    DataRequest(FakeRequest(), answers.id, answers)

  private class TestController extends AnswerExtractor {

    def get(query: Gettable[Int])(implicit request: DataRequest[AnyContent]): Result =
      getAnswer(query) {
        answer =>
          Ok(Json.toJson(answer))
      }

    def getAsync(query: Gettable[Int])(implicit request: DataRequest[AnyContent]): Future[Result] =
      getAnswerAsync(query) {
        answer =>
          Future.successful(Ok(Json.toJson(answer)))
      }

    def getRelationship(implicit request: DataRequest[AnyContent]): Result =
      getRelationshipToChild {
        relationship =>
          Ok(Json.toJson(relationship))
      }

    def getRelationshipAsync(implicit request: DataRequest[AnyContent]): Future[Result] =
      getRelationshipToChildAsync {
        relationship =>
          Future.successful(Ok(Json.toJson(relationship)))
      }

    def getPatReason(implicit request: DataRequest[AnyContent]): Result =
      getPaternityReason {
        reason =>
          Ok(Json.toJson(reason))
      }

    def getPatReasonAsync(implicit request: DataRequest[AnyContent]): Future[Result] =
      getPaternityReasonAsync {
        reason =>
          Future.successful(Ok(Json.toJson(reason)))
      }
  }

  "getAnswer" - {

    "must pass the answer into the provided block when the answer exists in user answers" in {

      val answers = emptyUserAnswers.set(TestPage, 1).success.value
      implicit val request = buildRequest(answers)

      val controller = new TestController()

      controller.get(TestPage) mustEqual Ok(Json.toJson(1))
    }

    "must redirect to Journey Recovery when the answer does not exist in user answers" in {

      implicit val request = buildRequest(emptyUserAnswers)

      val controller = new TestController()

      controller.get(TestPage) mustEqual Redirect(routes.JourneyRecoveryController.onPageLoad())
    }
  }

  "getAnswerAsync" - {

    "must pass the answer into the provided block when the answer exists in user answers" in {

      val answers = emptyUserAnswers.set(TestPage, 1).success.value
      implicit val request = buildRequest(answers)

      val controller = new TestController()

      controller.getAsync(TestPage).futureValue mustEqual Ok(Json.toJson(1))
    }

    "must redirect to Journey Recovery when the answer does not exist in user answers" in {

      implicit val request = buildRequest(emptyUserAnswers)

      val controller = new TestController()

      controller.getAsync(TestPage).futureValue mustEqual Redirect(routes.JourneyRecoveryController.onPageLoad())
    }
  }

  "getRelationshipToChild" - {

    "must pass BirthChild to the provided block when the answer to IsAdopting is false" in {

      val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value
      implicit val request = buildRequest(answers)

      val controller = new TestController()

      controller.getRelationship mustEqual Ok(JsString(BirthChild.toString))
    }

    "must pass the ReasonForRequesting to the provided block when IsAdopting is true" in {

      forAll(Gen.oneOf(RelationshipToChild.values)) {
        reason =>

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(ReasonForRequestingPage, reason).success.value

          implicit val request = buildRequest(answers)

          val controller = new TestController()

          controller.getRelationship mustEqual Ok(JsString(reason.toString))
      }
    }

    "must redirect to journey recovery when IsAdopting has not been answered" in {

      implicit val request = buildRequest(emptyUserAnswers)

      val controller = new TestController()

      controller.getRelationship mustEqual Redirect(routes.JourneyRecoveryController.onPageLoad())
    }

    "must redirect to journey recovery when IsAdopting is true and Reason has not been answered" in {

      val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, true).success.value
      implicit val request = buildRequest(answers)

      val controller = new TestController()

      controller.getRelationship mustEqual Redirect(routes.JourneyRecoveryController.onPageLoad())
    }
  }

  "getRelationshipToChildAsync" - {

    "must pass BirthChild to the provided block when the answer to IsAdopting is false" in {

      val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value
      implicit val request = buildRequest(answers)

      val controller = new TestController()

      controller.getRelationshipAsync.futureValue mustEqual Ok(JsString(BirthChild.toString))
    }

    "must pass the ReasonForRequesting to the provided block when IsAdopting is true" in {

      forAll(Gen.oneOf(RelationshipToChild.values)) {
        reason =>

          val answers =
            emptyUserAnswers
              .set(IsAdoptingOrParentalOrderPage, true).success.value
              .set(ReasonForRequestingPage, reason).success.value

          implicit val request = buildRequest(answers)

          val controller = new TestController()

          controller.getRelationshipAsync.futureValue mustEqual Ok(JsString(reason.toString))
      }
    }

    "must redirect to journey recovery when IsAdopting has not been answered" in {

      implicit val request = buildRequest(emptyUserAnswers)

      val controller = new TestController()

      controller.getRelationshipAsync.futureValue mustEqual Redirect(routes.JourneyRecoveryController.onPageLoad())
    }

    "must redirect to journey recovery when IsAdopting is true and Reason has not been answered" in {

      val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, true).success.value
      implicit val request = buildRequest(answers)

      val controller = new TestController()

      controller.getRelationshipAsync.futureValue mustEqual Redirect(routes.JourneyRecoveryController.onPageLoad())
    }
  }

  "getPaternityReason" - {

    "must return Parental From Birth Child when the user is not adopting or a parental order parent" in {

      val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value
      implicit val request = buildRequest(answers)

      val controller = new TestController()

      contentAsJson(Future.successful(controller.getPatReason)) mustEqual JsString(PaternityReason.PaternityFromBirth.toString)
    }

    "must return Parental From Birth Child when the user is a parental order child" in {

      val answers =
        emptyUserAnswers
          .set(IsAdoptingOrParentalOrderPage, false).success.value
          .set(IsAdoptingFromAbroadPage, false).success.value
          .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value

      implicit val request = buildRequest(answers)

      val controller = new TestController()

      contentAsJson(Future.successful(controller.getPatReason)) mustEqual JsString(PaternityReason.PaternityFromBirth.toString)
    }

    "must return Parental From Uk Adoption when the user is adopting or supporting adoption from a child in the UK" in {

      val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value
      val answers =
        emptyUserAnswers
          .set(IsAdoptingOrParentalOrderPage, true).success.value
          .set(IsAdoptingFromAbroadPage, false).success.value
          .set(ReasonForRequestingPage, relationship).success.value

      implicit val request = buildRequest(answers)

      val controller = new TestController()

      contentAsJson(Future.successful(controller.getPatReason)) mustEqual JsString(PaternityReason.PaternityFromUkAdoption.toString)
    }

    "must return Parental From Adoption From Abroad when the user is adopting or supporting adoption from abroad" in {

      val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value
      val answers =
        emptyUserAnswers
          .set(IsAdoptingOrParentalOrderPage, true).success.value
          .set(IsAdoptingFromAbroadPage, true).success.value
          .set(ReasonForRequestingPage, relationship).success.value

      implicit val request = buildRequest(answers)

      val controller = new TestController()

      contentAsJson(Future.successful(controller.getPatReason)) mustEqual JsString(PaternityReason.PaternityFromAdoptionFromAbroad.toString)
    }
  }

  "getPaternityReasonAsync" - {

    "must return Parental From Birth Child when the user is not adopting or a parental order parent" in {

      val answers = emptyUserAnswers.set(IsAdoptingOrParentalOrderPage, false).success.value
      implicit val request = buildRequest(answers)

      val controller = new TestController()

      contentAsJson(controller.getPatReasonAsync) mustEqual JsString(PaternityReason.PaternityFromBirth.toString)
    }

    "must return Parental From Birth Child when the user is a parental order child" in {

      val answers =
        emptyUserAnswers
          .set(IsAdoptingOrParentalOrderPage, false).success.value
          .set(IsAdoptingFromAbroadPage, false).success.value
          .set(ReasonForRequestingPage, RelationshipToChild.ParentalOrder).success.value

      implicit val request = buildRequest(answers)

      val controller = new TestController()

      contentAsJson(controller.getPatReasonAsync) mustEqual JsString(PaternityReason.PaternityFromBirth.toString)
    }

    "must return Parental From Uk Adoption when the user is adopting or supporting adoption from a child in the UK" in {

      val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value
      val answers =
        emptyUserAnswers
          .set(IsAdoptingOrParentalOrderPage, true).success.value
          .set(IsAdoptingFromAbroadPage, false).success.value
          .set(ReasonForRequestingPage, relationship).success.value

      implicit val request = buildRequest(answers)

      val controller = new TestController()

      contentAsJson(controller.getPatReasonAsync) mustEqual JsString(PaternityReason.PaternityFromUkAdoption.toString)
    }

    "must return Parental From Adoption From Abroad when the user is adopting or supporting adoption from abroad" in {

      val relationship = Gen.oneOf(RelationshipToChild.Adopting, RelationshipToChild.SupportingAdoption).sample.value
      val answers =
        emptyUserAnswers
          .set(IsAdoptingOrParentalOrderPage, true).success.value
          .set(IsAdoptingFromAbroadPage, true).success.value
          .set(ReasonForRequestingPage, relationship).success.value

      implicit val request = buildRequest(answers)

      val controller = new TestController()

      contentAsJson(controller.getPatReasonAsync) mustEqual JsString(PaternityReason.PaternityFromAdoptionFromAbroad.toString)
    }
  }
}
