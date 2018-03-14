<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="AnalyticalReferenceBlock.xsl"/>
	<xsl:include href="CustomTitleAndCourtBlock.xsl"/>
	<xsl:include href="Attorney.xsl"/>
	<xsl:include href="Synopsis.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Headnote.xsl"/>
	<xsl:include href="TribalCourtHeadnote.xsl"/>
	<xsl:include href="WestlawDescription.xsl"/>
	<xsl:include href="Caveat.xsl"/>
	<xsl:include href="CaselawOpinionBlock.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCaselawWPADCClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.descriptions/md.westlawdescrip/md.doc.caveats" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="top" />
			<xsl:apply-templates select="n-docbody/node()"/>
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="bottom" />
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="md.references">
		<xsl:choose>
			<xsl:when test="$Toggle = 'True'">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="md.print.rendition.id" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="title.block[not(following-sibling::title.block)]" priority="2">
		<xsl:apply-templates select="../court.block" mode="customCourtAndTitle" />
		<xsl:call-template name="titleBlock" />
		<xsl:variable name="docketDateContents">
			<xsl:apply-templates select="../docket.block" mode="customCourtAndTitle" />
			<xsl:apply-templates select="../date.block" mode="customCourtAndTitle" />
		</xsl:variable>
		<xsl:if test="string-length($docketDateContents) &gt; 0">
			<div class="&docketDateClass;">
				<xsl:copy-of select="$docketDateContents"/>
			</div>
		</xsl:if>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Date.Line -->
	<xsl:template match="date.line | docket.line">
		<xsl:choose>
			<xsl:when test="child::justified.line">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithSpan"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="date.line/justified.line | docket.line/justified.line">
		<xsl:call-template name="wrapWithSpan"/>
	</xsl:template>

	<xsl:variable name="primaryCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info" />
	<xsl:variable name="nRSCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@type = 'West_special']
																			| /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@type = 'West_court_special']"/>
	<xsl:variable name="firstParallelCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite[1]/md.parallelcite.info/md.display.parallelcite"/>
	<xsl:variable name="displayParallelAtTop" select="not($primaryCite) or $primaryCite[md.display.primarycite/@display = 'N' or md.display.primarycite/@status]" />
	<xsl:variable name="displayableParallelCites" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info[md.display.parallelcite/@display = 'Y' or md.display.parallelcite/@userEntered = 'Y']" />

	<xsl:template match="md.cites" mode="top">
		<div class="&citesClass;">
			<xsl:choose>
				<xsl:when test="$primaryCite/md.display.primarycite[@status = 'nr' or @status = 'slip' or @status = 'dash']">
					<xsl:apply-templates select="$firstParallelCite"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="not($firstParallelCite) or $firstParallelCite[@type = 'Westlaw']">
							<xsl:apply-templates select="$primaryCite" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="$firstParallelCite" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:variable name="parallelCites">
		<xsl:choose>
			<xsl:when test="$nRSCite/md.display.parallelcite/@display = 'Y'">
				<xsl:variable name="renderedPrimaryCite">
					<xsl:apply-templates select="$primaryCite" />
				</xsl:variable>
				<xsl:copy-of select="$renderedPrimaryCite" />
				<xsl:for-each select="$displayableParallelCites">
					<xsl:if test="not(self::node()[md.display.parallelcite/@type = 'West_special'] or self::node()[md.display.parallelcite/@type = 'West_court_special'])">
						<xsl:text>,<![CDATA[ ]]></xsl:text>
						<xsl:apply-templates select="." />
					</xsl:if>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="$displayableParallelCites">
					<xsl:if test="($displayParallelAtTop and position() != 1) or not($displayParallelAtTop)">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

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

</xsl:stylesheet>