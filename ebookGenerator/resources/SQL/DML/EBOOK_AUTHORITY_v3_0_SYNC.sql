INSERT INTO EBOOK_AUTHORITY.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (3.0, 'Added PACE_METADATA table.', TO_DATE('05/08/2012', 'MM/DD/YYYY'));

COMMIT;

-- Table EBOOK_AUTHORITY.PACE_METADATA

CREATE TABLE "EBOOK_AUTHORITY"."PACE_METADATA"(
  "PUBLICATION_ID" Number NOT NULL,
  "PUBLICATION_CODE" Number NOT NULL,
  "PRIMARY_CATEGORY" Varchar2(2 ),
  "SECONDARY_CATEGORY" Varchar2(2 ),
  "SPECIFIC_CATEGORY" Varchar2(2 ),
  "AUTHORITY_NAME" Varchar2(100 ),
  "PUBLICATION_NAME" Varchar2(255 ) NOT NULL,
  "WESTPUB_FLAG" Varchar2(1 ),
  "STD_PUB_NAME" Varchar2(255 ),
  "LONG_PUB_NAME" Varchar2(500 ),
  "TYPE" Varchar2(30 ) NOT NULL,
  "AUDIT_ID" Number,
  "ACTIVE" Char(1 ) NOT NULL
)
/

-- Add keys for table EBOOK_AUTHORITY.PACE_METADATA

ALTER TABLE "EBOOK_AUTHORITY"."PACE_METADATA" ADD CONSTRAINT "PK_PACE_METADATA" PRIMARY KEY ("PUBLICATION_ID")
/

EXEC EBOOK_AUTHORITY.GRANT_PRIVILEGES;

COMMIT;