package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeServiceImpl;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionServiceImpl;

public class EditBookDefinitionServiceImplTest {

	private EditBookDefinitionServiceImpl bookService;

	private CodeService mockCodeService;
	private File tempRootDir;

	@Before
	public void setUp() {
		bookService = new EditBookDefinitionServiceImpl();

		mockCodeService = EasyMock.createMock(CodeServiceImpl.class);
		tempRootDir = new File(System.getProperty("java.io.tmpdir") + "\\EvenMoreTemp");
		this.tempRootDir.mkdir();

		bookService.setCodeService(mockCodeService);
		bookService.setRootCodesWorkbenchLandingStrip(tempRootDir);
	}

	@After
	public void tearDown() throws Exception {
		/*
		 * recursively deletes the root directory, and all its subdirectories
		 * and files
		 */
		FileUtils.deleteDirectory(tempRootDir);
	}

	@Test
	public void testGetStates() {

		StateCode code = new StateCode();
		code.setName("aAa");
		List<StateCode> codes = new ArrayList<StateCode>();
		codes.add(code);
		EasyMock.expect(mockCodeService.getAllStateCodes()).andReturn(codes);
		EasyMock.replay(mockCodeService);

		Map<String, String> states = bookService.getStates();
		Assert.assertNotNull(states);
		Assert.assertEquals(1, states.size());
	}

	@Test
	public void testGetJurisdictions() {

		JurisTypeCode code = new JurisTypeCode();
		code.setName("aAa");
		List<JurisTypeCode> codes = new ArrayList<JurisTypeCode>();
		codes.add(code);
		EasyMock.expect(mockCodeService.getAllJurisTypeCodes()).andReturn(codes);
		EasyMock.replay(mockCodeService);

		Map<String, String> juris = bookService.getJurisdictions();
		Assert.assertNotNull(juris);
		Assert.assertEquals(1, juris.size());
	}

	@Test
	public void testGetPubTypes() {

		PubTypeCode code = new PubTypeCode();
		code.setName("aAa");
		List<PubTypeCode> codes = new ArrayList<PubTypeCode>();
		codes.add(code);
		EasyMock.expect(mockCodeService.getAllPubTypeCodes()).andReturn(codes);
		EasyMock.replay(mockCodeService);

		Map<String, String> pubType = bookService.getPubTypes();
		Assert.assertNotNull(pubType);
		Assert.assertEquals(1, pubType.size());
	}

	@Test
	public void testGetPublishers() {

		PublisherCode code = new PublisherCode();
		code.setName("aAa");
		List<PublisherCode> codes = new ArrayList<PublisherCode>();
		codes.add(code);
		EasyMock.expect(mockCodeService.getAllPublisherCodes()).andReturn(codes);
		EasyMock.replay(mockCodeService);

		Map<String, String> pubs = bookService.getPublishers();
		Assert.assertNotNull(pubs);
		Assert.assertEquals(1, pubs.size());
	}

	@Test
	public void testGetCodesWorkbenchDirectory() throws IOException {
		File folder = new File(tempRootDir, "folder");
		folder.mkdir();
		File file = new File(folder, "file");
		file.createNewFile();

		List<String> fileList = bookService.getCodesWorkbenchDirectory(folder.getName());
		Assert.assertEquals(1, fileList.size());

		fileList = bookService.getCodesWorkbenchDirectory("");
		Assert.assertEquals(1, fileList.size());
	}
}
