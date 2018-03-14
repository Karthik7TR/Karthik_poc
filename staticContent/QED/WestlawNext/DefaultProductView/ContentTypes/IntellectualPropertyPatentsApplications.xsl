<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="IntellectualPropertyPatentsGranted.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypePatentsApplicationsClass;'"/>
			</xsl:call-template>
			<div class="&sazanamiMinchoClass; &contentTypeIPDocumentClass;">
				<!--<xsl:apply-templates select="//md.ip.image.link" />-->
				<xsl:call-template name="displayPatentDocument" />
			</div>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!-- Patent Applications use the same DTD as Granted Patents so we can include Granted Patents and extend or override for any necessary changes. -->
	
	<!-- Other Parties -->
	<xsl:template match="agent.b">
		<div>
			<xsl:call-template name="join" >
				<xsl:with-param name="nodes" select="agent.name | agent.addr.b/agent.addr.line" />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!-- Classification Info -->
	<xsl:template match="cpc.list">
		<xsl:call-template name="join" >
			<xsl:with-param name="nodes" select="*" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="applicant.b">
		<div>
			<xsl:call-template name="join" >
				<xsl:with-param name="nodes" select="applicant.name | applicant.addr.b/applicant.addr.line" />
			</xsl:call-template>
		</div>
	</xsl:template>


	<!-- Currently the inline images for Patents are not functional so we need to ignore those elements. -->
	<!-- Once the work is done to make these links work then just remove this line of code and then the images should appear. -->
	<xsl:template match="//image.link[@ttype='inline']" />

</xsl:stylesheet>