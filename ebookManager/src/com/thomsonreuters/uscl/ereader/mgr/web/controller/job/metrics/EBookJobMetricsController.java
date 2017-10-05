package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.metrics;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EBookJobMetricsController {
    private final PublishingStatsService publishingStatsService;
    private final BookDefinitionService bookDefinitionService;

    @Autowired
    public EBookJobMetricsController(
        final PublishingStatsService publishingStatsService,
        final BookDefinitionService bookDefinitionService) {
        this.publishingStatsService = publishingStatsService;
        this.bookDefinitionService = bookDefinitionService;
    }

    /**
     *
     * @param jobInstanceId
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_JOB_METRICS, method = RequestMethod.GET)
    public ModelAndView bookPublishingHistory(final Long jobInstanceId, final Model model) throws Exception {
        final PublishingStats ebookPublishingStats = publishingStatsService.findPublishingStatsByJobId(jobInstanceId);
        if (ebookPublishingStats != null) {
            final BookDefinition book =
                bookDefinitionService.findBookDefinitionByEbookDefId(ebookPublishingStats.getEbookDefId());
            model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);
            model.addAttribute(WebConstants.KEY_PUBLISHING_STATS, ebookPublishingStats);
        }

        return new ModelAndView(WebConstants.VIEW_BOOK_JOB_METRICS);
    }
}
