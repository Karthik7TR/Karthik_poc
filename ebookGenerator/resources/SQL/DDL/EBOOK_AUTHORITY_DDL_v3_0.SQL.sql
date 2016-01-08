--843012:Update Generator for CWB Reorder Changes
--QA
alter table EBOOK_AUTHORITY_QA.DOCUMENT_METADATA modify DOC_UUID  Varchar2(40);
alter table EBOOK_AUTHORITY_QA.IMAGE_METADATA modify DOC_UUID  Varchar2(40);

--TEST
alter table EBOOK_AUTHORITY_TEST.DOCUMENT_METADATA modify DOC_UUID  Varchar2(40);
alter table EBOOK_AUTHORITY_TEST.IMAGE_METADATA modify DOC_UUID  Varchar2(40);

--PROD
alter table EBOOK_AUTHORITY.DOCUMENT_METADATA modify DOC_UUID  Varchar2(40);
alter table EBOOK_AUTHORITY.IMAGE_METADATA modify DOC_UUID  Varchar2(40);
