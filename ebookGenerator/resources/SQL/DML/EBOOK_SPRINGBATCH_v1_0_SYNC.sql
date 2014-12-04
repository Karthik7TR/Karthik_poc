--------------Spring Batch Changes--------------------------
--------------------------------------------------------

CREATE TABLE EBOOK_SPRINGBATCH.BATCH_JOB_EXECUTION_PARAMS
(
  JOB_EXECUTION_ID  NUMBER                      NOT NULL,
  TYPE_CD           VARCHAR2(6 BYTE)            NOT NULL,
  KEY_NAME          VARCHAR2(100 BYTE)          NOT NULL,
  STRING_VAL        VARCHAR2(250 BYTE),
  DATE_VAL          TIMESTAMP(6)                DEFAULT NULL,
  LONG_VAL          NUMBER,
  DOUBLE_VAL        FLOAT(126),
  IDENTIFYING       CHAR(1 BYTE)                NOT NULL
);

ALTER TABLE EBOOK_SPRINGBATCH.BATCH_JOB_EXECUTION_PARAMS ADD (
  CONSTRAINT JOB_EXEC_PARAMS_FK 
  FOREIGN KEY (JOB_EXECUTION_ID) 
  REFERENCES EBOOK_SPRINGBATCH.BATCH_JOB_EXECUTION (JOB_EXECUTION_ID) ENABLE VALIDATE);


GRANT DELETE, INSERT, SELECT, UPDATE ON EBOOK_SPRINGBATCH.BATCH_JOB_EXECUTION_PARAMS TO EBOOK_USER;
GRANT SELECT ON EBOOK_SPRINGBATCH.BATCH_JOB_EXECUTION_PARAMS TO EBOOK_READ;

CREATE OR REPLACE SYNONYM EBOOK_USER.BATCH_JOB_EXECUTION_PARAMS FOR EBOOK_SPRINGBATCH.BATCH_JOB_EXECUTION_PARAMS;

ALTER TABLE EBOOK_SPRINGBATCH.BATCH_JOB_EXECUTION MODIFY EXIT_CODE varchar(2500) DEFAULT NULL;
ALTER TABLE EBOOK_SPRINGBATCH.BATCH_JOB_EXECUTION ADD JOB_CONFIGURATION_LOCATION varchar(2500) DEFAULT NULL;
ALTER TABLE EBOOK_SPRINGBATCH.BATCH_STEP_EXECUTION MODIFY EXIT_CODE varchar(2500) DEFAULT NULL;

COMMIT;