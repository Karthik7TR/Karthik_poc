<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters.All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!-- I18n Completed As Of 4/18/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:import href="Universal.xsl"/>
  <xsl:include href="Copyright.xsl"/>

  <xsl:variable name="docTitle">
    <xsl:value-of select="/Document/n-docbody/book.element.version/title" />
  </xsl:variable>

  <xsl:variable name="ruleNumber">
    <xsl:value-of select="/Document/n-docbody/book.element.version/rule.number" />
  </xsl:variable>


  <xsl:template match="Document">
    <div id="&documentId;" class="&documentFixedHeaderView;">
      <xsl:call-template name="AddDocumentClasses">
        <xsl:with-param name="contentType" select="&contentTypeSelfRegulatoryOrganization;"/>
      </xsl:call-template>      

      <div id="&coDocContentHeaderPrelim;">
        <div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
          <div id="&coDocHeaderContainer;">
            <div class="&titleClass;">
              <xsl:choose>
                <xsl:when test="string-length($ruleNumber) &gt; 0">
                  <xsl:value-of select="concat($ruleNumber, '. ', $docTitle)" />
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$docTitle" />
                </xsl:otherwise>
              </xsl:choose>
            </div>
          </div>
      </div>
      <xsl:call-template name="buildHeader">
        <xsl:with-param name="breadstring" select="n-docbody/book.element.version/breadstring" />
      </xsl:call-template>
      
      <xsl:comment>&EndOfDocumentHead;</xsl:comment>
      <xsl:apply-templates />
      <xsl:call-template name="RemoveSaveToWidget" />
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>
  
  <xsl:template name="AddProductDocumentClasses">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:text>&documentFixedHeaderView;</xsl:text>
	</xsl:template>

  <xsl:template match="book.element.version">
    <xsl:apply-templates select="body" />
  </xsl:template>

  <xsl:template name="buildHeader">
    <xsl:param name="breadstring" />
    <xsl:if test="string-length($breadstring) &gt; 0">
      <div class="&paraMainClass;">
        <xsl:choose>
          <xsl:when test="contains($breadstring, '>>')">
            <xsl:value-of select="substring-before($breadstring,'>>')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$breadstring"/>
          </xsl:otherwise>
        </xsl:choose>
      </div>
      <xsl:call-template name="buildHeader">
        <xsl:with-param name="breadstring" select="substring-after($breadstring,'>>')"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="cite.query[@w-ref-type = 'PF']">
    <div class="&imageBlockClass;">
      <xsl:call-template name="createDocumentBlobLink">
        <xsl:with-param name="guid" select="@w-normalized-cite"/>
        <xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
        <xsl:with-param name="contents">
          <xsl:apply-templates />
          <xsl:text> &pdfLabel;</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
        <xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
        <xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
      </xsl:call-template>
    </div>
  </xsl:template>

  <!--Suppress links with the 'LQ' ref-type and the manually-edit attrobute-->
  <xsl:template match="cite.query[@w-ref-type = 'LQ' and @manual-edit = 'true']">
    <xsl:value-of select="." />
  </xsl:template>

  <!--Suppress email links-->
  <xsl:template match="cite.query[@w-ref-type = 'FE']">
    <xsl:value-of select="." />
  </xsl:template>
  
  <xsl:template name="EndOfDocument">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:param name="endOfDocumentCopyrightTextVerbatim" select="false()"/>
   
		<xsl:choose>
			<xsl:when test="$PreviewMode = 'True'">
				<xsl:call-template name="AdditionalContent" />
				<xsl:if test="$DeliveryMode = 'True' ">
					<xsl:call-template name="LinkBackToDocDisplay" />
				</xsl:if>
			</xsl:when>
			<xsl:when test="not($EasyEditMode)">
				<table>
					<xsl:choose>
						<!--Cannot use id for public records documents because we render and print multiple documents a-->
						<xsl:when test="$IsPublicRecords = true()">
							<xsl:attribute name="class">
								<xsl:text>&endOfDocumentId;</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="id">
							<xsl:text>&endOfDocumentId;</xsl:text>
						</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<tr>
						<td>
              <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&endOfDocumentTextKey;', '&endOfDocumentText;')"/>
            </td>
          </tr>
          <tr>
            <td class="&endOfDocumentCopyrightClass;">
              <xsl:apply-templates select="n-docbody/include.copyright[@n-include_collection = 'grc_rulebook02']" mode="fromFooter"/>
            </td>
          </tr>
          <tr>
						<xsl:choose>
							<xsl:when test="$endOfDocumentCopyrightTextVerbatim">
								<td class="&endOfDocumentCopyrightClass;"><xsl:copy-of select="$endOfDocumentCopyrightText"/></td>
							</xsl:when>
							<xsl:otherwise>
								<td class="&endOfDocumentCopyrightClass;">&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/></td>	
							</xsl:otherwise>
						</xsl:choose>
					</tr>
				</table>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
  
  <xsl:template match="n-docbody/include.copyright[@n-include_collection = 'grc_rulebook02']" priority="1" mode="fromFooter">
    <xsl:call-template name="copyrightBlock">
      <xsl:with-param name="copyrightNode" select="." />
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="copyrightBlock" priority="1">
		<xsl:param name="copyrightNode" select="."/>

		<xsl:variable name="copyright">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="normalize-space($copyrightNode)" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="should-display">
			<xsl:call-template name="check-copyright-text">
				<xsl:with-param name="string">
					<xsl:call-template name="upper-case">
						<xsl:with-param name="string" select="translate($copyright, '/', '&space;')"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="contains($should-display, 'true')">
				&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$copyright"/>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>