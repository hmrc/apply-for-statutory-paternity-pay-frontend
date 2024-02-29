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

import config.Constants
import generators.Generators
import models.CountryOfResidence.{England, NorthernIreland, Scotland, Wales}
import models.{CountryOfResidence, LeaveTakenTogetherOrSeparately, PaternityLeaveLengthGbPostApril24, PaternityLeaveLengthGbPreApril24OrNi}
import org.scalacheck.Arbitrary.arbitrary

import java.time.LocalDate
import org.scalacheck.{Arbitrary, Gen}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.behaviours.PageBehaviours

class ChildPlacementDatePageSpec extends PageBehaviours with ScalaCheckPropertyChecks with Generators {

  "ChildPlacementDatePage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](ChildPlacementDatePage)

    beSettable[LocalDate](ChildPlacementDatePage)

    beRemovable[LocalDate](ChildPlacementDatePage)


    val arbitraryCountry = arbitrary[CountryOfResidence].sample.value

    def paternityPageAnswers(country: CountryOfResidence = arbitraryCountry) =
      emptyUserAnswers
        .set(CountryOfResidencePage, country).success.value
        .set(PaternityLeaveLengthGbPreApril24OrNiPage, PaternityLeaveLengthGbPreApril24OrNi.OneWeek).success.value
        .set(PayStartDateGbPreApril24OrNiPage, LocalDate.now).success.value
        .set(PaternityLeaveLengthGbPostApril24Page, PaternityLeaveLengthGbPostApril24.OneWeek).success.value
        .set(LeaveTakenTogetherOrSeparatelyPage, LeaveTakenTogetherOrSeparately.Separately).success.value
        .set(PayStartDateGbPostApril24Page, Some(LocalDate.now)).success.value
        .set(PayStartDateWeek1Page, LocalDate.now).success.value
        .set(PayStartDateWeek2Page, LocalDate.now).success.value

    val dateBeforeLegislation = LocalDate.of(2000, 1, 1)
    val dateAfterLegislation  = LocalDate.of(2100, 1, 1)

    "when the date is before 7th April 2024" - {

      "must remove all paternity questions for GB post April" in {

        forAll(datesBetween(dateBeforeLegislation, Constants.april24LegislationEffective)) { date =>

          val result = paternityPageAnswers().set(BabyDueDatePage, date).success.value

          result.isDefined(PaternityLeaveLengthGbPreApril24OrNiPage) mustEqual true
          result.isDefined(PayStartDateGbPreApril24OrNiPage)         mustEqual true

          result.isDefined(PaternityLeaveLengthGbPostApril24Page) mustEqual false
          result.isDefined(LeaveTakenTogetherOrSeparatelyPage)    mustEqual false
          result.isDefined(PayStartDateGbPostApril24Page)         mustEqual false
          result.isDefined(PayStartDateWeek1Page)                 mustEqual false
          result.isDefined(PayStartDateWeek2Page)                 mustEqual false
        }
      }
    }

    "when the date is on or after 7th April 2024" - {

      "and the user is in Northern Ireland" - {

        "must remove all paternity questions for GB post April or NI" in {

          forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation)) { date =>

            val result = paternityPageAnswers(NorthernIreland).set(BabyDueDatePage, date).success.value

            result.isDefined(PaternityLeaveLengthGbPreApril24OrNiPage) mustEqual true
            result.isDefined(PayStartDateGbPreApril24OrNiPage)         mustEqual true

            result.isDefined(PaternityLeaveLengthGbPostApril24Page) mustEqual false
            result.isDefined(LeaveTakenTogetherOrSeparatelyPage)    mustEqual false
            result.isDefined(PayStartDateGbPostApril24Page)         mustEqual false
            result.isDefined(PayStartDateWeek1Page)                 mustEqual false
            result.isDefined(PayStartDateWeek2Page)                 mustEqual false
          }
        }
      }

      "and the user is in England, Scotland or Wales" - {

        "must remove all paternity questions for GB post April or NI" in {

          forAll(datesBetween(Constants.april24LegislationEffective, dateAfterLegislation), Gen.oneOf(England, Scotland, Wales)) { case (date, country) =>

            val result = paternityPageAnswers(country).set(BabyDueDatePage, date).success.value

            result.isDefined(PaternityLeaveLengthGbPreApril24OrNiPage) mustEqual false
            result.isDefined(PayStartDateGbPreApril24OrNiPage)         mustEqual false

            result.isDefined(PaternityLeaveLengthGbPostApril24Page) mustEqual true
            result.isDefined(LeaveTakenTogetherOrSeparatelyPage)    mustEqual true
            result.isDefined(PayStartDateGbPostApril24Page)         mustEqual true
            result.isDefined(PayStartDateWeek1Page)                 mustEqual true
            result.isDefined(PayStartDateWeek2Page)                 mustEqual true
          }
        }
      }
    }
  }
}
