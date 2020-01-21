package com.thomsonreuters.uscl.ereader.mgr.web.service.proviewlist;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CLEANUP_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.FINAL_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REMOVED_BOOK_STATUS;
import static com.thomsonreuters.uscl.ereader.core.CoreConstants.REVIEW_BOOK_STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thomsonreuters.uscl.ereader.common.notification.entity.NotificationEmail;
import com.thomsonreuters.uscl.ereader.common.notification.service.EmailServiceImpl;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.TitleAction;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.TitleActionResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleListServiceImpl;
import java.util.concurrent.Callable;
import javax.mail.internet.InternetAddress;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewTitleListServiceTest {
    private static final String ERROR_MESSAGE = "Error message";
    private static final String EMAIL_SUBJECT = "Subject";
    private static final String SUCCESSFULLY_UPDATED = "Successfully updated:";
    private static final String FAILED_TO_UPDATE = "Failed to update:";
    private static final String EMAIL = "user@thomsonreuters.com";
    private static final String _PT = "_pt";

    private ProviewTitleListService proviewTitleListService;

    @Mock
    private BookDefinitionService bookDefinitionService;

    @Mock
    private ProviewHandler proviewHandler;

    @Mock
    private BookTitlesUtil bookTitlesUtil;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private EmailServiceImpl emailService;

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
            bookTitlesUtil, proviewHandler, emailUtil, emailService, "environmentName");

        username = "User";
        setUpAuthentication();

        version = new Version("v2.0");
        versionString = version.getFullVersion();
        anotherVersion1 = "v4.0";
        anotherVersion2 = "v6.0";

        headTitle = "an/uscl/book_title";
        bookPartTitle1 = headTitle + _PT + 2;
        bookPartTitle2 = headTitle + _PT + 3;
        bookPartTitle3 = headTitle + _PT + 4;
        anotherTitle = "uscl/an/another_book";

        setUpProviewTitles();

        when(form.getTitleId()).thenReturn(bookPartTitle1);
        when(form.getVersion()).thenReturn(versionString);

        when(emailUtil.getEmailRecipientsByUsername(username))
            .thenReturn(Collections.singletonList(new InternetAddress(EMAIL)));
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
        when(titleActionResult.getErrorMessage()).thenReturn(ERROR_MESSAGE);

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
        when(titleActionResult.getErrorMessage()).thenReturn(ERROR_MESSAGE);

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
        when(titleActionResult.getErrorMessage()).thenReturn(ERROR_MESSAGE);

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
        final Collection<GrantedAuthority> authorities = new HashSet<>();
        final CobaltUser user = new CobaltUser(username, "first", "last", "testing", authorities);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
    }
}
