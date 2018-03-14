<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:include href="CanadianCites.xsl"/>
	<xsl:include href="CanadianDate.xsl"/>
	<xsl:include href="CanadianFancyTable.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="CanadianFootnotes.xsl"/>
	<xsl:include href="CanadianOutline.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!--We render these in the Prelim-->
	<xsl:template match="title.block | citation.block | panel.block | date.block | docket.block | 
								message.block.carswell | court.block | message.block" priority="1"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&crswCaselaw;'"/>
			</xsl:call-template>
			<xsl:apply-templates select="n-metadata/metadata.block/md.references/md.toggle.links/md.toggle.link"/>
			<xsl:call-template name="CreateGoToLinks"/>
			<xsl:call-template name="StarPageMetadata" />

			<xsl:choose>
				<xsl:when test="not($DeliveryMode) or $DisplayKeyCiteTreatment">
					<xsl:apply-templates select="n-docbody/decision/content.metadata.block/cmd.content.block/cmd.negative.trtmt/cmd.neg.text"/>
					<xsl:apply-templates select="n-docbody/decision/content.metadata.block/cmd.content.block/cmd.negative.trtmt/cmd.neg.case"/>
				</xsl:when>
			</xsl:choose>

			<xsl:if test="not($DeliveryMode) or $DisplayOriginalImageLink">
				<xsl:apply-templates select="n-docbody/decision/img/image.block"/>
			</xsl:if>
			<div class="&headnotesClass; &centerClass;">
				<xsl:apply-templates select="n-docbody/decision/content.metadata.block/cmd.content.block/cmd.prelim1"/>
				<xsl:apply-templates select="n-docbody/decision/content.block/court.block/court.line/court"/>
				<xsl:apply-templates select="n-docbody/decision/content.block/title.block/text.line[1]"/>
				<xsl:apply-templates mode="caselaw" select="n-metadata/metadata.block/md.identifiers/md.cites" />
				<xsl:apply-templates select="n-docbody/decision/content.block/title.block/text.line[2]"/>
				<xsl:apply-templates select="n-docbody/decision/content.block/title.block/text.line[position() &gt; 2]" mode="remainingTextLine"/>
				<xsl:apply-templates select="n-docbody/decision/content.block/panel.block/panel.line/judge"/>
				<xsl:apply-templates select="n-docbody/decision/content.block/date.block/date.line"/>
				<xsl:apply-templates select="n-docbody/decision/content.block/docket.block/docket.line"/>
				<xsl:apply-templates select="n-docbody/decision/content.block/message.block/message.line"/>
			</div>
			
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<xsl:apply-templates select="n-docbody/decision/content.block"/>
			<xsl:call-template name="RenderFootnoteSection"/>

			<xsl:call-template name="EndOfDocument" />

		</div>
	</xsl:template>

        <!-- Treatment -->
        <xsl:template match="cmd.neg.text">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&crswMostNegativeTreatment;'"/>
			</xsl:call-template>
        </xsl:template>

        <xsl:template match="cmd.neg.case">
			<xsl:call-template name="wrapWithDiv">
                        <xsl:with-param name="class" select="'&crswMostRecentTreatment;'"/>
                </xsl:call-template>
        </xsl:template>
	
	<!-- Cite -->
	<xsl:template match="cmd.prelim1" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&citesClass;'"/>
		</xsl:call-template>
	</xsl:template>	

	<!-- Court -->
	<xsl:template match="court.block/court.line/court | message.block/message.line" priority="1">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="''"/>
		</xsl:call-template>
	</xsl:template>

  <xsl:template match="title.block/text.line[1]">
    <xsl:call-template name="wrapWithDiv">
		<xsl:with-param name="class" select="'&titleClass;'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="title.block/text.line[2]">
    <xsl:call-template name="wrapWithDiv">
		<xsl:with-param name="class" select="'&titleClass;'"/>
    </xsl:call-template>
  </xsl:template>
  
  <!-- Rest of the text.line text -->
  <xsl:template match="title.block/text.line" mode="remainingTextLine">
	  <xsl:call-template name="wrapWithDiv" />
  </xsl:template>

  

	<!-- Judge(s)/Decider(s) -->
	<xsl:template match="panel.line/judge">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&judge;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Docket Number -->
	<xsl:template match="docket.block/docket.line">
		<xsl:choose>
			<!-- Docket line exists in the document -->
			<xsl:when test="string-length(text()) &gt; 0">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&docketBlockClass;'"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="panel.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&alignHorizontalCenterClass; &paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&alignHorizontalCenterClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="catchphrase.group | classification.group | action.block | 
								headtext | section.body | code.reference | headnote.body |
								wordphrase | wordphrase.para | order.block | analytical.reference">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="action.summary.block">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="case.reference | code.reference/cite">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &paraIndentLeftClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&attorneyBlockLabelClass; &paraMainClass;'" />
			<xsl:with-param name="id" select="'&crswCounselId;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.line">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&simpleContentBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="reference.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&simpleContentBlockClass; &referenceBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

  <xsl:template match="editorial.note.block">
    <xsl:call-template name="wrapWithDiv">
      <xsl:with-param name="id" select="'&crswAnnotationHeaderId;'" />
    </xsl:call-template>
  </xsl:template>
  
	<xsl:template match="opinion.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="'&opinionId;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="order.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="id" select="'&crswDispositionId;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="catchphrase | classification">
		<span>
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<!--Prefomatted Tables-->
	<xsl:template match="layout.block">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and $DualColumnMode">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<pre>
					<xsl:apply-templates/>
				</pre>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="layout.block/text()">
		<xsl:call-template name="SpecialCharacterTranslator">
			<xsl:with-param name="notPreformatted" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="br">
		<xsl:if test="parent::layout.block">
			<br/>
		</xsl:if>
	</xsl:template>

  <xsl:template match="paratext/ital/text()">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
