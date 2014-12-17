package com.thomsonreuters.uscl.ereader.proview.rest;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.thomsonreuters.uscl.ereader.deliver.rest.CloseableAuthenticationHttpClientFactory;

public class CloseableAuthenticationHttpClientFactoryTest {

	private CloseableAuthenticationHttpClientFactory httpClientFactory;
	private InetAddress mockHost;
	
	@Before
	public void setUp() throws Exception {
		mockHost = InetAddress.getLocalHost();
		httpClientFactory = new CloseableAuthenticationHttpClientFactory(mockHost.getHostName(), "captain", "j4ck_sp4rr0w");
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
//	@Test
//	public void testFactoryIsCorrectlyConfiguredUponInstantiation() throws Exception {
//		DefaultHttpClient httpClient = (DefaultHttpClient) httpClientFactory.getBasicAuthenticationHttpClient();
//		Credentials credentials = httpClient.getCredentialsProvider().getCredentials(new AuthScope("pirate.com", AuthScope.ANY_PORT));
//		assertTrue("Configured password should match, but didn't.", credentials.getPassword().equals("j4ck_sp4rr0w"));
//	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenHostIsNull() throws Exception {
		CloseableAuthenticationHttpClientFactory httpClientFactory = new CloseableAuthenticationHttpClientFactory(null, "captain", "j4ck_sp4rr0w");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenUsernameIsNull() throws Exception {
		CloseableAuthenticationHttpClientFactory httpClientFactory = new CloseableAuthenticationHttpClientFactory(mockHost.getHostName(), null, "j4ck_sp4rr0w");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenUsernameIsBlank() throws Exception {
		CloseableAuthenticationHttpClientFactory httpClientFactory = new CloseableAuthenticationHttpClientFactory(mockHost.getHostName(), "", "j4ck_sp4rr0w");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenPasswordIsNull() throws Exception {
		CloseableAuthenticationHttpClientFactory httpClientFactory = new CloseableAuthenticationHttpClientFactory(mockHost.getHostName(), "captain", null);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testCannotCreateFactoryWhenPasswordIsBlank() throws Exception {
		CloseableAuthenticationHttpClientFactory httpClientFactory = new CloseableAuthenticationHttpClientFactory(mockHost.getHostName(), "captain", "");
	}
}