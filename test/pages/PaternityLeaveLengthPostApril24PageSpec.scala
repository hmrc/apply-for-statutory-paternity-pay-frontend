package pages

import models.PaternityLeaveLengthPostApril24
import pages.behaviours.PageBehaviours

class PaternityLeaveLengthPostApril24Spec extends PageBehaviours {

  "PaternityLeaveLengthPostApril24Page" - {

    beRetrievable[PaternityLeaveLengthPostApril24](PaternityLeaveLengthPostApril24Page)

    beSettable[PaternityLeaveLengthPostApril24](PaternityLeaveLengthPostApril24Page)

    beRemovable[PaternityLeaveLengthPostApril24](PaternityLeaveLengthPostApril24Page)
  }
}
