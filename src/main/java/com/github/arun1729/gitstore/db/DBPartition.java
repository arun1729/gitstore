package com.github.arun1729.gitstore.db;

import com.github.arun1729.gitstore.api.Partition;

public class DBPartition implements Partition{

	private String root;
	private String leaf;
	
	public DBPartition(String leaf){
		this.leaf = leaf.endsWith("/") ? leaf : leaf + "/";
		this.root = leaf.split("/")[0];
	}

	@Override
	public String root() {
		return this.root;
	}

	@Override
	public String leaf() {
		return this.leaf;
	}

}
