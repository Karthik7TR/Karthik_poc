INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.0, 'Added SPLIT_DOCUMENT table. Added EBOOK_AUDIT.SPLIT_DOCUMENT_CONCAT, EBOOK_AUDIT.IS_SPLIT_BOOK, EBOOK_AUDIT.IS_SPLIT_TYPE_AUTO, EBOOK_AUDIT.SPLIT_EBOOK_PARTS
and EBOOK_DEFINITION.IS_SPLIT_BOOK, EBOOK_DEFINITION.IS_SPLIT_TYPE_AUTO, EBOOK_DEFINITION.SPLIT_EBOOK_PARTS columns', TO_DATE('04212015', 'MMDDYYYY'));

COMMIT;

-- Add column to EBOOK.BOOK_DEFINITION

ALTER TABLE EBOOK.EBOOK_DEFINITION ADD (IS_SPLIT_BOOK  CHAR(1 BYTE) DEFAULT 'N' NOT NULL);
ALTER TABLE EBOOK.EBOOK_DEFINITION ADD (IS_SPLIT_TYPE_AUTO  CHAR(1 BYTE) DEFAULT 'Y' NOT NULL);
ALTER TABLE EBOOK.EBOOK_DEFINITION ADD (SPLIT_EBOOK_PARTS  NUMBER  );

ALTER TABLE EBOOK.EBOOK_AUDIT ADD (IS_SPLIT_BOOK  CHAR(1 BYTE));
 ALTER TABLE EBOOK.EBOOK_AUDIT ADD (IS_SPLIT_TYPE_AUTO  CHAR(1 BYTE));
  ALTER TABLE EBOOK.EBOOK_AUDIT ADD (SPLIT_EBOOK_PARTS  NUMBER);

-- Table EBOOK.SPLIT_DOCUMENT

CREATE TABLE "EBOOK"."SPLIT_DOCUMENT"(
  EBOOK_DEFINITION_ID Number NOT NULL,
  TOC_GUID Varchar2(33 ) NOT NULL,
  NOTE Varchar2(512 ) NOT NULL
);


-- Add keys for table EBOOK.SPLIT_DOCUMENT

ALTER TABLE EBOOK.SPLIT_DOCUMENT ADD CONSTRAINT SPLIT_DOCUMENT_PK PRIMARY KEY (TOC_GUID,EBOOK_DEFINITION_ID);


CREATE INDEX EBOOK.IX_SPLIT_DOCUMENT_FK ON EBOOK.SPLIT_DOCUMENT (EBOOK_DEFINITION_ID);


ALTER TABLE EBOOK.SPLIT_DOCUMENT ADD CONSTRAINT SPLIT_DOCUMENT_FK FOREIGN KEY (EBOOK_DEFINITION_ID) REFERENCES EBOOK.EBOOK_DEFINITION (EBOOK_DEFINITION_ID);


ALTER TABLE EBOOK.EBOOK_AUDIT
 ADD (SPLIT_DOCUMENTS_CONCAT  VARCHAR2(2048 BYTE));
 
 GRANT DELETE, INSERT, SELECT, UPDATE ON EBOOK.SPLIT_DOCUMENT TO EBOOK_USER;
 
CREATE OR REPLACE SYNONYM EBOOK_USER.SPLIT_DOCUMENT FOR EBOOK.SPLIT_DOCUMENT;

COMMIT;


-- Map doc to images

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.1, 'Added EBOOK_AUTHORITY.DOC_UUID columns', TO_DATE('05042015', 'MMDDYYYY'));

ALTER TABLE EBOOK_AUTHORITY.IMAGE_METADATA ADD (DOC_UUID Varchar2(36 ));

ALTER TABLE EBOOK_AUTHORITY.IMAGE_METADATA DROP CONSTRAINT IMAGE_METADATA_PK;

update EBOOK_AUTHORITY.IMAGE_METADATA set DOC_UUID='PAST BOOK';

alter table EBOOK_AUTHORITY.IMAGE_METADATA modify DOC_UUID  Varchar2(36 ) NOT NULL;

ALTER TABLE EBOOK_AUTHORITY.IMAGE_METADATA ADD CONSTRAINT IMAGE_METADATA_PK
PRIMARY KEY (JOB_INSTANCE_ID,IMAGE_GUID, DOC_UUID);

--Metadata Authority

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.2, 'Added EBOOK_AUTHORITY.DOC_SIZE,EBOOK_AUTHORITY.SPLIT_BOOK_TITLE_ID columns', TO_DATE('05052015', 'MMDDYYYY'));


