package com.thomsonreuters.uscl.ereader.format.step;

public class DocumentInfo {
    private Long docSize;
    private String splitTitleId;

    public Long getDocSize() {
        return docSize;
    }

    public void setDocSize(final Long docSize) {
        this.docSize = docSize;
    }

    public String getSplitTitleId() {
        return splitTitleId;
    }

    public void setSplitTitleId(final String splitTitleId) {
        this.splitTitleId = splitTitleId;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("docSize=[").append(docSize).append("] ");
        buffer.append("splitTitleId=[").append(splitTitleId).append("] ");
        return buffer.toString();
    }
}
