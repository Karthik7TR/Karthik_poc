package com.thomsonreuters.uscl.ereader.gather.services;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TocServiceTest {

	@Autowired
	private TocService tocService;
	
	@Test
	public void testGetTocDataFromNovus( )
	{
	
		List<EBookToc> tocList = null;
		try {
			tocList = tocService.getTocDataFromNovus("I7b3ec600675a11da90ebf04471783734","w_an_rcc_cajur_toc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue(tocList.size() > 0) ;
	}
	
	@Test (expected=GatherException.class)
	public void testGetTocDataFromNovus_Exception( ) throws Exception
	{
	
		List<EBookToc> tocList = null;
		
		tocList = tocService.getTocDataFromNovus("Dummy_Guid_for_Exception_test","Dummy_Collection_name");
		

	}

}
