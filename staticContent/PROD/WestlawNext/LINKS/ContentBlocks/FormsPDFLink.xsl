<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.LINKS.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="BlobLink.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--
		Links product view override: hyperlink to PDF forms have href host stay within Links rather than link out to WLN.
		Example document: http://links.ci.westlaw.com/Document/I062f73dbc8f811dc8f4f9bc40009ef64/View/FullText.html?originationContext=documenttoc&transitionType=CategoryPageItem&contextData=(sc.Default)&sp=DEVLINKS-1301

		Current known collection -> content types with links to PDF forms:
			Reporting Name  |  Collection Set  |  Collection        |  Content Type
			=============================================================================
			CACOUNTYF       |  wlnv_cacco      |  w_an_rcc_forms    |  Analytical - Forms
			CAJCF           |  wlnv_cacjc      |  w_an_rcc_forms    |  Analytical - Forms
			USCA            |  w_codesstausp   |  w_codesstausnvdp  |  Codes - Statutes
			FBKR-FORMS      |  wlnv_fbkrforms  |  w_an_rcc_forms    |  Analytical - Forms
			IMMOF           |  wlnv_immof      |  w_an_rcc_forms    |  Analytical - Forms
			PATFORMS        |  wlnv_patforms   |  w_an_rcc_forms    |  Analytical - Forms
			AMJUR-PP        |  wlnv_amjurpp    |  w_an_rcc_ajpp     |  Analytical - Jurs
			FSEC-FORMS      |  w_gsi_sec_frm   |  w_gsi_sec_frm     |  Analytical - Forms
			SSA-FORMS       |  wlnv_ssaof      |  w_an_rcc_forms    |  Analytical - Forms
			MISCAO-FRM      |  wlnv_miscao     |  w_an_rcc_forms    |  Analytical - Forms

		Additional collection -> content types found from testing:
			Collection      |  Content Type
			==========================================================
			w_3rd_eforms    |  Analytical - EForms
			w_3rd_fedall    |  Analytical - EForms
			w_an_cle_forms  |  Analytical - Treatise - Practice Guides
			w_an_rcc_texts  |  Analytical - Treatise - Practice Guides
			w_an_ea_mnprac  |  Analytical Treatise - Other

		Add an <xsl:include> of this file to stylesheets of content types that would like to allow hyperlinks to PDF forms be accessible.
	-->

	<xsl:template name="CreateBlobLinkUrl">
		<xsl:param name="guid" />
		<xsl:param name="highResolution" />
		<xsl:param name="targetType" />
		<xsl:param name="maxHeight" />
		<xsl:param name="originationContext" />
		<xsl:param name="prettyName" />
		<xsl:param name="hash" />
		<xsl:param name="docGuid" />
		<xsl:param name="extension" />

		<!-- Set the host to Links regardless for these content types. -->
		<xsl:call-template name="CreateBlobLinkUrlLINKS">
			<xsl:with-param name="guid" select="$guid"/>
			<xsl:with-param name="highResolution" select="$highResolution"/>
			<xsl:with-param name="targetType" select="$targetType"/>
			<xsl:with-param name="maxHeight" select="$maxHeight"/>
			<xsl:with-param name="originationContext" select="$originationContext"/>
			<xsl:with-param name="prettyName" select="$prettyName"/>
			<xsl:with-param name="hash" select="$hash"/>
			<xsl:with-param name="docGuid" select="$docGuid"/>
			<xsl:with-param name="extension" select="$extension"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
