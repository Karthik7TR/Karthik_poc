/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.DeleteBookDefinitionControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.EditBookDefinitionControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.EditBookDefinitionFormValidatorTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.ErrorControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.GenerateEbookControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.LoginControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.SmokeTestControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.ViewBookDefinitionControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig.JobThrottleConfigControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jurisdictioncode.JurisdictionCodeControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jurisdictioncode.JurisdictionCodeFormValidatorTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode.KeywordCodeControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordcode.KeywordCodeFormValidatorTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue.KeywordValueControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.keywordvalue.KeywordValueFormValidatorTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.main.AdminControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode.PublishTypeCodeControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode.PublishTypeCodeFormValidatorTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode.StateCodeControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.statecode.StateCodeFormValidatorTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.fmpreview.FmPreviewControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details.JobExecutionControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.queue.QueueControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary.JobSummaryFilterControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.UserPreferencesControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.UserPreferencesFormValidatorTest;

@RunWith(Suite.class)
@SuiteClasses( {
			BookLibraryControllerTest.class,
			EditBookDefinitionControllerTest.class,
			EditBookDefinitionFormValidatorTest.class,
			FmPreviewControllerTest.class,
			GenerateEbookControllerTest.class,
			JobExecutionControllerTest.class,
			JobSummaryControllerTest.class,
			JobSummaryFilterControllerTest.class,
			JobThrottleConfigControllerTest.class,
			QueueControllerTest.class,
			LoginControllerTest.class,
			ViewBookDefinitionControllerTest.class,
			JurisdictionCodeControllerTest.class,
			JurisdictionCodeFormValidatorTest.class,
			KeywordCodeControllerTest.class,
			KeywordCodeFormValidatorTest.class,
			KeywordValueControllerTest.class,
			KeywordValueFormValidatorTest.class,
			AdminControllerTest.class,
			PublishTypeCodeControllerTest.class,
			PublishTypeCodeFormValidatorTest.class,
			StateCodeControllerTest.class,
			StateCodeFormValidatorTest.class,
			BookAuditControllerTest.class,
			BookAuditFilterControllerTest.class,
			DeleteBookDefinitionControllerTest.class,
			ErrorControllerTest.class,
			SmokeTestControllerTest.class,
			UserPreferencesControllerTest.class,
			UserPreferencesFormValidatorTest.class
		} )

public class ManagerTestSuite {

}
