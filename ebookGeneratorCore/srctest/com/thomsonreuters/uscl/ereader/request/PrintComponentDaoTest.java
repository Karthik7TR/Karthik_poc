package com.thomsonreuters.uscl.ereader.request;

import java.util.Arrays;

import com.thomsonreuters.uscl.ereader.request.dao.PrintComponentDao;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class PrintComponentDaoTest {
    private PrintComponentDao componentDao;

    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private Criteria mockCriteria;

    private Capture<PrintComponent> capturedComponent;

    @Before
    public void setUp() {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);

        capturedComponent = new Capture<>();

        componentDao = new PrintComponentDao(mockSessionFactory);
    }

    @Test
    public void testFindByPrimaryKey() {
        final long pkey = 1L;
        final PrintComponent expected = createPrintComponent(pkey);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(PrintComponent.class, pkey)).andReturn(expected);
        replayAll();

        final PrintComponent actual = componentDao.findByPrimaryKey(pkey);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteByPrimaryKey() {
        final long pkey = 1L;
        final PrintComponent expected = createPrintComponent(pkey);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession).times(2);
        EasyMock.expect(mockSession.get(PrintComponent.class, pkey)).andReturn(expected);
        mockSession.delete(EasyMock.and(EasyMock.capture(capturedComponent), EasyMock.isA(PrintComponent.class)));
        mockSession.flush();
        EasyMock.expectLastCall();
        replayAll();

        componentDao.deleteComponent(pkey);
        final PrintComponent actual = capturedComponent.getValue();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFindByMaterialNumber() {
        final String materialNumber = "ThisIsAMaterialNumber";
        final Capture<Criterion> captured = new Capture<>();
        final PrintComponent expected = createPrintComponent(0);
        expected.setMaterialNumber(materialNumber);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(PrintComponent.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add(EasyMock.capture(captured))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(Arrays.asList(expected));
        replayAll();

        final PrintComponent actual = componentDao.findByMaterialNumber(materialNumber);

        final Criterion criterion = captured.getValue();
        Assert.assertEquals("materialNumber=" + materialNumber, criterion.toString());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFindByComponentName() {
        final String componentName = "ThisIsAComponentName";
        final Capture<Criterion> captured = new Capture<>();
        final PrintComponent expected = createPrintComponent(0);
        expected.setComponentName(componentName);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(PrintComponent.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.add(EasyMock.capture(captured))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(Arrays.asList(expected));
        replayAll();

        final PrintComponent actual = componentDao.findByComponentName(componentName);

        final Criterion criterion = captured.getValue();
        Assert.assertEquals("componentName=" + componentName, criterion.toString());
        Assert.assertEquals(expected, actual);
    }

    private PrintComponent createPrintComponent(final long pkey) {
        final PrintComponent component = new PrintComponent();
        component.setPrintComponentId(String.valueOf(pkey));
        component.setMaterialNumber("ThisIsAMaterialNumber");
        component.setComponentOrder(1);
        component.setComponentName("ThisIsAName");
        return component;
    }

    private void replayAll() {
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);
    }
}
