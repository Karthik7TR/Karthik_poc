INSERT INTO EBOOK_TEST.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (8.0, 'Added SUPPORT_PAGE_LINK table.', TO_DATE('06/12/2012', 'MM/DD/YYYY'));

COMMIT;

-- Table EBOOK_TEST.SUPPORT_PAGE_LINK

CREATE TABLE "EBOOK_TEST"."SUPPORT_PAGE_LINK"(
  "SUPPORT_LINK_ID" Number NOT NULL,
  "LINK_DESCRIPTION" Varchar2(512 ) NOT NULL,
  "LINK_ADDRESS" Varchar2(1024 ) NOT NULL,
  "LAST_UPDATED" Timestamp(6) DEFAULT SYSTIMESTAMP NOT NULL
)
/

-- Add keys for table EBOOK_TEST.SUPPORT_PAGE_LINK

ALTER TABLE "EBOOK_TEST"."SUPPORT_PAGE_LINK" ADD CONSTRAINT "PK_SUPPORT_PAGE_LINK" PRIMARY KEY ("SUPPORT_LINK_ID")
/

CREATE SEQUENCE "EBOOK_TEST"."SUPPORT_LINK_ID_SEQ"
 INCREMENT BY 1
 START WITH 1
 NOMAXVALUE
 NOMINVALUE
 CACHE 20
/

ALTER TABLE EBOOK_TEST.EBOOK_DEFINITION
 ADD (INCLUDE_ANNOTATIONS  CHAR(1 BYTE)             DEFAULT 'N'                   NOT NULL);
 
ALTER TABLE EBOOK_TEST.EBOOK_AUDIT
 ADD (INCLUDE_ANNOTATIONS  CHAR(1 BYTE));

exec EBOOK_TEST.GRANT_PRIVILEGES;

COMMIT;