package com.gentics.cr.lucene.indexer.transformer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.gentics.cr.CRResolvableBean;
import com.gentics.cr.configuration.GenericConfiguration;
import com.gentics.cr.util.CRUtil;

/**
 * Last changed: $Date: 2011-05-13 10:40:00 +0200 (Fr, 13 May 2011) $
 * 
 * @version $Revision:  $
 * @author $Author: bernhard.friedreich@extern.brz.gv.at $
 * 
 */
public class EvalExpressionParserTransformer extends ContentTransformer {
	private static final String SRC_ATTRIBUTE_KEY = "srcattribute";
	private static final String TARGET_ATTRIBUTE_KEY = "targetattribute";
	private String src_attribute = "";
	private String target_attribute = "ul";

	/**
	 * Create Instance of EvalExpressionParserTransformer.
	 * 
	 * @param config Configuration to read the attributes from.
	 */
	public EvalExpressionParserTransformer(final GenericConfiguration config) {
		super(config);
		src_attribute = (String) config.get(SRC_ATTRIBUTE_KEY);
		target_attribute = (String) config.get(TARGET_ATTRIBUTE_KEY);
	}

	@Override
	public void processBean(CRResolvableBean bean) {

	}

	private String getStringContents(Object obj) {
		String str = null;
		if (obj instanceof String) {
			str = (String) obj;
		} else if (obj instanceof byte[]) {
			try {
				str = CRUtil.readerToString(new InputStreamReader(new ByteArrayInputStream((byte[]) obj)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	@Override
	public void destroy() {

	}

}