ALTER TABLE EBOOK_AUTHORITY.DOCUMENT_METADATA ADD (DOC_SIZE  NUMBER);
ALTER TABLE EBOOK_AUTHORITY.DOCUMENT_METADATA ADD (SPLIT_BOOK_TITLE_ID  VARCHAR2(64));

--Lock

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.3, 'Added EBOOK_DEFINITION.IS_SPLIT_LOCK column', TO_DATE('05072015', 'MMDDYYYY'));

ALTER TABLE EBOOK.EBOOK_DEFINITION ADD (IS_SPLIT_LOCK  CHAR(1 BYTE) DEFAULT 'Y' NOT NULL);

-- Tracking split nodes for notes migration

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.4, 'Added SPLIT_NODE_INFO table.', TO_DATE('08072015', 'MMDDYYYY'));

COMMIT;

CREATE TABLE "EBOOK"."SPLIT_NODE_INFO"(
  EBOOK_DEFINITION_ID Number NOT NULL,
  BOOK_VERSION_SUBMITTED Varchar2(10 ) NOT NULL,
  SPLIT_BOOK_TITLE_ID VARCHAR2(64 BYTE) NOT NULL, 
  SPLIT_NODE_GUID Varchar2(33 ) NOT NULL
);


ALTER TABLE EBOOK.SPLIT_NODE_INFO ADD CONSTRAINT SPLIT_NODE_INFO_PK PRIMARY KEY (EBOOK_DEFINITION_ID,BOOK_VERSION_SUBMITTED,SPLIT_BOOK_TITLE_ID);


CREATE INDEX EBOOK.IX_SPLIT_NODE_INFO_FK ON EBOOK.SPLIT_NODE_INFO (EBOOK_DEFINITION_ID);


ALTER TABLE EBOOK.SPLIT_NODE_INFO ADD CONSTRAINT SPLIT_NODE_INFO_FK FOREIGN KEY (EBOOK_DEFINITION_ID) REFERENCES EBOOK.EBOOK_DEFINITION (EBOOK_DEFINITION_ID);

GRANT DELETE, INSERT, SELECT, UPDATE ON EBOOK.SPLIT_NODE_INFO TO EBOOK_USER;
 
CREATE OR REPLACE SYNONYM EBOOK_USER.SPLIT_NODE_INFO FOR EBOOK.SPLIT_NODE_INFO;

COMMIT;

//
--- Adding group version
INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.5, 'Added PUBLISHING_STATS.GROUP_VERSION columns.', TO_DATE('08/24/2015', 'MM/DD/YYYY'));

ALTER TABLE EBOOK.PUBLISHING_STATS
 ADD (GROUP_VERSION  NUMBER);

COMMIT;

//
--- Adding SubGroup Heading
INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.6, 'Added EBOOK_DEFINITION.SUBGROUP_HEADING,EBOOK_AUDIT.SUBGROUP_HEADING columns.', TO_DATE('09/01/2015', 'MM/DD/YYYY'));

ALTER TABLE EBOOK.EBOOK_DEFINITION
 ADD (SUBGROUP_HEADING  Varchar2(64));
 
ALTER TABLE EBOOK.EBOOK_AUDIT
 ADD (SUBGROUP_HEADING  Varchar2(64));
 
COMMIT;

//
--Adding Threshold value for auto split
INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.7, 'Added DOCUMENT_TYPE_CODES.THRESHOLD_VALUE,DOCUMENT_TYPE_CODES.THRESHOLD_PERCENT columns.', TO_DATE('09/15/2015', 'MM/DD/YYYY'));
ALTER TABLE EBOOK.DOCUMENT_TYPE_CODES ADD (THRESHOLD_VALUE NUMBER DEFAULT 7500 NOT NULL, THRESHOLD_PERCENT NUMBER DEFAULT 10 NOT NULL);

--Adding group name to ebook

INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (14.6, 'Added EBOOK_DEFINITION.GROUP_NAME,EBOOK_AUDIT.GROUP_NAME columns.', TO_DATE('09/16/2015', 'MM/DD/YYYY'));

ALTER TABLE EBOOK.EBOOK_DEFINITION
 ADD (GROUP_NAME  Varchar2(64));
 
ALTER TABLE EBOOK.EBOOK_AUDIT
 ADD (GROUP_NAME  Varchar2(64));

--Adding group name and ID to user preferences
 
INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES ( 14.8, 'Added USER_PREFERENCE.GROUP_NAME_FILTER, USERPREFERENCE.GROUP_ID_FILTER columns.', TO_DATE('04/14/2016', 'MM/DD/YYYY'));

ALTER TABLE "EBOOK"."USER_PREFERENCE"
 ADD (GROUP_NAME_FILTER  VARCHAR2(1024 BYTE),
 	  GROUP_ID_FILTER  VARCHAR2(1024 BYTE));