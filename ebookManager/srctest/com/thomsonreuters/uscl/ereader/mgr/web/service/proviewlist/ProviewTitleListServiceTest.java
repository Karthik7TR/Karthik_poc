package com.thomsonreuters.uscl.ereader.mgr.web.service.proviewlist;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CLEANUP_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.FINAL_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REMOVED_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REVIEW_BOOK_STATUS;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.*;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitlesProvider;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.TitleAction;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.TitleActionResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListServiceImpl;
import java.util.concurrent.Callable;
import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewTitleListServiceTest {
    private static final String ERROR_MESSAGE = "Error message";
    private static final String EMAIL_SUBJECT = "Subject";
    private static final String SUCCESSFULLY_UPDATED = "Successfully updated:";
    private static final String FAILED_TO_UPDATE = "Failed to update:";
    private static final String EMAIL = "user@thomsonreuters.com";
    private static final String _PT = "_pt";
    private static final String TEST_TITLE_NAME = "testTitle";
    private static final String TEST_TITLE_ID = "testId".toLowerCase();
    private static final String TEST_TITLE_SUFFIX_ID = "testIdSuffix".toLowerCase();
    private static final Integer TOTAL_NUMBER_OF_VERSIONS = 1;
    private static final String PERCENT = "%";
    private static final String SUFFIX = "suffix";
    private static final String SUFFIX_2 = "suffix2";
    private static final String SUFFIX_3 = "suffix3";
    private static final String ANY_STATUS = "";

    private ProviewTitleListService proviewTitleListService;
    @Mock
    private BookDefinitionService bookDefinitionService;
    @Mock
    private ProviewAuditService proviewAuditService;
    @Mock
    private ProviewHandler proviewHandler;
    @Mock
    private ProviewTitlesProvider proviewTitlesProvider;
    @Mock
    private EmailUtil emailUtil;
    @Mock
    private EmailServiceImpl emailService;
    @Mock
    private VersionIsbnService versionIsbnService;
    @Mock
    private ProviewTitleForm form;
    @Mock
    private TitleAction titleAction;
    @Mock
    private TitleActionResult titleActionResult;
    @Mock
    private Callable<TitleActionResult> callable;

    private String headTitle;
    private String bookPartTitle1;
    private String bookPartTitle2;
    private String bookPartTitle3;
    private String anotherTitle;
    private Version version;
    private String versionString;
    private String anotherVersion1;
    private String anotherVersion2;
    private String username;
    private Map<String, ProviewTitleContainer> proviewTitleInfo;

    @SneakyThrows
    @Before
    public void setUp() {
        proviewTitleListService = new ProviewTitleListServiceImpl(bookDefinitionService,
                proviewAuditService, proviewHandler, proviewTitlesProvider, emailUtil, emailService,
                "environmentName", versionIsbnService);

        initiateVersions();
        initiateTitleNames();
        setUpProviewTitles();
        setUpForm();
        setUpAuthentication();

        when(emailUtil.getEmailRecipientsByUsername(username))
            .thenReturn(Collections.singletonList(new InternetAddress(EMAIL)));
    }

    @Test(expected = ProviewException.class)
    public void getSelectedProviewTitleInfo_proviewIsDown_proviewExceptionIsThrown() throws ProviewException {
        when(proviewTitlesProvider.provideAll(anyBoolean())).thenThrow(new ProviewException(""));

        proviewTitleListService.getSelectedProviewTitleInfo(new ProviewListFilterForm());
    }

    @Test
    public void getSelectedProviewTitleInfo_formWithRefreshCommandIsSent_allProviewTitlesAreRefreshedFromProview() throws ProviewException {
        final ProviewListFilterForm form = new ProviewListFilterForm();
        form.setCommand(Command.REFRESH);
        final ArgumentCaptor<Boolean> isRefreshCaptor = ArgumentCaptor.forClass(Boolean.class);
        final Map<String, ProviewTitleContainer> allProviewTitleInfo =
                Collections.singletonMap("test", new ProviewTitleContainer());
        when(proviewTitlesProvider.provideAll(isRefreshCaptor.capture())).thenReturn(allProviewTitleInfo);
        final List<ProviewTitleInfo> allLatestProviewTitleInfo = new ArrayList<>();
        final ProviewTitleInfo testInfo = new ProviewTitleInfo();
        testInfo.setTitle("test");
        testInfo.setTitleId("test");
        allLatestProviewTitleInfo.add(testInfo);
        when(proviewTitlesProvider.provideAllLatest()).thenReturn(allLatestProviewTitleInfo);

        final List<ProviewTitleInfo> selectedProviewTitleInfo =
                proviewTitleListService.getSelectedProviewTitleInfo(form);

        assertEquals(Boolean.TRUE, isRefreshCaptor.getValue());
        assertEquals(allLatestProviewTitleInfo, selectedProviewTitleInfo);
    }

    @Test
    public void getSelectedProviewTitleInfo_formWithRefreshCommandIsSent_latestUpdateDateIsSet() throws ProviewException {
        final String lastStatusUpdateDate = "20081001";
        final ProviewListFilterForm form = new ProviewListFilterForm();
        form.setCommand(Command.REFRESH);
        final Map<String, ProviewTitleContainer> allProviewTitleInfo =
                Collections.singletonMap(TEST_TITLE_ID, new ProviewTitleContainer());
        when(proviewTitlesProvider.provideAll(Boolean.TRUE)).thenReturn(allProviewTitleInfo);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2008, Calendar.OCTOBER, 1);
        when(proviewAuditService.findMaxRequestDateByTitleIds(any()))
                .thenReturn(Collections.singletonMap(TEST_TITLE_ID, calendar.getTime()));
        final List<ProviewTitleInfo> allLatestProviewTitleInfo = new ArrayList<>();
        final ProviewTitleInfo testInfo = new ProviewTitleInfo();
        testInfo.setTitle("test");
        testInfo.setTitleId(TEST_TITLE_ID);
        allLatestProviewTitleInfo.add(testInfo);
        when(proviewTitlesProvider.provideAllLatest()).thenReturn(allLatestProviewTitleInfo);

        final List<ProviewTitleInfo> selectedProviewTitleInfo =
                proviewTitleListService.getSelectedProviewTitleInfo(form);

        assertEquals(lastStatusUpdateDate, selectedProviewTitleInfo.get(0).getLastStatusUpdateDate());
    }

    @Test
    public void updateMaterialId_Test() {
        List <ProviewTitleReportInfo> selectedProviewTitleReportInfoList = new ArrayList<ProviewTitleReportInfo> ();
        ProviewTitleReportInfo title1 = new ProviewTitleReportInfo();
        title1.setId("CW/OTS/ZUKER_EN");
        title1.setVersion("v2020.1");
        title1.setMaterialId("Mtrl01");
        title1.setSubMaterialId("SubMtrl01");
        title1.setIsbn("9780314860002");
        title1.setTotalNumberOfVersions(4);

        ProviewTitleReportInfo title2 = new ProviewTitleReportInfo();
        title2.setId("CW/OTS/ZUKER_EN");
        title2.setVersion("v2020.2");

        ProviewTitleReportInfo title3 = new ProviewTitleReportInfo();
        title3.setId("CW/OTS/ZUKER_EN");
        title3.setVersion("v2020.3");

        ProviewTitleReportInfo title4 = new ProviewTitleReportInfo();
        title4.setId("CW/OTS/ZUKER_EN");
        title4.setVersion("v2020.4");

        //Second title
        ProviewTitleReportInfo title5 = new ProviewTitleReportInfo();
        title5.setId("uscl/an/immls2d");
        title5.setVersion("v1.0");
        title5.setMaterialId("42995644");
        title5.setSubMaterialId("42816069");
        title5.setIsbn("9781668720776");
        title5.setTotalNumberOfVersions(3);

        ProviewTitleReportInfo title6 = new ProviewTitleReportInfo();
        title6.setId("uscl/an/immls2d");
        title6.setVersion("v1.1");

        ProviewTitleReportInfo title7 = new ProviewTitleReportInfo();
        title7.setId("uscl/an/immls2d");
        title7.setVersion("v2.0");

        selectedProviewTitleReportInfoList.addAll(Arrays.asList(title1,title2,title3,title4,title5,title6,title7));

        proviewTitleListService.updateMaterialId(selectedProviewTitleReportInfoList);
        assertEquals("Mtrl01", selectedProviewTitleReportInfoList.get(1).getMaterialId());
        assertEquals("Mtrl01", selectedProviewTitleReportInfoList.get(2).getMaterialId());
        assertEquals("Mtrl01", selectedProviewTitleReportInfoList.get(3).getMaterialId());
        assertEquals("SubMtrl01", selectedProviewTitleReportInfoList.get(1).getSubMaterialId());
        assertEquals("9780314860002", selectedProviewTitleReportInfoList.get(1).getIsbn());

        assertEquals("42995644", selectedProviewTitleReportInfoList.get(5).getMaterialId());
        assertNull(selectedProviewTitleReportInfoList.get(6).getMaterialId());

    }

    @Test
    public void getSelectedProviewTitleInfo_filteringParamsStartingWithWildcardAreGiven_titlesFilteredSuccessfully() throws Exception {
        final ProviewListFilterForm form = initFilteringRequest(PERCENT + TEST_TITLE_NAME,
                PERCENT + TEST_TITLE_ID.toUpperCase(), TOTAL_NUMBER_OF_VERSIONS, TOTAL_NUMBER_OF_VERSIONS);
        when(proviewTitlesProvider.provideAll(anyBoolean())).thenReturn(Collections.emptyMap());
        when(proviewTitlesProvider.provideAllLatest())
                .thenReturn(Arrays.asList(getProviewTitleInfo(StringUtils.EMPTY), getProviewTitleInfo(SUFFIX)));

        final List<ProviewTitleInfo> selectedProviewTitleInfo =
                proviewTitleListService.getSelectedProviewTitleInfo(form);

        assertTrue(CollectionUtils.isNotEmpty(selectedProviewTitleInfo));
        assertEquals(1, selectedProviewTitleInfo.size());
        assertEquals(TEST_TITLE_ID, selectedProviewTitleInfo.get(0).getTitleId());
    }

    private ProviewListFilterForm initFilteringRequest(final String proviewDisplayName, final String titleId,
            final Integer minVersionsFilter, final Integer maxVersionsFilter) {
        return initFilteringRequest(proviewDisplayName, titleId, minVersionsFilter, maxVersionsFilter, ANY_STATUS);
    }

    private ProviewListFilterForm initFilteringRequest(final String proviewDisplayName, final String titleId,
            final Integer minVersionsFilter, final Integer maxVersionsFilter, final String status) {
        final ProviewListFilterForm form = new ProviewListFilterForm();
        form.setProviewDisplayName(proviewDisplayName);
        form.setTitleId(titleId);
        form.setMinVersions(minVersionsFilter.toString());
        form.setMaxVersions(maxVersionsFilter.toString());
        form.setStatus(status);
        return form;
    }

    private ProviewTitleInfo getProviewTitleInfo(final String suffix) {
        return getProviewTitleInfo(suffix, FINAL_BOOK_STATUS);
    }

    private ProviewTitleInfo getProviewTitleInfo(final String suffix, final String status) {
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setTitle(TEST_TITLE_NAME + suffix);
        titleInfo.setTitleId(TEST_TITLE_ID + suffix);
        titleInfo.setTotalNumberOfVersions(TOTAL_NUMBER_OF_VERSIONS);
        titleInfo.setStatus(status);
        return titleInfo;
    }

    @Test
    public void getSelectedProviewTitleInfo_filteringParamsEndingWithWildcardAreGiven_titlesFilteredSuccessfully() throws Exception {
        final ProviewListFilterForm form =
                initFilteringRequest(TEST_TITLE_NAME + PERCENT, TEST_TITLE_ID + PERCENT, 0, 2);
        when(proviewTitlesProvider.provideAll(anyBoolean())).thenReturn(Collections.emptyMap());
        when(proviewTitlesProvider.provideAllLatest())
                .thenReturn(Collections.singletonList(getProviewTitleInfo(SUFFIX)));

        final List<ProviewTitleInfo> selectedProviewTitleInfo =
                proviewTitleListService.getSelectedProviewTitleInfo(form);

        assertTrue(CollectionUtils.isNotEmpty(selectedProviewTitleInfo));
        assertEquals(1, selectedProviewTitleInfo.size());
        assertEquals(TEST_TITLE_ID + SUFFIX, selectedProviewTitleInfo.get(0).getTitleId());
    }

    @Test
    public void getSelectedProviewTitleInfo_filteringParamsWithBothWildcardsAreGiven_titlesFilteredSuccessfully() throws Exception {
        final ProviewListFilterForm form = initFilteringRequest(PERCENT + TEST_TITLE_NAME + PERCENT,
                PERCENT + TEST_TITLE_ID + PERCENT, TOTAL_NUMBER_OF_VERSIONS, TOTAL_NUMBER_OF_VERSIONS);
        when(proviewTitlesProvider.provideAll(anyBoolean())).thenReturn(Collections.emptyMap());
        when(proviewTitlesProvider.provideAllLatest())
                .thenReturn(Collections.singletonList(getProviewTitleInfo(SUFFIX)));

        final List<ProviewTitleInfo> selectedProviewTitleInfo =
                proviewTitleListService.getSelectedProviewTitleInfo(form);

        assertTrue(CollectionUtils.isNotEmpty(selectedProviewTitleInfo));
        assertEquals(1, selectedProviewTitleInfo.size());
        assertEquals(TEST_TITLE_ID + SUFFIX, selectedProviewTitleInfo.get(0).getTitleId());
    }

    @Test
    public void getSelectedProviewTitleInfo_filteringParamsWithNoWildcardsAreGiven_titlesFilteredSuccessfully() throws Exception {
        final ProviewListFilterForm form = initFilteringRequest(TEST_TITLE_NAME, TEST_TITLE_ID,
                TOTAL_NUMBER_OF_VERSIONS, TOTAL_NUMBER_OF_VERSIONS);
        when(proviewTitlesProvider.provideAll(anyBoolean())).thenReturn(Collections.emptyMap());
        when(proviewTitlesProvider.provideAllLatest())
                .thenReturn(Arrays.asList(getProviewTitleInfo(StringUtils.EMPTY), getProviewTitleInfo(SUFFIX)));

        final List<ProviewTitleInfo> selectedProviewTitleInfo =
                proviewTitleListService.getSelectedProviewTitleInfo(form);

        assertTrue(CollectionUtils.isNotEmpty(selectedProviewTitleInfo));
        assertEquals(1, selectedProviewTitleInfo.size());
        assertEquals(TEST_TITLE_ID, selectedProviewTitleInfo.get(0).getTitleId());
    }

    @Test
    public void getSelectedProviewTitleInfo_filteringParamsWithReviewStatus_titlesFilteredSuccessfully() throws Exception {
        final ProviewListFilterForm form = initFilteringRequest(null, null,
                TOTAL_NUMBER_OF_VERSIONS, TOTAL_NUMBER_OF_VERSIONS, ProviewStatus.Review.name());
        when(proviewTitlesProvider.provideAll(anyBoolean())).thenReturn(Collections.emptyMap());
        when(proviewTitlesProvider.provideAllLatest())
                .thenReturn(Arrays.asList(
                        getProviewTitleInfo(StringUtils.EMPTY, REVIEW_BOOK_STATUS),
                        getProviewTitleInfo(SUFFIX, REVIEW_BOOK_STATUS),
                        getProviewTitleInfo(SUFFIX_2, FINAL_BOOK_STATUS),
                        getProviewTitleInfo(SUFFIX_3, REMOVED_BOOK_STATUS)
                        ));

        final List<ProviewTitleInfo> selectedProviewTitleInfo =
                proviewTitleListService.getSelectedProviewTitleInfo(form);

        assertTrue(CollectionUtils.isNotEmpty(selectedProviewTitleInfo));
        assertEquals(2, selectedProviewTitleInfo.size());
        assertEquals(TEST_TITLE_ID, selectedProviewTitleInfo.get(0).getTitleId());
        assertEquals(TEST_TITLE_SUFFIX_ID, selectedProviewTitleInfo.get(1).getTitleId());
    }

    @Test
    public void getAllSplitBookTitleIdsOnProviewTest_forPromote() {
        final List<String> splitBookTitleIds = proviewTitleListService.getAllSplitBookTitleIdsOnProview(headTitle,
            version, REVIEW_BOOK_STATUS);

        assertEquals(1, splitBookTitleIds.size());
        assertTrue(splitBookTitleIds.contains(bookPartTitle1));
    }

    @Test
    public void getAllSplitBookTitleIdsOnProviewTest_forRemove() {
        final List<String> splitBookTitleIds = proviewTitleListService.getAllSplitBookTitleIdsOnProview(headTitle,
            version, REVIEW_BOOK_STATUS, FINAL_BOOK_STATUS);

        assertEquals(2, splitBookTitleIds.size());
        assertTrue(splitBookTitleIds.contains(headTitle));
        assertTrue(splitBookTitleIds.contains(bookPartTitle1));
    }

    @Test
    public void getAllSplitBookTitleIdsOnProviewTest_forDelete() {
        final List<String> splitBookTitleIds = proviewTitleListService.getAllSplitBookTitleIdsOnProview(headTitle,
            version, REMOVED_BOOK_STATUS, CLEANUP_BOOK_STATUS);

        splitBookTitleIds.forEach(System.out::println);
        assertEquals(2, splitBookTitleIds.size());
        assertTrue(splitBookTitleIds.contains(bookPartTitle2));
        assertTrue(splitBookTitleIds.contains(bookPartTitle3));
    }

    @SneakyThrows
    @Test
    public void updateTitleStatusesInProviewTest() {
        when(proviewHandler.removeTitle(headTitle, version)).thenReturn(true);
        when(proviewHandler.removeTitle(bookPartTitle1, version))
            .thenThrow(new ProviewException(ERROR_MESSAGE));

        TitleActionResult titleActionResult = proviewTitleListService.updateTitleStatusesInProview(form,
            title -> proviewTitleListService.removeTitleFromProview(form, title),
            FINAL_BOOK_STATUS, REVIEW_BOOK_STATUS);

        List<String> titlesToUpdate = titleActionResult.getTitlesToUpdate();
        assertEquals(1, titlesToUpdate.size());
        assertTrue(titlesToUpdate.contains(bookPartTitle1));
        List<String> updatedTitles = titleActionResult.getUpdatedTitles();
        assertEquals(1, updatedTitles.size());
        assertTrue(updatedTitles.contains(headTitle));
        assertEquals(ERROR_MESSAGE, titleActionResult.getErrorMessage());
    }

    @SneakyThrows
    @Test
    public void promoteTitleOnProviewTest() {
        proviewTitleListService.promoteTitleOnProview(form, headTitle);

        verify(proviewHandler).promoteTitle(headTitle, versionString);
    }

    @SneakyThrows
    @Test
    public void removeTitleOnProviewTest() {
        proviewTitleListService.removeTitleFromProview(form, headTitle);

        verify(proviewHandler).removeTitle(headTitle, version);
    }

    @SneakyThrows
    @Test
    public void deleteTitleOnProviewTest() {
        proviewTitleListService.deleteTitleFromProview(form, headTitle);

        verify(proviewHandler).deleteTitle(headTitle, version);
    }

    @SneakyThrows
    @Test
    public void executeTitleActionTestPartlyUpdated() {
        when(titleActionResult.getTitlesToUpdate()).thenReturn(getTitlesToUpdate());
        when(titleActionResult.getUpdatedTitles()).thenReturn(getUpdatedTitles());
        when(titleActionResult.hasErrorMessage()).thenReturn(true);

        when(titleAction.getAction()).thenReturn(callable);
        when(callable.call()).thenReturn(titleActionResult);
        when(titleAction.getEmailSubjectTemplate()).thenReturn(EMAIL_SUBJECT);
        ArgumentCaptor<NotificationEmail> argumentCaptor = ArgumentCaptor
            .forClass(NotificationEmail.class);

        proviewTitleListService.executeTitleAction(form, titleAction, false);

        verify(emailService).send(argumentCaptor.capture());
        NotificationEmail notificationEmail = argumentCaptor.getValue();
        String emailBody = notificationEmail.getBody();
        assertTrue(emailBody.contains(SUCCESSFULLY_UPDATED));
        assertTrue(emailBody.contains(FAILED_TO_UPDATE));
        verifyTitlesToUpdate(emailBody);
        verifyUpdatedTitles(emailBody);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    public void executeTitleActionTestFailedToUpdate() {
        when(titleActionResult.getTitlesToUpdate()).thenReturn(getTitlesToUpdate());
        when(titleActionResult.getUpdatedTitles()).thenReturn(Collections.EMPTY_LIST);
        when(titleActionResult.hasErrorMessage()).thenReturn(true);

        when(titleAction.getAction()).thenReturn(callable);
        when(callable.call()).thenReturn(titleActionResult);
        when(titleAction.getEmailSubjectTemplate()).thenReturn(EMAIL_SUBJECT);
        ArgumentCaptor<NotificationEmail> argumentCaptor = ArgumentCaptor
            .forClass(NotificationEmail.class);

        proviewTitleListService.executeTitleAction(form, titleAction, false);

        verify(emailService).send(argumentCaptor.capture());
        NotificationEmail notificationEmail = argumentCaptor.getValue();
        String emailBody = notificationEmail.getBody();
        assertTrue(emailBody.contains(FAILED_TO_UPDATE));
        verifyTitlesToUpdate(emailBody);
        assertFalse(emailBody.contains(SUCCESSFULLY_UPDATED));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    public void executeTitleActionTestSimpleBookFailedToUpdate() {
        when(titleActionResult.getTitlesToUpdate()).thenReturn(Collections.singletonList(headTitle));
        when(titleActionResult.getUpdatedTitles()).thenReturn(Collections.EMPTY_LIST);
        when(titleActionResult.hasErrorMessage()).thenReturn(true);

        when(titleAction.getAction()).thenReturn(callable);
        when(callable.call()).thenReturn(titleActionResult);
        when(titleAction.getEmailSubjectTemplate()).thenReturn(EMAIL_SUBJECT);
        ArgumentCaptor<NotificationEmail> argumentCaptor = ArgumentCaptor
            .forClass(NotificationEmail.class);

        proviewTitleListService.executeTitleAction(form, titleAction, false);

        verify(emailService).send(argumentCaptor.capture());
        NotificationEmail notificationEmail = argumentCaptor.getValue();
        String emailBody = notificationEmail.getBody();
        assertFalse(emailBody.contains(SUCCESSFULLY_UPDATED));
        assertFalse(emailBody.contains(FAILED_TO_UPDATE));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    public void executeTitleActionTestFullyUpdated() {
        when(titleActionResult.getTitlesToUpdate()).thenReturn(Collections.EMPTY_LIST);
        when(titleActionResult.getUpdatedTitles()).thenReturn(getUpdatedTitles());

        when(titleAction.getAction()).thenReturn(callable);
        when(callable.call()).thenReturn(titleActionResult);
        when(titleAction.getEmailSubjectTemplate()).thenReturn(EMAIL_SUBJECT);
        ArgumentCaptor<NotificationEmail> argumentCaptor = ArgumentCaptor
            .forClass(NotificationEmail.class);

        proviewTitleListService.executeTitleAction(form, titleAction, false);

        verify(emailService).send(argumentCaptor.capture());
        NotificationEmail notificationEmail = argumentCaptor.getValue();
        String emailBody = notificationEmail.getBody();
        assertFalse(emailBody.contains(SUCCESSFULLY_UPDATED));
        assertFalse(emailBody.contains(FAILED_TO_UPDATE));
    }

    @Test
    public void shouldGetPreviousVersions() throws ProviewException {
        when(proviewTitlesProvider.provideAll(false)).thenReturn(proviewTitleInfo);

        List<String> previousVersions = proviewTitleListService.getPreviousVersions(headTitle);

        assertEquals(3, previousVersions.size());
        assertEquals("v6.0", previousVersions.get(0));
        assertEquals("v4.0", previousVersions.get(1));
        assertEquals("v2.0", previousVersions.get(2));
    }

    private void initiateTitleNames() {
        headTitle = "an/uscl/book_title";
        bookPartTitle1 = headTitle + _PT + 2;
        bookPartTitle2 = headTitle + _PT + 3;
        bookPartTitle3 = headTitle + _PT + 4;
        anotherTitle = "uscl/an/another_book";
    }

    private void initiateVersions() {
        version = new Version("v2.0");
        versionString = version.getFullVersion();
        anotherVersion1 = "v4.0";
        anotherVersion2 = "v6.0";
    }

    @SneakyThrows
    private void setUpProviewTitles() {
        proviewTitleInfo = new HashMap<>();
        setUpHeadTitle();
        setUpPart1();
        setUpPart2();
        setUpPart3();
        setUpAnotherTitle();
        when(proviewHandler.getAllProviewTitleInfo()).thenReturn(proviewTitleInfo);
    }

    private void setUpHeadTitle() {
        final ProviewTitleContainer headTitleContainer = new ProviewTitleContainer();
        addTitleInfo(headTitleContainer, headTitle, versionString, FINAL_BOOK_STATUS);
        addTitleInfo(headTitleContainer, headTitle, anotherVersion1, FINAL_BOOK_STATUS);
        addTitleInfo(headTitleContainer, headTitle, anotherVersion2, REMOVED_BOOK_STATUS);
        proviewTitleInfo.put(headTitle, headTitleContainer);
    }

    private void setUpPart1() {
        final ProviewTitleContainer part1Container = new ProviewTitleContainer();
        addTitleInfo(part1Container, bookPartTitle1, versionString, REVIEW_BOOK_STATUS);
        addTitleInfo(part1Container, bookPartTitle1, anotherVersion1, REVIEW_BOOK_STATUS);
        proviewTitleInfo.put(bookPartTitle1, part1Container);
    }

    private void setUpPart2() {
        final ProviewTitleContainer part2Container = new ProviewTitleContainer();
        addTitleInfo(part2Container, bookPartTitle2, versionString, REMOVED_BOOK_STATUS);
        addTitleInfo(part2Container, bookPartTitle2, anotherVersion1, REMOVED_BOOK_STATUS);
        proviewTitleInfo.put(bookPartTitle2, part2Container);
    }

    private void setUpPart3() {
        final ProviewTitleContainer part3Container = new ProviewTitleContainer();
        addTitleInfo(part3Container, bookPartTitle3, versionString, CLEANUP_BOOK_STATUS);
        proviewTitleInfo.put(bookPartTitle3, part3Container);
    }

    private void setUpAnotherTitle() {
        final ProviewTitleContainer anotherTitleContainer = new ProviewTitleContainer();
        addTitleInfo(anotherTitleContainer, anotherTitle, versionString, REVIEW_BOOK_STATUS);
        addTitleInfo(anotherTitleContainer, anotherTitle, anotherVersion1, FINAL_BOOK_STATUS);
        addTitleInfo(anotherTitleContainer, anotherTitle, anotherVersion2, REMOVED_BOOK_STATUS);
        proviewTitleInfo.put(anotherTitle, anotherTitleContainer);
    }

    private void addTitleInfo(final ProviewTitleContainer titleContainer, final String title, final String version,
        final String status) {
        final ProviewTitleInfo titleInfo = new ProviewTitleInfo();
        titleInfo.setTitleId(title);
        titleInfo.setVersion(version);
        titleInfo.setStatus(status);
        titleContainer.getProviewTitleInfos().add(titleInfo);
    }

    private List<String> getTitlesToUpdate() {
        List<String> titlesToUpdate = new ArrayList<>();
        titlesToUpdate.add(headTitle);
        titlesToUpdate.add(bookPartTitle3);
        return titlesToUpdate;
    }

    private List<String> getUpdatedTitles() {
        List<String> updatedTitles = new ArrayList<>();
        updatedTitles.add(bookPartTitle2);
        updatedTitles.add(bookPartTitle1);
        return updatedTitles;
    }

    private void verifyTitlesToUpdate(String emailBody) {
        String titlesToUpdate = headTitle + System.lineSeparator() + bookPartTitle3;
        assertTrue(emailBody.contains(titlesToUpdate));
    }

    private void verifyUpdatedTitles(String emailBody) {
        String updatedTitles = bookPartTitle1 + System.lineSeparator() + bookPartTitle2;
        assertTrue(emailBody.contains(updatedTitles));
    }

    private void setUpAuthentication() {
        username = "User";
        final Collection<GrantedAuthority> authorities = new HashSet<>();
        final CobaltUser user = new CobaltUser(username, "first", "last", "testing", authorities);
        new UsernamePasswordAuthenticationToken(user, null);
    }

    private void setUpForm() {
        when(form.getTitleId()).thenReturn(bookPartTitle1);
        when(form.getVersion()).thenReturn(versionString);
    }
}
