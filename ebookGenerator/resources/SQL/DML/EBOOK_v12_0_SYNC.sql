INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (12.0, 'Added EBOOK_DEFINITION.USE_RELOAD_CONTENT column', TO_DATE('06/25/2013', 'MM/DD/YYYY'));

ALTER TABLE EBOOK.EBOOK_DEFINITION
 ADD (USE_RELOAD_CONTENT  CHAR(1 BYTE) DEFAULT 'N' NOT NULL);
 
ALTER TABLE EBOOK.EBOOK_AUDIT
 ADD (USE_RELOAD_CONTENT  CHAR(1 BYTE));
 
COMMIT;