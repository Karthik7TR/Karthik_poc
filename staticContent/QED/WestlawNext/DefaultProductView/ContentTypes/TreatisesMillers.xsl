<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">	
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeTreatisesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<xsl:template match="doc.title[not(following-sibling::sub.title)]" priority="1">
		<xsl:call-template name="titleBlock"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<xsl:template match="sub.title[preceding-sibling::doc.title]">
		<xsl:apply-templates />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<xsl:template match="prop.head[not(following::doc.title)][last()]" priority="1">
		<xsl:apply-templates />
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<xsl:template match="prop.head[last()]/headtext" priority="1">
		<xsl:variable name="cite">
			<xsl:apply-templates select="//cmd.cites"/>
		</xsl:variable>
		<xsl:if test="$cite != .">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>
	
	<!-- Render the Pdf link -->
	<xsl:template match="md.print.rendition.id">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and not($DisplayOriginalImageLink)">
				<!-- Do nothing -->
			</xsl:when>
			<xsl:otherwise>
				<!-- We need to wrap the link in Div in order to properly display in delivered RTF document -->
				<div>
					<xsl:variable name="pdfLinkName">
						<xsl:choose>
							<xsl:when test="//pdf.link">
								<xsl:apply-templates select="//pdf.link"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates select="/Document/document-data/cite"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="pdfSource">
						<xsl:choose>
							<xsl:when test="/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.pdf.source">
								<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.identifiers/md.westlawids/md.pdf.source"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="translate(/Document/document-data/cite//text(),'&space;', '&lowline;')"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
						
					<xsl:call-template name="createDocumentBlobLink">
						<xsl:with-param name="guid" select="."/>
						<xsl:with-param name="targetType" select="@ttype"/>
						<xsl:with-param name="mimeType" select="'&pdfMimeType;'" />
						<xsl:with-param name="contents">
							<xsl:value-of select="$pdfLinkName"/>
						</xsl:with-param>
						<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
						<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
						<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
						<xsl:with-param name="originationContext" select="'&docOriginalImageOriginationContext;'" />
						<xsl:with-param name="prettyName" select="$pdfSource" />
					</xsl:call-template>
				</div>
			</xsl:otherwise>		
		</xsl:choose>
	</xsl:template>

	<xsl:template match="party.para | language.para | annotation/caselaw.link | policy.link | annotation/para">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="annotations.block/annotation">
		<xsl:call-template name="para"/>
	</xsl:template>

	<xsl:template match="definition">
		<xsl:call-template name="wrapContentBlockWithGenericClass" />
	</xsl:template>

	<xsl:template match="index.link">
		<div id="co_millersIndex">
			<xsl:call-template name="categoryPageLink">
				<xsl:with-param name="id" select="cite.query/@ID"/>
				<xsl:with-param name="databaseId" select="'MILLERS-IDX'"/>
				<xsl:with-param name="linkContents">
					<xsl:apply-templates select="cite.query/text()"/>
				</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="cite.query[parent::index.link]" />
	
	<xsl:template match="policy.note | editorial.note[not(parent::policy.line)]">
		<xsl:call-template name="wrapContentBlockWithCobaltClass" />
	</xsl:template>
	
	<xsl:template match="policy.block/policy">
		<table>		
			<xsl:apply-templates />
		</table>
	</xsl:template>
	
	<xsl:template match="policy/para | policy/head | policy/editorial.note | policy/tbl | policy.line/editorial.note | policy.line/tbl">
		<tr>
			<td class="&policyNumberColumnClass;">
				<xsl:text><![CDATA[ ]]></xsl:text>
			</td>
			<td class="&policyDefinitionColumnClass;">
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="policy.definition[not(preceding-sibling::policy.definition)]">
		<tr>
			<xsl:choose>
				<xsl:when test="preceding-sibling::policy.number">
					<xsl:apply-templates select="preceding-sibling::policy.number" mode="policyNumber"/>
				</xsl:when>
				<xsl:otherwise>			
					<td class="&policyNumberColumnClass;">
						<xsl:text><![CDATA[ ]]></xsl:text>
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<td class="&policyDefinitionColumnClass;">
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="policy.definition[preceding-sibling::policy.definition]">
		<tr>
			<td class="&policyNumberColumnClass;">
				<xsl:text><![CDATA[ ]]></xsl:text>
			</td>
			<td class="&policyDefinitionColumnClass;">
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="policy.number" mode="policyNumber">
		<td class="&policyNumberColumnClass;">
			<xsl:apply-templates/>
		</td>
	</xsl:template>
	
	<xsl:template match="policy.number"/>

	<xsl:template match="policy.definition/para">
		<div>
			<xsl:variable name="style">
				<xsl:if test="@level">
					<!--each level is indented by 20 px to the right-->
					<xsl:variable name="indentAmount" select="(number(@level) * 20)"/>
					<xsl:value-of select="'margin-left:'"/>
					<xsl:value-of select="$indentAmount"/>
					<xsl:value-of select="'px;'"/>

					<!--only bolded text or undersocred text are to be indented-->
					<xsl:variable name="stringToBeIndented">
						<xsl:value-of select="paratext/bold"/>
						<xsl:value-of select="paratext/underscore"/>
					</xsl:variable>

					<!--condition to indent the string.-->
					<xsl:if test="string-length($stringToBeIndented)!=0">
						<xsl:if test="contains($stringToBeIndented,'.') or string-length(paratext)-string-length($stringToBeIndented) &lt; 6">
							<xsl:value-of select="'text-indent: -'"/>
							<xsl:value-of select="20"/>
							<xsl:value-of select="'px;'"/>
						</xsl:if>
					</xsl:if>
				</xsl:if>
			</xsl:variable>

			<xsl:attribute name="style">
				<xsl:value-of select="$style"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="policy.line/policy.definition/para/paratext | policy.definition/line" priority="2">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="formfinder.block | policy.line/column | policy.line/pagebreak" />
	
	<xsl:template match="headtext[child::pdf.link]" />
		
</xsl:stylesheet>
