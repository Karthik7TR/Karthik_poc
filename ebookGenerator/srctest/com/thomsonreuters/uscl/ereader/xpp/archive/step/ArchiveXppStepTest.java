package com.thomsonreuters.uscl.ereader.xpp.archive.step;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.thomsonreuters.uscl.ereader.common.archive.service.ArchiveAuditService;
import com.thomsonreuters.uscl.ereader.common.archive.service.ArchiveService;
import com.thomsonreuters.uscl.ereader.common.service.environment.EnvironmentUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ArchiveXppStepTest {
    @InjectMocks
    private ArchiveXppStep step;
    @Mock
    private ArchiveAuditService archiveAuditService;
    @Mock
    private ArchiveService archiveService;
    @Mock
    private EnvironmentUtil environmentUtil;

    @Test
    public void shouldSaveAudit() throws Exception {
        //given
        //when
        step.executeStep();
        //then
        then(archiveAuditService).should().saveAudit(step);
    }

    @Test
    public void shouldStoreArchiveForProd() throws Exception {
        //given
        given(environmentUtil.isProd()).willReturn(true);
        //when
        step.executeStep();
        //then
        then(archiveService).should().archiveBook(step);
    }
}
