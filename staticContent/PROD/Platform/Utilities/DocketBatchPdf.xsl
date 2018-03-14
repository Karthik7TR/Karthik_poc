<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
 
	<xsl:template match="Document">
		<xsl:text>{</xsl:text>
		<xsl:call-template name="renderBatchPdfInformation" />
		<xsl:text>"pdfItems":[</xsl:text>
		<xsl:apply-templates select="/Document/n-docbody/r/docket.entries.block | /Document/n-docbody/r/docket.proceedings.block | /Document/n-docbody/r/available.image.block" />
		<xsl:text>]}</xsl:text>
	</xsl:template>

	<xsl:template name="renderBatchPdfInformation">
		<xsl:variable name="title">
			<xsl:apply-templates select="/Document/n-docbody/r/case.information.block/title.block/primary.title/text() | /Document/n-docbody/r/title.block/primary.title//text() | /Document/n-docbody/r/court.info.block/title.block/primary.title/text()"/>
		</xsl:variable>
		<xsl:variable name="court">
			<xsl:apply-templates select="/Document/n-docbody/r/case.information.block/court.block/court/text() | /Document/n-docbody/r/court.block/court/text() | /Document/n-docbody/r/court.info.block/court.block/court/text()"/>
		</xsl:variable>
		<xsl:variable name="county">
			<xsl:apply-templates select="/Document/n-docbody/r/case.information.block/court.block/filing.county/text()"/>
		</xsl:variable>
		<xsl:variable name="state">
			<xsl:apply-templates select="/Document/n-docbody/r/full.state/text()"/>
		</xsl:variable>
		<xsl:variable name="courtForDisplay">
			<xsl:copy-of select="$court"/>
			<xsl:if test="string-length($county) &gt; 0">
				<xsl:text>, </xsl:text>
				<xsl:copy-of select="$county"/>
				<xsl:text> COUNTY</xsl:text>
			</xsl:if>
			<xsl:if test="string-length($state) &gt; 0">
				<xsl:text>, </xsl:text>
				<xsl:copy-of select="$state"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="number">
			<xsl:apply-templates select="/Document/n-docbody/r/case.information.block/docket.block/docket.number/text() | /Document/n-docbody/r/docket.block/docket.number/text() | /Document/n-docbody/r/case.information.block/docket.number.block/docket.number/text() | /Document/n-docbody/r/court.info.block/docket.block/docket.number/text()"/>
		</xsl:variable>
		<xsl:text>"title":"</xsl:text>
		<xsl:call-template name="json-encode">
			<xsl:with-param name="str" select="$title" />
		</xsl:call-template>
		<xsl:text>","court":"</xsl:text>
		<xsl:value-of select="$courtForDisplay"/>
		<xsl:text>","caseNumber":"</xsl:text>
		<xsl:value-of select="$number"/>
		<xsl:text>",</xsl:text>
	</xsl:template>
  
  <xsl:template match="docket.description">
    <xsl:apply-templates select="document.description.block" />
    <xsl:apply-templates select="docket.entry.description.block" />
  </xsl:template>

  <xsl:template match="docket.entry.description.block">
    <xsl:apply-templates select="label" />
    <xsl:text><![CDATA[ ]]></xsl:text>
    <xsl:apply-templates select="docket.entry.description" />
    <xsl:text><![CDATA[ ]]></xsl:text>
  </xsl:template>

  <xsl:template match="document.description.block">
    <xsl:apply-templates select="label" />
    <xsl:text><![CDATA[ ]]></xsl:text>
    <xsl:apply-templates select="document.description" />
    <xsl:text><![CDATA[ ]]></xsl:text>
  </xsl:template>

	<xsl:template match="docket.proceedings.block">
		<xsl:for-each select="docket.entry">
			<xsl:choose>
				<xsl:when test="docket.entry.number.block/image.block/image.gateway.link">
					<xsl:variable name="date">
						<xsl:apply-templates select="docket.date" />
					</xsl:variable>
					<xsl:variable name="number">
						<xsl:apply-templates select="docket.entry.number.block/number" />
					</xsl:variable>
					<xsl:variable name="description">
						<xsl:apply-templates select="docket.description" />
					</xsl:variable>
					<xsl:variable name="index">
						<xsl:value-of select="position()"/>
					</xsl:variable>
					<xsl:variable name="courtNumber" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisnum" />
					<xsl:variable name="court">
						<xsl:choose>
							<xsl:when test="string-length(docket.entry.number.block/image.block/image.gateway.link/@court) &gt; 0">
								<xsl:value-of select="docket.entry.number.block/image.block/image.gateway.link/@court"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$courtNumber"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="courtNorm">
						<xsl:call-template name="getDocketCourtNorm" />
					</xsl:variable>
					<xsl:variable name="platform" select="docket.entry.number.block/image.block/image.gateway.link/@platform" />
					<xsl:variable name="id" select="docket.entry.number.block/image.block/image.gateway.link/@image.ID" />
					<xsl:variable name="truncatedId" 
					select="substring-before(docket.entry.number.block/image.block/image.gateway.link/@image.ID, ';')"/>
					<xsl:variable name="caseNumber" select="docket.entry.number.block/image.block/image.gateway.link/@casenumber" />
					<xsl:text>{"index":"</xsl:text>
					<xsl:value-of select="$index"/>
					<xsl:text>","date":"</xsl:text>
					<xsl:value-of select="$date"/>
					<xsl:text>","number":"</xsl:text>
					<xsl:value-of select="$number"/>
					<xsl:text>","description":"</xsl:text>
					<xsl:call-template name="json-encode">
						<xsl:with-param name="str" select="$description" />
					</xsl:call-template>
					<xsl:text>","platform":"</xsl:text>
					<xsl:value-of select="$platform"/>
					<xsl:text>","court":"</xsl:text>
					<xsl:value-of select="$court"/>
					<xsl:text>","courtNorm":"</xsl:text>
					<xsl:value-of select="$courtNorm"/>					
					<xsl:text>","courtNumber":"</xsl:text>
					<xsl:value-of select="$courtNumber"/>
					<xsl:text>","revertPipeSeparator":"</xsl:text>
					<xsl:value-of select="contains($truncatedId, '|')"/>						
					<xsl:text>","id":"</xsl:text>
					<xsl:value-of select="translate($truncatedId, '|', ',')"/>
					<xsl:text>","caseNumber":"</xsl:text>
					<xsl:value-of select="$caseNumber"/>
					<xsl:text>","pdfUrl":"</xsl:text>
					<xsl:call-template name="createGatewayBlobHref">
						<xsl:with-param name="court" select="$court"/>
						<xsl:with-param name="courtNumber" select="$courtNumber"/>
						<xsl:with-param name="casenumber" select="$caseNumber"/>
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="filename">
							<xsl:call-template name="createPdfFilename">
								<xsl:with-param name="cite" select="/Document/document-data/cite"/>
								<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'"/>
								<xsl:with-param name="date" select="date"/>
								<xsl:with-param name="number" select="docket.entry.number.block/number"/>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="platform" select="$platform"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
						<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
					</xsl:call-template>
					<xsl:text>"}</xsl:text>
					<xsl:if test="position() != last()">
						<xsl:text>,</xsl:text>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<!-- specific to MA Supreme and Appellate Courts.  commented out items are NOT in the XML. -->
	<xsl:template match="available.image.block">
		<xsl:for-each select="available.image">
			<xsl:choose>
				<xsl:when test="image.block/image.gateway.link">
					<!--<xsl:variable name="date">
						<xsl:apply-templates select="docket.date" />
					</xsl:variable>-->
					<!--<xsl:variable name="number">
						<xsl:apply-templates select="docket.entry.number.block/number" />
					</xsl:variable>-->
					<xsl:variable name="description">
						<xsl:apply-templates select="available.image.description" />
					</xsl:variable>
					<xsl:variable name="index">
						<xsl:value-of select="position()"/>
					</xsl:variable>
					<xsl:variable name="courtNumber" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisnum" />
					<xsl:variable name="court">
						<xsl:choose>
							<xsl:when test="string-length(image.block/image.gateway.link/@court) &gt; 0">
								<xsl:value-of select="image.block/image.gateway.link/@court"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$courtNumber"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="courtNorm">
						<xsl:call-template name="getDocketCourtNorm" />
					</xsl:variable>
					<xsl:variable name="platform" select="image.block/image.gateway.link/@platform" />
					<xsl:variable name="id" select="image.block/image.gateway.link/@image.ID" />
					<xsl:variable name="truncatedId"
					select="substring-before(image.block/image.gateway.link/@image.ID, ';')"/>
					<xsl:variable name="caseNumber" select="image.block/image.gateway.link/@casenumber" />
					<xsl:text>{"index":"</xsl:text>
					<xsl:value-of select="$index"/>
					<!--<xsl:text>","date":"</xsl:text>
					<xsl:value-of select="$date"/>-->
					<!--<xsl:text>","number":"</xsl:text>
					<xsl:value-of select="$number"/>-->
					<xsl:text>","description":"</xsl:text>
					<xsl:call-template name="json-encode">
						<xsl:with-param name="str" select="$description" />
					</xsl:call-template>
					<xsl:text>","platform":"</xsl:text>
					<xsl:value-of select="$platform"/>
					<xsl:text>","court":"</xsl:text>
					<xsl:value-of select="$court"/>
					<xsl:text>","courtNorm":"</xsl:text>
					<xsl:value-of select="$courtNorm"/>
					<xsl:text>","courtNumber":"</xsl:text>
					<xsl:value-of select="$courtNumber"/>
					<xsl:text>","revertPipeSeparator":"</xsl:text>
					<xsl:value-of select="contains($truncatedId, '|')"/>
					<xsl:text>","id":"</xsl:text>
					<xsl:value-of select="translate($truncatedId, '|', ',')"/>
					<xsl:text>","caseNumber":"</xsl:text>
					<xsl:value-of select="$caseNumber"/>
					<xsl:text>","pdfUrl":"</xsl:text>
					<xsl:call-template name="createGatewayBlobHref">
						<xsl:with-param name="court" select="$court"/>
						<xsl:with-param name="courtNumber" select="$courtNumber"/>
						<xsl:with-param name="casenumber" select="$caseNumber"/>
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="filename">
							<xsl:call-template name="createPdfFilename">
								<xsl:with-param name="cite" select="/Document/document-data/cite"/>
								<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'"/>
								<!--<xsl:with-param name="date" select="date"/>-->
								<!--<xsl:with-param name="number" select="docket.entry.number.block/number"/>-->
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="platform" select="$platform"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
						<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
					</xsl:call-template>
					<xsl:text>"}</xsl:text>
					<xsl:if test="position() != last()">
						<xsl:text>,</xsl:text>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="docket.entries.block">
		<xsl:for-each select="docket.entry">
			<xsl:choose>
				<xsl:when test="number.block/image.block">
					<xsl:variable name="date">
						<xsl:apply-templates select="date" />
					</xsl:variable>
					<xsl:variable name="number">
						<xsl:apply-templates select="number.block/number" />
					</xsl:variable>
					<xsl:variable name="description">
						<xsl:apply-templates select="docket.description/text()" />
					</xsl:variable>
					<xsl:variable name="index">
						<xsl:value-of select="position()"/>
					</xsl:variable>
					<xsl:variable name="courtNumber" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisnum" />
					<xsl:variable name="court">
						<xsl:choose>
							<xsl:when test="string-length(number.block/image.block/image.gateway.link/@court) &gt; 0">
								<xsl:value-of select="number.block/image.block/image.gateway.link/@court"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$courtNumber"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="courtNorm">
						<xsl:call-template name="getDocketCourtNorm" />
					</xsl:variable>					
					<xsl:variable name="platform" select="number.block/image.block/image.gateway.link/@platform" />
					<xsl:variable name="id" select="number.block/image.block/image.gateway.link/@image.ID" />
					<xsl:variable name="truncatedId" 
					select="substring-before(number.block/image.block/image.gateway.link/@image.ID, ';')" />
					<xsl:variable name="caseNumber" select="number.block/image.block/image.gateway.link/@casenumber" />
					<xsl:variable name="hasAttachments">
						<xsl:choose>
							<xsl:when test="docket.description/image.gateway.link[@item.type='ATTACHMENT']">
								<xsl:value-of select="true()"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="false()"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:text>{"index":"</xsl:text>
					<xsl:value-of select="$index"/>
					<xsl:text>","date":"</xsl:text>
					<xsl:value-of select="$date"/>
					<xsl:text>","number":"</xsl:text>
					<xsl:value-of select="$number"/>
					<xsl:text>","description":"</xsl:text>
					<xsl:call-template name="json-encode">
						<xsl:with-param name="str" select="$description" />
					</xsl:call-template>
					<xsl:text>","platform":"</xsl:text>
					<xsl:value-of select="$platform"/>
					<xsl:text>","court":"</xsl:text>
					<xsl:value-of select="$court"/>
					<xsl:text>","courtNorm":"</xsl:text>
					<xsl:value-of select="$courtNorm"/>
					<xsl:text>","courtNumber":"</xsl:text>
					<xsl:value-of select="$courtNumber"/>
					<xsl:text>","revertPipeSeparator":"</xsl:text>
					<xsl:value-of select="contains($truncatedId, '|')"/>
					<xsl:text>","id":"</xsl:text>
					<xsl:value-of select="translate($truncatedId, '|', ',')"/>
					<xsl:text>","caseNumber":"</xsl:text>
					<xsl:value-of select="$caseNumber"/>
					<xsl:text>","pdfUrl":"</xsl:text>
					<xsl:call-template name="createGatewayBlobHref">
						<xsl:with-param name="court" select="$court"/>
						<xsl:with-param name="courtNumber" select="$courtNumber"/>
						<xsl:with-param name="casenumber" select="$caseNumber"/>
						<xsl:with-param name="id" select="$id"/>
						<xsl:with-param name="filename">
							<xsl:call-template name="createPdfFilename">
								<xsl:with-param name="cite" select="/Document/document-data/cite"/>
								<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'"/>
								<xsl:with-param name="date" select="date"/>
								<xsl:with-param name="number" select="number.block/number"/>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="platform" select="$platform"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
						<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
					</xsl:call-template>
					<xsl:text>&amp;attachments=</xsl:text>
					<xsl:value-of select="$hasAttachments"/>
					<xsl:text>&amp;isFromBatchDownload=</xsl:text>
					<xsl:value-of select="true()"/>
					<xsl:text>","exhibitItems":[</xsl:text>
					<xsl:apply-templates select="docket.description/image.gateway.link" />
					<xsl:text>]}</xsl:text>
					<xsl:if test="position() != last()">
						<xsl:text>,</xsl:text>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="docket.description/image.gateway.link">
		<xsl:variable name="description" select="following-sibling::text()" />
		<xsl:variable name="index">
			<xsl:value-of select="position()"/>
		</xsl:variable>
		<xsl:variable name="courtNumber" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.jurisnum" />
		<xsl:variable name="court">
			<xsl:choose>
				<xsl:when test="string-length(@court) &gt; 0">
					<xsl:value-of select="@court"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$courtNumber"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="platform" select="@platform" />
		<xsl:variable name="id" select="@image.id|@image.ID" />
		<xsl:variable name="truncatedId" 
		select="substring-before(@image.id|@image.ID, ';')" />		
		<xsl:variable name="number" select="text()" />
		<xsl:variable name="caseNumber" select="@casenumber" />
		<xsl:variable name="poundSign">
			<xsl:variable name="precedingText" select="preceding-sibling::text()" />
			<xsl:variable name="precedingText2" select="preceding-sibling::text()[preceding-sibling::gateway.image.link]" />
			<xsl:choose>
				<xsl:when test="string-length($precedingText) &gt; 0 and contains($precedingText, '#')">
					<xsl:variable name="afterPoundString" select="substring-after($precedingText, '#')"/>
					<xsl:if test="string-length($afterPoundString) = 0">
						<xsl:text>#</xsl:text>
					</xsl:if>
				</xsl:when>
				<xsl:when test="string-length($precedingText2) &gt; 0 and contains($precedingText2, '#')">
					<xsl:variable name="afterPoundString2" select="substring-after($precedingText2, '#')"/>
					<xsl:if test="string-length($afterPoundString2) = 0">
						<xsl:text>#</xsl:text>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>{"index":"</xsl:text>
		<xsl:value-of select="$index"/>
		<xsl:text>","description":"</xsl:text>
		<xsl:call-template name="json-encode">
			<xsl:with-param name="str" select="$description" />
		</xsl:call-template>
		<xsl:text>","revertPipeSeparator":"</xsl:text>
		<xsl:value-of select="contains($truncatedId, '|')"/>		
		<xsl:text>","id":"</xsl:text>
		<xsl:value-of select="translate($truncatedId, '|', ',')"/>
		<xsl:text>","number":"</xsl:text>
		<xsl:value-of select="concat($poundSign, $number)"/>
		<xsl:text>","pdfUrl":"</xsl:text>
		<xsl:call-template name="createGatewayBlobHref">
			<xsl:with-param name="court" select="$court"/>
			<xsl:with-param name="courtNumber" select="$courtNumber"/>
			<xsl:with-param name="casenumber" select="$caseNumber"/>
			<xsl:with-param name="id" select="$id"/>
			<xsl:with-param name="filename">
				<xsl:call-template name="createPdfFilename">
					<xsl:with-param name="cite" select="/Document/document-data/cite"/>
					<xsl:with-param name="baseName" select="'&docketEntryBasePdfFilename;'"/>
					<xsl:with-param name="date" select="ancestor::docket.entry/date"/>
					<xsl:with-param name="number" select="concat(ancestor::docket.entry/number.block/number, '-', ./text())"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="platform" select="@platform"/>
			<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
			<xsl:with-param name="originationContext" select="'&docDisplayOriginationContext;'"/>
		</xsl:call-template>
		<xsl:text>&amp;isFromBatchDownload=true</xsl:text>
		<xsl:text>"}</xsl:text>
		<xsl:if test="position() != last()">
			<xsl:text>,</xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>