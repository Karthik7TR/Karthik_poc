/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.springframework.validation.Errors;

public abstract class BaseFormValidator {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionFormValidator.class);

	protected void checkMaxLength(Errors errors, int maxValue ,String text, String fieldName,  Object[]  args) {
		if (StringUtils.isNotEmpty(text)) {
			if(text.length() > maxValue) {
				errors.rejectValue(fieldName, "error.max.length", args, "Must be maximum of " + maxValue + " characters or under");
			}
		}
	}
	
	protected void checkDateFormat(Errors errors, String text, String fieldName) {
		if (StringUtils.isNotEmpty(text)) {
			try {
				@SuppressWarnings("unused")
				Date date = new SimpleDateFormat("MM/dd/yyyy").parse(text);
			} catch (Exception  e) {
				errors.rejectValue(fieldName, "error.date.format");
			}
		}
	}
	
	protected void checkGuidFormat(Errors errors, String text, String fieldName) {
		if (StringUtils.isNotEmpty(text)) {
			//Pattern pattern = Pattern.compile("^\\w[0-9a-fA-F]{32}$");
			// Just checking for 33 characters.  Some publications has custom Root Guids like IFEDCIVDISC9999999999999999999999
			Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{33}$");
			Matcher matcher = pattern.matcher(text);
			
			if(!matcher.find()) {
				errors.rejectValue(fieldName, "error.guid.format");
			}
		}
	}
	
	protected void checkForSpaces(Errors errors, String text, String fieldName, String arg) {
		if (StringUtils.isNotEmpty(text)) {
			Pattern pattern = Pattern.compile("\\s");
			Matcher matcher = pattern.matcher(text);
			
			if(matcher.find()) {
				errors.rejectValue(fieldName, "error.no.spaces", new Object[]{arg}, "No spaces allowed");
			}
		}
	}
	
	protected void checkSpecialCharacters(Errors errors, String text, String fieldName, boolean includeUnderscore) {
		if (StringUtils.isNotEmpty(text)) {
			Pattern pattern = includeUnderscore ? Pattern.compile("[^a-z0-9_ ]", Pattern.CASE_INSENSITIVE):
				Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
			
			Matcher matcher = pattern.matcher(text);
			
			if(matcher.find()) {
				if (includeUnderscore) {
					errors.rejectValue(fieldName, "error.alphanumeric.underscore");
				} else {
					errors.rejectValue(fieldName, "error.alphanumeric");
				}
				
			}
		}
	}
	
	protected static void validateDate(String dateString, Date parsedDate, String label, Errors errors) {
		if (StringUtils.isNotBlank(dateString)) {
			if (parsedDate == null) {
				Object[] args = { label };
				errors.reject("error.invalid.date", args, "Invalid Date: " + label);
			}
		}
	}
	
	protected static void validateDateRange(Date fromDate, Date toDate, Errors errors) {
		Date timeNow = new Date();
		String codeDateAfterToday = "error.date.after.today";
		if (fromDate != null) {
			if (fromDate.after(timeNow)) {
				String[] args = { "FROM" };
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
		if (toDate != null) {
			if (toDate.after(timeNow)) {
				String[] args = { "TO" };
				errors.reject(codeDateAfterToday, args, "ERR: TO date cannot be after today");
			}
		}
	}
}
