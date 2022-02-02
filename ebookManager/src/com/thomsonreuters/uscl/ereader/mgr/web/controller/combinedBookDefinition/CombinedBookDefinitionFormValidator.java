package com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CombinedBookDefinitionSourceService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("combinedBookDefinitionFormValidator")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CombinedBookDefinitionFormValidator implements Validator {
    static final String ERROR_COMBINED_BOOK_DEFINITION_PRIMARY = "error.combinedbookdefinition.primary";
    static final String ERROR_COMBINED_BOOK_DEFINITION_PRIMARY_EXIST = "error.combinedbookdefinition.primary.exist";
    static final String ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_DUPLICATED = "error.combinedbookdefinition.titleid.duplicated";
    static final String ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_EMPTY = "error.combinedbookdefinition.titleid.empty";
    static final String ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_NOT_EXIST = "error.combinedbookdefinition.titleid.not.exist";
    private final BookDefinitionService bookDefinitionService;
    private final CombinedBookDefinitionSourceService combinedBookDefinitionSourceService;

    @Override
    public boolean supports(Class<?> clazz) {
        return CombinedBookDefinitionForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final CombinedBookDefinitionForm form = (CombinedBookDefinitionForm) obj;
        Set<CombinedBookDefinitionSource> sources = form.getSourcesSet();
        validateDuplicates(errors, sources);
        validateEmptyTitleId(errors, sources);
        validateTitleIdExist(errors, sources);
        validatePrimaryFlag(errors, sources);
    }

    private void validateTitleIdExist(Errors errors, final Set<CombinedBookDefinitionSource> sources) {
        sources.stream()
                .filter(item -> StringUtils.isNotEmpty(item.getBookDefinition().getFullyQualifiedTitleId()))
                .forEach(item -> {
                    BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(item.getBookDefinition().getFullyQualifiedTitleId());
                    if (book == null) {
                        errors.reject(ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_NOT_EXIST, new Object[]{item.getBookDefinition().getFullyQualifiedTitleId()}, StringUtils.EMPTY);
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
            errors.reject(ERROR_COMBINED_BOOK_DEFINITION_TITLE_ID_DUPLICATED, new Object[]{duplicatedTitleIds}, StringUtils.EMPTY);
        }
    }

    private void validatePrimaryFlag(final Errors errors, final Set<CombinedBookDefinitionSource> sources) {
        List<CombinedBookDefinitionSource> primaries = sources.stream()
                .filter(CombinedBookDefinitionSource::isPrimarySource)
                .collect(Collectors.toList());
        if (primaries.size() != 1) {
            errors.reject(ERROR_COMBINED_BOOK_DEFINITION_PRIMARY);
        } else {
            primaries.stream()
                    .findAny()
                    .map(CombinedBookDefinitionSource::getBookDefinition)
                    .map(BookDefinition::getEbookDefinitionId)
                    .flatMap(combinedBookDefinitionSourceService::findPrimarySourceWithBookDefinition)
                    .ifPresent(source -> errors.reject(ERROR_COMBINED_BOOK_DEFINITION_PRIMARY_EXIST));
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
