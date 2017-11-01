package com.thomsonreuters.uscl.ereader.mgr.web.service.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import org.springframework.stereotype.Component;

@Component("printComponentUtil")
public class PrintComponentUtil {
    @Resource(name = "xppBundleArchiveService")
    private XppBundleArchiveService xppBundleArchiveService;

    public List<PrintComponent> getAllInitializedPrintComponents(final List<PrintComponent> sourcePrintComponentList) {
        final List<String> currentMaterialNumberList = new ArrayList<>();
        for (final PrintComponent element : sourcePrintComponentList) {
            currentMaterialNumberList.add(element.getMaterialNumber());
        }
        final List<XppBundleArchive> xppBundleArchiveList =
            xppBundleArchiveService.findByMaterialNumberList(currentMaterialNumberList);
        for (final PrintComponent element : sourcePrintComponentList) {
            element.setComponentInArchive(
                containsArchiveWithTargetMaterialNumber(xppBundleArchiveList, element.getMaterialNumber()));
        }
        return sourcePrintComponentList;
    }

    private boolean containsArchiveWithTargetMaterialNumber(
        final List<XppBundleArchive> xppBundleArchiveList,
        final String materialNumber) {
        for (final XppBundleArchive element : xppBundleArchiveList) {
            if (element.getMaterialNumber().equals(materialNumber)) {
                return true;
            }
        }
        return false;
    }
}
