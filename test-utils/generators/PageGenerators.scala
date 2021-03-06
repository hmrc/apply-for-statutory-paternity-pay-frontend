/*
 * Copyright 2022 HM Revenue & Customs
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

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryIsAdoptingPage: Arbitrary[IsAdoptingPage.type] =
    Arbitrary(IsAdoptingPage)

  implicit lazy val arbitraryNinoPage: Arbitrary[NinoPage.type] =
    Arbitrary(NinoPage)

  implicit lazy val arbitraryWantPayToStartOnDueDatePage: Arbitrary[WantPayToStartOnDueDatePage.type] =
    Arbitrary(WantPayToStartOnDueDatePage)

  implicit lazy val arbitraryWillTakeTimeToSupportMotherPage: Arbitrary[WillTakeTimeToSupportMotherPage.type] =
    Arbitrary(WillTakeTimeToSupportMotherPage)

  implicit lazy val arbitraryWillTakeTimeToCareForChildPage: Arbitrary[WillTakeTimeToCareForChildPage.type] =
    Arbitrary(WillTakeTimeToCareForChildPage)

  implicit lazy val arbitraryWillHaveCaringResponsibilityPage: Arbitrary[WillHaveCaringResponsibilityPage.type] =
    Arbitrary(WillHaveCaringResponsibilityPage)

  implicit lazy val arbitraryWantPayToStartOnBirthDatePage: Arbitrary[WantPayToStartOnBirthDatePage.type] =
    Arbitrary(WantPayToStartOnBirthDatePage)

  implicit lazy val arbitraryPayStartDateBabyBornPage: Arbitrary[PayStartDateBabyBornPage.type] =
    Arbitrary(PayStartDateBabyBornPage)

  implicit lazy val arbitraryPayStartDateBabyDuePage: Arbitrary[PayStartDateBabyDuePage.type] =
    Arbitrary(PayStartDateBabyDuePage)

  implicit lazy val arbitraryPaternityLeaveLengthPage: Arbitrary[PaternityLeaveLengthPage.type] =
    Arbitrary(PaternityLeaveLengthPage)

  implicit lazy val arbitraryNamePage: Arbitrary[NamePage.type] =
    Arbitrary(NamePage)

  implicit lazy val arbitraryIsInQualifyingRelationshipPage: Arbitrary[IsInQualifyingRelationshipPage.type] =
    Arbitrary(IsInQualifyingRelationshipPage)

  implicit lazy val arbitraryIsCohabitingPage: Arbitrary[IsCohabitingPage.type] =
    Arbitrary(IsCohabitingPage)

  implicit lazy val arbitraryIsBiologicalFatherPage: Arbitrary[IsBiologicalFatherPage.type] =
    Arbitrary(IsBiologicalFatherPage)

  implicit lazy val arbitraryBabyHasBeenBornPage: Arbitrary[BabyHasBeenBornPage.type] =
    Arbitrary(BabyHasBeenBornPage)

  implicit lazy val arbitraryBabyDueDatePage: Arbitrary[BabyDueDatePage.type] =
    Arbitrary(BabyDueDatePage)

  implicit lazy val arbitraryBabyDateOfBirthPage: Arbitrary[BabyDateOfBirthPage.type] =
    Arbitrary(BabyDateOfBirthPage)
}
