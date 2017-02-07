ALTER TABLE EBOOK.JOB_REQUEST MODIFY (JOB_STATUS NULL);

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (17.1, 'Allow NULL in JOB_STATUS of JOB_REQUEST.', TO_DATE('02/07/2017', 'MM/DD/YYYY'));

ALTER TABLE EBOOK.JOB_REQUEST DROP COLUMN JOB_STATUS;
ALTER TABLE EBOOK.JOB_REQUEST DROP COLUMN JOB_SCHEDULE_TIMESTAMP;

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (17.2, 'Remove unused JOB_STATUS and JOB_SCHEDULE_TIMESTAMP from JOB_REQUEST.', TO_DATE('02/07/2017', 'MM/DD/YYYY'));

COMMIT;