package com.thomsonreuters.uscl.ereader.group.step;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewHttpResponseErrorHandler;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewMessageConverter;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClientImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandlerImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupServiceImpl;

public class GroupEbooksIntegrationTest {

	private static final String PROVIEW_DOMAIN_PREFIX = "proviewpublishing.int.demo.thomsonreuters.com";
	private static final String PROVIEW_USERNAME = "publisher";
	private static final String PROVIEW_PASSWORD = "f9R_zBq37a";
	private GroupServiceImpl groupEbooks;
	private CloseableAuthenticationHttpClientFactory defaultHttpClient;
	private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
	private ProviewResponseExtractorFactory proviewResponseExtractorFactory;
	private ProviewClientImpl proviewClient;
	private ProviewHandler proviewHandler;
	
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
		
		proviewHandler = new ProviewHandlerImpl();
		
		groupEbooks = new GroupServiceImpl();
		groupEbooks.setProviewHandler(proviewHandler);
	}

	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void testGetGroupInfoByVersion() throws Exception {
		GroupDefinition group = groupEbooks.getGroupInfoByVersion("uscl/groupTest", new Long(10));
//		String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group id=\"uscl/grouptest\" status=\"Review\">"
//				+ "<name>CR MN FED</name><type>standard</type><headtitle>uscl/cr/mn_fed</headtitle><members><subgroup heading=\"2014\">"
//				+ "<title>uscl/cr/mn_fed</title><title>uscl/sc/ca_env</title></subgroup></members></group>";
//		Assert.assertEquals(expectedResponse,response);
	}
	
	@Test
	public void testCreateGroup() throws ProviewException{
		proviewClient.setCreateGroupUriTemplate("http://"
				+ "proviewpublishing.int.demo.thomsonreuters.com" + "/v1/group/{groupId}/{groupVersionNumber}");	
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
		groupDefinition.setProviewGroupVersionString("v1");
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
