package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("codeService")
public class CodeServiceImpl implements CodeService {
    private final CodeDao dao;

    @Autowired
    public CodeServiceImpl(final CodeDao dao) {
        this.dao = dao;
    }

    /**
     * Get all the PubType codes from the PUB_TYPE_CODES table
     * @return a list of pubTypeCode objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<PubTypeCode> getAllPubTypeCodes() {
        return dao.findAllByOrderByNameAsc();
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
     * @param pubTypeCodeId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PubTypeCode getPubTypeCodeById(final Long pubTypeCodeId) {
        return dao.findOne(pubTypeCodeId);
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_NAME
     * @param pubTypeCodeName
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public PubTypeCode getPubTypeCodeByName(final String pubTypeCodeName) {
        return dao.findFirstByNameIgnoreCase(pubTypeCodeName);
    }

    /**
     * Create or Update a PubType Code to the PUB_TYPE_CODES table
     * @param pubTypeCode
     * @return
     */
    @Override
    @Transactional
    public void savePubTypeCode(final PubTypeCode pubTypeCode) {
        dao.save(pubTypeCode);
    }

    /**
     * Delete a PubType Code in the PUB_TYPE_CODES table
     * @param pubTypeCode
     * @return
     */
    @Override
    @Transactional
    public void deletePubTypeCode(final PubTypeCode pubTypeCode) {
        dao.delete(pubTypeCode);
    }
}
