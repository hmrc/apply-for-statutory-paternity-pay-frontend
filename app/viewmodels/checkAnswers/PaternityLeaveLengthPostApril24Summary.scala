package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.PaternityLeaveLengthPostApril24Page
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object PaternityLeaveLengthPostApril24Summary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PaternityLeaveLengthPostApril24Page).map {
      answer =>

        val value = ValueViewModel(
          HtmlContent(
            HtmlFormat.escape(messages(s"paternityLeaveLengthPostApril24.$answer"))
          )
        )

        SummaryListRowViewModel(
          key     = "paternityLeaveLengthPostApril24.checkYourAnswersLabel",
          value   = value,
          actions = Seq(
            ActionItemViewModel("site.change", routes.PaternityLeaveLengthPostApril24Controller.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("paternityLeaveLengthPostApril24.change.hidden"))
          )
        )
    }
}
