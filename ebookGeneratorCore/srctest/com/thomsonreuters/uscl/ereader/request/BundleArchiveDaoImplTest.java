package com.thomsonreuters.uscl.ereader.request;

import java.io.File;
import java.util.Date;

import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.request.dao.BundleArchiveDaoImpl;


public class BundleArchiveDaoImplTest {
	
	private BundleArchiveDaoImpl archiveDao;
	
	private SessionFactory mockSessionFactory;
	private Session mockSession;
	private Criteria mockCriteria;
	
	@Before
	public void setUp() {
		this.mockSessionFactory = EasyMock.createMock(SessionFactory.class);
		this.mockSession = EasyMock.createMock(Session.class);
		this.mockCriteria = EasyMock.createMock(Criteria.class);
		
		this.archiveDao = new BundleArchiveDaoImpl(mockSessionFactory);
	}
	
	@Test
	public void happyPath() {
		long pkey = 1L;
		EBookRequest expected = createEbookRequest();
		EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
		EasyMock.expect(mockSession.get(EBookRequest.class, pkey)).andReturn(expected);
		replayAll();
		
		EBookRequest actual = archiveDao.findByPrimaryKey(pkey);
		Assert.assertEquals(expected, actual);
	}
	
	private EBookRequest createEbookRequest() {
		EBookRequest request = new EBookRequest();
		request.setBundleHash("asdfasda");
		request.setDateTime(new Date());
		request.setEBookArchiveId(127L);
		request.setEBookSrcFile(new File("asdfasdfa"));
		return request;
	}
	
	private void replayAll() {
		EasyMock.replay(mockSessionFactory);
		EasyMock.replay(mockSession);
		EasyMock.replay(mockCriteria);
	}
}