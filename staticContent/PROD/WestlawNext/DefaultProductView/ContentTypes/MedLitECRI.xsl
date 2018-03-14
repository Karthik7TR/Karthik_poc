<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="FootnoteBlock.xsl"/>
	<xsl:include href="Para.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeMedLitECRIClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="DisplayPublisherLogo"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument"/>
			<xsl:call-template name="DisplayPublisherLogo"/>
		</div>
	</xsl:template>

	<xsl:template match="ihta.doc/title.block" priority="1">
		<div class="&ihtaTitleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match ="date.line/date" priority="1">
		<xsl:choose>
			<xsl:when test="string-length(.) = 8 and not(number(.) = NaN)">
				<xsl:call-template name="parseYearMonthDayDateFormat">
					<xsl:with-param name="displayDay" select="'true'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="reference" priority="1">
		<xsl:call-template name="para" />
	</xsl:template>
	
	<xsl:template match="reference/label.name | author.block/label.name | title.block/label.name | list.item[label.designator]/para/label.name" priority="1">
		<strong>
			<xsl:apply-templates />
			<xsl:text><![CDATA[ ]]></xsl:text>
		</strong>
	</xsl:template>

	<xsl:template match="author.block" priority="1">
		<xsl:if test="string-length(author.name) &gt; 0">
			<xsl:call-template name="author" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="title.block" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="title.line[preceding-sibling::label.name]" priority="2">
		<xsl:call-template name="wrapWithSpan"/>
	</xsl:template>

	<xsl:template match="message.block/include.copyright" priority="1">
		<div class="&copyrightClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="para/para" priority="1">
		<xsl:call-template name="nestedParas">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="list/list.item/para[preceding-sibling::node()[1][self::para]]" priority="2">
		<xsl:call-template name="para" />
	</xsl:template>

	<!-- Mark Nordstrom - Fixed bug #702508 (1/15/2015) - Example Guid: If69bab08203311dca51ecfdfa1ed2cd3 -->
	<xsl:template match="list.item/label.designator[following-sibling::node()[1]/self::para]" priority="2" />

	<!-- Mark Nordstrom - Fixed bug #702508 (1/15/2015) - Example Guid: If69bab08203311dca51ecfdfa1ed2cd3 -->
	<xsl:template match="para[parent::list.item and preceding-sibling::node()[1]/self::label.designator]" priority="2">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&paraMainClass;</xsl:text>
			</xsl:attribute>
			<span class="&labelClass;">
				<xsl:value-of select="preceding-sibling::node()[1]/self::label.designator" />
			</span>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="para/label.name[following-sibling::node()[1]/self::list]">
		<xsl:apply-templates />
	</xsl:template>

</xsl:stylesheet>
