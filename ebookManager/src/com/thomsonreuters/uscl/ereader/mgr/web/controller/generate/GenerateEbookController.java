package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.StringBool;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.TitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils.SecurityRole;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.form.GenerateHelperService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class GenerateEbookController {
    private static final Logger log = LogManager.getLogger(GenerateEbookController.class);
    private static final String REMOVE_GROUP_WARNING_MESSAGE = "Groups will be removed from ProView for %s";
    private static final String REVIEW_STATUS = "Review";
    private static final String NOT_PUBLISHED = "Not published";

    @Autowired
    private BookDefinitionService bookDefinitionService;
    @Autowired(required = false)
    private ProviewHandler proviewHandler;
    @Autowired
    private GroupService groupService;
    @Autowired(required = false)
    private JobRequestService jobRequestService;
    @Autowired
    private OutageService outageService;
    @Autowired
    private GenerateHelperService generateFormService;
    @Autowired
    private VersionIsbnService versionIsbnService;

    @RequestMapping(value = WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW, method = RequestMethod.GET)
    public ModelAndView generateBulkEbookPreview(@RequestParam("id") final List<Long> id, final Model model) {
        final List<GenerateBulkBooksContainer> booksToGenerate = new ArrayList<>();
        for (final Long bookId : id) {
            final BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(bookId);
            if (book != null) {
                final GenerateBulkBooksContainer bookToGenerate = new GenerateBulkBooksContainer();
                bookToGenerate.setBookId(bookId);
                bookToGenerate.setFullyQualifiedTitleId(book.getFullyQualifiedTitleId());
                bookToGenerate.setProviewDisplayName(book.getProviewDisplayName());
                bookToGenerate.setDeleted(book.isDeletedFlag());

                booksToGenerate.add(bookToGenerate);
            }
        }
        model.addAttribute(WebConstants.KEY_BULK_PUBLISH_LIST, booksToGenerate);
        model.addAttribute(WebConstants.KEY_BULK_PUBLISH_SIZE, booksToGenerate.size());
        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW);
    }

    @RequestMapping(value = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, method = RequestMethod.POST)
    public ModelAndView doPost(
        @ModelAttribute(GenerateBookForm.FORM_NAME) final GenerateBookForm form,
        final HttpSession session,
        final RedirectAttributes redirectAttributes) {
        log.debug(form);

        final String path;
        final String queryString = String.format("?%s=%s", WebConstants.KEY_ID, form.getId());
        final Command command = form.getCommand();

        switch (command) {
        case GENERATE: {
            final String version = generateFormService.getVersion(form);
            final Integer priority = form.isHighPriorityJob() ? 10 : 5;
            final String submittedBy = UserUtils.getAuthenticatedUserName();

            final BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(form.getId());
            final Optional<String> error = generateFormService.getError(book, form);
            if (error.isPresent()) {
                redirectAttributes.addFlashAttribute("errMessage", error.get());
                path = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW;
                break;
            } else {
                jobRequestService.saveQueuedJobRequest(book, version, priority, submittedBy);
                if (!book.getPublishedOnceFlag()) {
                    bookDefinitionService.updatePublishedStatus(book.getEbookDefinitionId(), true);
                }
                final Object[] args = {book.getFullyQualifiedTitleId(), generateFormService.getPriorityLabel(form)};
                redirectAttributes
                    .addFlashAttribute("infoMessage", generateFormService.getMessage("mesg.job.enqueued.success", null, args));
                redirectAttributes
                    .addFlashAttribute(WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS, "disabled=\"disabled\"");
            }

            path = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW;
            break;
        }
        case EDIT: {
            path = WebConstants.MVC_BOOK_DEFINITION_EDIT;
            break;
        }
        case CANCEL: {
            session.setAttribute(WebConstants.KEY_BOOK_GENERATE_CANCEL, "Book generation cancelled");
            path = WebConstants.MVC_BOOK_DEFINITION_VIEW_GET;
            break;
        }
        case GROUP: {
            path = WebConstants.MVC_GROUP_DEFINITION_EDIT;
            break;
        }
        default:
            session.setAttribute(WebConstants.KEY_BOOK_GENERATE_CANCEL, "Book generation cancelled");
            path = WebConstants.MVC_BOOK_DEFINITION_VIEW_GET;
        }
        return new ModelAndView(new RedirectView(path + queryString));
    }

    /**
     *
     * @param id
     * @param form
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView generateEbookPreview(
        @RequestParam("id") final Long id,
        @ModelAttribute(GenerateBookForm.FORM_NAME) final GenerateBookForm form,
        final Model model) throws Exception {
        final BookDefinition book = bookDefinitionService.findBookDefinitionByEbookDefId(id);
        if (book != null) {
            // Redirect to error page if book is marked as deleted
            if (book.isDeletedFlag()) {
                return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
            }

            String cutOffDate = null;
            if (book.getPublishCutoffDate() != null) {
                cutOffDate = new SimpleDateFormat(CoreConstants.DATE_FORMAT_PATTERN)
                    .format(book.getPublishCutoffDate().getTime());
            }
            model.addAttribute(WebConstants.TITLE, book.getProviewDisplayName());
            model.addAttribute(WebConstants.KEY_ISBN, book.getIsbn());
            model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);
            model.addAttribute(WebConstants.KEY_PUBLISHING_CUT_OFF_DATE, cutOffDate);
            model.addAttribute(
                WebConstants.KEY_USE_PUBLISHING_CUT_OFF_DATE,
                StringBool.toString(book.getDocumentTypeCodes().getUsePublishCutoffDateFlag()));
            model.addAttribute(
                WebConstants.KEY_PUBLISHING_CUTOFF_DATE_GREATER_THAN_TODAY,
                StringBool.toString(isCutOffDateGreaterThanToday(book.getPublishCutoffDate())));

            model.addAttribute(WebConstants.KEY_IS_COMPLETE, book.getEbookDefinitionCompleteFlag());
            model.addAttribute(WebConstants.KEY_PILOT_BOOK_STATUS, book.getPilotBookStatus());

            form.setFullyQualifiedTitleId(book.getFullyQualifiedTitleId());
            setModelVersion(model, form, book);

            if (StringUtils.isNotBlank(form.getNewMajorVersion())) {
                setModelGroup(book, model, form);
            }
            setModelIsbn(book, model);
        }
        final SecurityRole[] roles =
            {SecurityRole.ROLE_PUBLISHER, SecurityRole.ROLE_SUPERUSER, SecurityRole.ROLE_PUBLISHER_PLUS};
        if (!model.containsAttribute(WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS)) {
            model.addAttribute(
                WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS,
                UserUtils.isUserInRole(roles) ? "" : "disabled=\"disabled\"");
        }
        final SecurityRole[] superUserPublisherPlusRoles = {SecurityRole.ROLE_SUPERUSER, SecurityRole.ROLE_PUBLISHER_PLUS};
        model.addAttribute(
            WebConstants.KEY_SUPER_PUBLISHER_PLUS,
            UserUtils.isUserInRole(superUserPublisherPlusRoles) ? "" : "disabled=\"disabled\"");

        model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE, outageService.getAllPlannedOutagesToDisplay());
        return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_PREVIEW);
    }

    /**
     *
     * @param model
     * @param form
     * @param latestVersion
     * @param status
     */
    private void calculateVersionNumbers(
        final Model model,
        final GenerateBookForm form,
        String latestVersion,
        final String status) {
        final String newMajorVersion;
        final String newMinorVersion;
        final String newOverwriteVersion;
        final String majorPart;
        final String minorPart;
        final BigInteger newMajorPartInteger;
        final BigInteger newMinorPartInteger;

        if (latestVersion == null) {
            newMajorVersion = "1.0";
            newMinorVersion = "1.0";
            newOverwriteVersion = "1.0";
        } else {
            if (latestVersion.startsWith("v")) {
                latestVersion = latestVersion.substring(1);
            }

            if (latestVersion.contains(".")) {
                majorPart = latestVersion.substring(0, latestVersion.indexOf("."));
                minorPart = latestVersion.substring(latestVersion.indexOf(".") + 1);

                newMajorPartInteger = new BigInteger(majorPart).add(BigInteger.ONE);
                newMinorPartInteger = new BigInteger(minorPart).add(BigInteger.ONE);
            } else {
                majorPart = latestVersion;

                newMajorPartInteger = new BigInteger(majorPart).add(BigInteger.ONE);
                newMinorPartInteger = BigInteger.ONE;
            }

            newMajorVersion = newMajorPartInteger.toString() + ".0";
            newMinorVersion = majorPart + "." + newMinorPartInteger.toString();
            newOverwriteVersion = latestVersion;
        }

        form.setNewOverwriteVersion(newOverwriteVersion);
        form.setNewMajorVersion(newMajorVersion);
        form.setNewMinorVersion(newMinorVersion);

        model.addAttribute(WebConstants.KEY_NEW_OVERWRITE_VERSION_NUMBER, newOverwriteVersion);
        model.addAttribute(WebConstants.KEY_NEW_MAJOR_VERSION_NUMBER, newMajorVersion);
        model.addAttribute(WebConstants.KEY_NEW_MINOR_VERSION_NUMBER, newMinorVersion);
        model.addAttribute(WebConstants.BOOK_STATUS_IN_PROVIEW, status);
        model.addAttribute(WebConstants.KEY_OVERWRITE_ALLOWED, REVIEW_STATUS.equals(status) ? "Y" : "N");

        model.addAttribute(GenerateBookForm.FORM_NAME, form);
    }

    /**
     *
     * @param model
     * @param form
     * @param book
     * @throws Exception
     */
    private void setModelVersion(final Model model, final GenerateBookForm form, final BookDefinition book) {
        try {
            final String titleId = book.getFullyQualifiedTitleId();
            final String latestProviewVersion = getLatestProviewVersion(proviewHandler.getLatestProviewTitleInfo(titleId));
            setIsIsbnChanged(model, book, latestProviewVersion);
            final ProviewTitleInfo publishedProviewTitleInfo = proviewHandler.getLatestPublishedProviewTitleInfo(titleId);
            setCurrentVersion(model, form, publishedProviewTitleInfo);
            calculateVersionNumbers(model, form, latestProviewVersion, getStatus(publishedProviewTitleInfo));
        } catch (final ProviewException e) {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
                "Proview Exception occured. Please contact your administrator.");
            log.debug(e);
        }
    }

    private void setCurrentVersion(final Model model, final GenerateBookForm form,
        final ProviewTitleInfo publishedProviewTitleInfo) {
        final String currentVersion = getCurrentVersion(publishedProviewTitleInfo);
        form.setCurrentVersion(currentVersion);
        model.addAttribute(WebConstants.KEY_VERSION_NUMBER, currentVersion);
        model.addAttribute(WebConstants.KEY_IS_PUBLISHED, publishedProviewTitleInfo != null);
    }

    private String getLatestProviewVersion(final ProviewTitleInfo latestProviewTitleInfo) {
        return Optional.ofNullable(latestProviewTitleInfo)
                .map(TitleInfo::getVersion)
                .orElse(null);
    }

    private String getCurrentVersion(final ProviewTitleInfo publishedProviewTitleInfo) {
        return Optional.ofNullable(publishedProviewTitleInfo)
                .map(titleInfo -> new Version(titleInfo.getVersion()).getVersionWithoutPrefix())
                .orElse(NOT_PUBLISHED);
    }

    private String getStatus(final ProviewTitleInfo publishedProviewTitleInfo) {
        return Optional.ofNullable(publishedProviewTitleInfo)
                .map(TitleInfo::getStatus)
                .orElse(null);
    }

    private void setIsIsbnChanged(final Model model, final BookDefinition book, final String latestProviewTitleInfoVersion) {
        if (latestProviewTitleInfoVersion != null) {
            boolean isIsbnChanged = versionIsbnService.isIsbnChangedFromPreviousGeneration(book,
                    new Version(latestProviewTitleInfoVersion).getVersionWithoutPrefix());
            model.addAttribute(WebConstants.KEY_ISBN_CHANGED, isIsbnChanged);
        }
    }

    private Integer getMajorVersion(final String versionStr) {
        final Double valueDouble = Double.valueOf(versionStr);
        return valueDouble.intValue();
    }

    private void setModelGroup(final BookDefinition book, final Model model, final GenerateBookForm form) {
        final Integer currentVersion = getMajorVersion(form.getNewMinorVersion());
        final Integer nextVersion = getMajorVersion(form.getNewMajorVersion());

        GroupDefinition currentGroup = null;
        GroupDefinition nextGroup = null;
        try {
            // Setup next groups
            if (StringUtils.isNotBlank(book.getGroupName())) {
                List<String> splitTitles = createSplitTitles(book);
                currentGroup = groupService
                    .createGroupDefinition(book, GroupDefinition.VERSION_NUMBER_PREFIX + currentVersion, splitTitles);
                splitTitles = createSplitTitles(book);
                nextGroup = groupService
                    .createGroupDefinition(book, GroupDefinition.VERSION_NUMBER_PREFIX + nextVersion, splitTitles);
            }

            final GroupDefinition lastGroupDefinition = groupService.getLastGroup(book);
            if (lastGroupDefinition != null) {
                if (lastGroupDefinition.subgroupExists()) {
                    if (StringUtils.isNotBlank(book.getGroupName()) && StringUtils.isBlank(book.getSubGroupHeading())) {
                        model.addAttribute(
                            WebConstants.KEY_WARNING_MESSAGE,
                            "Previous group in ProView had subgroup(s). Currently, book definition is setup to have no subgroup.");
                    } else if (StringUtils.isBlank(book.getGroupName())) {
                        model.addAttribute(
                            WebConstants.KEY_WARNING_MESSAGE,
                            String.format(REMOVE_GROUP_WARNING_MESSAGE, book.getFullyQualifiedTitleId()));
                    }
                } else {
                    if (StringUtils.isBlank(book.getGroupName())) {
                        model.addAttribute(
                            WebConstants.KEY_WARNING_MESSAGE,
                            String.format(REMOVE_GROUP_WARNING_MESSAGE, book.getFullyQualifiedTitleId()));
                    }
                }
                // If groups are similar then new group will not be created
                if (nextGroup.isSimilarGroup(lastGroupDefinition)) {
                    nextGroup.setGroupVersion(lastGroupDefinition.getGroupVersion());
                }
                if (currentGroup.isSimilarGroup(lastGroupDefinition)) {
                    currentGroup.setGroupVersion(lastGroupDefinition.getGroupVersion());
                }
            }
        } catch (final ProviewException e) {
            final String errorMessage = e.getMessage();
            if (errorMessage.equalsIgnoreCase(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE)) {
                // If published, check for SubGroup Heading change
                model.addAttribute(WebConstants.KEY_GROUP_NEXT_ERROR, e.getMessage());
            } else if (errorMessage.equalsIgnoreCase(CoreConstants.SUBGROUP_ERROR_MESSAGE)) {
                if (currentGroup != null && nextGroup == null) {
                    model.addAttribute(WebConstants.KEY_GROUP_NEXT_ERROR, e.getMessage());
                } else {
                    model.addAttribute(WebConstants.KEY_ERR_MESSAGE, e.getMessage());
                }
            }
            log.debug(e);
        } catch (final Exception e) {
            model.addAttribute(WebConstants.KEY_ERR_MESSAGE, e.getMessage());
            log.debug(e);
        }

        model.addAttribute(WebConstants.KEY_GROUP_CURRENT_PREVIEW, currentGroup);
        model.addAttribute(WebConstants.KEY_GROUP_NEXT_PREVIEW, nextGroup);
    }

    private List<String> createSplitTitles(final BookDefinition book) {
        List<String> splitTitles = null;
        if (book.isSplitBook()) {
            splitTitles = new ArrayList<>();
            if (book.isSplitTypeAuto()) {
                splitTitles.add("Auto Split");
            } else {
                final int count = book.getSplitDocuments().size();
                final String titleId = book.getFullyQualifiedTitleId();
                splitTitles.add(titleId);
                for (int i = 0; i < count; i++) {
                    final int part = i + 2;
                    final String nextTitleId = titleId + "_pt" + part;
                    splitTitles.add(nextTitleId);
                }
            }
        }

        return splitTitles;
    }

    /**
     *
     * @param book
     * @param model
     */
    private void setModelIsbn(final BookDefinition book, final Model model) {
        final boolean isPublished = versionIsbnService.isIsbnExists(book.getIsbn());
        // If published, ISBN is not new
        model.addAttribute(WebConstants.KEY_IS_NEW_ISBN, StringBool.toString(!isPublished));
    }

    /**
     *
     * @param cutOffDate
     * @return
     */
    private boolean isCutOffDateGreaterThanToday(final Date cutOffDate) {
        final Date today = new DateTime().toDateMidnight().toDate();
        return cutOffDate != null && cutOffDate.after(today);
    }
}
