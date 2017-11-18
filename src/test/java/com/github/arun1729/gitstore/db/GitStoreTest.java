package com.github.arun1729.gitstore.db;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class GitStoreTest {

	/*DONOT CHANGE BASE_PATH, IT WILL BE DELETED AFTER THE TEST IS RUN.*/
	private static String BASE_PATH ="src/test/resources/temp/git-test/test-repo";
	
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
		gitStore.delete(filename, "deleting doc for testing.");
	}
	
	@Test
	public void testList() throws Exception {
		String doc1 = "Test Doc1..";
		String doc2 = "Test Doc1..";
		gitStore.connect("arun", "arun.mahendra@someemail.com");
		gitStore.put("doc1", doc1, "new doc 1");
		gitStore.put("doc2", doc2, "new doc 2");
		assertEquals("[doc1, doc2]", gitStore.list().toString());
	}
	
	@Test
	public void testPutGetPartition() throws Exception {
		String content = "Test test Test test";
		String filename = "test-doc";
		DBPartition partName = new DBPartition("test/part/1");
		gitStore.connect("arun", "arun.mahendra@someemail.com");
		gitStore.inPartition(partName).put(filename, content, "created for testing");
		assertEquals(content, gitStore.inPartition(partName).get(filename));
		gitStore.inPartition(partName).delete(filename, "remocing file after testing");
		gitStore.dropPartitionTree(partName, "dropping partition after testing.");
	}
	
	@AfterClass
	public static void cleanUp() throws Exception{
		/*safety check to prevent accidentally deleting the wrong directory.*/
		if(!BASE_PATH.contains("/temp/")){
			System.out.println("Test git repo must be inside a 'temp' directory.");
			return;
		}
		if(!BASE_PATH.contains("git-test/test-repo")){
			System.out.println("Test git repo must be inside a 'git-test/test-repo' directory.");
			return;
		}
		if(BASE_PATH.indexOf("/temp") > BASE_PATH.indexOf("/git-test")){
			System.out.println("Will not clean up test git repo. test repo must be inside a temp directory!");
			return;
		}
		
		String gitPath = BASE_PATH;
		FileUtils.deleteDirectory(new File(gitPath));
		System.out.println("Cleaned up repo dir: "+gitPath);
	}
}
