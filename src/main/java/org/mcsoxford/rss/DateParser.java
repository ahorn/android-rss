package org.mcsoxford.rss;

/**
 * Generic interface for parsing RSS dates
 */
public interface DateParser {
    /**
     * Turn the given string date into a Date object
     *
     * @param date the raw date found in the RSS feed
     * @return parsed Date, or null if unsuccessful
     */
    java.util.Date parse(String date);
}
