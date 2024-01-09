CREATE SEQUENCE "EBOOK_TEST"."VERSION_ISBN_ID_SEQ"
 INCREMENT BY 1
 START WITH 1
 NOMAXVALUE
 NOMINVALUE
 CACHE 20

GRANT SELECT, ALTER ON EBOOK_TEST.VERSION_ISBN_ID_SEQ TO EBOOK_TEST_USER;

CREATE OR REPLACE SYNONYM EBOOK_TEST_USER.VERSION_ISBN_ID_SEQ FOR EBOOK_TEST.VERSION_ISBN_ID_SEQ;

CREATE TABLE "EBOOK_TEST"."VERSION_ISBN"(
  "VERSION_ISBN_ID" Number NOT NULL,
  "EBOOK_DEFINITION_ID" Number NOT NULL,
  "VERSION" Varchar2(10 ) NOT NULL,
  "ISBN" Varchar2(64 ) NOT NULL
)

ALTER TABLE "EBOOK_TEST"."VERSION_ISBN" ADD CONSTRAINT "VERSION_ISBN_PK" PRIMARY KEY ("VERSION_ISBN_ID");

GRANT DELETE, INSERT, SELECT, UPDATE ON EBOOK_TEST.VERSION_ISBN TO EBOOK_TEST_USER;

CREATE OR REPLACE SYNONYM EBOOK_TEST_USER.VERSION_ISBN FOR EBOOK_TEST.VERSION_ISBN;
