package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.Messages

class $className$FormProvider @Inject() extends Mappings {

  def apply()(implicit messages: Messages): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "$className;format="decap"$.error.invalid",
        allRequiredKey = "$className;format="decap"$.error.required.all",
        twoRequiredKey = "$className;format="decap"$.error.required.two",
        requiredKey    = "$className;format="decap"$.error.required"
      )
    )
}
