/*
 * Copyright 2018-2022 Scala Steward contributors
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

package org.scalasteward.core.vcs.github

import org.scalasteward.core.util.UnexpectedResponse
import org.scalasteward.core.vcs.data.Repo
import scala.util.control.NoStackTrace

sealed trait GitHubException extends RuntimeException with NoStackTrace

object GitHubException {
  final case class RepositoryArchived(repo: Repo, override val getCause: UnexpectedResponse)
      extends GitHubException {
    override val getMessage: String = repo.show
  }

  object RepositoryArchived {
    def fromThrowable(repo: Repo): PartialFunction[Throwable, Throwable] = {
      case response: UnexpectedResponse if response.body.contains("Repository was archived") =>
        RepositoryArchived(repo, response)
    }
  }

  final case class SecondaryRateLimitExceeded(override val getCause: UnexpectedResponse)
      extends GitHubException

  object SecondaryRateLimitExceeded {
    def fromThrowable: PartialFunction[Throwable, Throwable] = {
      case response: UnexpectedResponse
          if response.body.contains("You have exceeded a secondary rate limit") =>
        SecondaryRateLimitExceeded(response)
    }
  }
}