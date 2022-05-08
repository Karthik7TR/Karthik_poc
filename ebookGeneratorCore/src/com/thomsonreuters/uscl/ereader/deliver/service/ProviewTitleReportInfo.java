package com.thomsonreuters.uscl.ereader.deliver.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString(of = {"titleId", "version", "status", "bookName", "keywords", "isbn", "materialNo"})
@EqualsAndHashCode(of = {"titleId", "version", "status", "bookName", "keywords", "isbn", "materialNo"})
@AllArgsConstructor
@NoArgsConstructor
public class ProviewTitleReportInfo {

    private static final long serialVersionUID = -4229230493652304110L;

    private String id; //titleId
    private String version;
    private String status;
    private String name; //bookName
    private List<String> authors;
    private String isbn;
    private Date lastupdate;
    private List<ProviewTitleReportKeyword> keyword;

}
