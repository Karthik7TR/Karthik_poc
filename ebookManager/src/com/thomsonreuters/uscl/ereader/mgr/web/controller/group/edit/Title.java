package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class Title {
    private String titleId;
    private String proviewName;
    private BigInteger version;
    private int numberOfParts;
}
