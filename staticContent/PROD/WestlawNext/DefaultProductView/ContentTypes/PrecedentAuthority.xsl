<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Toc.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- riskScore feature access?  default to NO -->
	<xsl:param name="riskScoreAccess" select="N"/>

	<!-- for <a> links back to Top of doc -->
	<xsl:variable name="docTopId" select="'&wlbcDocTopId;'"></xsl:variable>

	<!-- the main match -->
	<xsl:template match="Document">

		<!-- put TOC and doc content on display -->
		<div  id="&documentId;">
			<xsl:if test ="not($DeliveryMode)">
				<xsl:call-template name="CreateSkipToDropdown"/>
			</xsl:if>
			<xsl:call-template name="TableOfContents"/>
			<xsl:call-template name="Content"/>
			<xsl:call-template name="EndOfDocument" />
			<!--<xsl:if test="$DeliveryMode">
				<xsl:call-template name="CreateSkipToDropdown"/>
			</xsl:if>-->
		</div>

	</xsl:template>


	<!-- the document content -->
	<xsl:template name="Content">
		<a>
			<xsl:attribute name="id">
				<xsl:value-of select="$docTopId"/>
			</xsl:attribute>
			<xsl:text>&#160;</xsl:text>
		</a>
		<xsl:apply-templates select="/Document/n-docbody/document"/>
		<xsl:apply-templates select="//clauseList/*[starts-with(name(), 'clause')]" mode="Content"/>
	</xsl:template>


	<!-- title, governingLaw, party, lawFirm, lawyerList get special treatment -->
	<xsl:template match="document">
		<xsl:apply-templates select="document.title"/>
		<dl class="&analyzerMetaData;">
			<xsl:apply-templates select="governingLaw"/>
			<xsl:apply-templates select="partyList/party"/>
			<xsl:apply-templates select="lawFirmList/lawFirm"/>
		</dl>
		<div class="&clear;"></div>
    <xsl:call-template name="EndOfDocumentHeader" />
	</xsl:template>


	<xsl:template match="document.title">
		<h2 class="&titleClass;">
			<xsl:apply-templates/>
		</h2>
	</xsl:template>


	<xsl:template match="governingLaw">
		<dt>&governingLawLabel;:</dt>
		<dd>
			<xsl:apply-templates/>
		</dd>
	</xsl:template>


	<xsl:template match="party">
		<xsl:apply-templates/>
	</xsl:template>


	<xsl:template match="partyName">
		<dt>&partyLabel;:</dt>
		<dd>
			<xsl:apply-templates/>
		</dd>
	</xsl:template>


	<xsl:template match="partyJurisdiction">
		<dt>&partyJurisdictionLabel;:</dt>
		<dd>
			<xsl:apply-templates/>
		</dd>
	</xsl:template>


	<xsl:template match="lawFirm">
		<xsl:apply-templates/>
	</xsl:template>


	<xsl:template match="lawyerList/name">
		<dt>&lawyerLabel;:</dt>
		<dd>
			<xsl:apply-templates/>
		</dd>
	</xsl:template>


	<xsl:template match="lawFirm/name">
		<dt>&lawFirmLabel;:</dt>
		<dd>
			<xsl:apply-templates/>
		</dd>
	</xsl:template>


	<!-- main content is each clause in doc, display clause appropriately -->
	<xsl:template match="clauseList/*[starts-with(name(), 'clause')]" mode="Content">
	<xsl:param name="bodypara" select="body/para/paratext" />
		<div class="&clauseContainer;">

			<xsl:variable name="anchorName">
				<xsl:choose>
					<xsl:when test="@clause.uuid">
						<xsl:value-of select="concat('clause', @clause.uuid)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat('clause', @ID)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="bodyContent">
				<xsl:for-each select="body">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>

			<!-- 
        Render a 'Top' link which when clicked, scrolls to top of document 
        Also scrolls into view when associated outline <a> clicked	
      -->
			<a>
				<xsl:attribute name="id">
					<xsl:value-of select="$anchorName"/>
				</xsl:attribute>
				<xsl:if test="not($DeliveryMode)">
					<xsl:attribute name="href">
						<xsl:value-of select="concat('#', $docTopId)"/>
					</xsl:attribute>
					<xsl:attribute name="class">&clauseTopLink;</xsl:attribute>
					<xsl:text>&topOfDocumentText;</xsl:text>
				</xsl:if>
			</a>

			<!-- Display the Risk Score iff user has feature access to risk scores -->
			<xsl:if test="$riskScoreAccess = 'Y' and (boolean(@clause.uuid))">
				<span class="&riskScoreContainer;">
					<xsl:call-template name="displayRiskScore"/>
				</span>
			</xsl:if>
			
			<xsl:if test="(string-length($bodypara) &gt; 0  or child::clauseList or ancestor::clause  or preceding-sibling::clause or following-sibling::clause)">				
				<!-- Display clause title -->
				<div class="&clause;">
					<div class="&headText;">
						<xsl:apply-templates select="clause.title"/>
					</div>
				</div>
		</xsl:if>
				<!-- Display the clause body -->
				<div>
					<xsl:apply-templates select="body"/>
				</div>
			
		</div>

	</xsl:template>


	<xsl:template name="displayRiskScore">
		<xsl:variable name="clauseGuid"
    select="translate(@clause.uuid,'abcdefghijklmnopqrstuvwxyz',
    'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
		<xsl:variable name="tocGuid">
			<xsl:value-of select="//Document/RiskScoreList/RiskScores[translate(Guid,
			'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ') = 
			$clauseGuid]/TocGuid"/>
		</xsl:variable>
		<xsl:variable name="riskScoreLinkBase">
			<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.SearchResults', 
			'categoryPageUrl=Home/InMarketAgreements', 'transitionType=Search', 
			'contextData=(sc.Search)', concat('docGuid=', $Guid), 
			concat('tocNodes=A-', $tocGuid))"/>
		</xsl:variable>
		<!-- 
				For some reason, the number of parameters to the UrlBuilder is limited, 
				so the last one that is mssType keeps getting missed. Need to concatenate it 
		-->
		<xsl:variable name="riskScoreLink">
			<xsl:value-of select="concat($riskScoreLinkBase, '&amp;mssType=riskscore')"/>
		</xsl:variable>
		<a href="{$riskScoreLink}" class="&riskScore;">
			<span class="&riskScoreValue;">
				<xsl:value-of select="//Document/RiskScoreList/RiskScores[translate(Guid, 
				'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ') = 
				$clauseGuid]/Score"/>
			</span>
			<!--
        TODO: 
        We should implement the following: 
				<img alt="&wlbcRiskScoreText;" src="{$Images}&riskScoreIcon;"/>
        However, this currently creates an exception. Using the actual path for now.
      -->
			<img alt="&riskScoreText;" src="WLBStaticContent/images/v2/riskScore.png"/>
		</a>
	</xsl:template>


	<!--
    Somewhat of a hack in order to create the SkipTo dropdown, which under Cobalt
    was done only for 2 contentTypes.  We follow Cobalt and put our links here.
    The client-side widget will place these links in a client-side generated HTML 
    structure (the SkipTo dropdown) and remove this co_nrsOutline from the HTML 
    DOM.
  -->
	<xsl:template name="CreateSkipToDropdown">
		<div id="&nrsOutlineId;" class="&excludeFromAnnotationsClass;">
			<div>
				<div>
					<a href="#&wlbcTocTopId;">
						<xsl:text>&tableOfContentsText;</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&wlbcDocTopId;">
						<xsl:text>&documentText;</xsl:text>
					</a>
				</div>
			</div>
		</div>
	</xsl:template>


  <xsl:template name="EndOfDocumentHeader">
    <xsl:text disable-output-escaping="yes">&lt;!--</xsl:text>
    <xsl:value-of select="'&EndOfDocumentHead;'"/>
    <xsl:text disable-output-escaping="yes">--&gt;</xsl:text>
  </xsl:template>
</xsl:stylesheet>
