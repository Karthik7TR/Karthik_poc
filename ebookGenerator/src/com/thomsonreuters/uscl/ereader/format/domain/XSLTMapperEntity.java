package com.thomsonreuters.uscl.ereader.format.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * XSLTMapperEntity is the table entity in the data store from which a look
 * to retrieve the XSLT style sheet will be don.
 *
 * @author Ripu Jain U0115290
 */
@NamedQueries({
    @NamedQuery(
        name = "getXSLT",
        query = "SELECT xsltMapperEntity FROM XSLTMapperEntity xsltMapperEntity WHERE xsltMapperEntity.collection = :collection "
            + "AND xsltMapperEntity.doc_type = :doc_type"),
    @NamedQuery(
        name = "getXSLTWhereDocTypeIsNull",
        query = "SELECT xsltMapperEntity FROM XSLTMapperEntity xsltMapperEntity WHERE xsltMapperEntity.collection = :collection "
            + "AND xsltMapperEntity.doc_type is null")})

@Entity
@Table(name = "XSLT_MAPPER")
public class XSLTMapperEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Collection")
    private String collection;

    @Column(name = "DOC_TYPE")
    private String doc_type;

    @Column(name = "CONTENT_TYPE")
    private String content_type;

    @Column(name = "XSLT")
    private String xslt;

    public String getCollection() {
        return collection;
    }

    public void setCollection(final String collection) {
        this.collection = collection;
    }

    public String getDOC_TYPE() {
        return doc_type;
    }

    public void setDOC_TYPE(final String dOC_TYPE) {
        doc_type = dOC_TYPE;
    }

    public String getCONTENT_TYPE() {
        return content_type;
    }

    public void setCONTENT_TYPE(final String cONTENT_TYPE) {
        content_type = cONTENT_TYPE;
    }

    public String getXSLT() {
        return xslt;
    }

    public void setXSLT(final String xSLT) {
        xslt = xSLT;
    }
}
