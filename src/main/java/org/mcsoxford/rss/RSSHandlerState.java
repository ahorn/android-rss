package org.mcsoxford.rss;

/**
 * Internal enumerable data type for RSS SAX handler.
 * 
 * @author Mr Horn
 */
enum RSSHandlerState {

  RSS_CHANNEL, RSS_ITEM, RSS_TITLE, RSS_DESCRIPTION, RSS_CATEGORY, RSS_LINK,
  RSS_PUBDATE;

}
