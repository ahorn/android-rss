package org.mcsoxford.rss.test;

import java.io.*;

import org.mcsoxford.rss.RSSReader;

/**
 * Factory to create extensions of RSSReader that reads the information from a string instead of
 *  an Internet connection.
 * 
 */
public class RSSReaderOfflineFactory  {
	
	static class RSSReaderFake extends RSSReader {
		
		InputStream feedStream;
		
		public RSSReaderFake(String s) {
			feedStream = new ByteArrayInputStream( s.getBytes( ) );
		}
		
		@Override
		public InputStream connectAndGetFeed(String uri) {
			return this.feedStream;
		}

	}

	
	
	
	/**
	 * Factory method
	 * @param s String with the RSS XML code
	 * @return a RSSReader
	 */
	public static RSSReader createReaderFrom(String s) {
		return new RSSReaderFake(s);
	}
	

}
