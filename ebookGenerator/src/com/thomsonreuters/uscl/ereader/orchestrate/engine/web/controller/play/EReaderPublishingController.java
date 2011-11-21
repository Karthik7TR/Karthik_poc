package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller.play;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.EngineManager;

/**
 * Demonstration and spike sample running of a play job.
 */
@Controller
public class EReaderPublishingController {
	private static final Logger log = Logger.getLogger(EReaderPublishingController.class);
	
//	@Resource(name="normalThreadPriorityJobLauncher")
//	private JobLauncher jobLauncher;
//	@Resource(name="eReaderPublishingJob")
//	private Job job;
	@Autowired
	private EngineManager engineManager;
	
	@RequestMapping(value="/eReaderPublishingJob.mvc", method = RequestMethod.GET)
	public ModelAndView eReaderPublishingJob(HttpServletRequest request, HttpServletResponse response)
							  throws Exception {
		log.debug(">>>");
		Map<String,JobParameter> paramMap = new HashMap<String,JobParameter>();
		Date dateNow = new Date();
		paramMap.put("p1", new JobParameter("foo"+dateNow.getTime()));
		paramMap.put("p2", new JobParameter("bar"+dateNow.getTime()));
		JobParameters jobParameters = new JobParameters(paramMap);
		JobExecution je = engineManager.runJob("eReaderPublishingJob", jobParameters, 5);
		request.setAttribute("jobExecution", je);
		return new ModelAndView("results");
	}
}
