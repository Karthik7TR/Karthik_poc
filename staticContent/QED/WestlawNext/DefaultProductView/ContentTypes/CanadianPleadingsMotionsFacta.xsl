<?xml version="1.0" encoding="utf-8"?>
<!--Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>	
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="Date.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <!--Do not render-->
  <xsl:template match="content.metadata.block | message.block.carswell | prelim.leg.equivalent"/>
	
	<!--Document template-->
	<xsl:template match="Document">    
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">		
				<xsl:with-param name="contentType" select="'&crswPleadingsMotionAndFactaClass;'"/>
			</xsl:call-template>		

			<!--Render Document Prelims-->
			<div>
				<xsl:call-template name="GetDocPrelim"/>
			</div>
			
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			
			<!--Render document content-->
			<xsl:apply-templates select="n-docbody/precedent"/>
			<xsl:call-template name="RenderFootnoteSection"/>      
			<xsl:call-template name="EndOfDocument" />				
		</div>
	</xsl:template>
	
	<!--Document content-->
	<xsl:template match="precedent">
		
		<!--Render more document header content-->
		<xsl:apply-templates select="pleading/p[1] | pleadingwrapper/p[1] | motion/p[1] | motionwrapper/p[1] | order/p[1] | factum/p[1]"/>    
		<xsl:apply-templates select="//disclaimer"/>
		<xsl:apply-templates select="//courtjur"/>
		<xsl:apply-templates select="//judge"/>
		<xsl:apply-templates select="//partyblock"/>
		<xsl:apply-templates select="//counselblock"/>

		<!--Render internal TOC-->
		<xsl:apply-templates select="//internaltoc"/>

		<!-- render main document contents-->
		<xsl:apply-templates select="//freeform"/>    
	</xsl:template>

	<!--Template for creating the Document Prelims-->
	<xsl:template name="GetDocPrelim">
		<!--<div class="&blobLinkClass;">-->
		<xsl:apply-templates select="n-docbody/precedent/pleading/img | n-docbody/precedent/motion/img | n-docbody/precedent/factum/img | 
                         n-docbody/precedent/order/img | n-docbody/precedent/pleadingwrapper/img | n-docbody/precedent/motionwrapper/img"/>
		<!--</div>-->
		
		<!-- Render Prelim -->
		<div class="&documentHeadClass;">
			<div class="&citesClass;">
				<xsl:apply-templates select="n-docbody/precedent//doc_heading/headers/header1"/>	
			
			</div>
			<div class="&headnotesClass; &centerClass;">         										
					<xsl:apply-templates select="n-docbody/precedent//wrapperlinks"/>
				<xsl:apply-templates select="n-docbody/precedent//doc_heading/prelims/*"/>
			</div>
		</div>
	</xsl:template>

	<!--Template for centering and paragraphing content-->
	<xsl:template match="internaltoc">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&alignHorizontalCenterClass; &paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!--Template for base paragraph styles-->
	<xsl:template match="block1 | block2 | disclaimer | courtjur | judge | party | partyblock | partyline | partyrole 
								| counsel | counselblock | address | connector | lawfirm | line | include.copyright | img 
								| header1 | prelimsubject | prelimstatref | firmname | firmtype">
		<xsl:call-template name="FormatParagraph"/>
	</xsl:template>

	<!--Template to render title element based on a couple factors-->
	<xsl:template match="title">
		<xsl:if test="@tocid">
			<a>
				<xsl:attribute name="id">
					<xsl:value-of select="@tocid"/>
				</xsl:attribute>
			</a>
		</xsl:if>
		<xsl:call-template name="FormatParagraph"/>    
	</xsl:template>

	<!--Template for rendering internal toc tocp-->
	<xsl:template match="tocp">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&paraMainClass;'" />
      <xsl:with-param name="contents">
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="concat('#', @tocidref)"/>
          </xsl:attribute>
          <xsl:apply-templates/>
        </a>
      </xsl:with-param>
    </xsl:call-template>
	</xsl:template>

	<!--Template for rendering counsel names *firstname is spelt wrong to stay in line with XML data [firsname]*-->
	<xsl:template match="firsname">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[  ]]></xsl:text>
	</xsl:template>
	<xsl:template match="lastname">		
		<xsl:value-of select="."/>			
	</xsl:template>
  <xsl:template match="prelimtype">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&paraMainClass; &title;'" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="prelimdate">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="class" select="'&dateClass;'" />
    </xsl:call-template>  
  </xsl:template>	
	
	
</xsl:stylesheet>
