<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:pcl="http://xmlgraphics.apache.org/fop/extensions/pcl"
                exclude-result-prefixes="html">
	<!-- Import the platform FO transform so we can override named template-->
	<xsl:import href="xhtml2fo.xsl" forceDefaultProduct="true"/>

	<xsl:output method="xml"
          version="1.0"
          encoding="UTF-8"
          indent="no"
          omit-xml-declaration="yes" />

	<!-- Including KnowHow as platform list have RIGHT aligned bullets. -->
	<xsl:template match="html:ul[contains(@class, '&coAssetList;')]" priority="1">
		<xsl:for-each select="./node()[name() = 'li']">
			<fo:list-block>
				<xsl:call-template name="getUlList"/>
			</fo:list-block>
		</xsl:for-each>
	</xsl:template>

	<!--Convert list to table fo resource history in doc and rtf for correct display of bottom border-->
	<xsl:template name="ResourceHistoryTable">
		<fo:table>
			<fo:table-column>
				<xsl:attribute name="column-width">
					<xsl:value-of select='"178mm"'/>
				</xsl:attribute>
			</fo:table-column>
			<fo:table-body>
				<xsl:for-each select="./node()[name() = 'li']">
					<fo:table-row keep-together.within-page="always">
						<fo:table-cell padding-top="12pt" border-bottom-style="solid" border-bottom-width="1pt">
							<fo:block>
								<xsl:apply-templates/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--Override base template for list-->
	<xsl:template match="html:ul">
		<xsl:if test="html:div">
			<xsl:apply-templates select="html:div" mode="list" />
		</xsl:if>
		<xsl:variable name="content">
			<fo:list-block>
				<xsl:call-template name="process-common-attributes-and-children"/>
			</fo:list-block>
		</xsl:variable>
		<xsl:if test="string-length($content) &gt; 0 ">
			<xsl:choose>
				<xsl:when test="ancestor::html:div[contains(@class, '&coDocRevHisoryDetail;')] and ($isRTF or $isMSWord)">
					<xsl:call-template name="ResourceHistoryTable"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$content"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="html:div[@id='co_eodTextTable']" priority="1">
		<fo:table>
			<fo:table-column column-width="69mm"/>
			<fo:table-column column-width="40mm"/>
			<fo:table-column column-width="69mm"/>
			<fo:table-body text-align="center">
				<fo:table-row keep-together.within-page="always">
					<fo:table-cell border-top="solid 1px #979797">
						<fo:block>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border="solid 1px #979797">
						<fo:block>
							<xsl:text>&endOfDocumentText;</xsl:text>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-top="solid 1px #979797">
						<fo:block>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--Override tamplate for links in resource history block to content in document when 'Only pages with terms'is checked-->
	<xsl:template match="html:a[(string-length(@href) &gt; 0) and ancestor::html:div[contains(@class, '&coDocRevHisoryDetailDeliveryOnlyPagesWithTems;')]]" priority="3">
		<xsl:choose>
			<xsl:when test="starts-with(@href,'#')">
				<fo:inline>
					<xsl:apply-templates />
				</fo:inline>
			</xsl:when>
			<xsl:when test="$hrefLimit and string-length(@href) &gt; number($hrefLimit)">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="process-a-link"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildListItemBodyContents">
		<xsl:choose>
			<xsl:when test="//html:div[@id = '&documentClass;'][contains(@class, '&keyLegalConceptsDocumentClass;')]">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="//html:div[contains(@class, '&topicOverviewDocClass;')]">
						<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<fo:block>
					<xsl:apply-templates />
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="process-rowspan-attribute">
		<xsl:if test="@rowspan">
			<xsl:attribute name="number-rows-spanned">
				<xsl:value-of select="@rowspan"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template  priority="1" match="html:p">
		<xsl:choose>
			<xsl:when test="../@class = 'askCommentBody'">
				<fo:block xsl:use-attribute-sets="p" linefeed-treatment="preserve">
					<xsl:call-template name="process-common-attributes-and-children"/>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<fo:block xsl:use-attribute-sets="p">
					<xsl:call-template name="process-common-attributes-and-children"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- initial paragraph, preceded by h1..6 or div -->
	<xsl:template priority="1" match="html:p[preceding-sibling::*[1][self::html:h1 or self::html:h2 or self::html:h3 or self::html:h4 or self::html:h5 or self::html:h6 or self::html:div]]">
		<xsl:choose>
			<xsl:when test="../@class = 'askCommentBody'">
				<fo:block xsl:use-attribute-sets="p-initial" linefeed-treatment="preserve">
					<xsl:call-template name="process-common-attributes-and-children"/>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<fo:block xsl:use-attribute-sets="p-initial">
					<xsl:call-template name="process-common-attributes-and-children"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- initial paragraph, first child of div, body or td -->
	<xsl:template priority="1" match="html:p[not(preceding-sibling::*) and (parent::html:div or parent::html:body or parent::html:td)]">
		<xsl:choose>
			<xsl:when test="../@class = 'askCommentBody'">
				<fo:block xsl:use-attribute-sets="p-initial-first" linefeed-treatment="preserve">
					<xsl:call-template name="process-common-attributes-and-children"/>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<fo:block xsl:use-attribute-sets="p-initial-first">
					<xsl:call-template name="process-common-attributes-and-children"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
