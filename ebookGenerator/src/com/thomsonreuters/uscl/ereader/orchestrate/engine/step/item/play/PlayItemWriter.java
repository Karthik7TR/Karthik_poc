package com.thomsonreuters.uscl.ereader.orchestrate.engine.step.item.play;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

public class PlayItemWriter implements ItemWriter<String> {
	private static final Logger log = Logger.getLogger(PlayItemWriter.class);
	
	/**
	 * Write out a batched chunk of items.
	 */
	@Override
	public void write(List<? extends String> items) throws Exception {
		
		log.debug(">>> Play write: " + items);
	}
}
