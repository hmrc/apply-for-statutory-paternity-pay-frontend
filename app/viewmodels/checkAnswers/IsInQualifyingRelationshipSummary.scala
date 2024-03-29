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

package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, RelationshipToChild, UserAnswers}
import pages.{IsAdoptingOrParentalOrderPage, IsInQualifyingRelationshipPage, ReasonForRequestingPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object IsInQualifyingRelationshipSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    val relationshipToChild = answers.get(IsAdoptingOrParentalOrderPage).flatMap {
      case true => answers.get(ReasonForRequestingPage)
      case false => Some(RelationshipToChild.BirthChild)
    }

    for {
      relationship <- relationshipToChild
      inQualifyingRelationship <- answers.get(IsInQualifyingRelationshipPage)
    } yield {

      val value = if (inQualifyingRelationship) "site.yes" else "site.no"

      SummaryListRowViewModel(
        key = s"isInQualifyingRelationship.${relationship.toString}.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel("site.change", routes.IsInQualifyingRelationshipController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages(s"isInQualifyingRelationship.${relationship.toString}.change.hidden"))
        )
      )
    }
  }
}