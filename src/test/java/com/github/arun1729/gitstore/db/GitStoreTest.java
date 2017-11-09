package com.github.arun1729.gitstore.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GitStoreTest {

	/*DONOT CHANGE BASE_PATH, IT WILL BE DELETED AFTER THE TEST IS RUN.*/
	private static String BASE_PATH ="src/test/resources/git-test/test-repo";
	
	GitStore gitStore = null;
	
	@Before
	public void setUp() throws Exception {
		if(this.gitStore == null) this.gitStore = new GitStore(BASE_PATH);
	}


	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPut() throws Exception {
		String content = "A new document to store into GitStore";
		String filename = "document-1";
		gitStore.connect("arun", "arun.mahendra@someemail.com");
		gitStore.put(filename, content, "inserted new document into git store.");
		assertEquals(content, gitStore.get(filename));
	}

}
