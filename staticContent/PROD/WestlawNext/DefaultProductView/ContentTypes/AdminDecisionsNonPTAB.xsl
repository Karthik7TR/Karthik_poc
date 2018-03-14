<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="AdminDecisionsPTABCommon.xsl"/>
	<xsl:include href="ConstruedTerms.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- START Construed Terms -->

	<xsl:template match="construed.terms.block" priority="1">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<div style="border:1px solid;border-color:rgb(218,218,218);margin: 25px 0px;">
						<xsl:call-template name="CreateConstruedTermsHeadingForDelivery">
							<xsl:with-param name="construedTermsLinkId" select="'&admindecisionsPTABconstruedTermsId;'" />
							<xsl:with-param name="isExpanded" select="false()" />
							<xsl:with-param name="linkToBrowserPageText" select="'&PTABLinkToBrowserPageText;'" />
						</xsl:call-template>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="&docketsReportSection;">
						<xsl:call-template name="CreateConstruedTermsHeadingForBrowserPage">
							<xsl:with-param name="construedTermType" select="'PTAB'" />
							<xsl:with-param name="arrowClass" select="'&iconGreyRightArrow;'" />
							<xsl:with-param name="construedTermsDiscloserId" select="'&admindecisionsPTABconstruedTermsId;'" />
						</xsl:call-template>
					</div>
				</xsl:otherwise>
			</xsl:choose>
  </xsl:template>
  
  <!-- END Construed Terms -->

</xsl:stylesheet>