package com.github.arun1729.gitstore.api;

/**
 * This interface defines a data partition.
 * @author Arun Mahendra
 *
 */
public interface Partition {
	
	public void process();
	
	public String getRoot();
	
	public String getLeaf(); 

}
