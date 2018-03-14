<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="CitesForCaselaw.xsl" forcePlatform="true" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Variables primaryCite, firstParallelCite, regionalCite are defined in OriginalImage.xsl -->
	<xsl:variable name="nRSCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@type = 'West_special']
																			| /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@type = 'West_court_special']"/>
	<xsl:variable name="displayParallelAtTop" select="not($primaryCite) or $primaryCite[md.display.primarycite/@display = 'N' or md.display.primarycite/@status]" />
	<xsl:variable name="displayableParallelCites" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y']" />

	<xsl:template match="md.cites" mode="top">
		<div class="&citesClass;">
			<xsl:choose>
				<xsl:when test="$regionalCite">
					<xsl:apply-templates select="$regionalCite" />
				</xsl:when>
				<xsl:when test="$primaryCite/md.display.primarycite[@display = 'N' or @status = 'nr' or @status = 'slip' or @status = 'dash']">
					<xsl:apply-templates select="$firstParallelCite"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$primaryCite" />
				</xsl:otherwise>
			</xsl:choose>
		</div>

		<!-- For CWR paragraph pinpointing -->
		<xsl:call-template name="InjectParaNumbersSourceMetadata" />
	</xsl:template>

	<xsl:variable name="allCitations">
		<xsl:for-each select="citationOrderFunction:GetCites(//md.cites/*)/*">
			<xsl:apply-templates select="." />
			<xsl:if test="position() != last()">
				<xsl:text>,<![CDATA[ ]]></xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>

	<xsl:template match="md.cites" mode="bottom">
		<xsl:if test="string-length($allCitations) &gt; 0 and not($PreviewMode)">
			<div class="&allCitationsClass;">
				<h2 id="&allCitationsId;" class="&allCitationsBlockLabelClass; &printHeadingClass;">
					<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&allCitationsTitleKey;', '&allCitationsTitle;')"/>
				</h2>
				<xsl:copy-of select="$allCitations" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="InjectParaNumbersSourceMetadata">
		<xsl:variable name="possibleParaNumbersSources" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info[md.display.primarycite/@display = 'Y' or md.display.primarycite/@userEntered = 'Y']
																| /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y']" />
		<xsl:call-template name="SelectParaNumbersSource">
			<xsl:with-param name="possibleParaNumbersSources" select="$possibleParaNumbersSources"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
