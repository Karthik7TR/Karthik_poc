<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses" />
			<div class="&sazanamiMinchoClass; &contentTypeCopyrightClass; &contentTypeIPDocumentClass;">
				<xsl:call-template name="displayCopyrightHeader" />
				<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				<table class="&layout_table;">
					<xsl:apply-templates />
				</table>
				<xsl:call-template name="EndOfDocument" />
			</div>
		</div>
	</xsl:template>

	<!-- Do not display these elements. -->
	<xsl:template match="unprocessed.bucket | work.type.code | regis.class.code | md.first.line.cite | cmd.first.line.cite" />
	<xsl:template match="status.b" priority="1" />
	
	<xsl:template name="displayCopyrightHeader">
		<table class="&layout_table;">
			<tr>
				<td>
					<div>
						<h2>
							<xsl:apply-templates select="//md.ip.title" mode="displayCopyrightHeader" />
						</h2>
					</div>
					<div>
						<xsl:choose>
							<xsl:when test="//content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite and string-length(normalize-space(//content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite)) &gt; 0">
								<xsl:value-of select="//content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite"/>
							</xsl:when>
							<xsl:when test="//content.metadata.block/cmd.identifiers/cmd.cites/cmd.second.line.cite and string-length(normalize-space(//content.metadata.block/cmd.identifiers/cmd.cites/cmd.second.line.cite)) &gt; 0">
								<xsl:value-of select="//content.metadata.block/cmd.identifiers/cmd.cites/cmd.second.line.cite"/>
							</xsl:when>
							<xsl:when test="//content.metadata.block/cmd.identifiers/cmd.cites/cmd.third.line.cite and string-length(normalize-space(//content.metadata.block/cmd.identifiers/cmd.cites/cmd.third.line.cite)) &gt; 0">
								<xsl:value-of select="//content.metadata.block/cmd.identifiers/cmd.cites/cmd.third.line.cite"/>
							</xsl:when>
						</xsl:choose>
					</div>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="copyright.record/node()[not(self::content.metadata.block) and not(self::prelim) and not(self::unprocessed.bucket) and not(self::status.b)]" priority="1">
		<tr class="&borderTopClass;">
			<td>
				<xsl:apply-templates select="./label"/>
			</td>
			<td>
				<xsl:apply-templates select="./node()[not(name() = 'label')]" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="prelim" />
	<xsl:template match="prelim/doc.title" />
	<xsl:template match="prelim/doc.title" mode="displayCopyrightHeader">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="prelim/source" />
	<xsl:template match="prelim/source" mode="displayCopyrightHeader">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="label">
		<div>
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="node()[preceding-sibling::*[preceding-sibling::label]] | app.author.res | app.author.citzn | app.author.nature">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="node()[label[1]] | app.author[position() > 1]">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="name.b/role">
		<xsl:text>;<![CDATA[ ]]></xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="app.author.name | app.author.dates | app.author.work.type">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="claimant/*">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="imprint.b/*[position() > 1]">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="regis.no">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::regis.date">
			<xsl:text><![CDATA[ ]]> / <![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="regis.date[preceding-sibling::regis.no]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="publisher">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::publisher.no">
			<xsl:text><![CDATA[ ]]> / <![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="publisher.no[preceding-sibling::publisher]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="claimant[preceding-sibling::claimant]">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- The following two matches go together.  The first one hides the regis.class.b block if there is a code but no description.
			The second template match will hide the label for regis.class.b since it still gets displayed by a base stylesheet otherwise. -->
	<xsl:template match="regis.class.b">
		<xsl:if test="regis.class.code and regis.class">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="regis.class.b/label[following-sibling::regis.class = '']" priority="1" />

</xsl:stylesheet>