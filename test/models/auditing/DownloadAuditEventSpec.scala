/*
 * Copyright 2023 HM Revenue & Customs
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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

import java.time.LocalDate

class DownloadAuditEventSpec extends AnyFreeSpec with Matchers {

  "BirthDetails" - {

    "must write to json" - {

      "for BirthDetails.AlreadyBorn" - {

        val birthDate = LocalDate.of(2000, 2, 1)

        "when payStartDate exists" in {

          val model = DownloadAuditEvent.BirthDetails.AlreadyBorn(
            birthDate = birthDate,
            payShouldStartFromBirthDay = false
          )

          val expected = Json.obj(
            "birthDate" -> birthDate,
            "payShouldStartFromBirthDay" -> false
          )

          Json.toJson[DownloadAuditEvent.BirthDetails](model) mustEqual expected
        }

        "when payStartDate is empty" in {

          val model = DownloadAuditEvent.BirthDetails.AlreadyBorn(
            birthDate = birthDate,
            payShouldStartFromBirthDay = true
          )

          val expected = Json.obj(
            "birthDate" -> birthDate,
            "payShouldStartFromBirthDay" -> true
          )

          Json.toJson[DownloadAuditEvent.BirthDetails](model) mustEqual expected
        }
      }

      "for BirthDetails.Due" - {

        "when payStartDate exists" in {

          val model = DownloadAuditEvent.BirthDetails.Due(
            payShouldStartFromDueDate = false
          )

          val expected = Json.obj(
            "payShouldStartFromDueDate" -> false
          )

          Json.toJson[DownloadAuditEvent.BirthDetails](model) mustEqual expected
        }

        "when payStartDate is empty" in {

          val model = DownloadAuditEvent.BirthDetails.Due(
            payShouldStartFromDueDate = true
          )

          val expected = Json.obj(
            "payShouldStartFromDueDate" -> true
          )

          Json.toJson[DownloadAuditEvent.BirthDetails](model) mustEqual expected
        }
      }
    }
  }
}
