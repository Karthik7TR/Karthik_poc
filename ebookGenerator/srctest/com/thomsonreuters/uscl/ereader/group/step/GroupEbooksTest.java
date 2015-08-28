package com.thomsonreuters.uscl.ereader.group.step;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewHttpResponseErrorHandler;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewMessageConverter;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClientImpl;

public class GroupEbooksTest {

	private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.demo.thomsonreuters.com";
	private static final String PROVIEW_USERNAME = "publisher";
	private static final String PROVIEW_PASSWORD = "f9R_zBq37a";
	private GroupEbooks groupEbooks;
	private CloseableAuthenticationHttpClientFactory defaultHttpClient;
	private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
	private ProviewResponseExtractorFactory proviewResponseExtractorFactory;
	private ProviewClientImpl proviewClient;
	
	@Before
	public void setUp() throws Exception
	{
		proviewClient = new ProviewClientImpl();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setBufferRequestBody(false);

		defaultHttpClient = new CloseableAuthenticationHttpClientFactory("proviewpublishing.int.demo.thomsonreuters.com",PROVIEW_USERNAME,PROVIEW_PASSWORD);		requestFactory.setHttpClient(defaultHttpClient.getCloseableAuthenticationHttpClient());

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.getMessageConverters().add(
				new ProviewMessageConverter<File>());
		restTemplate.setErrorHandler(new ProviewHttpResponseErrorHandler());
		proviewClient.setRestTemplate(restTemplate);
		proviewRequestCallbackFactory = new ProviewRequestCallbackFactory();
		proviewResponseExtractorFactory = new ProviewResponseExtractorFactory();
		proviewClient
				.setProviewRequestCallbackFactory(proviewRequestCallbackFactory);
		proviewClient
				.setProviewResponseExtractorFactory(proviewResponseExtractorFactory);
		proviewClient.setGetGroupUriTemplate("http://"
				+ PROVIEW_DOMAIN_PREFIX + "/v1/group/{groupId}/{groupVersionNumber}/info");

		proviewClient.setProviewHostname("proviewpublishing.int.demo.thomsonreuters.com");
		
		groupEbooks = new GroupEbooks();
		groupEbooks.setProviewClient(proviewClient);
	}

	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void testGetGroupInfoByVersion() throws Exception {
		String response = groupEbooks.getGroupInfoByVersion("uscl/groupT", new Long(10));
		Assert.assertEquals(null,response);
	}
	
	@Test
	public void testCreateGroup() throws ProviewException{
		proviewClient.setCreateGroupUriTemplate("http://"
				+ "proviewpublishing.int.demo.thomsonreuters.com" + "/v1/group/{groupId}/{groupVersionNumber}");	
		groupEbooks.setBaseRetryInterval(100);
		boolean thrown = false;
		try{
		groupEbooks.createGroup(mockGroup());
		}
		catch(ProviewRuntimeException ex){
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	protected GroupDefinition mockGroup(){			
		GroupDefinition groupDefinition = new GroupDefinition();
		groupDefinition.setGroupId("uscl/groupTest");
		groupDefinition.setGroupVersion("v1");
		groupDefinition.setName("Group Test");
		groupDefinition.setType("standard");
		groupDefinition.setOrder("newerfirst");
		groupDefinition.setHeadTitle("uscl/an/book_lohisplitnodeinfo/v1");
		
		List<SubGroupInfo> subGroupInfoList = new ArrayList<SubGroupInfo>();
		SubGroupInfo subGroupInfo = new SubGroupInfo();
		subGroupInfo.setHeading("2014");
		List<String> titleList = new ArrayList<String>();
		titleList.add("uscl/an/book_lohisplitnodeinfo/v1");
		titleList.add("uscl/an/book_lohisplitnodeinfo_pt/v1");
		subGroupInfo.setTitles(titleList);
		subGroupInfoList.add(subGroupInfo);
		groupDefinition.setSubGroupInfoList(subGroupInfoList);
		
		return groupDefinition;
	}
	
	
	
	
	
	
}
