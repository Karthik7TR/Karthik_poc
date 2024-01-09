ALTER TABLE "EBOOK_TEST".EBOOK_DEFINITION ADD (PUBLISHED_DATE TIMESTAMP(6));

ALTER TABLE "EBOOK_TEST".EBOOK_AUDIT ADD (PUBLISHED_DATE TIMESTAMP(6));

INSERT INTO "EBOOK_TEST".SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (35, 'Added column PUBLISHED_DATE to tables EBOOK_DEFINITION and EBOOK_AUDIT', TO_DATE('05/20/2020', 'MM/DD/YYYY'));
