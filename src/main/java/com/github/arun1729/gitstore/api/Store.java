package com.github.arun1729.gitstore.api;

import java.util.List;

/**
 * A Store can store any object that can be serialized in to string. 
 * This interface defines methods to connect, interact with the store.
 * @author Arun Mahendra
 */
public interface Store {
	
	/**
	 * Connect to store and return instance of Store
	 * @param userName
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public Store connect(String userName, String email) throws Exception;
	
	/**
	 * Initialize local db without external connection.
	 * @return
	 * @throws Exception
	 */
	public Store init() throws Exception;
	
	/**
	 * Connect to store with as user
	 * @param username
	 * @param email
	 * @return
	 */
	public Store asUser(String username, String email);
	
	/**
	 * Builder method to add remote Store credentials.
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public Store withRemoteCredential(String url, String username, String password);
	
	/**
	 * List all records in the store.
	 * @return
	 * @throws Exception
	 */
	public List<String> list() throws Exception;
	
	/**
	 * Get a specific record from the Store.
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String get(String name) throws Exception;
	
	/**
	 * Builder method to set a partitions.
	 * @param partition
	 * @return
	 * @throws Exception
	 */
	public Store inPartition(Partition partition) throws Exception;
	
	/**
	 * Add link from a Source to Target.
	 * @param source
	 * @param target
	 * @param message
	 * @throws Exception
	 */
	public void link(String source, String target, String message) throws Exception;
	
	/**
	 * Put a new record into the Store.
	 * @param documentName
	 * @param document
	 * @param message
	 * @throws Exception
	 */
	public void put(String documentName, String document, String message) throws Exception;
	
	/**
	 * Delete a record from the store.
	 * @param documentName
	 * @param message
	 * @throws Exception
	 */
	public void delete(String documentName, String message) throws Exception;
	
	/**
	 * Delete a leaf partition from the store.
	 * @param partition
	 * @param messagecd
	 * @throws Exception
	 */
	public void dropPartition(Partition partition, String message) throws Exception;
	
	/**
	 * Delete partition and all its children from the store.
	 * @param partition
	 * @param message
	 * @throws Exception
	 */
	public void dropPartitionTree(Partition partition, String message) throws Exception;
	
}
