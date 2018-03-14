<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- 
	**************************************************************************************
		the doc outline.  basically a bunch of links into doc content.                     
	**************************************************************************************
	-->
	<xsl:template name="TableOfContents">
		<a id="&wlbcTocTopId;">&#160;</a>
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<xsl:call-template name="TocDeliveryMode"/>
			</xsl:when>
			<xsl:when test="$IsMobile">
				<xsl:call-template name="TocMobileDocDisplayMode"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="TocDocDisplayMode"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!--
	**************************************************************************************
		 The Toc formatted for Doc Delivery (Word, PDF, etc).
	**************************************************************************************
	-->
	<xsl:template name="TocDeliveryMode">
		<div>
			<ul class="&tocTree; &tree;">
				<li>
					<div class="&listItem;" id="&wlbcTocId;">
						<ul class="&tocTree; &tree;">
							<li>
								<div class="&listItem;">
									<h2 id="&wlbcTocHeaderId;">&tableOfContentsText;</h2>
								</div>
							</li>
						</ul>
						<div>
							<!-- Container for TOC -->
							<div id="&wlbcTocContainerId;">
								<fieldset>
									<legend>
										<span class="&accessiblityLabel;">&tableOfContentsText;</span>
									</legend>
									<ul class="&tocTree; &tree;">
										<!-- Insert TOC list here -->
										<xsl:apply-templates
										select="n-docbody/document/subDocumentList/subDocument"/>
									</ul>
								</fieldset>
							</div>
							<!-- End Container for TOC -->
						</div>
					</div>
				</li>
			</ul>
		</div>
	</xsl:template>


	<!--
	**************************************************************************************
		 The Toc formatted for Doc Display (IE, FireFox, etc).
	**************************************************************************************
	-->
	<xsl:template name="TocDocDisplayMode">
		<div class="&expandBox;" id="&wlbcTocId;">
			<h2 class="&expandBoxHeader; &expandBoxExpanded;" id="&wlbcTocHeaderId;">
				<span class="&expandBoxHeaderSpan;">&tableOfContentsText;</span>
				<a id="&wlbcTocExpandCollapseLinkId;"
				class="&accessibleLink; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass; &expandBoxExpandLink;">
					&tocCollapse;
				</a>
			</h2>
			<div class="&expandBoxContentContainer;">
				<!-- Container for TOC -->
				<div id="&wlbcTocContainerId;">
					<fieldset>
						<legend>
							<span class="&accessiblityLabel;">&tableOfContentsText;</span>
						</legend>
						<ul class="&tocTree; &tree;">
							<!-- Insert TOC list here -->
							<xsl:apply-templates
							select="n-docbody/document/subDocumentList/subDocument"/>
						</ul>
					</fieldset>
				</div>
				<!-- End Container for TOC -->
			</div>
		</div>
	</xsl:template>

	<!--
	**************************************************************************************
		 The Toc formatted for Mobile Doc Display.
	**************************************************************************************
	-->
	<xsl:template name="TocMobileDocDisplayMode">
		<!-- Container for TOC -->
		<div id="&wlbcTocContainerId;">
			<fieldset>
				<legend>
					<span class="&accessiblityLabel;">&tableOfContentsText;</span>
				</legend>
				<ul class="&tocTree; &tree;">
					<!-- Insert TOC list here -->
					<xsl:apply-templates select="n-docbody/document/subDocumentList/subDocument"/>
				</ul>
			</fieldset>
		</div>
		<!-- End Container for TOC -->
	</xsl:template>


	<!--
	**************************************************************************************
		 generate an outermost fieldset in order to start off our recursion.
	**************************************************************************************
	-->
	<xsl:template match="subDocument">
		<xsl:apply-templates select="clauseList">
			<xsl:with-param name="legend">
				<xsl:text>&tableOfContentsText;</xsl:text>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>


	<!-- 
	**************************************************************************************
		each clauseList gets a new div (for indentation purposes)
	**************************************************************************************
	-->
	<xsl:template match="clauseList">
		<xsl:param name="legend"></xsl:param>
		<xsl:choose>
			<xsl:when test="$legend = '&tableOfContentsText;'">
				<xsl:apply-templates select="*[starts-with(name(), 'clause')]" mode="Toc"/>
			</xsl:when>
			<xsl:otherwise>
				<fieldset>
					<!-- deliveryMode=> don't hide sub nodes (keep TOC fully expanded) -->
					<xsl:if test="not($DeliveryMode)">
						<xsl:attribute name="class">&hideState;</xsl:attribute>
					</xsl:if>
					<legend>
						<span class="&accessiblityLabel;">
							<xsl:value-of select="$legend"/>
						</span>
					</legend>
					<ul>
						<xsl:apply-templates select="*[starts-with(name(), 'clause')]" mode="Toc"/>
					</ul>
				</fieldset>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- 
	**************************************************************************************
		each clause gets an anchor that links into our content
	************************************************************************************** 
	-->
	<xsl:template match="clauseList/*[starts-with(name(), 'clause')]" mode="Toc">
		<xsl:variable name="novusHit" select="norm.code//N-HIT or clause.title//N-HIT or
									body//N-HIT or clause.title//N-HIT"/>
		<li>
			<div class="&listItem;">
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
				<!-- anchor for accessability (pushed off screen) -->
				<xsl:if test="(count(clauseList) > 0) and (not($DeliveryMode)) and (not($IsMobile))">
					<!-- if children, make node toggleable -->
					<a href="#" class="&treeToggle; &excludeFromAnnotationsClass; &treeExpand;">
						Expand&#x20;
						<xsl:value-of select="clause.title"/>
					</a>
				</xsl:if>
				<xsl:variable name="bodyContent">
					<xsl:for-each select="body/para/paratext">
						<xsl:value-of select="."/>
					</xsl:for-each>
				</xsl:variable>
				<xsl:if test="(string-length($bodyContent) &gt; 0  or child::clauseList or ancestor::clause)">
					<div class="&treeElement;">
						<!-- viewable anchor, jumps us into content -->
						<a href="#{$anchorName}" class="&treeName;">
							<xsl:if test="$novusHit">
								<xsl:attribute name="class">
									&searchTermNoHighlightClass;
								</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="clause.title"/>
						</a>
					</div>
				</xsl:if>
			</div>
			<xsl:apply-templates select="clauseList">
				<xsl:with-param name="legend" select="clause.title"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>


</xsl:stylesheet>
