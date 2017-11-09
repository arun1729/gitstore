# GitStore [![Build Status](https://travis-ci.org/arun1729/gitstore.svg?branch=master)](https://travis-ci.org/arun1729/gitstore) [![codecov](https://codecov.io/gh/arun1729/gitstore/branch/master/graph/badge.svg)](https://codecov.io/gh/arun1729/gitstore)

A simple data store based on Git repository. Made purely for the fun of it!

## Example
```java
GitStore gitStore = new GitStore("/tmp/test-db");
String content = "A new document to store into GitStore";
String filename = "document-1";
gitStore.connect("arun", "arun.mahendra@someemail.com");
gitStore.put(filename, content, "inserted new document into git store.");
```

## Check git log
```bash
$cd /tmp/test-db
$git log
commit 03c8528435ada5e552ba561b4d69b3d3bad9b48d
Author: arun <arun.mahendra@someemail.com>
Date:   Thu Nov 9 12:18:54 2017 -0600

    inserted new document into git store.
```
## Get content back
```java
gitStore.get(filename)
A new document to store into GitStore
```
