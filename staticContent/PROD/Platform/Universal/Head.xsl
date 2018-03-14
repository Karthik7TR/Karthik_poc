<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="head[not(following-sibling::paratext)] | prop.head | form.head | fa.head" name="head">
		<xsl:param name="divId">
			<xsl:if test="string-length(@id|@ID) &gt; 0">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
			</xsl:if>
		</xsl:param>
		<xsl:if test=".//text() or .//textrule or .//predefined.charstring">
			<xsl:choose>
				<xsl:when test="headtext or form.headtext">
					<xsl:apply-templates select="node()[not(self::label.name) and not(self::label.designator)]">
						<xsl:with-param name="divId" select="$divId"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="head.info">
					<xsl:apply-templates>
						<xsl:with-param name="divId" select="$divId"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="predefined.charstring">
					<xsl:apply-templates/>
				</xsl:when>
				<xsl:when test=".//head">
					<xsl:apply-templates />
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test=".//textrule/@position = 'midline'">
							<div>
								<xsl:apply-templates />
							</div>
						</xsl:when>
						<xsl:otherwise>
							<!--<h2>
								<xsl:apply-templates />
							</h2>-->
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="head.info[not(justified.line/headtext)]">
		<xsl:param name="divId"/>
		<xsl:choose>
			<xsl:when test="headtext">
				<xsl:apply-templates select="headtext">
					<xsl:with-param name="divId" select="$divId"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates>
					<xsl:with-param name="divId" select="$divId"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="headtext | form.headtext">
		<xsl:param name="divId"/>
		<xsl:call-template name="renderHeadTextDiv">
			<xsl:with-param name="divId" select="$divId"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="renderHeadTextDiv">
		<xsl:param name="divId"/>
		<xsl:param name="extraClass"/>
		<xsl:param name="contents" />
		<xsl:if test=".//text() or .//textrule or string-length($contents) &gt; 0">
			<div>
				<xsl:if test="string-length($divId) &gt; 0">
					<xsl:attribute name="id">
						<xsl:value-of select="$divId"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="class">					
					<xsl:text>&headtextClass;</xsl:text>
					<xsl:choose>
						<xsl:when test="@indent-left and @indent-left &gt; 0">
							<xsl:text><![CDATA[ ]]>&paraIndentLeftClass;</xsl:text>
							<xsl:value-of select="@indent-left"/>
							<xsl:text><![CDATA[ ]]>&alignHorizontalLeftClass;</xsl:text>
						</xsl:when>
						<xsl:when test="@style = 'c' or ancestor::head/@style = 'c' or @align='center' or ancestor::head/@align = 'center' or ancestor::fa.head/@align = 'center'">
							<xsl:text><![CDATA[ ]]>&alignHorizontalCenterClass;</xsl:text>
						</xsl:when>
						<xsl:when test="ancestor::form.head/@style = 'c' or ancestor::form.head/@align = 'center' or (ancestor::form.head and $EasyEditMode)">
							<xsl:text><![CDATA[ ]]>&alignHorizontalCenterClass;</xsl:text>
						</xsl:when>
						<xsl:when test="@style = 'l' or ancestor::head/@align = 'left'">
							<xsl:text><![CDATA[ ]]>&alignHorizontalLeftClass;</xsl:text>
						</xsl:when>
						<xsl:otherwise />
					</xsl:choose>
					<xsl:if test="string-length($extraClass) &gt; 0">
						<xsl:value-of select="concat(' ', $extraClass)"/>				
					</xsl:if>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="string-length($contents) &gt; 0">
						<xsl:copy-of select="$contents"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="render-sibling-name-designator"/>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="render-sibling-name-designator">
		<xsl:if test="preceding-sibling::label.name">
			<xsl:apply-templates select="preceding-sibling::label.name"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:if test="preceding-sibling::label.designator">
			<xsl:apply-templates select="preceding-sibling::label.designator"/>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="/Document/n-docbody/doc/prop.block/prop.head/headtext/endline">
		<br/>
	</xsl:template>

	<xsl:template match="headnote.block.head/headtext | keysummary.block.head/headtext">
		<xsl:apply-templates/>
	</xsl:template>

  <xsl:template match="head[@level]" name="renderHeadLevel">
    <xsl:choose>
      <xsl:when test="@level = '1' or @level = '2' or @level = '3'">
	      <xsl:call-template name="displayHeadingElement">
	        <xsl:with-param name="headingSize" select="@level"/>
	      </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <!--In case of @level attribute is empty-->
        <xsl:call-template name="displayHeadingElement">
          <xsl:with-param name="headingSize" select="1"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="displayHeadingElement">
    <xsl:param name="headingSize" />
    <xsl:element name="h{$headingSize}">
      <xsl:attribute name="class">
        <xsl:text>&documentHeader; &alignHorizontalCenterClass; &indentTopClass; &indentBottomClass;</xsl:text>    
      </xsl:attribute>
      <xsl:value-of select="./headtext/text()[normalize-space(.)!='']"/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
