package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue.QueueForm.DisplayTagSortProperty;
import org.displaytag.pagination.PaginatedList;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public final class QueueControllerTest {
    private static final int OBJECTS_PER_PAGE = 20;
    private static final int JOB_REQUEST_COUNT = 21;
    private static final String PAGE = "page";
    private static final String SORT = "sort";
    private static final String DIR = "dir";
    private static final String DESC = "desc";
    private static final PageAndSort<DisplayTagSortProperty> PAGE_AND_SORT_WITH_INCORRECT_PAGE
            = new PageAndSort<>(3, OBJECTS_PER_PAGE, DisplayTagSortProperty.BOOK_NAME, true);
    private static final String JOB_QUEUE_URI = "/" + WebConstants.MVC_JOB_QUEUE;
    private static final String JOB_QUEUE_PAGE_AND_SORT_URI = "/" + WebConstants.MVC_JOB_QUEUE_PAGE_AND_SORT;
    private static final BookDefinition BOOK_DEF = new BookDefinition();
    private final PageAndSort<DisplayTagSortProperty> PAGE_AND_SORT =
            new PageAndSort<>(1, OBJECTS_PER_PAGE, DisplayTagSortProperty.BOOK_NAME, true);
    private List<JobRequest> jobRequests;

    private QueueController controller;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private JobRequestService mockJobRequestService;
    private OutageService mockOutageService;
    private HandlerAdapter handlerAdapter;

    @Before
    public void setUp() {
        jobRequests = LongStream.range(0, JOB_REQUEST_COUNT)
                .mapToObj(pk -> {
                    BOOK_DEF.setEbookDefinitionId(pk + 345L);
                    final JobRequest jr = JobRequest.createQueuedJobRequest(BOOK_DEF, "ver" + pk, (int) pk, "auser");
                    jr.setSubmittedAt(new Date(System.currentTimeMillis() - (pk * 1000)));
                    jr.setJobRequestId(pk);
                    return jr;
                })
                .collect(Collectors.toList());
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.getSession().setAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT, PAGE_AND_SORT);
        mockJobRequestService = EasyMock.createMock(JobRequestService.class);
        mockOutageService = EasyMock.createMock(OutageService.class);

        handlerAdapter = new AnnotationMethodHandlerAdapter();

        controller = new QueueController(mockJobRequestService, mockOutageService, new QueueFormValidator());

        EasyMock.expect(mockJobRequestService.findAllJobRequests()).andReturn(jobRequests);
        EasyMock.replay(mockJobRequestService);
        EasyMock.expect(mockOutageService.getAllPlannedOutagesToDisplay()).andReturn(Collections.emptyList());
        EasyMock.replay(mockOutageService);
    }

    @Test
    public void testInboundGet() throws Exception {
        request.setRequestURI(JOB_QUEUE_URI);
        request.setMethod(HttpMethod.GET.name());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        commonAssertions(mav);
    }

    @Test
    public void testPaging() throws Exception {
        final Integer pageNumber = 2;
        request.setRequestURI(JOB_QUEUE_PAGE_AND_SORT_URI);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(PAGE, pageNumber.toString());

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        commonAssertions(mav);
        final PageAndSort<DisplayTagSortProperty> actualPageAndSort = getPageAndSortFromSession();
        Assert.assertEquals(pageNumber, actualPageAndSort.getPageNumber());
    }

    /**
     * Test where the user clicks a paging page number 2 when the number of rows has dropped below the count of objects per page.
     * This was causing a sublist index problem.
     */
    @Test
    public void testPagingOnEmptyList() throws Exception {
        final String nextPageNumber = "2";
        jobRequests.remove(0);
        Assert.assertEquals(OBJECTS_PER_PAGE, jobRequests.size());
        request.setRequestURI(JOB_QUEUE_PAGE_AND_SORT_URI);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(PAGE, nextPageNumber);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        commonAssertions(mav);
    }

    @Test
    public void testDefaultViewOnWrongPageNumber() throws Exception {

        request.setRequestURI(JOB_QUEUE_URI);
        request.setMethod(HttpMethod.GET.name());
        request.getSession().setAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT, PAGE_AND_SORT_WITH_INCORRECT_PAGE);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        commonAssertions(mav);
    }


    @Test
    public void testSorting() throws Exception {
        request.setRequestURI(JOB_QUEUE_PAGE_AND_SORT_URI);
        request.setMethod(HttpMethod.GET.name());
        request.setParameter(SORT, DisplayTagSortProperty.PRIORITY.toString());
        request.setParameter(DIR, DESC);

        final ModelAndView mav = handlerAdapter.handle(request, response, controller);

        commonAssertions(mav);
        final List<JobRequest> actualJobRequestRows = ((PaginatedList) mav.getModel().get(WebConstants.KEY_PAGINATED_LIST)).getList();
        final List<JobRequest> expectedJobRequest = jobRequests.stream()
                .sorted((item1, item2) -> item2.getPriority() - item1.getPriority())
                .limit(OBJECTS_PER_PAGE)
                .collect(Collectors.toList());
        Assert.assertEquals(expectedJobRequest, actualJobRequestRows);
    }

    private PageAndSort<DisplayTagSortProperty> getPageAndSortFromSession() {
        return (PageAndSort<DisplayTagSortProperty>) request.getSession().getAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT);
    }

    private void commonAssertions(ModelAndView mav) {
        Assert.assertNotNull(mav);
        Assert.assertEquals(WebConstants.VIEW_JOB_QUEUE, mav.getViewName());
        final Map<String, Object> model = mav.getModel();
        final HttpSession httpSession = request.getSession();
        Assert.assertNotNull(model.get(WebConstants.KEY_PAGINATED_LIST));
        Assert.assertEquals(PAGE_AND_SORT, httpSession.getAttribute(WebConstants.KEY_JOB_QUEUED_PAGE_AND_SORT));
        EasyMock.verify(mockJobRequestService);
        EasyMock.verify(mockOutageService);
    }

}
