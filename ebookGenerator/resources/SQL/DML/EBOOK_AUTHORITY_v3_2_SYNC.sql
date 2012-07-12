INSERT INTO EBOOK_AUTHORITY.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (3.2, 'Increased DOCUMENT_METADATA.NORMALIZED_FIRSTLINE_CITE from Varchar(100) to Varchar2(256). Increased DOCUMENT_METADATA.FIRSTLINE_CITE from Varchar(128) to Varchar2(256). Increased DOCUMENT_METADATA.SECONDLINE_CITE from Varchar(128) to Varchar2(256). Added DOCUMENT_METADATA.THIRDLINE_CITE column.', TO_DATE('07/11/2012', 'MM/DD/YYYY'));

COMMIT;

ALTER TABLE EBOOK_AUTHORITY.DOCUMENT_METADATA
MODIFY(NORMALIZED_FIRSTLINE_CITE VARCHAR2(256 BYTE));

ALTER TABLE EBOOK_AUTHORITY.DOCUMENT_METADATA
MODIFY(FIRSTLINE_CITE VARCHAR2(256 BYTE));

ALTER TABLE EBOOK_AUTHORITY.DOCUMENT_METADATA
MODIFY(SECONDLINE_CITE VARCHAR2(256 BYTE));

ALTER TABLE EBOOK_AUTHORITY.DOCUMENT_METADATA
 ADD (THIRDLINE_CITE  VARCHAR2(512 BYTE));