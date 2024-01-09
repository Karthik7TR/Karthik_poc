ALTER TABLE EBOOK.EBOOK_DEFINITION ADD (PRINT_PAGE_NUMBERS CHAR(1 BYTE) DEFAULT 'N');

ALTER TABLE EBOOK.EBOOK_AUDIT ADD (PRINT_PAGE_NUMBERS CHAR(1 BYTE) DEFAULT 'N');

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (28, 'Added clumn PRINT_PAGE_NUMBERS to tables EBOOK_DEFINITION and EBOOK_AUDIT', TO_DATE('07/02/2019', 'MM/DD/YYYY'));