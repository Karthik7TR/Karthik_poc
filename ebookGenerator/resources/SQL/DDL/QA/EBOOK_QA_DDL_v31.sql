CREATE SEQUENCE "EBOOK_QA"."VERSION_ISBN_ID_SEQ"
 INCREMENT BY 1
 START WITH 1
 NOMAXVALUE
 NOMINVALUE
 CACHE 20

GRANT SELECT, ALTER ON EBOOK_QA.VERSION_ISBN_ID_SEQ TO EBOOK_QA_USER;

CREATE OR REPLACE SYNONYM EBOOK_QA_USER.VERSION_ISBN_ID_SEQ FOR EBOOK_QA.VERSION_ISBN_ID_SEQ;

CREATE TABLE "EBOOK_QA"."VERSION_ISBN"(
  "VERSION_ISBN_ID" Number NOT NULL,
  "EBOOK_DEFINITION_ID" Number NOT NULL,
  "VERSION" Varchar2(10 ) NOT NULL,
  "ISBN" Varchar2(64 ) NOT NULL
)

ALTER TABLE "EBOOK_QA"."VERSION_ISBN" ADD CONSTRAINT "VERSION_ISBN_PK" PRIMARY KEY ("VERSION_ISBN_ID");

GRANT DELETE, INSERT, SELECT, UPDATE ON EBOOK_QA.VERSION_ISBN TO EBOOK_QA_USER;

CREATE OR REPLACE SYNONYM EBOOK_QA_USER.VERSION_ISBN FOR EBOOK_QA.VERSION_ISBN;