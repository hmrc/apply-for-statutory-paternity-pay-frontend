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

package pages

import models._
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class CountryOfResidencePageSpec extends PageBehaviours with ScalaCheckPropertyChecks {

  "CountryOfResidencePage" - {

    beRetrievable[CountryOfResidence](CountryOfResidencePage)

    beSettable[CountryOfResidence](CountryOfResidencePage)

    beRemovable[CountryOfResidence](CountryOfResidencePage)

    "must delete paternity details pages when changed" in {

      forAll(Gen.oneOf(CountryOfResidence.values)) { country =>

        val answers =
          emptyUserAnswers
            .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
            .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value
            .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
            .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value
            .set(PayStartDateGbPostApril24Page, Some(LocalDate.now)).success.value
            .set(PayStartDateWeek1Page, Some(LocalDate.now)).success.value
            .set(PayStartDateWeek2Page, Some(LocalDate.now)).success.value

        val result = answers.set(CountryOfResidencePage, country).success.value

        result.isDefined(PaternityLeaveLengthGbPostApril24Page)    mustBe false
        result.isDefined(PaternityLeaveLengthGbPreApril24OrNiPage) mustBe false
        result.isDefined(PayStartDateGbPreApril24OrNiPage)         mustBe false
        result.isDefined(LeaveTakenTogetherOrSeparatelyPage)       mustBe false
        result.isDefined(PayStartDateGbPostApril24Page)            mustBe false
        result.isDefined(PayStartDateWeek1Page)                    mustBe false
        result.isDefined(PayStartDateWeek2Page)                    mustBe false
      }
    }
  }
}
