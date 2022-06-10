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

class IsAdoptingPageSpec extends PageBehaviours {

  "IsAdoptingPage" - {

    beRetrievable[Boolean](IsAdoptingPage)

    beSettable[Boolean](IsAdoptingPage)

    beRemovable[Boolean](IsAdoptingPage)

    "must remove all subsequent answers when the answer is yes" in {

      val answers =
        emptyUserAnswers
          .set(BabyDateOfBirthPage, LocalDate.now).success.value
          .set(BabyDueDatePage, LocalDate.now).success.value
          .set(BabyHasBeenBornPage, true).success.value
          .set(IsAdoptingPage, false).success.value
          .set(IsBiologicalFatherPage, true).success.value
          .set(IsCohabitingPage, true).success.value
          .set(IsInQualifyingRelationshipPage, true).success.value
          .set(NamePage, Name("first", "last")).success.value
          .set(NinoPage, arbitrary[Nino].sample.value).success.value
          .set(PaternityLeaveLengthPage, PaternityLeaveLength.Oneweek).success.value
          .set(PayStartDatePage, LocalDate.now).success.value
          .set(WantPayToStartOnBirthDatePage, true).success.value
          .set(WantPayToStartOnDueDatePage, true).success.value
          .set(WillHaveCaringResponsibilityPage, true).success.value
          .set(WillTakeTimeToCareForChildPage, true).success.value
          .set(WillTakeTimeToSupportMotherPage, true).success.value

      val result = answers.set(IsAdoptingPage, true).success.value

      result.get(IsAdoptingPage) must be(defined)

      result.get(BabyDateOfBirthPage)              must not be defined
      result.get(BabyDueDatePage)                  must not be defined
      result.get(BabyHasBeenBornPage)              must not be defined
      result.get(IsBiologicalFatherPage)           must not be defined
      result.get(IsCohabitingPage)                 must not be defined
      result.get(IsInQualifyingRelationshipPage)   must not be defined
      result.get(NamePage)                         must not be defined
      result.get(NinoPage)                         must not be defined
      result.get(PaternityLeaveLengthPage)         must not be defined
      result.get(PayStartDatePage)                 must not be defined
      result.get(WantPayToStartOnBirthDatePage)    must not be defined
      result.get(WantPayToStartOnDueDatePage)      must not be defined
      result.get(WillHaveCaringResponsibilityPage) must not be defined
      result.get(WillTakeTimeToCareForChildPage)   must not be defined
      result.get(WillTakeTimeToSupportMotherPage)  must not be defined
    }
  }
}
