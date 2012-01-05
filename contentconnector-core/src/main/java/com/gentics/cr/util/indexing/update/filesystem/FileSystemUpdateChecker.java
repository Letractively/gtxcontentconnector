package com.gentics.cr.util.indexing.update.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gentics.api.lib.resolving.Resolvable;
import com.gentics.cr.CRResolvableBean;
import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.exceptions.CRException;
import com.gentics.cr.util.indexing.IndexUpdateChecker;

/**
 * The FileSystemUpdateChecker checks if a given object is up to date in the filesystem.
 * @author bigbear3001
 *
 */
public class FileSystemUpdateChecker extends IndexUpdateChecker {

	/**
	 * directory containing the files
	 */
	File directory;
	
	/**
	 * ignore the pub_dir attribute in the bean. if set to true this will put all the files and pages into one directory.
	 */
	boolean ignorePubDir;
	
	/**
	 * list of files from the directory. this is used to delete files that are not existent in the source (were not checked till #deleteStaleObjects() is called)
	 */
	List<String> files;
	
	/**
	 * Initialize a new FileSystemUpdateChecker. You'll have to initialize a new one every index run as the list of files to be deleted is generated at initialization and every checked file is removed from it. 
	 * @param config - configuration of the FileSystemUpdateChecker.
	 * At the moment the following configuration options are implemented:
	 * <ul>
	 *  <li>directory - directory to check if the files/resolvables are up to date</li>
	 *  <li>ignorePubDir - ignores the pub_dir attribute of the resolvable. if set to true all files are put into one directory</li>
	 * </ul>
	 */
	public FileSystemUpdateChecker(GenericConfiguration config) {
		directory = new File(config.getString("directory"));
		ignorePubDir = config.getBoolean("ignorePubDir");
		String[] existingFiles = directory.list();
		if(existingFiles != null) {
			files = new ArrayList<String>(Arrays.asList(directory.list()));
		} else {
			files = new ArrayList<String>(); 
		}
	}

	/**
	 * check if the file corresponding to the object is newer than the given timestamp and remove the file from {@link #files}.
	 */
	@Override
	protected boolean checkUpToDate(String identifyer, Object timestamp,
			String timestampattribute, Resolvable object) {
		CRResolvableBean bean = new CRResolvableBean(object);
		if(!"10002".equals(bean.getObj_type())) {
			String publicationDirectory;
			if (ignorePubDir) {
				publicationDirectory = "";
			} else {
				publicationDirectory = bean.getString("pub_dir");
			}
			String filename = bean.getString("filename");
			assertNotNull("Bean " + bean.getContentid() + " has no attribute pub_dir.", publicationDirectory);
			assertNotNull("Bean " + bean.getContentid() + " has no attribute filename.", filename);
			Integer updatetimestamp = (Integer) timestamp;
			File file = new File(new File(directory, publicationDirectory), filename);
			files.remove(publicationDirectory + filename);
			if(file.exists() && file.isFile() && (file.lastModified() / 1000) >= updatetimestamp) {
				return true;
			}
		} else if(!ignorePubDir) {
			//it would just make no sense to check for check for folders existence if the pub_dir attribute is ignored
			String publicationDirectory = bean.getString("pub_dir");
			File file = new File(directory, publicationDirectory);
			files.remove(publicationDirectory);
			if (file.exists() && file.isDirectory()) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * throw a runtime exception if the given object is <code>null</code>.
	 * @param message - message to put into the exception.
	 * @param object - object to check for beeing not <code>null</code>
	 */
	protected static void assertNotNull(String message, Object object) {
		if (object == null) {
			throw new RuntimeException(new CRException(message));
		}
	}

	/**
	 * Delete the files in the {@link #directory} that were not checked if they are up to date since the initialization of the FileSystemUpdateChecker.
	 */
	@Override
	public void deleteStaleObjects() {
		for(String filename : files) {
			File file = new File(directory, filename);
			file.delete();
		}
	}

}
