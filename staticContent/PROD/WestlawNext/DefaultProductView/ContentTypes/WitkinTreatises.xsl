<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Treatises.xsl" />
	<xsl:include href="CommentaryTable.xsl" />
	<xsl:include href="CommentaryCommon.xsl" />

	<xsl:template match="Document">
		<xsl:apply-templates select="." mode="CheckEasyEdit">
			<xsl:with-param name="contentType">
				<xsl:call-template name="GetCommentaryDocumentClasses"/>
				<xsl:choose>
					<xsl:when test="$IsCommentaryEnhancementMode">
						<xsl:value-of select="' &contentTypeTreatisesClass; &commentaryDocumentEnhancementClass;'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="' &contentTypeTreatisesClass;'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="displayPublisherLogo" select="true()"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- Witkin displays nested <para> elements in a special way. -->
	<!-- Witkin needs its own stylesheet so that we don't make checks for collection name in our stylesheets. -->
	<!-- BUG 618922 - Indent nest paragraphs -->
	<xsl:template match="para" priority="1">
		<xsl:call-template name="nestedParas"/>
	</xsl:template>
	
</xsl:stylesheet>