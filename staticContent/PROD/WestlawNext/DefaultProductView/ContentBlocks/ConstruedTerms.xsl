<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template name="CreateConstruedTermsHeadingForBrowserPage">
		<xsl:param name ="construedTermType" />
		<xsl:param name ="arrowClass" />
		<xsl:param name ="construedTermsDiscloserId" />

		<div class="&docketsReportHeading;">
			<a id="{$construedTermsDiscloserId}" class="&documentPostLoadAppTextClass;" tabindex="0">
				<span class="&icon25; {$arrowClass}">&construedTermsDiscloserHiddenText;</span>
				<span id="&construedTermsLabelId;" class="&widgetToggleLabel;">&construedTermsHeadingText;</span>
			</a>
			<xsl:choose>
				<xsl:when test="$construedTermType='Markman'">
					<a class="&navToCategoryPageOnClickClass;" href="/Browse/Home/IntellectualProperty/MarkmanConstruedTermsIndex?transitionType=Default&amp;contextData=(sc.Default)">
						<span>&markmanTermsIndexLinkText;</span>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<a class="&navToCategoryPageOnClickClass;" href="/Browse/Home/IntellectualProperty/PTABConstruedTermsIndex?transitionType=Default&amp;contextData=(sc.Default)">
						<span>&PTABTermsIndexLinkText;</span>
					</a>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template name="CreateConstruedTermsHeadingForDelivery">
		<xsl:param name ="construedTermsLinkId" />
		<xsl:param name ="isExpanded" />
		<xsl:param name ="linkToBrowserPageText" />

		<xsl:choose>
			<xsl:when test="$isExpanded">
				<div style="font-weight:700;background-color:rgb(238,238,238);padding: 6px 14px;" >
					<a id="{$construedTermsLinkId}" >
						<xsl:text>&construedTermsHeadingText;</xsl:text>
					</a>
					<!-- Do not deliver Index link -->
				</div>
			</xsl:when>
			<xsl:otherwise>
				<div style="border-bottom:1px solid;border-bottom-color:rgb(218,218,218);font-weight:700;padding:6px;background-color:rgb(238,238,238)">
					<a id="{$construedTermsLinkId}" class="&documentPostLoadAppTextClass;"> </a>
					<a href="" class="&documentPostLoadAppTextClass;">
						<xsl:attribute name="href">
							<xsl:call-template name="GetDocumentUrl">
								<xsl:with-param name="documentGuid" select="$Guid" />
							</xsl:call-template>
						</xsl:attribute>
						<xsl:value-of select="$linkToBrowserPageText" />
						<span style="color:#145da4"></span>
					</a>
					<!-- Do not deliver Index link -->
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="CreateConstruedTermsListForDelivery">
		<xsl:param name ="construedTermLinkPrefix" />
		<div style="padding: 12px 14px;">
			<xsl:for-each select="construed.term">
				<xsl:sort select="internal.reference"/>
				<xsl:variable name="mlink" select="anchor/@ID"></xsl:variable>
				<xsl:variable name="alink" select="internal.reference/@refid"></xsl:variable>
				<div class="&documentPostLoadAppTextClass;">
				
					<a id="{concat($construedTermLinkPrefix, $mlink)}" />
					<a class="&documentPostLoadAppTextClass;" href="{concat('#', $construedTermLinkPrefix, $alink)}">
						<xsl:value-of select="internal.reference"/>
					</a>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template name="CreateConstruedTermsListForBrowserPage">
		<xsl:param name ="construedTermLinkPrefix" />

		<ul id="&markmanTermsListId;" class="&docketsReportBody; &docketsHasPadding;">
		<xsl:for-each select="construed.term">
			<xsl:sort select="internal.reference"/>
			<xsl:variable name="mlink" select="anchor/@ID"></xsl:variable>
			<xsl:variable name="alink" select="internal.reference/@refid"></xsl:variable>
			<li>
				<a id="{concat($construedTermLinkPrefix, $mlink)}" href="{concat('#', $construedTermLinkPrefix, $alink)}" class="&documentPostLoadAppTextClass;">
					<span class="&ellipsisClass; &documentPostLoadAppTextClass;">
						<xsl:value-of select="internal.reference"/>
					</span>
				</a>
			</li>
		</xsl:for-each>
		</ul>
	</xsl:template>
</xsl:stylesheet>
