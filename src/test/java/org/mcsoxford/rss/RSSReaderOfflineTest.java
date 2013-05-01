package org.mcsoxford.rss;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;
import org.mcsoxford.rss.test.RSSReaderOfflineFactory;

import android.test.AndroidTestCase;


public class RSSReaderOfflineTest extends AndroidTestCase {
	
	RSSReaderBBCOffline readerBBC;
	
	/**
	 * You don't have to extend RSSReader by yourself if you use the factory.
	 */
	class RSSReaderBBCOffline extends RSSReader {
		
		public StringBuffer sb;
		
		public RSSReaderBBCOffline() {
			sb = new StringBuffer();
			sb.append("<?xml version='1.0' encoding='UTF-8'?> ");
			sb.append("<?xml-stylesheet title='XSL_formatting' type='text/xsl' href='/shared/bsp/xsl/rss/nolsol.xsl'?>");
			sb.append("<rss xmlns:media='http://search.yahoo.com/mrss/' xmlns:atom='http://www.w3.org/2005/Atom' version='2.0'>  ");
			sb.append("  <channel> ");
			sb.append("    <title>BBC News - World</title>  ");
			sb.append("    <link>http://www.bbc.co.uk/news/world/#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>  ");
			sb.append("    <description>The latest stories from the World section of the BBC News web site.</description>  ");
			sb.append("    <language>en-gb</language>  ");
			sb.append("    <lastBuildDate>Wed, 01 May 2013 13:36:13 GMT</lastBuildDate>  ");
			sb.append("    <copyright>Copyright: (C) British Broadcasting Corporation, see http://news.bbc.co.uk/2/hi/help/rss/4498287.stm for terms and conditions of reuse.</copyright>  ");
			sb.append("    <image> ");
			sb.append("      <url>http://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif</url>  ");
			sb.append("      <title>BBC News - World</title>  ");
			sb.append("      <link>http://www.bbc.co.uk/news/world/#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>  ");
			sb.append("      <width>120</width>  ");
			sb.append("      <height>60</height> ");
			sb.append("    </image>  ");
			sb.append("    <ttl>15</ttl>  ");
			sb.append("    <atom:link href='http://feeds.bbci.co.uk/news/world/rss.xml' rel='self' type='application/rss+xml'/> "); 
			sb.append("    <item> ");
			sb.append("     <title>Bangladesh collapse toll passes 400</title>  ");
			sb.append("     <description>The number killed in the collapse of a factory building near Bangladesh's capital, Dhaka, passes 400 with 149 listed as missing, officials say.</description>  ");
			sb.append("      <link>http://www.bbc.co.uk/news/world-asia-22364891#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>  ");
			sb.append("      <guid isPermaLink='false'>http://www.bbc.co.uk/news/world-asia-22364891</guid> "); 
			sb.append("      <pubDate>Wed, 01 May 2013 11:53:38 GMT</pubDate>  ");
			sb.append("      <media:thumbnail width='66' height='49' url='http://news.bbcimg.co.uk/media/images/67341000/jpg/_67341723_844in8rv.jpg'/>  ");
			sb.append("     <media:thumbnail width='144' height='81' url='http://news.bbcimg.co.uk/media/images/67341000/jpg/_67341887_844in8rv.jpg'/> ");
			sb.append("    </item>  ");
			sb.append("    <item> ");
			sb.append("     <title>Greeks stage anti-austerity strike</title>  ");
			sb.append("     <description>A general strike against tough austerity measures is under way in Greece, with trade unions calling for 'mass mobilisation' of protesters amid a series of May Day demos around the world.</description>  ");
			sb.append("      <link>http://www.bbc.co.uk/news/world-asia-22364891#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa</link>  ");
			sb.append("      <guid isPermaLink='false'>http://www.bbc.co.uk/news/world-asia-22364891</guid> "); 
			sb.append("      <pubDate>Wed, 01 May 2013 11:53:38 GMT</pubDate>  ");
			sb.append("      <media:thumbnail width='66' height='49' url='http://news.bbcimg.co.uk/media/images/67341000/jpg/_67341723_844in8rv.jpg'/>  ");
			sb.append("     <media:thumbnail width='144' height='81' url='http://news.bbcimg.co.uk/media/images/67341000/jpg/_67341887_844in8rv.jpg'/> ");
			sb.append("    </item>  ");

			sb.append("  </channel> ");
			sb.append("</rss> ");

		}
		
		@Override
		public InputStream connectAndGetFeed(String uri) {
			InputStream feedStream = new ByteArrayInputStream( sb.toString().getBytes() );		
			return feedStream;
		}
		
	}


	protected void setUp() throws Exception {
		super.setUp();
		readerBBC = new RSSReaderBBCOffline();
	}

	/**
	 * Test a RSS reader offline by extending the RSSReader class
	 */
	public void testRSSReaderOffline() {
		RSSFeed feed = null;
		RSSReader reader = readerBBC;
		try {
			feed = reader.load("");
		} catch (RSSReaderException e) {
			e.printStackTrace();
			fail();
		} finally {
			reader.close();
		}
		
		assertEquals(2, feed.getItems().size());
	}
	

	/**
	 * Test a RSS reader offline created by the factory.
	 */
	public void testRSSReaderOfflineFactory() {
		RSSFeed feed = null;
		String s = readerBBC.sb.toString();
		RSSReader reader = RSSReaderOfflineFactory.createReaderFrom(s);
		try {
			feed = reader.load("");
		} catch (RSSReaderException e) {
			e.printStackTrace();
			fail();
		} finally {
			reader.close();
		}
		
		assertEquals(2, feed.getItems().size());
	}

}
