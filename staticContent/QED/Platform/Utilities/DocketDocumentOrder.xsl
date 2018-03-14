<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
	<xsl:include href="Universal.xsl"/>

	<xsl:template match="Document">
		<xsl:text>{</xsl:text>
		<xsl:call-template name="renderDocumentOrderingInformation" />
		<xsl:text>"orderItems":[</xsl:text>
		<xsl:apply-templates select="/Document/n-docbody/r/docket.proceedings.block | /Document/n-docbody/r/docket.entries.block | /Document/n-docbody/r/wcn.complaint.block/wcn.complaint" />
		<xsl:text>]}</xsl:text>
	</xsl:template>

	<xsl:template name="renderDocumentOrderingInformation">
		<xsl:variable name="title">
			<xsl:apply-templates select="/Document/n-docbody/r/case.information.block/title.block/primary.title//text() | /Document/n-docbody/r/title.block/primary.title//text() | /Document/n-docbody/r/court.info.block/title.block/primary.title//text()"/>
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

	<xsl:template match="docket.proceedings.block">
		<xsl:for-each select="docket.entry">
			<xsl:choose>
				<xsl:when test="docket.description/docket.entry.description.block/image.block" />
				<xsl:otherwise>
					<xsl:variable name="index">
						<xsl:choose>
							<xsl:when test="send.runner.link">
								<xsl:value-of select="send.runner.link/@indexvalue" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="position()"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="date">
						<xsl:apply-templates select="docket.date" />
					</xsl:variable>
					<xsl:variable name="number">
						<xsl:apply-templates select=".//docket.entry.number" />
					</xsl:variable>
					<xsl:variable name="description">
						<xsl:apply-templates select="docket.description" />
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
					<xsl:text>"}</xsl:text>
					<xsl:if test="position() != last()">
						<xsl:text>,</xsl:text>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="docket.entries.block">
		<xsl:for-each select="docket.entry">
			<xsl:choose>
				<xsl:when test="number.block/image.block" />
				<xsl:otherwise>
					<xsl:variable name="index">
						<xsl:choose>
							<xsl:when test="send.runner.link">
								<xsl:value-of select="send.runner.link/@indexvalue" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="position()"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="date">
						<xsl:apply-templates select="date" />
					</xsl:variable>
					<xsl:variable name="number">
						<xsl:apply-templates select="number.block/number" />
					</xsl:variable>
					<xsl:variable name="description">
						<xsl:apply-templates select="docket.description" />
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
					<xsl:text>"}</xsl:text>
					<xsl:if test="position() != last()">
						<xsl:text>,</xsl:text>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="wcn.complaint.block/wcn.complaint">
		<xsl:choose>
			<xsl:when test="not(/Document/n-docbody/r/docket.proceedings.block)">
				<xsl:variable name="filingdate">
				<xsl:call-template name="FilingsDate">
					<xsl:with-param name="date" select="/Document/n-docbody/r/case.information.block/filing.date.block/filing.date/text()" />
				</xsl:call-template>
				</xsl:variable>
				<xsl:text>{"index": "0</xsl:text>
				<xsl:text>","date":"</xsl:text><xsl:value-of select="$filingdate"/>
				<xsl:text>","number": "</xsl:text>
				<xsl:text>","description": "COMPLAINT REGARDING CASE : </xsl:text>
				<xsl:call-template name="json-encode">
					<xsl:with-param name="str" select="/Document/n-docbody/r/case.information.block/title.block/primary.title/text() | /Document/n-docbody/r/title.block/primary.title//text() | /Document/n-docbody/r/court.info.block/title.block/primary.title/text()" />
				</xsl:call-template>
				<xsl:text>"}</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
<xsl:template name="FilingsDate">
		<xsl:param name="date" />
				<xsl:variable name ="year" select="substring($date,1,4)"/>
				<xsl:variable name ="month" select="substring($date,5,2)"/>
				<xsl:variable name ="day" select="substring($date,7,2)"/>
				<xsl:value-of select="$month"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$day"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$year"/>
				</xsl:template>
</xsl:stylesheet>