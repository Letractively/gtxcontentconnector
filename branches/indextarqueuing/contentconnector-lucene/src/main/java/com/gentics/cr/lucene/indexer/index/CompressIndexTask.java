package com.gentics.cr.lucene.indexer.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.gentics.cr.CRConfig;
import com.gentics.cr.util.file.ArchiverUtil;
import com.gentics.cr.util.indexing.AbstractAfterActionTask;
import com.gentics.cr.util.indexing.IndexControllerSingleton;
import com.gentics.cr.util.indexing.IndexLocation;

public class CompressIndexTask extends AbstractAfterActionTask {

	/**
	 * Create an archive of the index.
	 * @param index Index to create a tarball of.
	 * @param response 
	 */
	public void execute(final CRConfig config) {
		ConcurrentHashMap<String, IndexLocation> indexLocations = IndexControllerSingleton.getIndexControllerInstance().getIndexes();
		String projectShortName = config.getName().substring("index.".length(), config.getName().length());
		IndexLocation location = indexLocations.get(projectShortName);
		if (location instanceof LuceneSingleIndexLocation) {
			LuceneSingleIndexLocation indexLocation = (LuceneSingleIndexLocation) location;
			File indexDirectory = new File(indexLocation.getReopenFilename()).getParentFile();
			File writeLock = null;
			boolean weWroteTheWriteLock = false;
			try {
				indexLocation.checkLock();
				if (indexDirectory.canWrite()) {
					writeLock = new File(indexDirectory, "write.lock");
					if (writeLock.createNewFile()) {
						weWroteTheWriteLock = true;
					} else {
						throw new LockedIndexException(new Exception("the write lock file already exists in the index."));
					}
					//set to read only so the index jobs will not delete it.
					writeLock.setReadOnly();

					FileOutputStream fileOutputStream = new FileOutputStream(new StringBuilder(indexLocation.getIndexLocation())
							.append("/../").append(projectShortName).append(".tar.gz").toString());
					ArchiverUtil.generateGZippedTar(fileOutputStream, indexDirectory);
				} else {
					LOGGER.error("Cannot lock the index directory to ensure the consistency of the archive.");
				}
			} catch (IOException e) {
				LOGGER.error("Cannot generate the archive correctly.", e);
			} catch (LockedIndexException e) {
				LOGGER.error("Cannot generate the archive while the index is locked.", e);
			} finally {
				if (writeLock != null && writeLock.exists() && weWroteTheWriteLock) {
					writeLock.delete();
				}
			}

		} else {
			LOGGER.error("generating an archive for " + location + " not supported yet.");
		}

	}

}