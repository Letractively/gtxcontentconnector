package com.gentics.cr.lucene.indexer.index;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.gentics.cr.CRConfig;
import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.lucene.indexaccessor.IndexAccessor;
import com.gentics.cr.lucene.indexaccessor.IndexAccessorFactory;

/**
 * Default MultiIndexAccessor implementation.
 * 
 * Last changed: $Date: 2009-09-02 17:57:48 +0200 (Mi, 02 Sep 2009) $
 * @version $Revision: 180 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class LuceneMultiIndexLocation extends LuceneIndexLocation {
	
	
	
	Hashtable<String,Directory> dirs = new Hashtable<String,Directory>();
	
	/**
	 * Create a new Instance of LuceneMultiIndexLocation this IndexLocation can search over multiple index directories.
	 * It is not able to write to the index.
	 * @param config
	 */
	public LuceneMultiIndexLocation(CRConfig config) {
		super(config);
		GenericConfiguration locs = (GenericConfiguration)config.get(INDEX_LOCATIONS_KEY);
		if(locs!=null)
		{
			Map<String,GenericConfiguration> locationmap = locs.getSortedSubconfigs();
			if(locationmap!=null)
			{
				for(GenericConfiguration locconf:locationmap.values())
				{
					String path = locconf.getString(INDEX_PATH_KEY);
					if(path!=null && !"".equals(path))
					{
						dirs.put(path,loadDirectory(path));
					}
				}
			}
		}
	}
	
	
	private Directory loadDirectory(String indexLocation)
	{
		Directory dir;
		if(RAM_IDENTIFICATION_KEY.equalsIgnoreCase(indexLocation) || indexLocation==null || indexLocation.startsWith(RAM_IDENTIFICATION_KEY))
		{
			dir = new RAMDirectory();
			
		}
		else
		{
			File indexLoc = new File(indexLocation);
			try
			{
				dir = createFSDirectory(indexLoc);
				if(dir==null) dir = createRAMDirectory();
			}
			catch(IOException ioe)
			{
				dir = createRAMDirectory();
			}
		}
		//Create index accessor
		IndexAccessorFactory IAFactory = IndexAccessorFactory.getInstance();
		if(!IAFactory.hasAccessor(dir)){
			try
			{
				IAFactory.createAccessor(dir, getConfiguredAnalyzer());
			}
			catch(IOException ex)
			{
				log.fatal("COULD NOT CREATE INDEX ACCESSOR"+ex.getMessage());
			}
		}
		else{
			log.debug("Accessor already present. we will not create a new one.");
		}
		return dir;
	}

	@Override
	protected IndexAccessor getAccessorInstance() {
		IndexAccessorFactory IAFactory = IndexAccessorFactory.getInstance();
		return IAFactory.getMultiIndexAccessor(this.dirs.values().toArray(new Directory[]{}));
	}

	@Override
	protected Directory[] getDirectories() {
		return this.dirs.values().toArray(new Directory[]{});
	}

	@Override
	public int getDocCount() {
		IndexAccessor indexAccessor = this.getAccessor();
		IndexReader reader  = null;
		int count = 0;
		try
		{
			reader = indexAccessor.getReader(false);
			count = reader.numDocs();
		}catch(IOException ex)
		{
			log.error("IOX happened during test of index. "+ex.getMessage());
		}
		finally{
			indexAccessor.release(reader, false);
		}
		
		return count;
	}

	
	private String getReopenFilename(String dir)
	{
		return dir+"/"+REOPEN_FILENAME;
	}

	@Override
	public void createReopenFile() {
		boolean write_reopen_file = Boolean.parseBoolean((String)config.get("writereopenfile"));
		
		if(write_reopen_file == true){
			for(String dir:this.dirs.keySet())
			{
				log.debug("Writing reopen to " + this.getReopenFilename(dir));
				try {
					new File(this.getReopenFilename(dir)).createNewFile();
				} catch (IOException e) {
					log.warn("Cannot create reopen file! " + e);
				}
			}
		}
	}


	@Override
	public boolean reopenCheck(IndexAccessor indexAccessor) {
		boolean reopened = false;
		if(this.reopencheck)
		{
			try
			{
				boolean found = false;
				for(String dir:this.dirs.keySet())
				{
					log.debug("Check for reopen file at "+this.getReopenFilename(dir));
					File reopenFile = new File(this.getReopenFilename(dir));
					if(reopenFile.exists())
					{
						reopenFile.delete();
						found=true;
					}
				}
				if(found)
				{
					indexAccessor.reopen();
					reopened = true;
					log.debug("Reopened index.");
				}
			}catch(Exception ex)
			{
				log.error(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return reopened;
	}
	
	@Override
	public void finalize() {
		IndexAccessorFactory.getInstance().close();
		
	}

}
