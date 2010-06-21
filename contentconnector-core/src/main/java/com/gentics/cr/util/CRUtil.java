package com.gentics.cr.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gentics.api.lib.datasource.Datasource;
import com.gentics.api.lib.datasource.Datasource.Sorting;
/**
 * 
 * Last changed: $Date: 2010-04-01 15:24:41 +0200 (Do, 01 Apr 2010) $
 * @version $Revision: 543 $
 * @author $Author: supnig@constantinopel.at $
 *
 */
public class CRUtil {
	
	/**
	 * Convert a String like "contentid:asc,name:desc" into an Sorting Array.
	 * Errorhandling or errorrcognition is poor at that time.
	 * 
	 * @param sortingString like "contentid:asc,name:desc"
	 * @return a Sorting[] Array suitable for contentrepository queries
	 */
	public final static Sorting[] convertSorting(String sortingString) {

		String[] sortingArray;
		HashSet<Sorting> sortingColl = new HashSet<Sorting>();

		if (sortingString != null) {

			// split sorting attributes on ,.
			sortingArray = sortingString.split(",");

			// look at each attribute for sort direction
			for (int i = 0; i < sortingArray.length; i++) {

				// split attribute on :. First element is attribute name the
				// second is the direction
				String[] sort = sortingArray[i].split(":");

				if (sort[0] != null) {
					if ("asc".equals(sort[1].toLowerCase())) {
						sortingColl.add(new Sorting(sort[0],
								Datasource.SORTORDER_ASC));
					} else if ("desc".equals(sort[1].toLowerCase())) {
						sortingColl.add(new Sorting(sort[0],
								Datasource.SORTORDER_DESC));
					} else {
						sortingColl.add(new Sorting(sort[0],
								Datasource.SORTORDER_NONE));
					}
				}
			}
		}

		// convert hashmap to uncomfortable Array :-/ and return that or return
		// null if converting need not succed for any reason.
		if (sortingColl != null) {
			return (Sorting[]) sortingColl.toArray(new Sorting[sortingColl
					.size()]);
		} else {
			return null;
		}
	}
	
	/**
	 * Convert a HTML-conform sorting request multivalue parameter array
	 * to a Sorting[], suitable for the contentrepository queries
	 * 
	 * @param sortingArray like ["contentid:asc","name:desc"]
	 * @return a Sorting[] Array suitable for contentrepository queries
	 */
	public final static Sorting[] convertSorting(String[] sortingArray) {
		Sorting[] ret = new Sorting[0];
		ArrayList<Sorting> sortingColl = new ArrayList<Sorting>();
		if(sortingArray!=null)
		{
			int arrlen = sortingArray.length;
			
			
			for (int i = 0; i < arrlen; i++) {
	
				// split attribute on :. First element is attribute name the
				// second is the direction
				String[] sort = sortingArray[i].split(":");
	
				if (sort[0] != null) {
					if ("asc".equals(sort[1].toLowerCase())) {
						sortingColl.add(new Sorting(sort[0],
								Datasource.SORTORDER_ASC));
					} else if ("desc".equals(sort[1].toLowerCase())) {
						sortingColl.add(new Sorting(sort[0],
								Datasource.SORTORDER_DESC));
					} else {
						sortingColl.add(new Sorting(sort[0],
								Datasource.SORTORDER_NONE));
					}
				}
			}
		}
		return(sortingColl.toArray(ret));
	}
	
	
	/**
	 * Convert an array of strings to an array that can be usen in a filter rule
	 * @param arr
	 * @return rule ready string
	 */
	public static String prepareParameterArrayForRule(String[] arr)
	{
		String ret = "[";
		int arrlen = arr.length;
		if(arrlen>0)
		{
			ret+="'"+arr[0]+"'";
			if(arrlen>1)
			{
				for(int i=1; i<arrlen;i++)
				{
					ret+=",'"+arr[i]+"'";
				}
			}
		}
		ret+="]";
		return(ret);
	}
	
	
	
	
	
    /**
     * pattern to find system properties
     */
    protected static Pattern findSystemProperties = Pattern
            .compile("\\$\\{([a-zA-Z0-9\\._]+)\\}");

    /**
     * FIXME copied from {@link com.gentics.lib.etc.StringUtils} - move it to API ?
     * 
     * 
     * Resolve system properties encoded in the string as ${property.name}
     * @param string string holding encoded system properties
     * @return string with the system properties resolved
     */
    public static String resolveSystemProperties(String string) {
        // avoid NPE here
        if (string == null) {
            return null;
        }
        //init com.gentics.portalnode.confpath if it isn't set
        if(System.getProperty("com.gentics.portalnode.confpath").equals("")){
          System.setProperty("com.gentics.portalnode.confpath", System.getProperty("catalina.base")+File.separator+"conf"+File.separator+"gentics"+File.separator);
        }
        // create a matcher
        Matcher m = findSystemProperties.matcher(string);
        StringBuffer output = new StringBuffer();
        int startIndex = 0;
        while(m.find()) {
            // copy static string between the last found system property and this one
            if(m.start() > startIndex) {
                output.append(string.substring(startIndex, m.start()));
            }
            output.append(System.getProperty(m.group(1), ""));
            startIndex = m.end();
        }
        // if some trailing static string exists, copy it
        if(startIndex < string.length()) {
            output.append(string.substring(startIndex));
        }

        return output.toString();
    }
    
    /**
     * Slurps a InputStream into a String
     * @param in
     * @return
     * @throws IOException
     */
    public static String inputStreamToString (InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	}
    
    /**
     * Slurps a Reader into a String
     * @param in
     * @return
     * @throws IOException
     */
    public static String readerToString (Reader in) throws IOException {
		StringBuffer out = new StringBuffer();
	    char[] b = new char[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	}
    
    /**
     * Slurps a Reader into a String and strips the character 0
     * @param in
     * @return
     * @throws IOException
     */
    public static String readerToPrintableCharString(Reader in)throws IOException
    {
    	StringBuffer out = new StringBuffer();
	    int n;
	    while((n = in.read())!=-1)
	    {
	    	if(n!=0)
	    	{
	    		char c = (char)n;
	    		out.append(c);
	    	}
	    }
	    return out.toString();
    }

}