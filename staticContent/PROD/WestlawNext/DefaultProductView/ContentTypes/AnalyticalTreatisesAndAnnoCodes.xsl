<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Analysis.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="CorrelationTable.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Form.xsl"/>
	<xsl:include href="Jurisdictions.xsl"/>
	<xsl:include href="Letter.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="CommentaryCommon.xsl"/>
	<xsl:include href="CommentaryTable.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType">
					<xsl:call-template name="GetCommentaryDocumentClasses"/>
					<xsl:value-of select="' &contentTypeAnalyticalTreatisesAndAnnoCodesClass;'"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:choose>
				<xsl:when test="$EasyEditMode">
					<xsl:apply-templates select="node()" mode="EasyEdit"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="//md.form.flag">
						<xsl:call-template name="EasyEditFlag"/>
					</xsl:if>
					<xsl:call-template name="StarPageMetadata" />
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="md.references"/>

	<xsl:template match="head[@ID]" priority="1">
		<xsl:call-template name="head">
			<xsl:with-param name="divId">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID)"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- suppresses all but the first content.metadata.block and the one that contains the copyright -->
	<xsl:template match="content.metadata.block[preceding-sibling::content.metadata.block and not(cmd.royalty/cmd.copyright)]" />

	<!-- suppress the first line cite at the bottom of the document -->
	<xsl:template match="content.metadata.block/cmd.identifiers/cmd.cites/cmd.first.line.cite" />

	<xsl:template match="paratext[ancestor::list] ">
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="suppressLabel" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="doc.title" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>


	<!-- author-->
	<xsl:template match="author.line" priority="2">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- message block copyright line-->
	<xsl:template match="message.block" priority="2">
		<xsl:apply-templates />
	</xsl:template>

	<!-- head text after currentness date-->
	<xsl:template match="prop.block//prop.head[name(preceding-sibling::*[1])='date']//headtext" priority="2">
		<br/>
		<br/>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Bug #708333 change how its processing label.designator to cover cases of repeats -->
	<xsl:template match="head[ancestor::section or ancestor::appendix.body]/label.designator">
		<xsl:if test="not(following-sibling::headtext)">
				<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="head[ancestor::section or ancestor::appendix.body]/headtext" priority="2">
		<div class="&alignHorizontalLeftClass;">
			<xsl:if test="preceding-sibling::label.name">
				<xsl:apply-templates select="preceding-sibling::label.name"/>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:if test="preceding-sibling::label.designator">
				<!-- Bug #708333 change how its processing label.designator to cover cases of repeats -->
				<xsl:choose>
					<xsl:when test="preceding-sibling::label.designator/*">
						<xsl:apply-templates select ="preceding-sibling::label.designator/*"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy-of select="preceding-sibling::label.designator/text()"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="head[ancestor::section]/bop" priority="2">
		<br/>
	</xsl:template>

	<!-- para-->
	<xsl:template match="para[ancestor::section]" priority="2">
		<div class="&paraIndentLeftClass;">
			<!-- add the id attribute in the case that this element is being linked to -->
			<xsl:if test="string-length(@id|@ID) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</div>
		<br></br>
	</xsl:template>

	<!-- suppresses start pages -->
	<xsl:template match="starpage.anchor" priority="2"/>

	<!-- suppress the first content metadata block -->
	<!--<xsl:template match="content.metadata.block[count(preceding-sibling::node()) = 0]" priority="2"/>-->
	
	<!--**Med lit drug templates start**-->
	<xsl:template match ="drug.generic | drug.brand">
		<div style="text-indent:-0.25in; padding-left:0.250in;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match ="drug.use.block | drug.generic.name | drug.brand.name | drug.family.name | drug.action | drug.xref">
		<xsl:apply-templates />
		<br/>
	</xsl:template>

	<xsl:template match ="drug.use">
		<xsl:apply-templates/>
		<xsl:if test ="following-sibling::drug.use">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>
	<!--**Med lit drug templates end**-->
	
	<!-- suppress the label.designator when within codes.para --> 
	<xsl:template match="codes.para/head[following-sibling::node()[1][self::paratext] and child::label.designator]" />
</xsl:stylesheet>