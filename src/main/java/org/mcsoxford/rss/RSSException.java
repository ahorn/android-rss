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
 * Contingency to which an RSS client should react. Unlike {@link RSSFault}
 * runtime exceptions, the occurrence of an {@link RSSException} denotes an
 * alternative execution flow for which RSS clients should account.
 * 
 * @author Mr Horn
 * @see RSSFault
 */
public class RSSException extends Exception {

  /**
   * Unsupported serialization
   */
  private static final long serialVersionUID = 1L;

  public RSSException(String message) {
    super(message);
  }

  public RSSException(Throwable cause) {
    super(cause);
  }

  public RSSException(String message, Throwable cause) {
    super(message, cause);
  }

}

