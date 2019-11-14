package com.thomsonreuters.uscl.ereader.group.service;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;

public interface GroupService {
    String getGroupId(BookDefinition bookDefinition);

    List<GroupDefinition> getGroups(String groupId) throws Exception;

    List<GroupDefinition> getGroups(BookDefinition book) throws Exception;

    GroupDefinition getGroupInfoByVersion(String groupId, Long groupVersion) throws ProviewException;

    GroupDefinition getGroupInfoByVersionAutoDecrement(String groupId, Long groupVersion) throws ProviewException;

    GroupDefinition getLastGroup(BookDefinition book) throws Exception;

    GroupDefinition getLastGroup(String groupId) throws Exception;

    GroupDefinition getGroupOfTitle(String title);

    void createGroup(GroupDefinition groupDefinition) throws ProviewException;

    boolean isTitleWithVersion(String fullyQualifiedTitle);

    GroupDefinition createGroupDefinition(BookDefinition bookDefinition, String bookVersion, List<String> splitTitles)
        throws Exception;

    void removeAllPreviousGroups(BookDefinition bookDefinition) throws Exception;

    Map<String, ProviewTitleInfo> getProViewTitlesForGroup(BookDefinition bookDef) throws Exception;

    Map<String, ProviewTitleInfo> getPilotBooksForGroup(BookDefinition book) throws Exception;

    List<ProviewTitleInfo> getMajorVersionProviewTitles(String titleId) throws ProviewException;

    List<String> getPilotBooksNotFound();
}
