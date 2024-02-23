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

package models.auditing

import models.PaternityLeaveLengthGbPreApril24OrNi
import models.auditing.DownloadAuditEvent._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

import java.time.LocalDate

class DownloadAuditEventSpec extends AnyFreeSpec with Matchers {

  "PaternityLeaveDetails" - {

    val date = LocalDate.now

    "must serialise to JSON from PaternityLeaveGbPreApril24OrNi" in {

      val model = PaternityLeaveGbPreApril24OrNi(
        leaveLength = PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks,
        payStartDate = date
      )

      val expectedJson = Json.obj(
        "leaveLength" -> PaternityLeaveLengthGbPreApril24OrNi.TwoWeeks.toString,
        "payStartDate" -> Json.toJson(date)
      )

      Json.toJson[PaternityLeaveDetails](model) mustEqual expectedJson
    }

    "must serialise to JSON from PaternityLeaveGbPostApril24OneWeek" - {

      "when pay start date has been specified" in {

        val model = PaternityLeaveGbPostApril24OneWeek(payStartDate = Some(date))

        val expectedJson = Json.obj(
          "leaveLength" -> "oneWeek",
          "payStartDate" -> Json.toJson(date)
        )

        Json.toJson[PaternityLeaveDetails](model) mustEqual expectedJson
      }

      "when pay start date has not been specified" in {

        val model = PaternityLeaveGbPostApril24OneWeek(payStartDate = None)

        val expectedJson = Json.obj(
          "leaveLength" -> "oneWeek"
        )

        Json.toJson[PaternityLeaveDetails](model) mustEqual expectedJson
      }
    }

    "must serialise to JSON from PaternityLeaveGbPostApril24TwoWeeksTogether" - {

      "when pay start date has been specified" in {

        val model = PaternityLeaveGbPostApril24TwoWeeksTogether(payStartDate = Some(date))

        val expectedJson = Json.obj(
          "leaveLength" -> "twoWeeks",
          "takenTogetherOrSeparately" -> "together",
          "payStartDate" -> Json.toJson(date)
        )

        Json.toJson[PaternityLeaveDetails](model) mustEqual expectedJson
      }

      "when pay start date has not been specified" in {

        val model = PaternityLeaveGbPostApril24TwoWeeksTogether(payStartDate = None)

        val expectedJson = Json.obj(
          "leaveLength" -> "twoWeeks",
          "takenTogetherOrSeparately" -> "together"
        )

        Json.toJson[PaternityLeaveDetails](model) mustEqual expectedJson
      }
    }

    "must serialise to JSON from PaternityLeaveGbPostApril24TwoWeeksSeparate" - {

      "when pay start dates has been specified" in {

        val model = PaternityLeaveGbPostApril24TwoWeeksSeparate(week1StartDate = Some(date), week2StartDate = Some(date.plusWeeks(2)))

        val expectedJson = Json.obj(
          "leaveLength" -> "twoWeeks",
          "takenTogetherOrSeparately" -> "separately",
          "week1StartDate" -> Json.toJson(date),
          "week2StartDate" -> Json.toJson(date.plusWeeks(2))
        )

        Json.toJson[PaternityLeaveDetails](model) mustEqual expectedJson
      }

      "when pay start date has not been specified" in {

        val model = PaternityLeaveGbPostApril24TwoWeeksSeparate(week1StartDate = None, week2StartDate = None)

        val expectedJson = Json.obj(
          "leaveLength" -> "twoWeeks",
          "takenTogetherOrSeparately" -> "separately"
        )

        Json.toJson[PaternityLeaveDetails](model) mustEqual expectedJson
      }
    }

    "must serialise to JSON from PaternityLeaveGbPostApril24Unsure" in {

      val expectedJson = Json.obj("leaveLength" -> "unsure")
      Json.toJson[PaternityLeaveDetails](DownloadAuditEvent.PaternityLeaveGbPostApril24Unsure) mustEqual expectedJson
    }
  }
}
