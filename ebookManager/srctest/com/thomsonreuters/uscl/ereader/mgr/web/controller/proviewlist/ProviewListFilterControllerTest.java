package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.FilterCommand;

public class ProviewListFilterControllerTest {

	private ProviewListFilterController controller;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;
	private AnnotationMethodHandlerAdapter handlerAdapter;

	@Before
	public void setUp() throws Exception {
		this.controller = new ProviewListFilterController();
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();

		handlerAdapter = new AnnotationMethodHandlerAdapter();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testStartWildcard() throws Exception {
		String title = "testTitle";
		String titleId = "testId";
		Integer totalNumberOfVersions = new Integer(1);

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_LIST_FILTERED_POST);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("FilterCommand", FilterCommand.SEARCH.toString());
		request.setParameter("proviewDisplayName", "%" + title);
		request.setParameter("titleId", "%" + titleId);
		request.setParameter("minVersions", totalNumberOfVersions.toString());
		request.setParameter("maxVersions", totalNumberOfVersions.toString());
		HttpSession session = request.getSession();

		List<ProviewTitleInfo> titleList = new ArrayList<ProviewTitleInfo>();
		ProviewTitleInfo titleInfo = new ProviewTitleInfo();
		titleInfo.setTitle(title);
		titleInfo.setTitleId(titleId);
		titleInfo.setTotalNumberOfVersions(totalNumberOfVersions);
		titleList.add(titleInfo);

		session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, titleList);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);

		Assert.assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEndWildcard() throws Exception {
		String title = "testTitle";
		String titleId = "testId";
		Integer totalNumberOfVersions = new Integer(1);

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_LIST_FILTERED_POST);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("FilterCommand", FilterCommand.SEARCH.toString());
		request.setParameter("proviewDisplayName", title + "%");
		request.setParameter("titleId", titleId + "%");
		request.setParameter("minVersions", "1.1");
		request.setParameter("maxVersions", "1.2");
		HttpSession session = request.getSession();

		List<ProviewTitleInfo> titleList = new ArrayList<ProviewTitleInfo>();
		ProviewTitleInfo titleInfo = new ProviewTitleInfo();
		titleInfo.setTitle(title);
		titleInfo.setTitleId(titleId);
		titleInfo.setTotalNumberOfVersions(totalNumberOfVersions);
		titleList.add(titleInfo);

		session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, titleList);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);

		Assert.assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testAllWildcard() throws Exception {
		String title = "testTitle";
		String titleId = "testId";
		Integer totalNumberOfVersions = new Integer(1);

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_LIST_FILTERED_POST);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("FilterCommand", FilterCommand.SEARCH.toString());
		request.setParameter("proviewDisplayName", "%" + title + "%");
		request.setParameter("titleId", "%" + titleId + "%");
		request.setParameter("minVersionsInt", totalNumberOfVersions.toString());
		request.setParameter("maxVersionsInt", totalNumberOfVersions.toString());
		HttpSession session = request.getSession();

		List<ProviewTitleInfo> titleList = new ArrayList<ProviewTitleInfo>();
		ProviewTitleInfo titleInfo = new ProviewTitleInfo();
		titleInfo.setTitle(title);
		titleInfo.setTitleId(titleId);
		titleInfo.setTotalNumberOfVersions(totalNumberOfVersions);
		titleList.add(titleInfo);

		session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, titleList);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);

		Assert.assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNoWildcard() throws Exception {
		String title = "testTitle";
		String titleId = "testId";
		Integer totalNumberOfVersions = new Integer(1);

		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_LIST_FILTERED_POST);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("FilterCommand", FilterCommand.SEARCH.toString());
		request.setParameter("proviewDisplayName", title);
		request.setParameter("titleId", titleId);
		request.setParameter("minVersionsInt", totalNumberOfVersions.toString());
		request.setParameter("maxVersionsInt", totalNumberOfVersions.toString());
		HttpSession session = request.getSession();

		List<ProviewTitleInfo> titleList = new ArrayList<ProviewTitleInfo>();
		ProviewTitleInfo titleInfo = new ProviewTitleInfo();
		titleInfo.setTitle(title + "a");
		titleInfo.setTitleId(titleId + "a");
		titleInfo.setTotalNumberOfVersions(totalNumberOfVersions);
		titleList.add(titleInfo);

		session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, titleList);

		ModelAndView mav = handlerAdapter.handle(request, response, controller);

		Assert.assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
	}
	
	@Test
	public void testReset() throws Exception {
		request.setRequestURI("/" + WebConstants.MVC_PROVIEW_LIST_FILTERED_POST);
		request.setMethod(HttpMethod.POST.name());
		request.setParameter("FilterCommand", FilterCommand.RESET.toString());
		
		ProviewTitleForm titleForm = new ProviewTitleForm();
		titleForm.setObjectsPerPage("50");
		
		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, new ArrayList<ProviewTitleInfo>());
		session.setAttribute(ProviewTitleForm.FORM_NAME, titleForm);
		
		ModelAndView mav = handlerAdapter.handle(request, response, controller);

		Assert.assertNotNull(mav);
		Assert.assertEquals(mav.getViewName(), WebConstants.VIEW_PROVIEW_TITLES);
	}
}