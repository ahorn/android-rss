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
 * Non-recoverable runtime exception.
 * 
 * @author Mr Horn
 */
public class RSSFault extends RuntimeException {

  /**
   * Unsupported serialization
   */
  private static final long serialVersionUID = 1L;

  public RSSFault(String message) {
    super(message);
  }

  public RSSFault(Throwable cause) {
    super(cause);
  }

  public RSSFault(String message, Throwable cause) {
    super(message, cause);
  }

}

