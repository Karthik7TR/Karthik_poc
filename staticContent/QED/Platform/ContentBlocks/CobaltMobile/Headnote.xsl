<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Headnote.xsl"/>
	
	<xsl:template match="topic.key.hierarchy">
		<xsl:param name="generateRefKeyTable" select="true()"/>
		<xsl:variable name="topicKeyContents">
			<xsl:apply-templates select="topic.key" />
		</xsl:variable>

		<xsl:if test="string-length($topicKeyContents) &gt; 0">
			<xsl:variable name="topicKeyRefKey">
				<xsl:apply-templates select="descendant::topic.key.ref/key" />
			</xsl:variable>
			<xsl:variable name="topicKeyRefKeyText">
				<xsl:apply-templates select="descendant::topic.key.ref/keytext" />
			</xsl:variable>
			<xsl:variable name="priorClassification">
				<xsl:apply-templates select="../prior.classification" />
			</xsl:variable>

			<div class="&headnoteTopicsClass;">
				<xsl:call-template name="startUnchunkableBlock" />
		
				<xsl:call-template name="RenderKeyIconImage">
					<xsl:with-param name="generateRefKeyTable" select="$generateRefKeyTable"/>
				</xsl:call-template>
		
				<!-- TODO: These processing instructions are only used for Mobile, and their only purpose to remove the contents between them... why don't we just NOT output the contents here to avoid twice as much work? -->
				<xsl:processing-instruction name="startTopicKeyHierarchy"/>

	<!-- startPI was here -->
				<xsl:call-template name="RenderTopicKeyHierarchy">
					<xsl:with-param name="generateRefKeyTable" select="$generateRefKeyTable"/>
					<xsl:with-param name="topicKeyContents" select="$topicKeyContents"/>
					<xsl:with-param name="topicKeyRefKey" select="$topicKeyRefKey"/>
					<xsl:with-param name="topicKeyRefKeyText" select="$topicKeyRefKeyText"/>
					<xsl:with-param name="priorClassification" select="$priorClassification"/>
				</xsl:call-template>

		<!-- endPI was here -->
				<!-- TODO: These processing instructions are only used for Mobile, and their only purpose to remove the contents between them... why don't we just NOT output the contents here to avoid twice as much work? -->
				<xsl:processing-instruction name="endTopicKeyHierarchy"/>				
				
				<xsl:call-template name="endUnchunkableBlock" />
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>