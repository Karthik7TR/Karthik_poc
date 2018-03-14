<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLConnect.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template name="DocumentByWestlaw">
		<xsl:if test="not($IsRuleBookMode)">
			<div class="&documentByWestlawClass;">
				<span>
					<i>	&documentByText; </i>
					<strong> &documentByWestlawText; </strong>
				</span>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="AddDocumentClasses">
		<xsl:param name="contentType"/>
		<xsl:attribute name="class">
			<xsl:text>&documentClass;</xsl:text>
			<xsl:if test="string-length($contentType) &gt; 0">
				<xsl:value-of select="concat(' ', $contentType)"/>
			</xsl:if>
			<xsl:if test="$DeliveryMode">
				<xsl:if test="string-length($DeliveryFormat) &gt; 0">
					<xsl:value-of select="concat(' co_', $DeliveryFormat)"/>
				</xsl:if>
				<xsl:if test="$LinkColor = '&linkColorBlack;'">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:text>&blackLinksClass;</xsl:text>
				</xsl:if>
				<xsl:if test="$LinkUnderline">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:text>&linkUnderlineClass;</xsl:text>
				</xsl:if>
				<xsl:if test="$FontSize = '&fontSizeLarge;'">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:text>&largeFontClass;</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test="string-length($SourceSerial) &gt; 0 or string-length($Quotes) &gt; 0">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:text>&citingRefSearchTerms;</xsl:text>
			</xsl:if>
			<xsl:if test="$PreviewMode">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:text>&previewClass;</xsl:text>
			</xsl:if>
			<xsl:if test="$EasyEditMode">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:text>&easyEditClass;</xsl:text>
			</xsl:if>
			<xsl:call-template name="AddProductDocumentClasses" />
		</xsl:attribute>
		<!-- This needs to be the first thing in the document from a transformation perspective. -->
		<xsl:call-template name="AddIconsforIPad"/>
		<!--If deliverymode and ListItemIdentifier present then add ex: <span id=DocGuid_ListItemIdentifier_x> _x appended to end of id comes from uniqueDocumentID - Website will include this in target links"-->
		<xsl:if test="$DeliveryMode and string-length($ListItemIdentifier) &gt; 0">
			<span>
				<xsl:attribute name="id">
					<xsl:value-of select="$Guid"/>
					<xsl:text>_</xsl:text>
					<xsl:value-of select="$ListItemIdentifier"/>
				</xsl:attribute>
			</span>
		</xsl:if>
		<xsl:call-template name="DocumentByWestlaw" />
	</xsl:template>
	
</xsl:stylesheet>

