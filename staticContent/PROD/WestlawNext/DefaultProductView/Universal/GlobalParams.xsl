<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--50 States Survey Inline KC flag POC-->
	<xsl:param name="ShowStateSurveyInlineKCFlag" />

	<xsl:param name="OutOfPlanInlinePreviewMode" />
	<xsl:param name="ShowCasesAndBriefsInlineKCFlag" />
	<xsl:param name="DisplayNYODigestLinks" select="false()"/>

	<!-- International Content Logo I<nformation -->
	<xsl:param name="InternationalLogoPath" />
	<xsl:param name="InternationalLogoText" />

	<!-- Docket Gateway meta information -->
	<xsl:param name="DocketGatewaySignon" />
	<xsl:param name="DocketGatewayLinkParams" />
	<xsl:param name="DocketGatewayCaseType" />
	<xsl:param name="DocketGatewayAlertInfo" />

	<!-- IACs-->
	<xsl:param name="IAC-LIGER-POPULARNAME" select="false()" />
	<xsl:param name="IAC-MASTERTAX-DOC-TABLE" select="false()" />

	<!-- Parameters for KeyRule document sections -->
	<xsl:param name="ShowKeyRuleChecklist" select ="false()"/>
	<xsl:param name="ShowKeyRuleTiming" select ="false()"/>
	<xsl:param name="ShowKeyRuleDocuments" select ="false()"/>

	<!-- Determine if RuleBookMode is enabled -->
	<xsl:param name="IsRuleBookMode" select="false()"/>

	<!-- Used by Practice Point to remember if the user has redlining turned on (a user preference).-->
	<xsl:param name="redline" select="false()" />

	<xsl:param name="RedLineToggle" select="false()"/>
	
	<!-- Add statutes compare redlining markup to document -->
	<xsl:param name="StatutesCompareRedlining" select="false()" />

	<!-- call this widget to disable SaveToWidget in document view page -->
	<xsl:template name="RemoveSaveToWidget">
		<div>
			<input type="hidden" id="co_nonFolderable" value="true"/>
		</div>
	</xsl:template>
</xsl:stylesheet>