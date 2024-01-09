INSERT INTO EBOOK.SCHEMA_VERSION (SCHEMA_VERSION, COMMENTS, CHANGE_DATE)
VALUES (21.0, 'Alter table ebook_definition add print_sub_number varchar2(64)', TO_DATE('10/10/2017', 'MM/DD/YYYY'));

ALTER TABLE ebook_definition ADD print_sub_number Varchar2(64);
