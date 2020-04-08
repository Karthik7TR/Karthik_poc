INSERT INTO EBOOK_TEST.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (5.1, 'Added XSLT_MAPPER.LAST_UPDATED column.', TO_DATE('05/14/2012', 'MM/DD/YYYY'));

COMMIT;

ALTER TABLE EBOOK_TEST.XSLT_MAPPER
 ADD (LAST_UPDATED  TIMESTAMP(6) DEFAULT SYSTIMESTAMP NOT NULL);