package com.github.arun1729.gitstore.db;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.arun1729.gitstore.api.Partition;

public class DBPartitionTest {

	@Test
	public void test() {
		Partition partition = new DBPartition("test/path/to/part/");
		assertEquals("test/path/to/part/", partition.leaf());
		assertEquals("test", partition.root());
	}

}
