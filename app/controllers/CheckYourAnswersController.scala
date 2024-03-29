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

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction, JourneyModelFilter}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers._
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            journeyModelFilter: JourneyModelFilter,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView
                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData andThen journeyModelFilter) {
    implicit request =>

      val applicationDetails = SummaryListViewModel(
        rows = Seq(
          CountryOfResidenceSummary.row(request.userAnswers),
          IsAdoptingOrParentalOrderSummary.row(request.userAnswers),
          IsApplyingForStatutoryAdoptionPaySummary.row(request.userAnswers),
          IsAdoptingFromAbroadSummary.row(request.userAnswers),
          ReasonForRequestingSummary.row(request.userAnswers),
        ).flatten
      )

      val relationshipDetails = SummaryListViewModel(
        rows = Seq(
          IsBiologicalFatherSummary.row(request.userAnswers),
          IsInQualifyingRelationshipSummary.row(request.userAnswers),
          IsCohabitingSummary.row(request.userAnswers),
          WillHaveCaringResponsibilitySummary.row(request.userAnswers),
          WillTakeTimeToCareForChildSummary.row(request.userAnswers),
          WillTakeTimeToSupportPartnerSummary.row(request.userAnswers)
        ).flatten
      )

      val personalDetails = SummaryListViewModel(
        rows = Seq(

          NameSummary.row(request.userAnswers),
          NinoSummary.row(request.userAnswers)
        ).flatten
      )

      val babyDetails = SummaryListViewModel(
        rows = Seq(
          BabyHasBeenBornSummary.row(request.userAnswers),
          BabyDateOfBirthSummary.row(request.userAnswers),
          BabyDueDateSummary.row(request.userAnswers),
          DateChildWasMatchedSummary.row(request.userAnswers),
          ChildHasBeenPlacedSummary.row(request.userAnswers),
          ChildPlacementDateSummary.row(request.userAnswers),
          ChildExpectedPlacementDateSummary.row(request.userAnswers),
          DateOfAdoptionNotificationSummary.row(request.userAnswers),
          ChildHasEnteredUkSummary.row(request.userAnswers),
          DateChildEnteredUkSummary.row(request.userAnswers),
          DateChildExpectedToEnterUkSummary.row(request.userAnswers)
        ).flatten
      )

      val paternityDetails = SummaryListViewModel(
        rows = Seq(
          PaternityLeaveLengthGbPreApril24OrNiSummary.row(request.userAnswers),
          PayStartDateGbPreApril24OrNiSummary.row(request.userAnswers),
          PaternityLeaveLengthGbPostApril24Summary.row(request.userAnswers),
          LeaveTakenTogetherOrSeparatelySummary.row(request.userAnswers),
          PayStartDateGbPostApril24Summary.row(request.userAnswers),
          PayStartDateWeek1Summary.row(request.userAnswers),
          PayStartDateWeek2Summary.row(request.userAnswers),
        ).flatten
      )

      Ok(view(applicationDetails, relationshipDetails, personalDetails, babyDetails, paternityDetails))
  }
}
