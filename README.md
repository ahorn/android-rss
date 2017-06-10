Package:     org.mcsoxford.rss
License:     Apache License Version 2.0
Authors:     A. Horn
Description: Lightweight Android library to read parts of RSS 2.0 feeds.

# Installation

Add as a Maven/Gradle dependency: https://jitpack.io/#ahorn/android-rss

Fetch the source code with Git:
```
  git clone https://ahorn@github.com/ahorn/android-rss.git
```
Alternatively, you can download the sources from github.com/ahorn.

To reference the downloaded library from within your Android mobile app,
navigate to the <sdk>/tools/ directory and use the following command:
```
  android update project \
    --target <target_ID> \
    --path path/to/your/project \
    --library path/to/android-rss
```
This command appends to the `default.properties` file in your Android
project a new `android.library.reference` property. The value of this
new property should be the relative path to the directory which you
created when you fetched the Android RSS library source code.

Henceforth, android-rss is compiled when ant builds the app which was
specified in the --path argument above.

However, Eclipse does not work using this method because it does not
reference anything that is not an Eclipse project. When you try to
transform android-rss into an Eclipse project you encounter errors
due to the testing directory. To fix these, you have to change the 
source directory by editing the .classpath file to set `/src/main/java`
as the source folder.

For additional questions, see also the Troubleshooting section below.

# Troubleshooting

Error "android resolve to a path with no default.properties file for project":
  http://groups.google.com/group/android-developers/browse_thread/thread/37e7728cc2e8f315

Android library project documentation:
  http://developer.android.com/guide/developing/projects/projects-cmdline.html#ReferencingLibraryProject

Eclipse reports the error: "Default target help does not exist in this project"
  https://github.com/ahorn/android-rss/issues/2

This library uses the [HttpURLConnection class](https://developer.android.com/reference/java/net/HttpURLConnection.html), which should work for all versions of Android including
 upcoming versions. (At least up to API 25.)


# API Usage
```
  RSSReader reader = new RSSReader();
  String uri = "http://feeds.bbci.co.uk/news/world/rss.xml";
  RSSFeed feed = reader.load(uri);
```
# Discussion

http://groups.google.com/group/android-developers/browse_thread/thread/b3de98eab436be20
