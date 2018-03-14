<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="SimpleContentBlocks.xsl"/>  
  <xsl:include href="CanadianLegalMemo.xsl"/>
  <xsl:include href="CanadianFootnotes.xsl"/>
  <xsl:include href="CanadianCites.xsl"/>  
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!--Do not render these-->  
  <xsl:template match="doc_heading"/>
  <xsl:template match="message.block.carswell"/>
  <xsl:template match="legalmemolinks"/>

  <xsl:template match="Document">
    <div id="&documentClass;">
      <xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswLegalMemo;'"/>
      </xsl:call-template>

      <xsl:variable name="legalmemodoc" select="n-docbody/legalmemorandum/legalmemodoc"/>

      <div class="&headnotesClass; &centerClass;">
        <xsl:apply-templates select="$legalmemodoc/doc_heading/headers/header1"/>
        <xsl:apply-templates select="$legalmemodoc/legalmemolinks//footnote.reference"/>
        <xsl:apply-templates select="$legalmemodoc/doc_heading/headers/header2"/>
      </div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			
			<xsl:apply-templates select="$legalmemodoc/legalmemolinks/p"/>
			
      <xsl:apply-templates select="$legalmemodoc"/>
			

      <xsl:call-template name="RenderFootnoteSection"/>
      <xsl:call-template name="EndOfDocument" />
    </div>
  </xsl:template>

  <xsl:template match="header1">
    <xsl:call-template name="wrapWithDiv">
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="header2">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&titleClass;'"></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="img">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&alignHorizontalCenterClass; &paraMainClass;'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="p" priority="1">
    <xsl:choose>
      <xsl:when test="sup/a[starts-with(@name, 'f') and starts-with(@href, '#r')] | 
								a[(starts-with(@name, 'f') and starts-with(@href, '#r')) or 
									(starts-with(@name,'N_') and substring(@name,string-length(@name)-1)='a_' and string-length(@href) = 0)
								 ]">
        <!--Do nothing if we have a footnote paragraph-->
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="FormatParagraph" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
	
	<!-- Link Enabled just for Legal Memoranda Full -->
		<xsl:template match="urllink">
		<xsl:call-template name="CreateExternalLink">
			<xsl:with-param name="url" select="@href"/>
			<xsl:with-param name ="title" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswCurrencyKey;', '&crswCurrency;')"/>
			<xsl:with-param name="text" select="text()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Create an External Link -->
	<xsl:template name="CreateExternalLink">
		<xsl:param name="url"/>
		<xsl:param name="title"/>		
		<xsl:param name="text" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&crswClickHereKey;', '&crswClickHere;')"/>		
		<xsl:param name="hasImgChild" select="false()"/>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<a href="{$url}">
					<xsl:copy-of select="$text"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string($hasImgChild) = 'true'">
						<!-- In this case the <a> encompasses the <img> tag, both of which have an onclick event handler. The img's one is in the platform code, 
             so it had to be overriden in the website_PreventImageClickAction function in Cobalt.Master.CRSW.js -->
						<a href="javascript:void(0);" class="&preventImageActionOnClickClass;" data-external-url="{$url}" data-external-title="{$title}">
							<xsl:copy-of select="$text"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a href="javascript:void(0);" class="&linkoutShowLightboxOnClickClass;" data-external-url="{$url}" data-external-title="{$title}">
							<xsl:copy-of select="$text"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
