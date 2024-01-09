package com.thomsonreuters.uscl.ereader.core.book.statecode;

import static com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeTestUtil.stateCode;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.then;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class StateCodeServiceImplTest {
    @InjectMocks
    private StateCodeServiceImpl service;
    @Mock
    private StateCodeDao dao;
    @Captor
    private ArgumentCaptor<StateCode> captor;

    @Test
    public void shouldCreateStateCodeIfIdIsNull() {
        // given
        final StateCode stateCode = stateCode(null);
        // when
        service.saveStateCode(stateCode);
        // then
        then(dao).should().createStateCode(captor.capture());
        assertThat(captor.getValue().getLastUpdated(), notNullValue());
    }

    @Test
    public void shouldUpdateStateCodeIfIdIsNotNull() {
        // given
        final StateCode stateCode = stateCode(1L);
        // when
        service.saveStateCode(stateCode);
        // then
        then(dao).should().updateStateCode(captor.capture());
        assertThat(captor.getValue().getLastUpdated(), notNullValue());
    }
}
