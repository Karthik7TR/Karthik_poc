package com.thomsonreuters.uscl.ereader.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.DATE_TIME_FORMATTER;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DateServiceImpl implements DateService {
    private final DateProvider dateProvider;

    @Override
    public LocalDateTime getLocalDateTime() {
        return dateProvider.getLocalDateTime();
    }

    @Override
    public LocalDate getLocalDate() {
        return getLocalDateTime().toLocalDate();
    }

    @Override
    public String getFormattedServerDateTime() {
        return getLocalDateTime().format(DATE_TIME_FORMATTER);
    }

    @Override
    public boolean isDateGreaterThanToday(final Date date) {
        return isDateGreaterThanToday(convertToLocalDate(date));
    }

    @Override
    public boolean isDateGreaterThanToday(final LocalDate localDate) {
        return ofNullable(localDate)
                .map(date -> date.isAfter(getLocalDate()))
                .orElse(false);
    }

    private LocalDate convertToLocalDate(final Date dateToConvert) {
        return ofNullable(dateToConvert)
                .map(Date::toInstant)
                .map(date -> date.atZone(ZoneId.systemDefault()))
                .map(ZonedDateTime::toLocalDate)
                .orElse(null);
    }
}
