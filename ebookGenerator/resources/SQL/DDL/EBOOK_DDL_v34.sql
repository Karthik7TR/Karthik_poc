INSERT INTO EBOOK.PUBLISHER_CODES (PUBLISHER_CODES_ID, PUBLISHER_NAME)
VALUES (2, 'cw');

ALTER TABLE EBOOK.DOCUMENT_TYPE_CODES ADD (PUBLISHER_CODES_ID NUMBER);

ALTER TABLE EBOOK.DOCUMENT_TYPE_CODES ADD FOREIGN KEY (PUBLISHER_CODES_ID)
REFERENCES PUBLISHER_CODES(PUBLISHER_CODES_ID);

UPDATE EBOOK.DOCUMENT_TYPE_CODES
SET PUBLISHER_CODES_ID = 1;

ALTER TABLE EBOOK.DOCUMENT_TYPE_CODES
MODIFY (PUBLISHER_CODES_ID NUMBER NOT NULL);

INSERT INTO EBOOK.DOCUMENT_TYPE_CODES (DOCUMENT_TYPE_CODES_ID, DOCUMENT_TYPE_CODES_ABBRV,
DOCUMENT_TYPE_CODES_NAME, PUBLISHER_CODES_ID, THRESHOLD_VALUE, THRESHOLD_PERCENT, USE_PUBLISH_CUTOFF_DATE_FLAG)
VALUES (4, 'eg', 'Evergreen', 2, 7500, 10, 'Y');

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (34, 'Added publisher cw and document type codes ots and eg', TO_DATE('04/27/2020', 'MM/DD/YYYY'));