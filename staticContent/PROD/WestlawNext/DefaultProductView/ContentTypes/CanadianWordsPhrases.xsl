<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="CanadianDate.xsl"/>
	<xsl:include href="CanadianFancyTable.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:include href="CanadianOutline.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- DO NOT RENDER -->
	<xsl:template match="message.block.carswell"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswWordsAndPhrasesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />

			<div class="&headnotesClass; &centerClass;">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites | n-docbody/db.primarycite"/>
				<xsl:apply-templates select="n-docbody/wordphrase.block/wordphrase.block.title"/>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<xsl:call-template name="RenderFootnoteSection"/>

			<xsl:apply-templates select="n-docbody/wordphrase.block/node()[not(self::wordphrase.block.title | self::message.block.carswell)]"/>

			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="wordphrase.block.title">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="wordphrase.block/wordphrase">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&crswWordPhraseClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="seealso | see">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&crswSeeAlsoClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="seealso/wordphrase | see/wordphrase">
		<xsl:text><![CDATA[  ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="wordphrase.subjtitls"/>

	<xsl:template match="wordphrase.group/jurisdics">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&crswJurisdictionClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="wordphrase.body">
		<xsl:apply-templates select="./node()[not(self::subjtitls | self::caseref)]"/>
		<xsl:apply-templates select="./caseref"/>
		<xsl:apply-templates select="./subjtitls"/>
	</xsl:template>

	<xsl:template match="wordphrase.body/caseref">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="wordphrase.body/subjtitls">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&crswSubjectClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="subjtitl">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::*[1][self::subjtitl]">
			<xsl:text>;<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="underscore">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="quote">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&crswQuoteClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="quote/p[@Indent]">
		<xsl:variable name="indent" select="@Indent"/>

		<div style="margin-left: {$indent}cm;">
			<xsl:choose>
				<xsl:when test="@align='center'">
					<xsl:attribute name="class">
						<xsl:value-of select="'&paraMainClass; &alignHorizontalCenterClass;'"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="@align='right'">
					<xsl:attribute name="class">
						<xsl:value-of select="'&paraMainClass; &alignHorizontalRightClass;'"/>
					</xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="class">
						<xsl:value-of select="'&paraMainClass;'"/>
					</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="paranum | subjtitls">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

</xsl:stylesheet>
