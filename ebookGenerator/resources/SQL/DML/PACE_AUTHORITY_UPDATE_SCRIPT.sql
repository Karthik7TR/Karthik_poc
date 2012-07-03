/** HOTFIXING PACE AUTHORITY TABLE*/
-- Updating authority data to ensure internal links work in certain books.

UPDATE EBOOK_AUTHORITY.PACE_METADATA p
SET P.PUBLICATION_NAME = 'SECBLUE'
WHERE P.STD_PUB_NAME = 'BLUESKYL';

COMMIT;

-- NPD needs to decide weather links within Ehrhardt's should remain external links or become internal
/**
UPDATE EBOOK_AUTHORITY.PACE_METADATA p
SET P.PUBLICATION_NAME = '1FLPRAC'
WHERE P.STD_PUB_NAME = 'FLST';

COMMIT;*/

UPDATE EBOOK_AUTHORITY.PACE_METADATA p
SET P.PUBLICATION_NAME = '1FLPRAC'
WHERE P.STD_PUB_NAME = 'FLPRAC';

COMMIT;

update pace_metadata p
set p.std_pub_name = 'FLAPRACEVIDENCE'
where p.publication_name = '1FLPRAC';

COMMIT;

update pace_metadata p
set p.publication_name = '1FLPRAC'
where p.std_pub_name = 'FLAPRACEVIDENCE';

commit;

update pace_metadata p
set p.std_pub_name='MINNPRACCRIMINALLAWPROCEDURE'
where  p.std_pub_name='MNPRAC';

UPDATE PACE_METADATA p
SET p.publication_name = 'MNPRAC'
where P.STD_PUB_NAME = 'MINNPRACCRIMINALLAWPROCEDURE';

commit;


update pace_metadata p
set  p.publication_name = 'WAPRAC'
where  P.STD_PUB_NAME = 'WASHPRACMETHODSOFPRACTICE'
and p.publication_name = '1WAPRAC';

commit;

