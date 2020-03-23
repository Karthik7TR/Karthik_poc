ALTER TABLE "EBOOK_QA".EBOOK_DEFINITION ADD (ELOOSELEAFS_ENABLED CHAR(1 BYTE) DEFAULT 'N' NOT NULL);

ALTER TABLE "EBOOK_QA".EBOOK_AUDIT ADD (ELOOSELEAFS_ENABLED CHAR(1 BYTE) DEFAULT 'N' NOT NULL);

INSERT INTO "EBOOK_QA".SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (32, 'Added column ELOOSELEAFS_ENABLED to tables EBOOK_DEFINITION and EBOOK_AUDIT', TO_DATE('03/18/2020', 'MM/DD/YYYY'));