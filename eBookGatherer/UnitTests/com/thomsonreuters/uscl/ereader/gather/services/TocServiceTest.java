package com.thomsonreuters.uscl.ereader.gather.services;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TocServiceTest {

	@Autowired
	private TocService tocService;
	
	@Test
	public void testGetTocDataFromNovus( )
	{
	
		List<EBookToc> tocList = tocService.getTocDataFromNovus("N04767C6077B911DAA16E8D4AC7636430","w_codesstawip");

		assertTrue(tocList.size() > 0) ;
	}
}
