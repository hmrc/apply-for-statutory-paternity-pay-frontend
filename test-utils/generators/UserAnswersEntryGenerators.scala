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

package generators

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.domain.Nino

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryPayStartDateWeek2UserAnswersEntry: Arbitrary[(PayStartDateWeek2Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayStartDateWeek2Page.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayStartDateWeek1UserAnswersEntry: Arbitrary[(PayStartDateWeek1Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayStartDateWeek1Page.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayStartDateSingleWeekUserAnswersEntry: Arbitrary[(PayStartDateSingleWeekPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayStartDateSingleWeekPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayStartDateGbPreApril2024OrNiUserAnswersEntry: Arbitrary[(PayStartDateGbPreApril2024OrNiPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayStartDateGbPreApril2024OrNiPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPaternityLeaveLengthGbPostApril24UserAnswersEntry: Arbitrary[(PaternityLeaveLengthGbPostApril24Page.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PaternityLeaveLengthGbPostApril24Page.type]
        value <- arbitrary[PaternityLeaveLengthGbPostApril24].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLeaveTakenTogetherOrSeparatelyUserAnswersEntry: Arbitrary[(LeaveTakenTogetherOrSeparatelyPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LeaveTakenTogetherOrSeparatelyPage.type]
        value <- arbitrary[LeaveTakenTogetherOrSeparately].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateOfAdoptionNotificationUserAnswersEntry: Arbitrary[(DateOfAdoptionNotificationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateOfAdoptionNotificationPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateChildExpectedToEnterUkUserAnswersEntry: Arbitrary[(DateChildExpectedToEnterUkPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateChildExpectedToEnterUkPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateChildEnteredUkUserAnswersEntry: Arbitrary[(DateChildEnteredUkPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateChildEnteredUkPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryChildHasEnteredUkUserAnswersEntry: Arbitrary[(ChildHasEnteredUkPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ChildHasEnteredUkPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryReasonForRequestingUserAnswersEntry: Arbitrary[(ReasonForRequestingPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReasonForRequestingPage.type]
        value <- arbitrary[RelationshipToChild].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsApplyingForStatutoryAdoptionPayUserAnswersEntry: Arbitrary[(IsApplyingForStatutoryAdoptionPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsApplyingForStatutoryAdoptionPayPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsAdoptingFromAbroadUserAnswersEntry: Arbitrary[(IsAdoptingFromAbroadPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsAdoptingFromAbroadPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryDateChildWasMatchedUserAnswersEntry: Arbitrary[(DateChildWasMatchedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[DateChildWasMatchedPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCountryOfResidenceUserAnswersEntry: Arbitrary[(CountryOfResidencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CountryOfResidencePage.type]
        value <- arbitrary[CountryOfResidence].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryChildPlacementDateUserAnswersEntry: Arbitrary[(ChildPlacementDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ChildPlacementDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryChildHasBeenPlacedUserAnswersEntry: Arbitrary[(ChildHasBeenPlacedPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ChildHasBeenPlacedPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryChildExpectedPlacementDateUserAnswersEntry: Arbitrary[(ChildExpectedPlacementDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ChildExpectedPlacementDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsAdoptingUserAnswersEntry: Arbitrary[(IsAdoptingOrParentalOrderPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsAdoptingOrParentalOrderPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNinoUserAnswersEntry: Arbitrary[(NinoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NinoPage.type]
        value <- arbitrary[Nino].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWillTakeTimeToSupportPartnerUserAnswersEntry: Arbitrary[(WillTakeTimeToSupportPartnerPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WillTakeTimeToSupportPartnerPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWillTakeTimeToCareForChildUserAnswersEntry: Arbitrary[(WillTakeTimeToCareForChildPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WillTakeTimeToCareForChildPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWillHaveCaringResponsibilityUserAnswersEntry: Arbitrary[(WillHaveCaringResponsibilityPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WillHaveCaringResponsibilityPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayStartDateBabyBornUserAnswersEntry: Arbitrary[(PayStartDateBabyBornPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayStartDateBabyBornPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPayStartDateBabyDueUserAnswersEntry: Arbitrary[(PayStartDateBabyDuePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PayStartDateBabyDuePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPaternityLeaveLengthUserAnswersEntry: Arbitrary[(PaternityLeaveLengthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PaternityLeaveLengthPage.type]
        value <- arbitrary[PaternityLeaveLength].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryNameUserAnswersEntry: Arbitrary[(NamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[NamePage.type]
        value <- arbitrary[Name].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsInQualifyingRelationshipUserAnswersEntry: Arbitrary[(IsInQualifyingRelationshipPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsInQualifyingRelationshipPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsCohabitingUserAnswersEntry: Arbitrary[(IsCohabitingPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsCohabitingPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsBiologicalFatherUserAnswersEntry: Arbitrary[(IsBiologicalFatherPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsBiologicalFatherPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBabyHasBeenBornUserAnswersEntry: Arbitrary[(BabyHasBeenBornPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BabyHasBeenBornPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBabyDueDateUserAnswersEntry: Arbitrary[(BabyDueDatePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BabyDueDatePage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBabyDateOfBirthUserAnswersEntry: Arbitrary[(BabyDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BabyDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }
}
