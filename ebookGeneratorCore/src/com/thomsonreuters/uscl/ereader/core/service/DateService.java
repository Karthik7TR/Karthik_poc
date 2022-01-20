package com.thomsonreuters.uscl.ereader.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface DateService {
    LocalDate getLocalDate();

    LocalDateTime getLocalDateTime();

    String getFormattedServerDateTime();

    boolean isDateGreaterThanToday(final Date date);

    boolean isDateGreaterThanToday(final LocalDate localDate);
}
