ALTER TABLE EBOOK.PROVIEW_AUDIT
MODIFY(TITLE_ID VARCHAR2(100 BYTE));

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (16, 'Updated PROVIEW_AUDIT.TITLE_ID from Varchar2(40) to Varchar2(100).', TO_DATE('11/28/2016', 'MM/DD/YYYY'));

COMMIT;