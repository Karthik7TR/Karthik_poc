INSERT INTO EBOOK_QA.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (4.0, 'Added EBOOK_DEFINITION_LOCK table.', TO_DATE('04/23/2012', 'MM/DD/YYYY'));

COMMIT;

-- Table EBOOK_DEFINITION_LOCK

CREATE TABLE EBOOK_QA."EBOOK_DEFINITION_LOCK"(
  "EBOOK_DEFINITION_LOCK_ID" Number NOT NULL,
  "EBOOK_DEFINITION_ID" Number NOT NULL,
  "CHECKOUT_TIMESTAMP" Timestamp(6) NOT NULL,
  "USERNAME" Varchar2(512 ) NOT NULL,
  "FULL_NAME" Varchar2(512 ) NOT NULL
)
/

-- Add keys for table EBOOK_DEFINITION_LOCK

ALTER TABLE EBOOK_QA."EBOOK_DEFINITION_LOCK" ADD CONSTRAINT "PK_EBOOK_DEF_LOCK" PRIMARY KEY ("EBOOK_DEFINITION_LOCK_ID")
/

CREATE INDEX EBOOK_QA."IX_EBOOK_DEFINITION_LOCK_FK" ON EBOOK_QA."EBOOK_DEFINITION_LOCK" ("EBOOK_DEFINITION_ID") 
/
ALTER TABLE EBOOK_QA."EBOOK_DEFINITION_LOCK" ADD CONSTRAINT "EBOOK_DEFINITION_LOCK_FK" FOREIGN KEY ("EBOOK_DEFINITION_ID") REFERENCES EBOOK_QA."EBOOK_DEFINITION" ("EBOOK_DEFINITION_ID")
/