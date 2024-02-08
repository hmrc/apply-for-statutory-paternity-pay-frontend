package forms

import forms.behaviours.OptionFieldBehaviours
import models.PaternityLeaveLengthPostApril24
import play.api.data.FormError

class PaternityLeaveLengthPostApril24FormProviderSpec extends OptionFieldBehaviours {

  val form = new PaternityLeaveLengthPostApril24FormProvider()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "paternityLeaveLengthPostApril24.error.required"

    behave like optionsField[PaternityLeaveLengthPostApril24](
      form,
      fieldName,
      validValues  = PaternityLeaveLengthPostApril24.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
