package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.DATE_FORMATTER;

public abstract class BaseFormValidator {
    private static final int PROVIEW_YEAR_RANGE_MIN = 1000;
    private static final int PROVIEW_YEAR_RANGE_MAX = 3000;
    private static final Pattern PATTERN_PROVIEW_FORBIDDEN = Pattern.compile("[\u0002]");

    protected void checkMaxLength(
        final Errors errors,
        final int maxValue,
        final String text,
        final String fieldName,
        final Object[] args) {
        if (StringUtils.isNotEmpty(text) && text.length() > maxValue) {
            errors.rejectValue(
                fieldName,
                "error.max.length",
                args,
                "Must be maximum of " + maxValue + " characters or under");
        }
    }

    protected void checkDateFormat(final Errors errors, final String text, final String fieldName) {
        if (StringUtils.isNotEmpty(text)) {
            try {
                LocalDate date = LocalDate.parse(text, DATE_FORMATTER);
                if (date.getYear() < PROVIEW_YEAR_RANGE_MIN || date.getYear() >= PROVIEW_YEAR_RANGE_MAX) {
                    errors.rejectValue(fieldName, "error.date.format");
                }
            } catch (final Exception e) {
                errors.rejectValue(fieldName, "error.date.format");
            }
        }
    }

    protected void checkGuidFormat(final Errors errors, final String text, final String fieldName) {
        if (StringUtils.isNotEmpty(text)) {
            //Pattern pattern = Pattern.compile("^\\w[0-9a-fA-F]{32}$");
            // Just checking for 33 characters.  Some publications has custom Root Guids like IFEDCIVDISC9999999999999999999999
            final Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{33}$");
            final Matcher matcher = pattern.matcher(text);

            if (!matcher.find()) {
                errors.rejectValue(fieldName, "error.guid.format");
            }
        }
    }

    protected void checkForSpaces(final Errors errors, final String text, final String fieldName, final String arg) {
        if (StringUtils.isNotEmpty(text)) {
            final Pattern pattern = Pattern.compile("\\s");
            final Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                errors.rejectValue(fieldName, "error.no.spaces", new Object[] {arg}, "No spaces allowed");
            }
        }
    }

    protected void checkSpecialCharacters(
        final Errors errors,
        final String text,
        final String fieldName,
        final boolean includeUnderscore) {
        if (StringUtils.isNotEmpty(text)) {
            final Pattern pattern = includeUnderscore
                ? Pattern.compile("[^a-z0-9_ ]", Pattern.CASE_INSENSITIVE)
                : Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

            final Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                if (includeUnderscore) {
                    errors.rejectValue(fieldName, "error.alphanumeric.underscore");
                } else {
                    errors.rejectValue(fieldName, "error.alphanumeric");
                }
            }
        }
    }

    protected void checkForbiddenProviewSymbolsFor(final String fieldName, final String text, final Errors errors) {
        if (StringUtils.isNotBlank(text)) {
            final Matcher matcher = PATTERN_PROVIEW_FORBIDDEN.matcher(text);
            if (matcher.find()) {
                errors.rejectValue(fieldName, "error.proview.forbidden.characters");
            }
        }
    }

    protected static void validateDate(
        final String dateString,
        final Date parsedDate,
        final String label,
        final Errors errors) {
        if (StringUtils.isNotBlank(dateString) && parsedDate == null) {
            final Object[] args = {label};
            errors.reject("error.invalid.date", args, "Invalid Date: " + label);
        }
    }

    protected static void validateDateRange(final Date fromDate, final Date toDate, final Errors errors) {
        final Date timeNow = new Date();
        final String codeDateAfterToday = "error.date.after.today";
        if (fromDate != null) {
            if (fromDate.after(timeNow)) {
                final String[] args = {"FROM"};
                errors.reject(codeDateAfterToday, args, "ERR: FROM date cannot be after today");
            }
            if (toDate != null) {
                if (fromDate.after(toDate)) {
                    errors.reject("error.from.date.after.to.date");
                }
                if (toDate.before(fromDate)) {
                    errors.reject("error.to.date.before.from.date");
                }
            }
        }
    }
}
