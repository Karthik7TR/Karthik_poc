ALTER TABLE EBOOK_QA.PRINT_COMPONENT RENAME COLUMN "ORDER" TO COMPONENT_ORDER;
ALTER TABLE EBOOK_QA.PRINT_COMPONENT ADD EBOOK_DEFINITION_ID Number NOT NULL;
DROP SEQUENCE EBOOK_QA.PRINT_COMPONENT_ID_SEQ;

ALTER TABLE EBOOK_QA.PRINT_COMPONENT ADD CONSTRAINT PRINT_COMPONENT_FK1 FOREIGN KEY ("EBOOK_DEFINITION_ID") REFERENCES EBOOK.EBOOK_DEFINITION ("EBOOK_DEFINITION_ID");

INSERT INTO EBOOK_QA.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (17.0, 'Talbe PRINT_COMPONENT: added column EBOOK_DEFINITION_ID, renamed column "ORDER" to "COMPONENT_ORDER". Link tables EBOOK_DEFINITION and PRINT_COMPONENT in relation OneToMany. Removed PRINT_COMPONENT_ID_SEQ.', 
        TO_DATE('04/21/2017', 'MM/DD/YYYY'));

COMMIT;

INSERT INTO DOCUMENT_TYPE_CODES
(
    DOCUMENT_TYPE_CODES_ID,
    DOCUMENT_TYPE_CODES_ABBRV,
    DOCUMENT_TYPE_CODES_NAME,
    USE_PUBLISH_CUTOFF_DATE_FLAG,
    THRESHOLD_VALUE,
    THRESHOLD_PERCENT,
    PUBLISHER_CODES_ID
)
VALUES
(5,'ots','One Time Sale','N', 7500, 10, 2);
COMMIT;