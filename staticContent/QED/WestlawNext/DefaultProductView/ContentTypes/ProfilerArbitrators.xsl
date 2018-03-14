<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="ProfilerSharedUtilities.xsl"/>
	<xsl:include href="ProfilerAddress.xsl"/>
	<xsl:include href="ProfilerAdmitted.xsl"/>
	<xsl:include href="ProfilerAffiliation.xsl"/>
	<xsl:include href="ProfilerCertification.xsl"/>
	<xsl:include href="ProfilerContact.xsl"/>
	<xsl:include href="ProfilerEducation.xsl"/>
	<xsl:include href="ProfilerMessage.xsl"/>
	<xsl:include href="ProfilerName.xsl"/>
	<xsl:include href="ProfilerNarrativeText.xsl"/>
	<xsl:include href="ProfilerOrganization.xsl"/>
	<xsl:include href="ProfilerOtherOffice.xsl"/>
	<xsl:include href="ProfilerPosition.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:apply-templates select="n-docbody/profile"/>
			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template match="profile">
		<xsl:call-template name="ProfilerContactInformation"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:call-template name="ProfilerContent"/>
	</xsl:template>

</xsl:stylesheet>