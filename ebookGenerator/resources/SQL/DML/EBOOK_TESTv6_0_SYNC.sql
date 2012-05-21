INSERT INTO EBOOK_TEST.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (6.0, 'Added USER_PREFERENCE table.', TO_DATE('05/21/2012', 'MM/DD/YYYY'));

COMMIT;

-- Table EBOOK_TEST.USER_PREFERENCE

CREATE TABLE "EBOOK_TEST"."USER_PREFERENCE"(
  "USER_NAME" Varchar2(1024 ) NOT NULL,
  "EMAIL_LIST" Varchar2(2048 ),
  "LIBRARY_PROVIEW_NAME_FILTER" Varchar2(1024 ),
  "LIBRARY_TITLE_ID_FILTER" Varchar2(1024 ),
  "AUDIT_PROVIEW_NAME_FILTER" Varchar2(1024 ),
  "AUDIT_TITLE_ID_FILTER" Varchar2(1024 ),
  "JOB_SUM_PROVIEW_NAME_FILTER" Varchar2(1024 ),
  "JOB_SUM_TITLE_ID_FILTER" Varchar2(1024 ),
  "START_PAGE" Varchar2(64 ),
  "LAST_UPDATED" Timestamp(6) NOT NULL
)
/

-- Add keys for table EBOOK_TEST.USER_PREFERENCE

ALTER TABLE "EBOOK_TEST"."USER_PREFERENCE" ADD CONSTRAINT "PK_USER_PREFERENCE" PRIMARY KEY ("USER_NAME")
/

EXEC EBOOK_TEST.GRANT_PRIVILEGES;

COMMIT;