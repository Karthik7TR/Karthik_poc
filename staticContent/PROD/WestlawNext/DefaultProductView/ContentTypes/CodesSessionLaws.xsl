<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--
  	NOTE:
  	Unlike most Codes/Statutes, Session Laws should NOT be disabling the default renderings for added.material or deleted.material elements.
  	See Bug #131337.
  -->
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesSessionLawsClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="renderKeyCiteSNTPlaceholder"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="FooterCitation" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<!--For Bug#692255-->
	<xsl:template match="ed.note">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<!--Suppress this element for BUG#692255-->
	<xsl:template match="cornerpiece.type"/>

	<!-- Message.Block/Message -->
	<xsl:template match="message.block/message">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="law.front[not(following-sibling::law.front)]">
		<xsl:call-template name="wrapContentBlockWithAdditionalCobaltClasses">
			<xsl:with-param name="additionalClass">
				<xsl:text>&centerClass;</xsl:text>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- suppresses all but the first content.metadata.block -->
	<xsl:template match="content.metadata.block[preceding::content.metadata.block]" />

	<!-- Display citation at the end of document of USCCAN collections-->
	<xsl:template name="FooterCitation">
		<xsl:variable name="citation">
			<xsl:choose>
				<xsl:when test="/Document/document-data/collection = 'w_codesslplnvdp'">
					<xsl:value-of select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="string-length($citation) &gt; 0">
			<div class="&citationClass;">
				<xsl:value-of	select="$citation"	/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="analysis">
		<div>
			<xsl:for-each select="analysis.entry">
				<xsl:apply-templates select="analysis.text"/>
				<br/>
			</xsl:for-each>
		</div>
	</xsl:template>

  <!-- signature.block/signature.line-->
  <xsl:template match="signature.block/signature.line">
    <div>
      <xsl:apply-templates />
    </div>
  </xsl:template>
		
		
	
	<!-- Star Pages -->
	<xsl:template match="starpage" priority="5">
		
		<xsl:if test="starpage.anchor">
			
			<xsl:call-template name="displayStarPage">
				<xsl:with-param name="starPageText" select="starpage.anchor/text()" />
				<xsl:with-param name="numberOfStars" select="1" />
				<xsl:with-param name="pageset" select="starpage.anchor/@ID" />
			</xsl:call-template>

			<xsl:if test="$IncludeCopyWithRefLinks = true()">
				<xsl:call-template name="generateCopyWithReferenceLink" />
			</xsl:if>
		
		</xsl:if>	
	</xsl:template>
	
		
	<xsl:template match="starpage.anchor" priority="5">
				<xsl:call-template name="displayStarPage">
				<xsl:with-param name="starPageText" select="text()" />
				<xsl:with-param name="numberOfStars" select="1" />
			<xsl:with-param name="pageset" select="@ID" /></xsl:call-template>
			<xsl:if test="$IncludeCopyWithRefLinks = true()">
				<xsl:call-template name="generateCopyWithReferenceLink" />
			</xsl:if>
	
	</xsl:template>

	<xsl:template name="render-sibling-name-designator" priority="5">
		<xsl:if test="preceding-sibling::label.name">
			<xsl:apply-templates select="preceding-sibling::label.name"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:if test="preceding-sibling::label.designator">
			<xsl:apply-templates select="preceding-sibling::label.designator"/>
				<xsl:if test="count(preceding-sibling::text()[normalize-space()!='']) > 0">
					<xsl:variable name="theText" select="normalize-space(string(preceding-sibling::text()[1]))"/>
					<xsl:variable name="normalizedText" select="translate($theText, '&#x20;&#x9;&#xD;&#xA;&emsp;', '')" />
					<xsl:value-of select="$normalizedText"/>
				</xsl:if>
				<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>
	
</xsl:stylesheet>
