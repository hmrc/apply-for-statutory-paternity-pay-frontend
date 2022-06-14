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

package pages

import models.{Name, PaternityLeaveLength}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

class IsCohabitingPageSpec extends PageBehaviours {

  "IsCohabitingPage" - {

    beRetrievable[Boolean](IsCohabitingPage)

    beSettable[Boolean](IsCohabitingPage)

    beRemovable[Boolean](IsCohabitingPage)

    "must remove all subsequent answers when the answer is no" in {

      val answers =
        emptyUserAnswers
          .set(BabyDateOfBirthPage, LocalDate.now).success.value
          .set(BabyDueDatePage, LocalDate.now).success.value
          .set(BabyHasBeenBornPage, true).success.value
          .set(IsAdoptingPage, false).success.value
          .set(IsBiologicalFatherPage, false).success.value
          .set(IsCohabitingPage, true).success.value
          .set(IsInQualifyingRelationshipPage, false).success.value
          .set(NamePage, Name("first", "last")).success.value
          .set(NinoPage, arbitrary[Nino].sample.value).success.value
          .set(PaternityLeaveLengthPage, PaternityLeaveLength.Oneweek).success.value
          .set(WantPayToStartOnBirthDatePage, false).success.value
          .set(WantPayToStartOnDueDatePage, false).success.value
          .set(PayStartDateBabyBornPage, LocalDate.now).success.value
          .set(PayStartDateBabyDuePage, LocalDate.now).success.value
          .set(WillHaveCaringResponsibilityPage, true).success.value
          .set(WillTakeTimeToCareForChildPage, true).success.value
          .set(WillTakeTimeToSupportMotherPage, true).success.value

      val result = answers.set(IsCohabitingPage, false).success.value

      result.get(IsAdoptingPage)                 must be(defined)
      result.get(IsBiologicalFatherPage)         must be(defined)
      result.get(IsInQualifyingRelationshipPage) must be(defined)
      result.get(IsCohabitingPage)               must be(defined)

      result.get(BabyDateOfBirthPage)              must not be defined
      result.get(BabyDueDatePage)                  must not be defined
      result.get(BabyHasBeenBornPage)              must not be defined
      result.get(NamePage)                         must not be defined
      result.get(NinoPage)                         must not be defined
      result.get(PaternityLeaveLengthPage)         must not be defined
      result.get(PayStartDateBabyBornPage)         must not be defined
      result.get(PayStartDateBabyDuePage)          must not be defined
      result.get(WantPayToStartOnBirthDatePage)    must not be defined
      result.get(WantPayToStartOnDueDatePage)      must not be defined
      result.get(WillHaveCaringResponsibilityPage) must not be defined
      result.get(WillTakeTimeToCareForChildPage)   must not be defined
      result.get(WillTakeTimeToSupportMotherPage)  must not be defined
    }
  }
}
