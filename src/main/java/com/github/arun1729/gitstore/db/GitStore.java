package com.github.arun1729.gitstore.db;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.arun1729.gitstore.api.Partition;
import com.github.arun1729.gitstore.api.Store;

/**
 * Git store implementation
 * @author Arun Mahendra
 *
 */
public class GitStore implements Store {
	
	private static Logger logger = LoggerFactory.getLogger(GitStore.class);

	private Git git = null;
	private String remoteRepoUrl = null;
	
	/*user credentials.*/
	private CredentialsProvider credentialProvider = null;
	private String user = null;
	private String email = null;
	private String currentUser = null;
	private String currentEmail = null;

	/*home directories.*/
	private String currentRoot = null;
	private final String root;
	
	public GitStore(String storeRoot) {
		this.currentRoot = storeRoot.endsWith("/") ? storeRoot : storeRoot + "/";
		this.root = this.currentRoot;
	}
	
	private String getFullPath(String subdir){
		subdir = subdir.startsWith("/") ? subdir.substring(1) : subdir;
		subdir = subdir.endsWith("/") ? subdir : subdir + "/";
		return this.currentRoot + subdir;
	}
	
	private void reset() {
		this.currentRoot = this.root;
		this.currentUser = this.user;
		this.currentEmail = this.email;
	}
	
	@Override
	public Store init() throws Exception {
		connect(null, null);
		return this;
	}

	@Override
	public synchronized Store connect(String user, String email) throws IOException, InvalidRemoteException, TransportException, GitAPIException {
			this.currentUser = this.user = user;
			this.currentEmail = this.email = email;
			if (this.remoteRepoUrl == null) {
				this.git = Git.init().setDirectory(new File(this.currentRoot)).call();
				logger.info("Initiated new git repository locally.");
			} else {
				this.git = Git.cloneRepository().setURI(this.remoteRepoUrl).setDirectory(new File(this.currentRoot))
						.setCredentialsProvider(this.credentialProvider).call();
				logger.info("Connected to remote git repo @"+this.remoteRepoUrl);
			}
			return this;
	}

	@Override
	public Store withRemoteCredential(String url, String username, String password) {
		this.remoteRepoUrl = url;
		this.credentialProvider = new UsernamePasswordCredentialsProvider(username, password);
		return this;
	}
	
	protected List<String> visibleFiles(File dir) {
		File[] visibleFiles = dir.listFiles((FileFilter) HiddenFileFilter.VISIBLE);
		List<String> fileList = new ArrayList<String>();
		for (File file : visibleFiles) {
			if (file.isDirectory()) {
				fileList.addAll(visibleFiles(file));
			} else {
				fileList.add(file.getPath().replace(this.currentRoot, ""));
			}
		}
		return fileList;
	}

	@Override
	public synchronized List<String> list() throws Exception {
		List<String> list = visibleFiles(new File(this.currentRoot));
		reset();
		return list;
	}

	@Override
	public synchronized String get(String name) throws IOException, InvalidRemoteException, TransportException, GitAPIException {

		if (this.remoteRepoUrl != null)
			git.fetch().call();

		git.checkout().addPath(name).call();

		String fileName = this.currentRoot + name;
		String data = "";

		if (Files.isReadable(Paths.get(fileName))) {
			data = new String(Files.readAllBytes(Paths.get(fileName)));
		} else {
			logger.warn("File does not exists: " + fileName);
		}
		
		reset();
		
		return data;
	}

	@Override
	public Store inPartition(Partition partition) throws Exception {
		String part = getFullPath(partition.getLeaf());
		boolean isExistingPart = new File(part).exists();
		if (isExistingPart) {
			logger.info("This partition already exists! " + part);
		} else {
			boolean ispartCreated = new File(part).mkdirs();
			if (ispartCreated) {
				logger.info("New partition created: " + part);
			} else {
				logger.error("Unable to create new partition: " + part);
				throw new RuntimeException("Unable to create new partition: " + part);
			}
		}
		this.currentRoot = part;
		return this;
	}

	@Override
	public void link(String source, String target, String message) throws Exception {
		if(new File(getFullPath(source)).exists()){
			logger.info("Link already exists.");
		}else{
			Files.createSymbolicLink(Paths.get(getFullPath(source)), Paths.get(target));
			git.commit().setAuthor(this.currentUser, this.currentEmail).setMessage(message).call();
			pushRemote();
		}
		reset();

	}

	@Override
	public void put(String name, String content, String message) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		Files.write(Paths.get(this.currentRoot + name), content.getBytes());
		this.git.add().addFilepattern(name).call();
		this.git.commit().setAuthor(this.currentUser, this.currentEmail).setMessage(message).call();
		pushRemote();
		reset();
	}
	
	private void pushRemote() throws InvalidRemoteException, TransportException, GitAPIException {
		if (this.remoteRepoUrl != null) {
			this.git.push().setCredentialsProvider(this.credentialProvider).call();
			logger.info("Pushed refs to remote.");
		}
	}

	@Override
	public void delete(String name, String message) throws Exception {
		git.rm().addFilepattern(name).call();
		File file = new File(this.currentRoot+name);
		file.delete();
		git.commit().setAuthor(this.currentUser, this.currentEmail).setMessage(message).call();
		logger.info("Deleted file: "+this.currentRoot+name);
		pushRemote();
		reset();

	}
	
	public synchronized void pullRemote() throws InvalidRemoteException, TransportException, GitAPIException {
		if (this.remoteRepoUrl != null) {
			this.git.pull().setCredentialsProvider(this.credentialProvider).call();
			logger.info("Pulled refs from remote.");
		}
	}

	@Override
	public void dropPartition(Partition partition, String message) throws Exception {
		String cPartition = getFullPath(partition.getLeaf());
		if(cPartition.trim().length() < 2){
			logger.error("Cannot delete this parition - partition name must be linger tha 2 charecters.");
			throw new RuntimeException("Cannot delete this parition - partition name must be linger tha 2 charecters.");
		}
		FileUtils.deleteDirectory(new File(cPartition));
		logger.info("Dropped partition: "+cPartition);
		git.commit().setAuthor(this.currentUser, this.currentEmail).setMessage("[DB] Dropped Partition. [/DB] -- "+message).call();
		pushRemote();

	}

	@Override
	public void dropPartitionTree(Partition partition, String message) throws Exception {
		String cPartition = getFullPath(partition.getRoot());
		if(cPartition.trim().length() < 2){
			logger.error("Cannot delete this parition - partition name must be linger tha 2 charecters.");
			throw new RuntimeException("Cannot delete this parition - partition name must be linger tha 2 charecters.");
		}
		FileUtils.deleteDirectory(new File(cPartition));
		logger.info("Dropped partition: "+cPartition);
		git.commit().setAuthor(this.currentUser, this.currentEmail).setMessage("[DB] Dropped Partition Tree. [/DB] -- "+message).call();
		pushRemote();
	}

	@Override
	public Store asUser(String username, String email) {
		this.currentUser = username;
		this.currentEmail = email;
		return this;
	}

}
