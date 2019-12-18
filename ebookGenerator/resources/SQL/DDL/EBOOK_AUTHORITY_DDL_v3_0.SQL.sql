--843012:Update Generator for CWB Reorder Changes
--QA
INSERT INTO EBOOK_QA.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (15.0, 'Alter EBOOK_AUTHORITY_QA.DOCUMENT_METADATA and EBOOK_AUTHORITY_QA.IMAGE_METADATA DOC_UUID columns', TO_DATE('01272016', 'MMDDYYYY'));

commit;
alter table EBOOK_AUTHORITY_QA.DOCUMENT_METADATA modify DOC_UUID  Varchar2(40);
alter table EBOOK_AUTHORITY_QA.IMAGE_METADATA modify DOC_UUID  Varchar2(40);

--TEST
alter table EBOOK_AUTHORITY_TEST.DOCUMENT_METADATA modify DOC_UUID  Varchar2(40);
alter table EBOOK_AUTHORITY_TEST.IMAGE_METADATA modify DOC_UUID  Varchar2(40);

--PROD

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (15.0, 'Alter EBOOK_AUTHORITY.DOCUMENT_METADATA and EBOOK_AUTHORITY.IMAGE_METADATA DOC_UUID columns', TO_DATE('01272016', 'MMDDYYYY'));

commit;

alter table EBOOK_AUTHORITY.DOCUMENT_METADATA modify DOC_UUID  Varchar2(40);
alter table EBOOK_AUTHORITY.IMAGE_METADATA modify DOC_UUID  Varchar2(40);