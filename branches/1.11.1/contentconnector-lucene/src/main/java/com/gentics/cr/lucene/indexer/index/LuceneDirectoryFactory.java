package com.gentics.cr.lucene.indexer.index;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

/**
 * Creates and caches directories.
 * @author Christopher
 *
 */
public final class LuceneDirectoryFactory {
	/**
	 * Logger.
	 */
	protected static final Logger LOG = Logger.getLogger(LuceneDirectoryFactory.class);
	/**
	 * Key that determines if a directory should be created in memory.
	 */
	protected static final String RAM_IDENTIFICATION_KEY = "RAM";

	/**
	 * ConcurrentHashMap to cache directories.
	 */
	private static ConcurrentHashMap<String, Directory> cachedDirectories = new ConcurrentHashMap<String, Directory>();

	/**
	 * Private constructor.
	 */
	private LuceneDirectoryFactory() {
	}

	/**
	 * Fetches a directory for the given location. 
	 * If there is none it creates a directory 
	 * on the given location. Directories will be cached by its location.
	 * @param directoyLocation String pointing to the location. 
	 * 			If the string starts with RAM,
	 * 			the directory will be created in memory.
	 * @return directory.
	 */
	public static Directory getDirectory(final String directoyLocation) {
		Directory dir = cachedDirectories.get(directoyLocation);
		if (dir == null) {
			dir = createDirectory(directoyLocation);
			cachedDirectories.put(directoyLocation, dir);
		}
		return dir;
	}

	/**
	 * Creates a new directory.
	 * @param directoryLocation location
	 * @return directory
	 */
	private static Directory createDirectory(final String directoryLocation) {
		Directory dir;
		if (RAM_IDENTIFICATION_KEY.equalsIgnoreCase(directoryLocation) || directoryLocation == null
				|| directoryLocation.startsWith(RAM_IDENTIFICATION_KEY)) {
			dir = createRAMDirectory(directoryLocation);

		} else {
			File indexLoc = new File(directoryLocation);
			try {
				dir = createFSDirectory(indexLoc, directoryLocation);
				if (dir == null) {
					dir = createRAMDirectory(directoryLocation);
				}
			} catch (IOException ioe) {
				dir = createRAMDirectory(directoryLocation);
			}
		}
		return dir;
	}

	/**
	 * Creates a FSDirectory on the given location.
	 * @param indexLoc location.
	 * @param name name for logging
	 * @return FSDirectory
	 * @throws IOException on error.
	 */
	protected static Directory createFSDirectory(final File indexLoc, final String name) throws IOException {
		if (!indexLoc.exists()) {
			LOG.debug("Indexlocation did not exist. Creating directories...");
			indexLoc.mkdirs();
		}
		Directory dir = FSDirectory.open(indexLoc);
		LOG.debug("Creating FS Directory for Index [" + name + "]");
		return (dir);
	}

	/**
	 * Creates a Directory in memory.
	 * @param name name of the directory
	 * @return directory.
	 */
	protected static Directory createRAMDirectory(final String name) {
		Directory dir = new RAMDirectory();
		LOG.debug("Creating RAM Directory for Index [" + name + "]");
		return (dir);
	}
}
