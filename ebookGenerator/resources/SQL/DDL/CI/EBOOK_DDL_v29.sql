ALTER TABLE EBOOK.EBOOK_DEFINITION ADD (INLINE_TOC_INCLUDED CHAR(1 BYTE) DEFAULT 'N' NOT NULL);

ALTER TABLE EBOOK.EBOOK_AUDIT ADD (INLINE_TOC_INCLUDED CHAR(1 BYTE) DEFAULT 'N' NOT NULL);

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (29, 'Added column INLINE_TOC_INCLUDED to tables EBOOK_DEFINITION and EBOOK_AUDIT', TO_DATE('08/06/2019', 'MM/DD/YYYY'));