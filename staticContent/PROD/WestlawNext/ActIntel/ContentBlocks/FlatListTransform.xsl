<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<!-- List item processing template -->
	<xsl:template name="ListItems">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>
	
	<!-- Transform a flat list to a valid html list -->
	<xsl:template name="TransformFlatListToHTMLList">
		<xsl:param name="ulClass" select="false()" />

		<xsl:variable name="thisListName" select="name()" />
		<ul>
			<xsl:if test="$ulClass">
				<xsl:attribute name="class">
					<xsl:value-of select="$ulClass" />	
				</xsl:attribute>
			</xsl:if>
			
			<!-- Element after the end of a flat list -->
			<xsl:variable name="thisListEnd" select="./following-sibling::*[name() != $thisListName][1]" />

			<xsl:choose>
				<xsl:when test="name($thisListEnd) = ''">
					<!-- List items are available till the end of a node -->
					<xsl:for-each select=". | ./following-sibling::*">
						<xsl:call-template name="ListItems" />
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<!-- List ends in a middle of a node -->

					<!-- Select all elements of a flat list -->
					<xsl:for-each select=". | ./following-sibling::*[name() = $thisListName and following-sibling::*[name() != $thisListName][1] = $thisListEnd]">
						<xsl:call-template name="ListItems" />
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</ul>
	</xsl:template>
</xsl:stylesheet>
