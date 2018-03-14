<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocLinks.xsl" forcePlatform="true"/>
	<xsl:include href="UKGeneralBlocks.xsl"/>

	<xsl:template name="BuildJournalTocContent">
		<xsl:variable name="AbstractUrl">
			<xsl:choose>
				<xsl:when test="//md.references/md.locatordoc">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name="documentGuid" select="//md.references/md.locatordoc/@href" />
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="FullTextUrl">
			<xsl:choose>
				<xsl:when test="//md.references/md.fulltext">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name="documentGuid" select="//md.references/md.fulltext/@href" />
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="not($DeliveryMode)">
			<xsl:choose>
				<xsl:when test="(//md.references/md.locatordoc/@href) or (//md.infotype!='&fullTextType;') ">
					<xsl:call-template name="WriteTocItem">
						<xsl:with-param name="TocItemAnchor">
							<xsl:choose>
								<xsl:when test="//md.infotype!='&fullTextType;'">
									<xsl:value-of select="'#'" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$AbstractUrl" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="IsAnchor" select="false()"/>
						<xsl:with-param name="TocItemCaption" select="'&abstractText;'"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>

			<xsl:choose>
				<xsl:when test="(//md.infotype='&fullTextType;') or (//md.references/md.fulltext/@href)">
					<xsl:call-template name="WriteTocItem">
						<xsl:with-param name="TocItemAnchor">
							<xsl:choose>
								<xsl:when test="//md.infotype='&fullTextType;'">
									<xsl:value-of select="'#'" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$FullTextUrl" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="IsAnchor" select="false()"/>
						<xsl:with-param name="TocItemCaption" select="'&fullTextText;'"/>
						<xsl:with-param name="TocItemClose" select="false()"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>

			<xsl:if test=".//journal-section/article/level1/title">
				<xsl:call-template name="WriteTocListOpen">
					<xsl:with-param name="includeClass" select="''"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>

		<xsl:if test="$DeliveryMode">
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemCaption" select="'&coTableOfContentsHeaderText;'"/>
			</xsl:call-template>
			<xsl:if test="count(.//journal-section/article/level1/title)=0">
				<xsl:call-template name="WriteTocItem">
					<xsl:with-param name="TocItemCaption" select="'&deliveryNoTocText;'"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>

		<xsl:for-each select =".//journal-section/article/level1/title/bold[not(ital)]">
			<xsl:call-template name="WriteTocItem">
				<xsl:with-param name="TocItemAnchor">
					<xsl:call-template name="anchor"/>
				</xsl:with-param>
				<xsl:with-param name="TocItemCaption">
					<xsl:apply-templates select="./text()" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:for-each>

		<xsl:if test="not($DeliveryMode)">
			<xsl:if test=".//journal-section/article/level1/title">
				<xsl:call-template name="WriteTocListClose"/>
			</xsl:if>
			<xsl:if test="(//md.infotype='&fullTextType;') or (//md.references/md.fulltext/@href)">
				<xsl:call-template name="WriteTocItemClose"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BuildDocumentTocContent">
		<xsl:call-template name="WriteTocListOpen">
			<xsl:with-param name="includeClass" select="'&coKhTocOlList; &ukResearchToCList;'"/>
		</xsl:call-template>
		<xsl:call-template name="BuildJournalTocContent" />		
		<xsl:call-template name="WriteTocListClose"/>
	</xsl:template>

	<xsl:template name ="anchor">
		<xsl:variable name="positionId">
			<xsl:number count="title" format="1" level="any"/>
		</xsl:variable>
		<xsl:value-of select="concat('&internalLinkIdPrefix;', $positionId)"/>
	</xsl:template>

</xsl:stylesheet>