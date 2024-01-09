package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static com.thomsonreuters.uscl.ereader.quality.step.QualityStep.DIVXML;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.quality.domain.request.CompareUnit;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.tuple.Pair;

public interface QualityFileSystem {
    MultiKeyMap<String, Collection<File>> getHtmlFileMap(BookStep step);

    BiConsumer<MultiKeyMap<String, Collection<File>>,
            Map.Entry<String, Collection<File>>> MULTI_KEY_MAP_FILE_CONSUMER = (map, obj) -> {
        final Collection<File> files = obj.getValue();
        final String materialNumber = obj.getKey();
        files.forEach(file -> {
            final String divXmlName = substringBefore(file.getName(), DIVXML);
            Optional.ofNullable(map.get(materialNumber, divXmlName))
                    .orElseGet(() -> {
                        final List<File> newList = new ArrayList<>();
                        map.put(materialNumber, divXmlName, newList);
                        return newList;
                    })
                    .add(file);
        });
    };

    Collector<Map.Entry<String, Collection<File>>,
            MultiKeyMap<String, Collection<File>>,
            MultiKeyMap<String, Collection<File>>> MULTI_KEY_MAP_COLLECTOR = Collector.of(MultiKeyMap<String, Collection<File>>::new,
            MULTI_KEY_MAP_FILE_CONSUMER,
            (firstMap, secondMap) -> {
                firstMap.putAll(secondMap);
                return firstMap;
            });

    Collector<Pair<CompareUnit, CompareUnit>,
            List<CompareUnit>,
            List<CompareUnit>> COMPARE_UNIT_LIST_COLLECTOR = Collector.of(ArrayList::new,
            (list, pair) -> {
                list.add(pair.getLeft());
                list.add(pair.getRight());
            }, (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            });
}
