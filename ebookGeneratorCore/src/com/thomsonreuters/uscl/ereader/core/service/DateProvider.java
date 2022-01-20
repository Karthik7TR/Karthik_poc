package com.thomsonreuters.uscl.ereader.core.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class DateProvider {
    public Date getDate() {
        return new Date();
    }
    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }
}
