<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:pcl="http://xmlgraphics.apache.org/fop/extensions/pcl">

  <xsl:output method="xml" indent="no" encoding="utf-8" omit-xml-declaration="yes" />

	<xsl:template match="/">
			<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="fo:table[not(ancestor::fo:static-content)]">
		<xsl:choose>
			<xsl:when test="ancestor::fo:table or contains(@id, 'headnotesTable') or contains(@id, 'co_inlineFootnote') or contains(@id, 'co_note_') or ancestor::fo:list-item-body">
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="guts">
					<xsl:call-template name="BuildCurrentElementAndAttributes">
						<xsl:with-param name="node" select="."/>
					</xsl:call-template>					
					
					<xsl:apply-templates/>
					
					<xsl:call-template name="BuildCurrentElementCloseTag">
						<xsl:with-param name="node" select="."/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="CloseAndOpenTagsWithAttributeThenCloseAndReOpen">
					<xsl:with-param name="guts" select="$guts"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="fo:block[@breakDualColumn = 'true' and not(ancestor::fo:static-content)]">
		<xsl:variable name="guts">
			<xsl:call-template name="BuildCurrentElementAndAttributes">
				<xsl:with-param name="node" select="."/>
			</xsl:call-template>

			<xsl:apply-templates/>

			<xsl:call-template name="BuildCurrentElementCloseTag">
				<xsl:with-param name="node" select="."/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="CloseAndOpenTagsWithAttributeThenCloseAndReOpen">
			<xsl:with-param name="guts" select="$guts"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fo:external-graphic">
		<xsl:choose>
			<!-- Do not do anything for the footer logo -->
			<xsl:when test="not(ancestor :: fo:static-content[@flow-name='xsl-region-after'])">
				<fo:block></fo:block><!-- try to move the image down so that it is displayed in its entirety, and not truncated. -->
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise><!-- logo in footer: keep as-is -->
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template name="BuildCurrentElementCloseTag">
		<xsl:param name="node"/>

		<xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
		<xsl:value-of select="name($node)"/>
		<xsl:text disable-output-escaping="yes">&gt;</xsl:text>

	</xsl:template>

	<xsl:template name="BuildCurrentElementAndAttributes">
		<xsl:param name="node"/>

		<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
		<xsl:value-of select="name($node)"/>
		<xsl:for-each select="$node/@*">
			<xsl:if test="name() != 'id' and name() != 'breakDualColumn'">
				<xsl:text xml:space="preserve"> </xsl:text>
				<xsl:value-of select="name()"/>
				<xsl:text disable-output-escaping="yes">="</xsl:text>
				<xsl:value-of select="translate(., '&quot;', '')"/>
				<xsl:text disable-output-escaping="yes">"</xsl:text>
			</xsl:if>
		</xsl:for-each>
		<xsl:text disable-output-escaping="yes">&gt;</xsl:text>

	</xsl:template>
	
	<xsl:template name="closeTags">
		<xsl:param name="node"/>
		<xsl:param name="closeTagList"/>
		<xsl:param name="openTagList"/>
		<xsl:param name="guts"/>
			
		<xsl:choose>
		<xsl:when test="not(name($node) = 'fo:flow')">

			<xsl:variable name="newCloseTagList">
				<xsl:copy-of select="$closeTagList"/>		
				<xsl:call-template name="BuildCurrentElementCloseTag">
					<xsl:with-param name="node" select="$node"/>
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="newOpenTagList">
				<xsl:call-template name="BuildCurrentElementAndAttributes">
					<xsl:with-param name="node" select="$node"/>
				</xsl:call-template>
				<xsl:copy-of select="$openTagList"/>
			</xsl:variable>

			<xsl:call-template name="closeTags">
				<xsl:with-param name="node" select="$node/.."/>
				<xsl:with-param name="closeTagList" select="$newCloseTagList"/>
				<xsl:with-param name="openTagList" select="$newOpenTagList"/>
				<xsl:with-param name="guts" select="$guts"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="$closeTagList"/>
				<fo:block span="all">
					<xsl:copy-of select="$guts"/>
				</fo:block>
				<xsl:copy-of select="$openTagList"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
  <xsl:template name="CloseAndOpenTagsWithAttributeThenCloseAndReOpen">
		<xsl:param name="guts"/>
		<xsl:call-template name="closeTags">
			<xsl:with-param name="node" select=".."/>
			<xsl:with-param name="guts" select="$guts"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>

