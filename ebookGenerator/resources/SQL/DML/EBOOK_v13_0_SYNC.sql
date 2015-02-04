INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (13.0, 'Add columns SOURCE_TYPE and CWB_BOOK_NAME in EBOOK_DEFINITION and EBOOK_AUDIT. Create table NORT_FILE_LOCATION.', TO_DATE('11/20/2014', 'MM/DD/YYYY'));

ALTER TABLE EBOOK.EBOOK_DEFINITION
ADD SOURCE_TYPE varchar2(10) DEFAULT 'TOC' NOT NULL;

ALTER TABLE EBOOK.EBOOK_AUDIT
ADD SOURCE_TYPE varchar2(10);

ALTER TABLE EBOOK.EBOOK_DEFINITION
ADD CWB_BOOK_NAME varchar2(1028);

ALTER TABLE EBOOK.EBOOK_AUDIT
ADD CWB_BOOK_NAME varchar2(1028);

UPDATE EBOOK.EBOOK_DEFINITION book
SET book.SOURCE_TYPE = 'TOC'
where book.IS_TOC_FLAG = 'Y';

UPDATE EBOOK.EBOOK_DEFINITION book
SET book.SOURCE_TYPE = 'NORT'
where book.IS_TOC_FLAG = 'N';

--------------------------------------------------------
--------------------------------------------------------

CREATE TABLE EBOOK.NORT_FILE_LOCATION
(
  NORT_FILE_LOCATION_ID  NUMBER               NOT NULL,
  EBOOK_DEFINITION_ID      NUMBER               NOT NULL,
  LOCATION_NAME          VARCHAR2(1024 BYTE) NOT NULL,
  SEQUENCE_NUMBER          NUMBER NOT NULL
);

CREATE INDEX EBOOK.IDX_NORT_FILE_EBOOK_DEF_ID ON EBOOK.NORT_FILE_LOCATION
(EBOOK_DEFINITION_ID);

CREATE UNIQUE INDEX EBOOK.PK_NORT_FILE_LOCATION ON EBOOK.NORT_FILE_LOCATION
(NORT_FILE_LOCATION_ID);


ALTER TABLE EBOOK.NORT_FILE_LOCATION ADD (
  CONSTRAINT PK_NORT_FILE_LOCATION
  PRIMARY KEY
  (NORT_FILE_LOCATION_ID)
  USING INDEX EBOOK.PK_NORT_FILE_LOCATION
  ENABLE VALIDATE);


GRANT SELECT ON EBOOK.NORT_FILE_LOCATION TO EBOOK_READ;

GRANT DELETE, INSERT, SELECT, UPDATE ON EBOOK.NORT_FILE_LOCATION TO EBOOK_USER;

CREATE OR REPLACE SYNONYM EBOOK_USER.NORT_FILE_LOCATION FOR EBOOK.NORT_FILE_LOCATION;

ALTER TABLE EBOOK.EBOOK_AUDIT
ADD NORT_FILE_LOCATION_CONCAT varchar2(2048);

----------------------------------------------------------
----------------------------------------------------------

CREATE SEQUENCE EBOOK.NORT_FILE_LOCATION_ID_SEQ
  START WITH 1
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER;


GRANT SELECT ON EBOOK.NORT_FILE_LOCATION_ID_SEQ TO EBOOK_USER;

CREATE OR REPLACE SYNONYM EBOOK_USER.NORT_FILE_LOCATION_ID_SEQ FOR EBOOK.NORT_FILE_LOCATION_ID_SEQ;

COMMIT;
