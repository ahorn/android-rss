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
 * Internal helper class for RSS 2.0 media attributes.
 * 
 * @author Mr Horn
 */
final class MediaAttributes {

  /* Hide constructor */
  private MediaAttributes() {}

  /**
   * Returns the RSS 2.0 attribute with the specified local name as a string.
   * The return value is {@code null} if no attribute with such name exists.
   */
  static String stringValue(org.xml.sax.Attributes attributes, String name) {
    return attributes.getValue(name);
  }

  /**
   * Returns the RSS 2.0 attribute with the specified local name as an integer. 
   * The {@code defaultValue} is returned if no attribute with such name exists.
   */
  static int intValue(org.xml.sax.Attributes attributes, String name, int defaultValue) {
    final String value = stringValue(attributes, name);
    if(value == null) {
      return defaultValue;
    }

    return Integer.parseInt(value);
  }

}

