package com.thomsonreuters.uscl.ereader.orchestrate.engine.step.item.play;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class PlayItemReader implements ItemReader<Integer> {
	private static final Logger log = Logger.getLogger(PlayItemReader.class);
	
	/**
	 * Provide data from an input source.
	 * @return the item read, or null to indicated no more items left to process
	 */
	public Integer read() throws Exception, UnexpectedInputException, ParseException {
		log.debug(">>>");
		int max = 100;
		int playInt = RandomUtils.nextInt(max);
		if (playInt < (max / 20)) {
			// Return null if there are no more items left to process
			return null;
		}
		return playInt;
	}
}
