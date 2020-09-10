package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Comparator;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.VersionIsbnDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.VersionIsbn;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@NoArgsConstructor
@AllArgsConstructor
@Service("versionIsbnService")
@Slf4j
public class VersionIsbnServiceImpl implements VersionIsbnService {
    @Autowired
    private VersionIsbnDao versionIsbnDao;
    @Autowired
    private BookDefinitionService bookDefinitionService;

    @Transactional
    @Override
    public void saveIsbn(String titleId, String version, String isbn) {
        BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByTitle(titleId);
        if (bookDefinition != null) {
            saveIsbn(bookDefinition, version, isbn);
        }
    }

    @Transactional
    @Override
    public void saveIsbn(final BookDefinition bookDefinition, final String version, final String isbn) {
        log.info("Saved isbn " + isbn + " of " + bookDefinition.getTitleId() + "/" + version);
        VersionIsbn versionIsbn = versionIsbnDao.findDistinctByEbookDefinitionAndVersion(bookDefinition, version);
        if (versionIsbn != null) {
            versionIsbn.setIsbn(isbn);
        } else {
            versionIsbn = new VersionIsbn(bookDefinition, version, isbn);
        }
        versionIsbnDao.save(versionIsbn);
    }

    @Transactional
    @Override
    public void deleteIsbn(final String titleId, final String version) {
        final String headTitleId = new TitleId(titleId).getHeadTitleId();
        log.info("Deleted isbn of " + headTitleId + "/" + version);
        final BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByTitle(headTitleId);
        final String versionWithoutPrefix = new Version(version).getVersionWithoutPrefix();
        final VersionIsbn versionIsbn = versionIsbnDao.findDistinctByEbookDefinitionAndVersion(bookDefinition,
            versionWithoutPrefix);
        if (versionIsbn != null) {
            versionIsbnDao.delete(versionIsbn);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isIsbnExists(final String isbnToFind) {
        final String digitalIsbn = getDigitalIsbn(isbnToFind);
        return versionIsbnDao.findAll().stream()
            .map(VersionIsbn::getIsbn)
            .map(this::getDigitalIsbn)
            .anyMatch(digitalIsbn::equals);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isIsbnChangedFromPreviousGeneration(final BookDefinition bookDefinition, final String currentProviewVersion) {
        return Optional.ofNullable(versionIsbnDao.findDistinctByEbookDefinitionAndVersion(bookDefinition, currentProviewVersion))
            .map(item -> !getDigitalIsbn(bookDefinition.getIsbn()).equals(getDigitalIsbn(item.getIsbn())))
            .orElse(false);
    }

    @Override
    public String getLastIsbnBeforeVersion(final String titleId, final Version maxVersion) {
        BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByTitle(titleId);
        if (bookDefinition != null) {
            Optional<Version> lastVersion = findLastVersionBeforeVersion(bookDefinition, maxVersion);
            if (lastVersion.isPresent()) {
                return versionIsbnDao.findDistinctByEbookDefinitionAndVersion(bookDefinition,
                        lastVersion.get().getVersionWithoutPrefix()).getIsbn();
            }
        }
        return StringUtils.EMPTY;
    }

    @Transactional
    @Override
    public void modifyIsbn(final String titleId, final String isbnToModify) {
        final BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByTitle(titleId);
        final String digitalIsbnToModify = getDigitalIsbn(isbnToModify);
        versionIsbnDao.getAllByEbookDefinition(bookDefinition).stream()
            .filter(versionIsbn -> digitalIsbnToModify.equals(getDigitalIsbn(versionIsbn.getIsbn())))
            .forEach(this::modifyIsbn);
    }

    @Transactional
    @Override
    public void resetIsbn(final String titleId, final String isbnToReset) {
        final BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByTitle(titleId);
        versionIsbnDao.getAllByEbookDefinition(bookDefinition).stream()
            .filter(versionIsbn -> getResetDigitalIsbn(isbnToReset).equals(getResetDigitalIsbn(versionIsbn.getIsbn())))
            .forEach(this::resetIsbn);
    }

    private Optional<Version> findLastVersionBeforeVersion(final BookDefinition bookDefinition, final Version maxVersion) {
        return versionIsbnDao.getAllByEbookDefinition(bookDefinition).stream()
                .map(VersionIsbn::getVersion)
                .map(version -> new Version(Version.VERSION_PREFIX + version))
                .filter(version -> version.compareTo(maxVersion) < 0)
                .max(Comparator.naturalOrder());
    }

    private void modifyIsbn(VersionIsbn versionIsbn) {
        versionIsbn.setIsbn(EbookAuditDao.MODIFY_ISBN_TEXT + versionIsbn.getIsbn());
        versionIsbnDao.save(versionIsbn);
    }

    private void resetIsbn(VersionIsbn versionIsbn) {
        versionIsbn.setIsbn(versionIsbn.getIsbn().replace(EbookAuditDao.MODIFY_ISBN_TEXT, ""));
        versionIsbnDao.save(versionIsbn);
    }

    private String getResetDigitalIsbn(String isbn) {
        return getDigitalIsbn(isbn.replace(EbookAuditDao.MODIFY_ISBN_TEXT, ""));
    }

    private String getDigitalIsbn(final String isbn) {
        return isbn.replace("-", "");
    }
}
