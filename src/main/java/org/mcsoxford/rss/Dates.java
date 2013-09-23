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

import java.util.Locale;
import java.util.TimeZone;

/**
 * Internal helper class for date conversions.
 * 
 * @author Mr Horn
 */
final class Dates {

	static private boolean isGMT = false;
	static final String[] standardFormats = { "EEEE', 'dd-MMM-yy HH:mm:ss z", // RFC
																				// 850
																				// (obsoleted
																				// by
																				// 1036)
			"EEEE', 'dd-MMM-yy HH:mm:ss", // ditto but no tz. Happens too often
			"EEE', 'dd-MMM-yyyy HH:mm:ss z", // RFC 822/1123
			"EEE', 'dd MMM yyyy HH:mm:ss z", // REMIND what rfc? Apache/1.1
			"EEEE', 'dd MMM yyyy HH:mm:ss z", // REMIND what rfc? Apache/1.1
			"EEE', 'dd MMM yyyy hh:mm:ss z", // REMIND what rfc? Apache/1.1
			"EEEE', 'dd MMM yyyy hh:mm:ss z", // REMIND what rfc? Apache/1.1
			"EEE MMM dd HH:mm:ss z yyyy", // Date's string output format
			"EEE MMM dd HH:mm:ss yyyy", // ANSI C asctime format()
			"EEE', 'dd-MMM-yy HH:mm:ss", // No time zone 2 digit year RFC 1123
			"EEE', 'dd-MMM-yyyy HH:mm:ss" // No time zone RFC 822/1123
	};

	/*
	 * because there are problems with JDK1.1.6/SimpleDateFormat with
	 * recognizing GMT, we have to create this workaround with the following
	 * hardcoded strings
	 */
	static final String[] gmtStandardFormats = { "EEEE',' dd-MMM-yy HH:mm:ss 'GMT'", // RFC
																						// 850
																						// (obsoleted
																						// by
																						// 1036)
			"EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'", // RFC 822/1123
			"EEE',' dd MMM yyyy HH:mm:ss 'GMT'", // REMIND what rfc? Apache/1.1
			"EEEE',' dd MMM yyyy HH:mm:ss 'GMT'", // REMIND what rfc? Apache/1.1
			"EEE',' dd MMM yyyy hh:mm:ss 'GMT'", // REMIND what rfc? Apache/1.1
			"EEEE',' dd MMM yyyy hh:mm:ss 'GMT'", // REMIND what rfc? Apache/1.1
			"EEE MMM dd HH:mm:ss 'GMT' yyyy" // Date's string output format
	};

	static String dateString;

	static java.util.Date parse(String date) {
		dateString = date.trim();
		if (dateString.indexOf("GMT") != -1) {
			isGMT = true;
		}
		return getDate();
	}

	static private java.util.Date getDate() {

		int arrayLen = isGMT ? gmtStandardFormats.length : standardFormats.length;
		for (int i = 0; i < arrayLen; i++) {
			java.util.Date d = null;

			if (isGMT) {
				d = tryParsing(gmtStandardFormats[i]);
			} else {
				d = tryParsing(standardFormats[i]);
			}
			if (d != null) {
				return d;
			}

		}

		return null;
	}

	static private java.util.Date tryParsing(String format) {

		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(format, Locale.US);
		if (isGMT) {
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		try {
			return df.parse(dateString);
		} catch (Exception e) {
			return null;
		}
	}
}



