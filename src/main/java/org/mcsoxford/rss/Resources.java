/*
 * Copyright (C) 2010 A. Horn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mcsoxford.rss;

/**
 * Internal helper class for resource-sensitive objects such as streams.
 * 
 * @author Mr Horn
 */
final class Resources {

  /* Hide constructor */
  private Resources() {}

  /**
   * Closes stream and suppresses IO faults.
   * 
   * @return {@code null} if stream has been successfully closed,
   *         {@link java.io.IOException} otherwise
   */
  static java.io.IOException closeQuietly(java.io.Closeable stream) {
    if (stream == null) {
      return null;
    }

    try {
      stream.close();
    } catch (java.io.IOException e) {
      return e;
    }

    return null;
  }

}

