<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n Completed As Of 6/27/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CanadianDocLinks.xsl"/>
	<xsl:include href="CanadianImage.xsl"/>
	<xsl:include href="WrappingUtilities.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="caption.code"/>
	<xsl:template match="supplier"/>
	<xsl:template match="subject"/>
	<xsl:template match="legalmemodoc/finaldate"/>


	<xsl:template match="supplierdisplay | caption | legalissue | facts/factscenario | lmreflists | jurisdics | date |
												facts/keywords | table.of.cases.block | reference.block | block2 | block3 | block4 | block5 | block6">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="caption/bold">

		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="contents">
				<strong>
					<xsl:value-of select="."/>
				</strong>
			</xsl:with-param>
			<xsl:with-param name="class" select="'&paraMainClass;'"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="legalmemotopics">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'"></xsl:with-param>
			<xsl:with-param name="id" select="'&crswLegalMemoTopics;'"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="legalmemolinks/p" priority="2">
		<div>
			<xsl:attribute name="class">
				<xsl:value-of select="'&paraMainClass; &headText;'"/>
			</xsl:attribute>
			<xsl:for-each select="node()">
				<!--Do nothing if node is footnote.reference we handled it previously-->
				<xsl:choose>
					<xsl:when test="name() = 'footnote.reference'"></xsl:when>
					<xsl:when test="name() = 'link'">
						<xsl:call-template name="DocumentLink">
							<xsl:with-param name="documentGuid" select="@tuuid"/>
							<xsl:with-param name="isSingleSearchDocument" select="true()"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</div>
	</xsl:template>


	<xsl:template match="facts/keywords/p" priority="2">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&crswSmallText;'"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="reflists//title | lmreflists//title | block2/title | block3/title | block4/title | block5/title | block6/title ">
		<strong>
			<xsl:apply-templates/>
		</strong>
	</xsl:template>

	<xsl:template match="reflists//cite | lmreflists//cite">
		<xsl:call-template name="wrapWithDiv">
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="reflists//refn | lmreflists//refn ">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraIndentLeftClass;'"></xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="code.reference | analytical.reference | legalmemotopic">
		<xsl:call-template name="wrapContentBlockWithGenericClass"/>
	</xsl:template>

	<xsl:template match="legalmemotopic/link | caption/link" priority="1">
		<xsl:call-template name="DocumentLink">
			<xsl:with-param name="documentGuid" select="@tuuid"/>
			<xsl:with-param name="browsePageUrl" select="'&crswLegalMemoPageUrl;'"/>
			<xsl:with-param name="isSearchLink" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<!--Overrides platform generate Footnote-->
	<xsl:template match="footnote" />

	<xsl:template match="keycite.flag">
    <xsl:if test="@flag.color.code = 'Y' or @flag.color.code = 'R'">
      <xsl:if test="not(ancestor::table.of.cases.block)">
        <xsl:text><![CDATA[   ]]> </xsl:text>
      </xsl:if>

      <a>
        <xsl:attribute name="href">
          <xsl:call-template name="GetLMDocumentUrl">
            <xsl:with-param name="documentGuid" select="@tuuid"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:call-template name="GetKeyCiteImageText"/>
        </xsl:attribute>
        <xsl:attribute name="class">
          <xsl:text>&crswKeyCiteImageLink;</xsl:text>
        </xsl:attribute>

        <xsl:call-template name="CreateKeyCiteInlineImage">
          <xsl:with-param name="icon">
            <xsl:choose>
              <!-- Red Flag -->
              <xsl:when test="@flag.color.code = 'R'">
                <xsl:text>&keyCiteRedFlagPath;</xsl:text>
              </xsl:when>

              <!-- Yellow Flag -->
              <xsl:when test="@flag.color.code = 'Y'">
                <xsl:text>&keyCiteYellowFlagPath;</xsl:text>
              </xsl:when>            
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="altText">
            <xsl:call-template name="GetKeyCiteImageText"/>
          </xsl:with-param>
          <xsl:with-param name="class">
            <xsl:text>&crswKeyCiteInlineImage;</xsl:text>
          </xsl:with-param>
        </xsl:call-template>
      </a>

      <xsl:text><![CDATA[   ]]> </xsl:text>
    </xsl:if>
	</xsl:template>

	<xsl:template name="GetKeyCiteImageText">
		<xsl:choose>
			<!-- Red Flag -->
			<xsl:when test="@flag.color.code = 'R'">
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswkeyCiteRedFlagAltTextKey;', '&crswkeyCiteRedFlagAltText;')"/>
			</xsl:when>

			<!-- Yellow Flag -->
			<xsl:when test="@flag.color.code = 'Y'">
        <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswkeyCiteYellowFlagAltTextKey;', '&crswkeyCiteYellowFlagAltText;')"/>
			</xsl:when>		
		</xsl:choose>
	</xsl:template>

	<xsl:template name="GetLMDocumentUrl">
		<xsl:param name ="documentGuid" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.KeyCiteFlagHistoryByLookup', concat('documentGuid=',$documentGuid), 'transitionType=Document')"/>
	</xsl:template>

</xsl:stylesheet>