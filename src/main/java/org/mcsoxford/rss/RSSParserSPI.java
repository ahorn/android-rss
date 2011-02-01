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
 * Thread-safe RSS parser service provider interface.
 * 
 * @author Mr Horn
 */
public interface RSSParserSPI {

  /**
   * Parses an input stream as an RSS feed. It is the responsibility of the
   * caller to close the specified RSS feed input stream.
   * 
   * @param feed RSS 2.0 feed input stream
   * @return in-memory representation of RSS feed
   * @throws RSSFault if an unrecoverable parse error occurs
   */
  RSSFeed parse(java.io.InputStream feed);

}

