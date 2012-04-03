package com.gentics.cr.lucene.indexer.index;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import com.gentics.cr.CRConfig;
import com.gentics.cr.lucene.indexaccessor.IndexAccessor;
import com.gentics.cr.lucene.indexaccessor.IndexAccessorFactory;

/**
 * @author Christopher
 *
 */
public class LuceneSingleIndexLocation extends LuceneIndexLocation {
	//Instance Members
	/**
	 * Directory.
	 */
	private Directory dir = null;
	/**
	 * Location.
	 */
	private String indexLocation;

	/**
	 * Timestamp to store the lastmodified value of the reopen file.
	 */
	private long lastmodifiedStored = 0;
	
	/**
	 * Create a new Instance of LuceneSingleIndexLocation. 
	 * This is the Default IndexLocation for Lucene.
	 * @param config configuration
	 */
	public LuceneSingleIndexLocation(final CRConfig config) {
		super(config);
		indexLocation = getFirstIndexLocation(config);
		dir = createDirectory(indexLocation);
		//Create index accessor
		IndexAccessorFactory iAFactory = IndexAccessorFactory.getInstance();
		if (!iAFactory.hasAccessor(dir)) {
			try {
				iAFactory.createAccessor(dir, getConfiguredAnalyzer());
			} catch (IOException ex) {
				log.fatal("COULD NOT CREATE INDEX ACCESSOR" + ex.getMessage());
			}
		} else {
			log.debug("Accessor already present.");
		}
	}

	@Override
	protected final IndexAccessor getAccessorInstance() {
		Directory directory = this.getDirectory();
		
		IndexAccessor indexAccessor = IndexAccessorFactory.getInstance()
						.getAccessor(directory);
		return indexAccessor;
	}

	@Override
	public final int getDocCount()  {
		IndexAccessor indexAccessor = this.getAccessor();
		IndexReader reader	= null;
		int count = 0;
		try {
			reader = indexAccessor.getReader(false);
			count = reader.numDocs();
		} catch (IOException ex) {
			log.error("IOException happened during test of index. ", ex);
		} finally {
			indexAccessor.release(reader, false);
		}
		
		return count;
	}

	@Override
	protected final Directory[] getDirectories() {
		Directory[] dirs = new Directory[]{this.getDirectory()};
		return dirs;
	}
	
	/**
	 * getDirectory.
	 * @return directory.
	 */
	private Directory getDirectory() {
		return this.dir;
	}
	
	
	/**
	 * Returns the filename of the reopen file.
	 * @return filename of the reopen file.
	 */
	public final String getReopenFilename() {
		return this.indexLocation + "/" + REOPEN_FILENAME;
	}
	
	/**
	 * Creates the reopen file to make portlet reload the index.
	 */
	public final void createReopenFile() {
		boolean writeReopenFile = config.getBoolean("writereopenfile");
		if (writeReopenFile) {
			String filename = this.getReopenFilename();
			log.debug("Writing reopen to " + filename);
			try {
				File reopenFile = new File(filename);
				FileUtils.touch(reopenFile);
			} catch (IOException e) {
				log.warn("Cannot create reopen file! " + e);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean reopenCheck(final IndexAccessor indexAccessor) {
		boolean reopened = false;
		if (reopencheck) {
			try {
				log.debug("Check for reopen file at "
						+ this.getReopenFilename());
				File reopenFile = new File(this.getReopenFilename());
				if (reopenFile.exists()) {
					if (reopencheckTimestamp) {
						long lastmodified = reopenFile.lastModified();
						if (lastmodified != lastmodifiedStored) {
							lastmodifiedStored	= lastmodified;
							indexAccessor.reopen();
							reopened = true;
							log.debug("Reopened index because reopen file has "
									+ "changed");
						} else {
							log.debug("Do not reopen index because reopen file "
									+ "hasn't changed.\n" + lastmodified
									+ " == " + lastmodifiedStored);
						}
					} else {
						reopenFile.delete();
						indexAccessor.reopen();
						reopened = true;
						log.debug("Reopened index because of simple "
								+ "reopencheck.");
					}
				} else {
					log.debug("Reopen file not found.");
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
		}
		return reopened;
	}

	@Override
	public final long indexSize() {
		File indexLocationFile = new File(indexLocation);
		long directorySize = FileUtils.sizeOfDirectory(indexLocationFile);
		//TODO add caching
		return directorySize;
	}

	@Override
	public final Date lastModified() {
		File reopenfile = new File(getReopenFilename());
		if (reopenfile.exists()) {
			return new Date(reopenfile.lastModified());
		} else {
			File directory = reopenfile.getParentFile();
			if (directory.exists()) {
				return new Date(directory.lastModified());
			} else {
				return new Date(0);
			}
		}
	}

	/**
	 * Checks if the index is optimized.
	 * @return true if it is optimized
	 */
	public final boolean isOptimized() {
		boolean ret = false;
		IndexAccessor indexAccessor = this.getAccessor();
			IndexReader reader	= null;
			try {
				reader = indexAccessor.getReader(false);
				ret = reader.isOptimized();
			} catch (IOException ex) {
				log.error("IOException happened during test of index. ", ex);
			} finally {
				indexAccessor.release(reader, false);
			}
			
		return ret;
	}

	@Override
	public final boolean isLocked() {
		boolean locked = false;
		IndexAccessor indexAccessor = this.getAccessor();
		locked = indexAccessor.isLocked(); 
		return locked;
	}
	/**
	 * Returns the hashCode of the LockID of the used Directory
	 * 
	 * @author Sebastian Vogel <s.vogel@gentics.com>
	 */
	@Override
	public int hashCode() {
		return dir.getLockID().hashCode();
	}
}