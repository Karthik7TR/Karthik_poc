<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:if test="contains(//md.wl.database.identifier/text(), 'ALM')">
				<xsl:call-template name="DisplayPublisherLogo">
					<xsl:with-param name="PublisherType" select="'&PublisherALMExperts;'" />
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:call-template name="EndOfDocument" />
			<xsl:if test="contains(//md.wl.database.identifier/text(), 'ALM')">
				<xsl:call-template name="DisplayPublisherLogo">
					<xsl:with-param name="PublisherType" select="'&PublisherALMExperts;'" />
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>

	<!-- Override the image.link element so the attachment does not download with the document, 
			this was done for the collection w_3rd_almexcv.  None of the other collections in the 
			Document Content Types have this element in their document XML. -->
	<xsl:template match="n-docbody/doc/image.block" priority="2">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div class="&centerClass; &messageBlockClass;">
					<xsl:text>&em_AttachmentNotDeliveredText;</xsl:text>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="imageBlock"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="doc">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | so | dl | ti | ti2 | dj | dj1 | gh1 | gh2 | gh3 | gh4 | gh5 | hcb"/>
		</div>
		<xsl:apply-templates select="*[not(self::hg0 or self::hg1 or self::hg2 or self::hg3 or self::hg4 or self::so or self::dl or self::ti or self::ti2 or self::dj or self::dj2 or self::cr or self::gh1 or self::gh2 or self::gh3 or self::gh4 or self::gh5 or self::hcb)]" />
	</xsl:template>

	<xsl:template match="gh1 | gh2 | gh3 | gh4 | gh5 | hcb">
		<xsl:apply-templates />
		<xsl:if test="not(following-sibling::til or following-sibling::gh1 or following-sibling::gh2 or following-sibling::gh3 or following-sibling::gh4 or following-sibling::gh5 or following-sibling::hcb)">
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>

	<xsl:template match="centv | so"/>
</xsl:stylesheet>