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
 * Contingency exception raised when RSS feeds could not be retrieved.
 * 
 * @author Mr Horn
 */
public class RSSReaderException extends RSSException {

  /**
   * Unsupported serialization
   */
  private static final long serialVersionUID = 1L;

  private final int status;

  public RSSReaderException(int status, String message) {
    super(message);
    this.status = status;
  }

  public RSSReaderException(int status, Throwable cause) {
    super(cause);
    this.status = status;
  }

  public RSSReaderException(int status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  /**
   * Return the HTTP status which caused the error.
   * 
   * @return HTTP error status code
   */
  public int getStatus() {
    return status;
  }
}

