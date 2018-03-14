<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Date.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />

	<xsl:variable name ="pubDate" select="/Document/n-metadata/metadata.block/md.dates" />
  <xsl:variable name ="pagePrice" select="/Document/n-docbody/investext/report" />
	<xsl:variable name ="citation" select="/Document/document-data/cite" />

	<!-- render the XML based on the desired VIEW. -->
	<xsl:template match="/">
		<xsl:call-template name="Content" />
	</xsl:template>

	<xsl:template name="Content">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
			</xsl:call-template>
			<center>
				<xsl:text>(C) Thomson Financial Services, </xsl:text>
				<xsl:value-of select="$currentYear"/>
			</center>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="n-metadata" />
	<xsl:template match="n-docbody/investext/report">
		<center>
			<div>
				<xsl:apply-templates select="research.firm"/>
			</div>
			<div>
				<xsl:apply-templates select="$pubDate/md.publisheddate"/>
			</div>
			<div>&nbsp;</div>
			<div>
				<xsl:apply-templates select="title"/>
			</div>
			<div>
				<xsl:for-each select ="author">
					<xsl:apply-templates select="."/>
					<xsl:if test="position()!=last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</div>
			</center>
		<div>
			<xsl:apply-templates select="gateway.doc.image.link"/>
			<xsl:apply-templates select="language | report.type"/>
		</div>
		<div>&nbsp;</div>
		<div>
			<xsl:apply-templates select="page.count | billable.pages.count | page.price | doc.price"/>
		</div>
		<div>&nbsp;</div>
		<div>
			<xsl:text>To retrieve the full report, click on the PDF icon. You will incur a charge for the FULL report (printing included), based on the number of billable pages and page price, unless otherwise provided in your subscription agreement.</xsl:text>
		</div>
		<div>&nbsp;</div>
		<div>
			<xsl:apply-templates select="company.b | industry"/>
		</div>
  </xsl:template>

	<xsl:template match="research.firm | title">
			<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="company.b">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="md.publisheddate">
		<xsl:call-template name="parseYearMonthDayDateFormat">
			<xsl:with-param name="displayDay" select="'true'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="company.name | industry ">
		<xsl:apply-templates />
		<br />
	</xsl:template>

	<xsl:template match="ticker.symbol">
		<div>
			<text>Ticker Symbol: </text>
			<xsl:apply-templates />
		</div>
		<div>&nbsp;</div>
	</xsl:template>

	<xsl:template match="language">
		<div>
			<text>Language: </text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="report.type">
		<div>
			<text>Report Type: </text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="billable.pages.count">
		<div>
			<text>Billable Page(s): </text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="page.count">
		<div>
			<text>Total Pages: </text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="page.price">
		<div>
			<text>Page Price: </text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="doc.price">
		<div>
			<text>Document Price: </text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="toc.block">
		<div>&nbsp;</div>
		<div>
			<text>Table of Contents</text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="toc.block/head/headtext">
		<div>
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="toc.body/head">
		<xsl:apply-templates />
	</xsl:template>

<xsl:template match="toc.body/para">
		<xsl:apply-templates />
</xsl:template>
    
	<xsl:template match="gateway.doc.image.link">
		<center>
			<div class="&imageBlockClass;">
				<xsl:variable name="ttype">
					<xsl:text>INVESTEXTAPIGARDEN</xsl:text>
				</xsl:variable>
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="$citation"/>
          <xsl:with-param name="targetType" select="$ttype"/>
					<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
          <xsl:with-param name="pageRate" select="$pagePrice/page.price"/>
          <xsl:with-param name="pubDate" select="$pubDate/md.publisheddate"/>
          <xsl:with-param name="originatingDocGuid" select="$Guid"/>
          <xsl:with-param name="contents">
						<xsl:apply-templates />
						<xsl:text> &pdfLabel;</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
					<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
					<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
					<xsl:with-param name="className">
						<xsl:if test=".//N-HIT">
							<xsl:text>&searchTermClass;</xsl:text>
						</xsl:if>
						<xsl:if test=".//N-WITHIN">
							<xsl:text> &searchWithinTermClass;</xsl:text>
						</xsl:if>
						<xsl:if test=".//N-LOCATE">
							<xsl:text> &locateTermClass;</xsl:text>
						</xsl:if>
						<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
							<xsl:text> &searchTermNoHighlightClass;</xsl:text>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
			</div>
		</center>
		<xsl:apply-templates />
	</xsl:template>

  <xsl:template name="createDocumentBlobLink">
    <xsl:param name="contents"/>
    <xsl:param name="guid" select="."/>
    <xsl:param name="targetType" select="@ttype"/>
    <xsl:param name="mimeType" />
    <xsl:param name="pubDate" />
    <xsl:param name="originatingDocGuid" />
    <xsl:param name="pageRate" />
    <xsl:param name="className" />
    <xsl:param name="displayIcon" />
    <xsl:param name="displayIconClassName" />
    <xsl:param name="displayIconAltText" select="'&defaultDisplayIconAltText;'"/>
    <xsl:param name="originationContext" select="'&docDisplayOriginationContext;'"/>
    <xsl:param name="prettyName" />
    <xsl:param name="hash" />

    <xsl:variable name="modifiedPageRate">
      <xsl:choose>
        <xsl:when test="$pageRate = '11.50'">
          <xsl:value-of select="'standard'"/>
        </xsl:when>
        <xsl:when test="$pageRate = '20.00'">
          <xsl:value-of select="'premium'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'NA'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
      
    <xsl:if test="string-length($contents) &gt; 0">
      <a>
        <xsl:attribute name="href">
            <xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', 'pdf'), concat('targetType=', $targetType), concat('pubDate=', $pubDate), concat('originatingDocGuid=', $originatingDocGuid), concat('investextContentType=', $modifiedPageRate), concat('originationContext=', $originationContext), '&transitionTypeParamName;=&transitionTypeDocumentImage;')"/>
        </xsl:attribute>
        <xsl:attribute name="class">
          <xsl:text>&blobLinkClass;</xsl:text>
          <xsl:if test="$className">
            <xsl:text><![CDATA[ ]]></xsl:text>
            <xsl:value-of select="$className"/>
          </xsl:if>
        </xsl:attribute>
        <xsl:if test="string-length($mimeType) &gt; 0">
          <xsl:attribute name="type">
            <xsl:value-of select="$mimeType"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="string-length($displayIcon) &gt; 0">
          <img>
            <xsl:attribute name="src">
              <xsl:value-of select="$Images"/>
              <xsl:value-of select="$displayIcon"/>
            </xsl:attribute>
            <xsl:attribute name="class">
              <xsl:value-of select="$displayIconClassName"/>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <xsl:value-of select="$displayIconAltText"/>
            </xsl:attribute>
          </img>
          <!-- HACK to make string-length evaluate to greater than 0 -->
          <xsl:text>&#x200B;</xsl:text>
        </xsl:if>
        <xsl:copy-of select="$contents"/>
      </a>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
