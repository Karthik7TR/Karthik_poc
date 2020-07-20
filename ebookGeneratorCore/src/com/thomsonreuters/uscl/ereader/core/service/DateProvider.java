package com.thomsonreuters.uscl.ereader.core.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DateProvider {
    public Date getDate() {
        return new Date();
    }
}
