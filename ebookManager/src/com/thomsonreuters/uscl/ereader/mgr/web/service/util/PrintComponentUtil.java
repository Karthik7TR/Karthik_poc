package com.thomsonreuters.uscl.ereader.mgr.web.service.util;

import java.util.List;
import java.util.stream.Collectors;

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
        final List<String> currentMaterialNumberList = sourcePrintComponentList.stream()
            .map(PrintComponent::getMaterialNumber)
            .collect(Collectors.toList());
        final List<XppBundleArchive> xppBundleArchiveList =
            xppBundleArchiveService.findByMaterialNumberList(currentMaterialNumberList);
        sourcePrintComponentList.stream()
            .filter(element -> containsArchiveWithTargetMaterialNumber(xppBundleArchiveList, element.getMaterialNumber()))
            .forEach(element -> {
                element.setSupplement(isSupplement(xppBundleArchiveList, element));
                element.setComponentInArchive(true);
            });
        return sourcePrintComponentList;
    }

    private boolean isSupplement(List<XppBundleArchive> xppBundleArchiveList, PrintComponent element) {
        return xppBundleArchiveList.stream()
            .filter(item -> item.getMaterialNumber().equals(element.getMaterialNumber()))
            .findAny()
            .get()
            .getEBookSrcPath()
            .endsWith("_supp.tar.gz");
    }

    private boolean containsArchiveWithTargetMaterialNumber(
        final List<XppBundleArchive> xppBundleArchiveList,
        final String materialNumber) {
        return xppBundleArchiveList.stream()
            .map(XppBundleArchive::getMaterialNumber)
            .anyMatch(materialNumber::equals);
    }
}
