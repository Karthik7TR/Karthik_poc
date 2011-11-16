package com.thomsonreuters.uscl.ereader.orchestrate.engine.step.item.play;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;

public class PlayItemProcessor implements ItemProcessor<Integer, String> {
	private static final Logger log = Logger.getLogger(PlayItemProcessor.class);
	
	/**
	 * Given one input object, transform it and return another.
	 */
	public String process(Integer intInput) throws Exception {
		log.debug(">>>");
		// Perform tranformation of input here....
		String transformedInput = String.valueOf(intInput);
		return transformedInput;
	}
}
