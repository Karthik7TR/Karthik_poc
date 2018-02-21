package com.thomsonreuters.uscl.ereader.core.book.dao;

import static com.thomsonreuters.uscl.ereader.config.BookDefinitionUtils.fillBookDefinition;
import static com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock.LOCK_TIMEOUT_SEC;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

//Cannot be declared final as needs to be subclassed to create a proxy for transaction support
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LockDaoTestConfig.class)
@Transactional
@ActiveProfiles("IntegrationTests")
public class BookDefinitionLockDaoIntegrationTest {
    private static final long CALC_ERROR = 1000;

    @Autowired
    private BookDefinitionLockDao lockDao;
    @Autowired
    private BookDefinitionService bookDefinitionService;
    @Autowired
    private CodeService codeService;

    private BookDefinition def;

    @Before
    public void setUp() {
        def = bookDefinitionService.saveBookDefinition(fillBookDefinition(codeService, "title"));
        lockBookDefinition(def, "UC123456", "Mickey Mouse");
    }

    @Test
    public void shouldExtendLock() {
        //given
        final BookDefinitionLock lock = lockDao.findLocksByBookDefinition(def).get(0);
        final Date expectedDate = DateUtils.addSeconds(lock.getCheckoutTimestamp(), LOCK_TIMEOUT_SEC);
        //when
        lockDao.extendLock(lock);
        //then
        final Date newDate = lockDao.findLocksByBookDefinition(def).get(0).getCheckoutTimestamp();
        assertThat((double) expectedDate.getTime(), closeTo(newDate.getTime(), CALC_ERROR));
    }

    private void lockBookDefinition(final BookDefinition bookDefinition, final String username, final String fullName) {
        final BookDefinitionLock lock = new BookDefinitionLock();
        lock.setEbookDefinition(bookDefinition);
        lock.setUsername(username);
        lock.setFullName(fullName);
        lock.setCheckoutTimestamp(new Date());

        lockDao.saveLock(lock);
    }
}
