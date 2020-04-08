INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (4.3, 'Added AUTHOR.USE_COMMA_BEFORE_SUFFIX column.', TO_DATE('04/30/2012', 'MM/DD/YYYY'));

COMMIT;

ALTER TABLE EBOOK.AUTHOR
 ADD (USE_COMMA_BEFORE_SUFFIX  CHAR(1 BYTE));