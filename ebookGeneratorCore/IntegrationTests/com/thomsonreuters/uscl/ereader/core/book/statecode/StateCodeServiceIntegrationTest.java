package com.thomsonreuters.uscl.ereader.core.book.statecode;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = StateCodeServiceIntegrationTestConf.class)
@Transactional
@ActiveProfiles("IntegrationTests")
public class StateCodeServiceIntegrationTest {
    @Autowired
    private StateCodeService service;
    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        final DbSetup dbSetup = new DbSetup(
            new DataSourceDestination(dataSource),
            sequenceOf(
                Operations.deleteAllFrom("STATE_CODES"),
                insertInto("STATE_CODES").columns("STATE_CODES_ID", "STATE_CODES_NAME", "LAST_UPDATED")
                    .values(3L, "z", new DateTime(2017, 2, 14, 17, 3, 0).toDate())
                    .values(1L, "a", new DateTime(2017, 2, 14, 16, 2, 0).toDate())
                    .values(2L, "b", new DateTime(2017, 2, 14, 16, 3, 0).toDate())
                    .build()));
        dbSetup.launch();
    }

    @Test
    public void shouldReturnAllStateCodesInAcsOrder() {
        // given
        final StateCode[] expectedStateCodes = new StateCode[] {
            stateCode(1L, "a", new DateTime(2017, 2, 14, 16, 2, 0)),
            stateCode(2L, "b", new DateTime(2017, 2, 14, 16, 3, 0)),
            stateCode(3L, "z", new DateTime(2017, 2, 14, 17, 3, 0))};
        // when
        final List<StateCode> stateCodes = service.getAllStateCodes();
        // then
        assertThat(stateCodes, contains(expectedStateCodes));
    }

    @Test
    public void shouldReturnStateCodeById() {
        // given
        final StateCode stateCode = stateCode(1L, "a", new DateTime(2017, 2, 14, 16, 2, 0));
        // when
        final StateCode stateCodeById = service.getStateCodeById(1L);
        // then
        assertThat(stateCodeById, is(stateCode));
    }

    @Test
    public void shouldReturnStateCodeByName() {
        // given
        final StateCode stateCode = stateCode(3L, "z", new DateTime(2017, 2, 14, 17, 3, 0));
        // when
        final StateCode stateCodeById = service.getStateCodeByName("z");
        // then
        assertThat(stateCodeById, is(stateCode));
    }

    @Test
    public void shouldCreateStateCodeIfIdNotFound() {
        // given
        final DateTime lastUpdate = new DateTime(2015, 6, 12, 10, 31, 10);
        final StateCode stateCode = stateCode(4L, "myState", lastUpdate);
        // when
        service.saveStateCode(stateCode);
        final StateCode stateCodeById = service.getStateCodeById(4L);
        // then
        assertThat(stateCodeById, is(stateCode));
        assertThat(stateCode.getLastUpdated(), not(lastUpdate.toDate()));
    }

    @Test
    public void shouldCreateStateCodeIfIdIsNull() {
        // given
        final DateTime lastUpdate = new DateTime(2015, 6, 12, 10, 31, 10);
        final StateCode stateCode = stateCode(null, "myState", lastUpdate);
        // when
        service.saveStateCode(stateCode);
        final List<StateCode> stateCodes = service.getAllStateCodes();
        // then
        assertThat(stateCodes, hasSize(4));
    }

    @Test
    public void shouldUpdateStateCode() {
        // given
        final DateTime lastUpdate = new DateTime(2015, 6, 12, 10, 31, 10);
        final StateCode stateCode = stateCode(3L, "myState", lastUpdate);
        // when
        service.saveStateCode(stateCode);
        final StateCode stateCodeById = service.getStateCodeById(3L);
        // then
        assertThat(stateCodeById, is(stateCode));
        assertThat(stateCode.getLastUpdated(), not(lastUpdate.toDate()));
    }

    @Test
    public void shouldDeleteStateCode() {
        // given
        final StateCode stateCode = stateCode(3L, "z", new DateTime(2017, 2, 14, 17, 3, 0));
        // when
        service.deleteStateCode(stateCode);
        final List<StateCode> stateCodes = service.getAllStateCodes();
        // then
        assertThat(stateCodes, hasSize(2));
    }

    @Test
    public void shouldDoNothingIfWillTryToDeleteNotExistedStateCode() {
        // given
        final StateCode stateCode = stateCode(42L, "notExist", new DateTime(2042, 11, 7, 10, 10, 10));
        // when
        service.deleteStateCode(stateCode);
        final List<StateCode> stateCodes = service.getAllStateCodes();
        // then
        assertThat(stateCodes, hasSize(3));
    }

    @NotNull
    public static StateCode stateCode(final Long id, final String name, final DateTime timestamp) {
        final StateCode stateCode = new StateCode();
        stateCode.setId(id);
        stateCode.setName(name);
        stateCode.setLastUpdated(new Timestamp(timestamp.toDate().getTime()));
        return stateCode;
    }
}
