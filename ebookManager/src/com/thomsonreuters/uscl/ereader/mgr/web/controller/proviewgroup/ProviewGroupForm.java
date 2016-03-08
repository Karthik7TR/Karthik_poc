package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.Date;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;

public class ProviewGroupForm{
	
	public static final String FORM_NAME = "proviewGroupForm";


	public enum Command {
		REMOVE, DELETE, PROMOTE, REFRESH, PAGESIZE
	};

	private String titleId;
	private String version;
	private Command command;
	private String objectsPerPage;

	public ProviewGroupForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProviewAudit createAudit() {
		ProviewAudit audit = new ProviewAudit();
		audit.setBookVersion(version);
		audit.setProviewRequest(command.toString());
		audit.setRequestDate(new Date());
		audit.setTitleId(titleId);
		audit.setUsername(UserUtils.getAuthenticatedUserName());

		return audit;
	}

	public ProviewGroupForm(String titleId, String version, String status,
			String lastUpdate) {
		super();
		this.titleId = titleId;
		this.version = version;
	}

	public String getObjectsPerPage() {
		return objectsPerPage;
	}

	public void setObjectsPerPage(String objectsPerPage) {
		this.objectsPerPage = objectsPerPage;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command cmd) {
		this.command = cmd;
	}

}
