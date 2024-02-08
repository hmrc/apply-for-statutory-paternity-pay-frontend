package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.PaternityLeaveLengthPostApril24

class PaternityLeaveLengthPostApril24FormProvider @Inject() extends Mappings {

  def apply(): Form[PaternityLeaveLengthPostApril24] =
    Form(
      "value" -> enumerable[PaternityLeaveLengthPostApril24]("paternityLeaveLengthPostApril24.error.required")
    )
}
