<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CitesForCaselaw.xsl"/>
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
	<xsl:include href="DateAndDocketLineForCaselaw.xsl"/>
	<xsl:include href="References.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCaselawNRSClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.references" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="top" />
			<xsl:apply-templates select="n-metadata/metadata.block/md.descriptions/md.westlawdescrip/md.doc.caveats" />
			<xsl:apply-templates select="n-docbody/node()"/>
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="bottom" />
			<xsl:call-template name="RenderFootnoteSection"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="title.block[1]" priority="2">
		<xsl:apply-templates select="../court.block"  mode="customCourtAndTitle" />
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

	<xsl:template match="title.block[preceding-sibling::title.block]" priority="2"/>

	<xsl:template match="md.toggle.links" priority="1">
		<xsl:if test="not($DeliveryMode)">
			<div>
				<xsl:apply-templates select ="md.toggle.link[1]" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="md.print.rendition.id">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and not($DisplayOriginalImageLink)">
				<!-- Do nothing -->
			</xsl:when>
			<xsl:when test="not($DisplayOriginalImageLink)">
				<!-- Do nothing -->
			</xsl:when>
			<xsl:otherwise>
				<!-- We need to wrap the link in Div in order to properly display in delivered RTF document -->
				<div>
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="."/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'" />
						<xsl:with-param name="contents">
							<xsl:text>&originalDocumentLinkText; </xsl:text>
							<xsl:choose>
								<xsl:when test="$regionalCite">
									<xsl:apply-templates select="$regionalCite" />
								</xsl:when>
								<xsl:when test="$primaryCite/md.display.primarycite[@status = 'nr' or @status = 'slip' or @status = 'dash'] or $firstParallelCite[@type='West_regional' and @display='Y']">
									<xsl:apply-templates select="$firstParallelCite"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:apply-templates select="$Cite" />
								</xsl:otherwise>
							</xsl:choose>
							<xsl:text> &pdfLabel;</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
						<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
						<xsl:with-param name="originationContext" select="'&docOriginalImageOriginationContext;'" />
						<xsl:with-param name="prettyName" select="translate($Cite//text(),'&space;', '&lowline;')" />
					</xsl:call-template>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="headtext" priority="1">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
			<xsl:with-param name="extraClass">
				<xsl:choose>
					<xsl:when test="parent::head/@ampexmnem = 'jm.ph1h'">&indentLeft1Class;</xsl:when>
					<xsl:when test="parent::head/@ampexmnem = 'jm.ph2h'">&indentLeft2Class;</xsl:when>
					<xsl:when test="parent::head/@ampexmnem = 'jm.ph3h'">&indentLeft3Class;</xsl:when>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>