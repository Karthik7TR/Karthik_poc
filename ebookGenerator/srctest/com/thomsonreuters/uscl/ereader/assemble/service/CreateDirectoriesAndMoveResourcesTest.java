package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.assemble.step.CreateDirectoriesAndMoveResources;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import static org.junit.Assert.assertTrue;

public class CreateDirectoriesAndMoveResourcesTest {
	
	CreateDirectoriesAndMoveResources createDirectoriesAndMoveResources;
	Map<String, List<Doc>> docMap = new HashMap<String, List<Doc>> ();
	Map<String, List<String>> splitBookImgMap = new HashMap<String, List<String>> ();
	private static final String FINE_NAME = "doc-To-SplitBook.txt";
	private File docToSplitBookFile;
	private File tempFile;
	
	@Before
	public void setUp() throws Exception {

		createDirectoriesAndMoveResources = new CreateDirectoriesAndMoveResources();
		tempFile = File.createTempFile("pirate", "ship");
		URL url = this.getClass().getResource(FINE_NAME);
		docToSplitBookFile = new File(url.getPath());
		
	}
	
	@After
	public void tearDown() throws Exception {
		//FileUtils.delete(eBook);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetAssestsFromDir(){
		createDirectoriesAndMoveResources.getAssestsfromDirectories(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetAssestsfromFileException()  {
		createDirectoriesAndMoveResources.getAssestsfromFile(null);
	}
	
	@Test
	public void testGetAssestsfromFile()  {
		Asset asset = createDirectoriesAndMoveResources.getAssestsfromFile(tempFile);
		assertTrue(asset.getId().contains("pirate"));
		}
	
	
	
	
	@Test
	public void testReadDocImgFile() throws Exception {
		createDirectoriesAndMoveResources.readDocImgFile(docToSplitBookFile,docMap,splitBookImgMap);
		List<Doc> docList = null;
		
		//Doc List
		Iterator<Map.Entry<String, List<Doc>>> itr = docMap.entrySet().iterator();
		 while (itr.hasNext()) {
			 Map.Entry<String, List<Doc>> pair = (Map.Entry<String, List<Doc>>)itr.next();
				
				if (pair.getKey().equals(new String("1"))){
					docList = pair.getValue();
					Assert.assertEquals(docList.size(),5);
				}
		}
		 
		 
		 //Img List
		 List<String> imgList = null;
		 Iterator<Map.Entry<String, List<String>>> it = splitBookImgMap.entrySet().iterator();
		 while (it.hasNext()) {
			 Map.Entry<String, List<String>> pair = (Map.Entry<String, List<String>>)it.next();
			 
			 if (pair.getKey().equals(new String("1"))){
				 imgList = pair.getValue();
					Assert.assertEquals(2,imgList.size());
			 }
			 if (pair.getKey().equals(new String("3"))){
					
					Assert.assertEquals(0,imgList.size());
			 }
			}
	}
	
	

}
