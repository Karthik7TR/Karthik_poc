package com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("combinedBookDefinitionFormValidator")
public class CombinedBookDefinitionFormValidator implements Validator {
    static final String ERROR_COMBINED_BOOK_DEFINITION_PRIMARY = "error.combinedbookdefinition.primary";
    static final String ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_DUPLICATED = "error.combinedbookdefinition.titleid.duplicated";
    static final String ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_EMPTY = "error.combinedbookdefinition.titleid.empty";
    static final String ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_NOT_EXIST = "error.combinedbookdefinition.titleid.not.exist";
    private final BookDefinitionService bookDefinitionService;

    @Autowired
    public CombinedBookDefinitionFormValidator(final BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CombinedBookDefinitionForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final CombinedBookDefinitionForm form = (CombinedBookDefinitionForm) obj;
        Set<CombinedBookDefinitionSource> sources = form.getSourcesSet();
        validatePrimaryFlag(errors, sources);
        validateDuplicates(errors, sources);
        validateEmptyTitleId(errors, sources);
        validateTitleIdExist(errors, sources);
    }

    private void validateTitleIdExist(Errors errors, final Set<CombinedBookDefinitionSource> sources) {
        sources.stream()
                .filter(item -> StringUtils.isNotEmpty(item.getBookDefinition().getFullyQualifiedTitleId()))
                .forEach(item -> {
                    BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(item.getBookDefinition().getFullyQualifiedTitleId());
                    if (book == null) {
                        errors.reject(ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_NOT_EXIST, new Object[]{item.getBookDefinition().getFullyQualifiedTitleId()}, Strings.EMPTY);
                    } else {
                        item.getBookDefinition().setEbookDefinitionId(book.getEbookDefinitionId());
                    }
                });
    }

    private void validateEmptyTitleId(final Errors errors, final Set<CombinedBookDefinitionSource> sources) {
        if (sources.stream()
                .map(item -> item.getBookDefinition().getFullyQualifiedTitleId())
                .anyMatch(StringUtils::isEmpty)) {
            errors.reject(ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_EMPTY);
        }
    }

    private void validateDuplicates(final Errors errors, final Set<CombinedBookDefinitionSource> sources) {
        String duplicatedTitleIds = sources.stream()
                .map(item -> item.getBookDefinition().getFullyQualifiedTitleId())
                .collect(Collectors.collectingAndThen(Collectors.groupingBy(Function.identity(), Collectors.counting()),
                        filterDuplicates()));
        if (StringUtils.isNotEmpty(duplicatedTitleIds)) {
            errors.reject(ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_DUPLICATED, new Object[]{duplicatedTitleIds}, Strings.EMPTY);
        }
    }

    private void validatePrimaryFlag(final Errors errors, final Set<CombinedBookDefinitionSource> sources) {
        if (sources.stream()
                .map(CombinedBookDefinitionSource::isPrimarySource)
                .filter(i -> i)
                .count() != 1) {
            errors.reject(ERROR_COMBINED_BOOK_DEFINITION_PRIMARY);
        }
    }

    @NotNull
    private Function<Map<String, Long>, String> filterDuplicates() {
        return map -> map.entrySet().stream()
                .filter(item -> item.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining());
    }
}
