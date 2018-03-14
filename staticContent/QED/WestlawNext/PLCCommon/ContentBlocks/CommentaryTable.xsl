<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CommentaryTable.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:key name="Tables" match="tbl[(@id or @ID) and .//*[self::thead[ancestor::table[1][not(@rowsep or @colsep or @frame)]] or @rowsep = '1' or @colsep = '1' or (string-length(@frame) > 0 and not(@frame = 'none'))]]" use="@id | @ID"/>

	<xsl:template match="tgroup" priority="1">
		<xsl:call-template name="BeforeRenderTable"/>
		<xsl:call-template name="TGroupTemplate">
			<xsl:with-param name="additionalClass">
				<xsl:if test="($IsCommentaryEnhancementMode) and key('Tables', ancestor::tbl[1]/@id | ancestor::tbl[1]/@ID)">
					<xsl:value-of select="'&borderedTableClass;'"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="BeforeRenderTable"/>

</xsl:stylesheet>
