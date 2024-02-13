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

import pages.behaviours.PageBehaviours

class IsInQualifyingRelationshipPageSpec extends PageBehaviours {

  "IsInQualifyingRelationshipPage" - {

    beRetrievable[Boolean](IsInQualifyingRelationshipPage)

    beSettable[Boolean](IsInQualifyingRelationshipPage)

    beRemovable[Boolean](IsInQualifyingRelationshipPage)
  }

  "must remove Is Cohabiting when the answer is true" in {

    val answers =
      emptyUserAnswers
        .set(IsInQualifyingRelationshipPage, false).success.value
        .set(IsCohabitingPage, true).success.value

    val result = answers.set(IsInQualifyingRelationshipPage, true).success.value

    result.get(IsCohabitingPage) must not be defined
  }

  "must not remove Is Cohabiting when the answer is false" in {

    val answers =
      emptyUserAnswers
        .set(IsInQualifyingRelationshipPage, true).success.value
        .set(IsCohabitingPage, true).success.value

    val result = answers.set(IsInQualifyingRelationshipPage, false).success.value

    result.get(IsCohabitingPage) mustBe defined
  }
}
