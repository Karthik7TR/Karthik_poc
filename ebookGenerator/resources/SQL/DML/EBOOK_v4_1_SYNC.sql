INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (4.1, 'Added EBOOK_DEFINITION_LOCK_ID_SEQ sequence.', TO_DATE('04/24/2012', 'MM/DD/YYYY'));

COMMIT;

CREATE SEQUENCE "EBOOK"."EBOOK_DEFINITION_LOCK_ID_SEQ"
 INCREMENT BY 1
 START WITH 1
 NOMAXVALUE
 NOMINVALUE
 CACHE 20
/

EXEC EBOOK.GRANT_PRIVILEGES;