-- in EBOOK_AUTHORITY schema
CREATE TABLE EBOOK_AUTHORITY.CANADIAN_TOPIC_CODE (
	TITLE_ID VARCHAR2(64),
	JOB_INSTANCE_ID NUMBER,
	DOC_UUID VARCHAR2(36),
    TOPIC_KEY VARCHAR2(36),
	CANADIAN_TOPIC_CODE_ID NUMBER,
    CONSTRAINT CANADIAN_TOPIC_CODE_PK PRIMARY KEY (CANADIAN_TOPIC_CODE_ID)
);
CREATE SEQUENCE EBOOK_AUTHORITY.CANADIAN_TOPIC_CODE_ID_SEQ
 INCREMENT BY 1
 START WITH 1
 NOMAXVALUE
 NOMINVALUE
 CACHE 20;

GRANT DELETE, INSERT, SELECT, UPDATE on EBOOK_AUTHORITY.CANADIAN_TOPIC_CODE to EBOOK_USER;
GRANT SELECT, ALTER ON EBOOK_AUTHORITY.CANADIAN_TOPIC_CODE_ID_SEQ TO EBOOK_USER;

CREATE TABLE EBOOK_AUTHORITY.CANADIAN_DIGEST (
	TITLE_ID VARCHAR2(64),
	JOB_INSTANCE_ID NUMBER,
	DOC_UUID VARCHAR2(36),
	CLASSIFNUM VARCHAR2(128),
	CLASSIFICATION VARCHAR2(512),
	CANADIAN_DIGEST_ID NUMBER,
	CONSTRAINT CANADIAN_DIGEST_PK PRIMARY KEY (CANADIAN_DIGEST_ID)
);
CREATE SEQUENCE EBOOK_AUTHORITY.CANADIAN_DIGEST_ID_SEQ
 INCREMENT BY 1
 START WITH 1
 NOMAXVALUE
 NOMINVALUE
 CACHE 20;

GRANT DELETE, INSERT, SELECT, UPDATE on EBOOK_AUTHORITY.CANADIAN_DIGEST to EBOOK_USER;
GRANT SELECT, ALTER ON EBOOK_AUTHORITY.CANADIAN_DIGEST_ID_SEQ TO EBOOK_USER;

-- in EBOOK_USER schema
CREATE OR REPLACE SYNONYM EBOOK_USER.CANADIAN_TOPIC_CODE FOR EBOOK_AUTHORITY.CANADIAN_TOPIC_CODE;
CREATE OR REPLACE SYNONYM EBOOK_USER.CANADIAN_TOPIC_CODE_ID_SEQ FOR EBOOK_AUTHORITY.CANADIAN_TOPIC_CODE_ID_SEQ;

CREATE OR REPLACE SYNONYM EBOOK_USER.CANADIAN_DIGEST FOR EBOOK_AUTHORITY.CANADIAN_DIGEST;
CREATE OR REPLACE SYNONYM EBOOK_USER.CANADIAN_DIGEST_ID_SEQ FOR EBOOK_AUTHORITY.CANADIAN_DIGEST_ID_SEQ;

