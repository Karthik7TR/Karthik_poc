package com.thomsonreuters.uscl.ereader.mgr.web.service.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrintComponentUtilTest {


    private static final String PATH_BOUND = "/apps/eBookBuilder/cicontent/xpp/archive/2018/08/TXLB_42070153_2018-08-06_09.40.59.397.-0400_bound.tar.gz";
    private static final String PATH_SUPP = "/apps/eBookBuilder/cicontent/xpp/archive/2018/08/TXLB_42114057_2018-08-02_05.46.16.901.-0400_supp.tar.gz";
    private static final String MATERIAL_NUMBER_BOUND = "11";
    private static final String MATERIAL_NUMBER_SUPP = "22";
    private static final String MATERIAL_NUMBER_ERROR = "222";
    @InjectMocks
    private PrintComponentUtil printComponentUtil;

    @Mock
    private XppBundleArchiveService xppBundleArchiveService;

    @Before
    public void setUp(){
        org.mockito.MockitoAnnotations.initMocks(this);
        final List<String> materialNumberList = Arrays.asList(MATERIAL_NUMBER_BOUND, MATERIAL_NUMBER_SUPP, MATERIAL_NUMBER_ERROR);
        when(xppBundleArchiveService.findByMaterialNumberList(materialNumberList))
            .thenReturn(getXppBundleArchiveByMaterialNumberList(materialNumberList));
    }

    PrintComponent getPrintComponent(String materialNumber, boolean componentInArchive, boolean supplement){
        PrintComponent printComponent = new PrintComponent();
        printComponent.setMaterialNumber(materialNumber);
        printComponent.setComponentInArchive(componentInArchive);
        printComponent.setSupplement(supplement);
        return printComponent;
    }
    private List<PrintComponent> getPrintComponentsWithSupplements(){
        final List<PrintComponent> printComponentsList = new ArrayList<>();
        printComponentsList.add(getPrintComponent(MATERIAL_NUMBER_BOUND, true, false));
        printComponentsList.add(getPrintComponent(MATERIAL_NUMBER_SUPP, true, true));
        printComponentsList.add(getPrintComponent(MATERIAL_NUMBER_ERROR, false, false));

        return printComponentsList;
    }
    private List<XppBundleArchive> getXppBundleArchiveByMaterialNumberList(final List<String> materialNumbers){
        final List<XppBundleArchive> xppBundleArchiveList = new ArrayList<>();

        XppBundleArchive xppBundleArchive = new XppBundleArchive();
        xppBundleArchive.setMaterialNumber(MATERIAL_NUMBER_BOUND);
        xppBundleArchive.setEBookSrcPath(PATH_BOUND);
        xppBundleArchiveList.add(xppBundleArchive);

        xppBundleArchive = new XppBundleArchive();
        xppBundleArchive.setMaterialNumber(MATERIAL_NUMBER_SUPP);
        xppBundleArchive.setEBookSrcPath(PATH_SUPP);
        xppBundleArchiveList.add(xppBundleArchive);

        return xppBundleArchiveList;
    }

    @Test
    public void testGetAllInitializedPrintComponentsWithSupplement() {
        //given
        final List<PrintComponent> printComponentsList = new ArrayList<>();

        PrintComponent printComponent = new PrintComponent();
        printComponent.setMaterialNumber(MATERIAL_NUMBER_BOUND);
        printComponentsList.add(printComponent);

        printComponent = new PrintComponent();
        printComponent.setMaterialNumber(MATERIAL_NUMBER_SUPP);
        printComponentsList.add(printComponent);

        printComponent = new PrintComponent();
        printComponent.setMaterialNumber(MATERIAL_NUMBER_ERROR);
        printComponentsList.add(printComponent);

        // when
        final List<PrintComponent> initializedPrintComponents =
            printComponentUtil.getAllInitializedPrintComponents(printComponentsList);
        // then
        assertEquals(initializedPrintComponents, getPrintComponentsWithSupplements());
    }
}
