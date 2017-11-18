# GitStore [![Build Status](https://travis-ci.org/arun1729/gitstore.svg?branch=master)](https://travis-ci.org/arun1729/gitstore) [![codecov](https://codecov.io/gh/arun1729/gitstore/branch/master/graph/badge.svg)](https://codecov.io/gh/arun1729/gitstore)

A simple data store based on Git repository. Get all the benefits of a Git repo such as revision history and tracking changes per user on a data store. This feature is especially useful for storing and managing configuration files that are accessed and modified by multiple users/applications.

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

## Storing data in partition
```
String content = "Test test Test test";
String filename = "test-doc";
DBPartition partName = new DBPartition("part/1");
gitStore.connect("arun", "arun.mahendra@someemail.com");
gitStore.inPartition(partName).put(filename, content, "created for testing");
```

## Retrieving data from partition
```
String content = gitStore.inPartition(partName).get(filename);
```

## Delete document and drop partition
```
gitStore.inPartition(partName).delete(filename, "removing file.");
gitStore.dropPartitionTree(partName, "dropping partition after testing.");
```

## Maven dependency (only snaphot available currently.)
```
<dependency>
  <groupId>com.github.arun1729</groupId>
  <artifactId>gitstore</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```
### to include snapshot repo, add this to your repository list:
```
<repository>
  <id>maven-snapshots</id>
  <url>http://oss.sonatype.org/content/repositories/snapshots</url>
  <releases>
    <enabled>false</enabled>
  </releases>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository>
```
