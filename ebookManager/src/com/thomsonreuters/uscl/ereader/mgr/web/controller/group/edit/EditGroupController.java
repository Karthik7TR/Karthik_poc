package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.annotaion.ShowOnException;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId.VERSION_SPLITTER;

@Controller
public class EditGroupController {
    private final BookDefinitionService bookDefinitionService;
    private final GroupService groupService;
    private final EBookAuditService auditService;
    private final Validator validator;

    @Autowired
    public EditGroupController(
        final BookDefinitionService bookDefinitionService,
        final GroupService groupService,
        final EBookAuditService auditService,
        @Qualifier("editGroupDefinitionFormValidator") final Validator validator) {
        this.bookDefinitionService = bookDefinitionService;
        this.groupService = groupService;
        this.auditService = auditService;
        this.validator = validator;
    }

    @InitBinder(EditGroupDefinitionForm.FORM_NAME)
    protected void initDataBinder(final WebDataBinder binder) {
        binder.setAutoGrowNestedPaths(false);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        binder.setValidator(validator);
    }

    /**
     * Handle the in-bound GET to the Group Definition edit view page.
     * @param id the primary key of the book as required query string parameter.
     */
    @RequestMapping(value = WebConstants.MVC_GROUP_DEFINITION_EDIT, method = RequestMethod.GET)
    @ShowOnException(errorViewName = WebConstants.VIEW_ERROR_BOOK_DEFINITION_NOT_FOUND)
    public ModelAndView editGroupDefinitionGet(
        @RequestParam("id") final Long id,
        @ModelAttribute(EditGroupDefinitionForm.FORM_NAME) final EditGroupDefinitionForm form,
        final BindingResult bindingResult,
        final Model model) {
        // Lookup the book by its primary key
        final BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(id);

        // Check if user needs to be shown an error
        if (bookDef != null) {
            // Check if book is soft deleted
            if (bookDef.isDeletedFlag()) {
                return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
            }

            try {
                final String groupId = groupService.getGroupId(bookDef);
                form.setBookDefinitionId(bookDef.getEbookDefinitionId());
                form.setFullyQualifiedTitleId(bookDef.getFullyQualifiedTitleId());
                form.setGroupId(groupId);

                final GroupDefinition group = groupService.getLastGroup(bookDef);
                setupVersion(group, form, model);

                // TODO: Need to refactor to account for types standard, periodicals, ereference.
                form.setGroupType("standard");

                final Map<String, ProviewTitleInfo> proviewTitleMap = getMajorVersionForTitleMap(bookDef.getFullyQualifiedTitleId());
                final Map<String, ProviewTitleInfo> pilotBookMap = groupService.getPilotBooksForGroup(bookDef);
                if (pilotBookMap.size() > 0) {
                    proviewTitleMap.putAll(pilotBookMap);
                }
                if (groupService.getPilotBooksNotFound().size() > 0) {
                    String msg = groupService.getPilotBooksNotFound().toString();
                    msg = msg.replaceAll("\\[|\\]|\\{|\\}", "");
                    model.addAttribute(
                        WebConstants.KEY_WARNING_MESSAGE,
                        "Pilot books are not available in Proview " + Arrays.asList(msg.split("\\s*,\\s*")));
                }

                setupModel(model, bookDef, proviewTitleMap.size());
                form.initialize(bookDef, proviewTitleMap, pilotBookMap, group);
            } catch (final ProviewException ex) {
                setupModel(model, bookDef, 1);
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, WebConstants.ERROR_PROVIEW);
                model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, Arrays.asList(ex.getMessage().split("\\s*,\\s*")));
            } catch (final Exception e) {
                model.addAttribute(WebConstants.KEY_ERR_MESSAGE, WebConstants.ERROR_PROVIEW);
            }
        }

        return new ModelAndView(WebConstants.VIEW_GROUP_DEFINITION_EDIT);
    }

    private void setupVersion(final GroupDefinition group, final EditGroupDefinitionForm form, final Model model) {
        String status = null;
        if (group != null) {
            status = group.getStatus();
        }

        model.addAttribute(WebConstants.KEY_GROUP_STATUS_IN_PROVIEW, status);
        if ("review".equalsIgnoreCase(status)) {
            String formatedVersion = EditGroupDefinitionForm.VersionType.OVERWRITE.toString();
            formatedVersion = formatedVersion.charAt(0) + formatedVersion.substring(1).toLowerCase();
            model.addAttribute(WebConstants.KEY_OVERWRITE_ALLOWED, formatedVersion);
        } else {
            String formatedVersion = EditGroupDefinitionForm.VersionType.MAJOR.toString();
            formatedVersion = formatedVersion.charAt(0) + formatedVersion.substring(1).toLowerCase();
            model.addAttribute(WebConstants.KEY_OVERWRITE_ALLOWED, formatedVersion);
        }
    }

    /**
     * Handle the in-bound POST to the Book Definition edit view page.
     *
     * @param form
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = WebConstants.MVC_GROUP_DEFINITION_EDIT, method = RequestMethod.POST)
    public ModelAndView editGroupDefinitionPost(
            @ModelAttribute(EditGroupDefinitionForm.FORM_NAME) @Valid final EditGroupDefinitionForm form,
            final BindingResult bindingResult,
            final Model model) throws Exception {
        BookDefinition bookDef = null;
        try {
            // Lookup the book by its primary key
            bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(form.getBookDefinitionId());
        } catch (final Exception e) {
            // Error happens when POST of form is over Tomcat post limit. // Default is set at 2 mb.
            // The processed form is empty.
            return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DEFINITION));
        }

        // Check if user needs to be shown an error
        if (bookDef == null) {
            return new ModelAndView(new RedirectView(WebConstants.MVC_ERROR_BOOK_DELETED));
        }

        final Map<String, ProviewTitleInfo> proviewTitleMap = getMajorVersionForTitleMap(bookDef.getFullyQualifiedTitleId());
        if (form.getIncludePilotBook()) {
            final Map<String, ProviewTitleInfo> pilotBookMap = groupService.getPilotBooksForGroup(bookDef);
            proviewTitleMap.putAll(pilotBookMap);
        }

        if (!bindingResult.hasErrors()) {
            try {
                final GroupDefinition lastGroup = groupService.getLastGroup(form.getGroupId());
                Map<String, List<String>> titleIdToPartsMap = Optional.ofNullable(lastGroup)
                        .map(group -> group.getSubGroupInfoList().stream()
                                .flatMap(item -> item.getTitles().stream())
                                .collect(Collectors.groupingBy(item -> item.contains(VERSION_SPLITTER)
                                        ? new BookTitleId(item).getHeadTitleIdWithMajorVersion()
                                        : new TitleId(item).getHeadTitleId())))
                        .orElse(Collections.emptyMap());
                final GroupDefinition groupDefinition = form.createGroupDefinition(proviewTitleMap.values(), titleIdToPartsMap);
                // determine group version
                if (lastGroup != null) {
                    if (lastGroup.getStatus().equalsIgnoreCase(GroupDefinition.REVIEW_STATUS)) {
                        groupDefinition.setGroupVersion(lastGroup.getGroupVersion());
                    } else {
                        groupDefinition.setGroupVersion(lastGroup.getGroupVersion() + 1);
                    }
                } else {
                    // first group being created
                    groupDefinition.setGroupVersion(1L);
                }
                groupService.createGroup(groupDefinition);

                // Update group information in book definition
                bookDef.setGroupName(groupDefinition.getName());
                bookDef.setSubGroupHeading(groupDefinition.getFirstSubgroupHeading());
                bookDef = bookDefinitionService.saveBookDefinition(bookDef);

                // Save in Audit
                final EbookAudit audit = new EbookAudit();
                audit.loadBookDefinition(
                    bookDef,
                    EbookAudit.AUDIT_TYPE.GROUP,
                    UserUtils.getAuthenticatedUserName(),
                    form.getComment());
                auditService.saveEBookAudit(audit);

                // Redirect user
                final String queryString = String.format("?%s=%s", WebConstants.KEY_ID, form.getBookDefinitionId());
                return new ModelAndView(new RedirectView(WebConstants.MVC_BOOK_DEFINITION_VIEW_GET + queryString));
            } catch (final Exception e) {
                model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, e.getMessage());
            }
        }

        final GroupDefinition group = groupService.getLastGroup(bookDef);
        setupVersion(group, form, model);
        setupModel(model, bookDef, proviewTitleMap.size());
        return new ModelAndView(WebConstants.VIEW_GROUP_DEFINITION_EDIT);
    }

    @NotNull
    private Map<String, ProviewTitleInfo> getMajorVersionForTitleMap(final String fullyQualifiedTitleId) throws ProviewException {
        return groupService.getMajorVersionProviewTitles(fullyQualifiedTitleId).stream()
                .collect(Collectors.toMap(info ->
                        new BookTitleId(info.getTitleId(), new Version(info.getVersion())).getTitleIdWithMajorVersion(), Function.identity()));
    }

    private void setupModel(final Model model, final BookDefinition bookDef, final Integer numOfProviewTitles) {
        model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, bookDef);
        model.addAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES, numOfProviewTitles);
    }
}
