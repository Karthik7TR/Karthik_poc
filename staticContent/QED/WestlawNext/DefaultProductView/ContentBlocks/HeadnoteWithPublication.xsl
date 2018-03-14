<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Headnote.xsl"/>

	<xsl:template name="headnotePublicationBlock">
		<xsl:param name="headnotePublicationBlockContent"/>
		<xsl:param name="metadata"/>
		<xsl:param name="headnoteBlock"/>
		<div class="&headnotePublicationBlock;">
			<h3 class="&headnotePublicationBlockTitle;">Internal users only</h3>
			<div class="&headnotePublicationBlockContainer;">
				<xsl:for-each select="$headnotePublicationBlockContent/*[not(self::headnote.id|self::headnote.id.class.number)]">
					<xsl:apply-templates select="self::current.key.source|self::key.source"/>
					<xsl:apply-templates select="self::current.prior.key.source|self::prior.key.source"/>
				</xsl:for-each>
				<xsl:apply-templates mode="publicationBlock" select="$metadata/md.identifiers/md.headnote.legacy.id | $metadata/md.identifiers/md.keysummary.legacy.id"/>
				<xsl:apply-templates select="$metadata/md.jurisdictions/md.jurisdiction/md.jurisstate"/>
				<xsl:call-template name="displayWordsAndPhrases">
					<xsl:with-param name="headnoteBody" select="$headnoteBlock/expanded.headnote/headnote/headnote.body/para | $headnoteBlock/expanded.keysummary/keysummary/keysummary.body/para"/>
				</xsl:call-template>
			</div>
		</div>
	</xsl:template>

  <xsl:template name="displayWordsAndPhrases">
    <xsl:param name="headnoteBody"/>
    <xsl:if test="count($headnoteBody/paratext/wordphrase) &gt; 0">
      <div class="&headnotePublicationBlockClassNumberID;">
        <div class="&headnotePublicationBlockWordsAndPhrases;">
          <xsl:for-each select="$headnoteBody/paratext/wordphrase">
            <xsl:variable name="currentTerm" select="."/>
            <div class="&headnotePublicationBlockWordsAndPhrasesItem;">
              <xsl:call-template name="renderTerm">
                <xsl:with-param name="term" select="$currentTerm"/>
              </xsl:call-template>
            </div>
          </xsl:for-each>
        </div>
      </div>
    </xsl:if>
  </xsl:template>

	<xsl:template name="renderTerm">
		<xsl:param name="term"/>
		<xsl:choose>
			<xsl:when test="$term[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
				<xsl:apply-templates select="$term[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$term" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="renderKeyHierarchy">
		<xsl:param name="generateRefKeyTable" select="true()"/>
		<xsl:param name="generateTopicKeyHierarchy" select="true()"/>
		<xsl:param name="generateKeyIconImage" select="false()"/>
		<div class="&headnotePublicationBlock;">
			<h3 class="&headnotePublicationBlockTitle;">Key Number Hierarchy</h3>
			<div class="&headnotePublicationBlockContainer;">
				<xsl:apply-templates select="/Document/n-docbody/digestheadnote/content.block/headnote.block/expanded.headnote/expanded.classification/topic.key.hierarchy | /Document/n-docbody/digestkeysummary/content.block/keysummary.block/expanded.keysummary/expanded.classification/topic.key.hierarchy">
					<xsl:with-param name="generateRefKeyTable" select="$generateRefKeyTable"/>
					<xsl:with-param name="generateTopicKeyHierarchy" select="$generateTopicKeyHierarchy"/>
					<xsl:with-param name="generateKeyIconImage" select="$generateKeyIconImage"/>
					<xsl:with-param name="renderWithTheLine" select="false()"/>
				</xsl:apply-templates>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="current.prior.key.source|prior.key.source">
		<div class="&headnotePublicationBlockPriorItem;">
			<xsl:value-of select="text()"/>
		</div>
	</xsl:template>

	<xsl:template match="current.key.source|key.source">
		<div class="&headnotePublicationBlockNotPriorItem;">
			<xsl:value-of select="text()"/>
		</div>
	</xsl:template>

	<xsl:template mode="publicationBlock" match="md.headnote.legacy.id">
		<div class="&headnotePublicationBlockClassNumberID;">ID: <xsl:value-of select="text()"/></div>
	</xsl:template>

	<xsl:template mode="publicationBlock" match="md.keysummary.legacy.id">
		<div class="&headnotePublicationBlockClassNumberID;">ID: <xsl:value-of select="text()"/></div>
	</xsl:template>

	<xsl:template match="md.jurisstate">
		<div class="&headnotePublicationBlockJurisState;">VW: <xsl:value-of select="text()"/></div>
	</xsl:template>

<xsl:template match="topic.key.hierarchy">
		<xsl:param name="generateRefKeyTable" select="true()"/>
		<xsl:param name="generateTopicKeyHierarchy" select="true()"/>
		<xsl:param name="generateKeyIconImage" select="true()"/>
		<xsl:param name="renderWithTheLine" select="true()"/>

		<xsl:variable name="topicKeyContents">
			<xsl:apply-templates select="topic.key" />
		</xsl:variable>
		<xsl:if test="string-length($topicKeyContents) &gt; 0">
			<xsl:variable name="topicKeyRefKey">
				<xsl:apply-templates select="descendant::topic.key.ref/key" />
			</xsl:variable>
			<xsl:variable name="topicKeyRefKeyText">
				<xsl:apply-templates select="descendant::topic.key.ref/keytext" />
			</xsl:variable>
			<xsl:variable name="priorClassification">
				<xsl:apply-templates select="../prior.classification" />
			</xsl:variable>

			<div>
				<xsl:choose>
					<xsl:when test="$renderWithTheLine">
						<xsl:attribute name="class">&headnoteTopicsClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:call-template name="RenderKeyIconImage">
					<xsl:with-param name="generateRefKeyTable" select="$generateRefKeyTable"/>
					<xsl:with-param name="generateKeyIconImage" select="$generateKeyIconImage"/>
				</xsl:call-template>
				<xsl:if test="$generateTopicKeyHierarchy">
					<xsl:call-template name="RenderTopicKeyHierarchy">
						<xsl:with-param name="generateRefKeyTable" select="$generateRefKeyTable"/>
						<xsl:with-param name="topicKeyContents" select="$topicKeyContents"/>
						<xsl:with-param name="topicKeyRefKey" select="$topicKeyRefKey"/>
						<xsl:with-param name="topicKeyRefKeyText" select="$topicKeyRefKeyText"/>
						<xsl:with-param name="priorClassification" select="$priorClassification"/>
						<xsl:with-param name="renderWithTheLine" select="$renderWithTheLine"/>
					</xsl:call-template>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderTopicKeyHierarchy">
		<xsl:param name="generateRefKeyTable" select="true()"/>
		<xsl:param name="topicKeyContents"/>
		<xsl:param name="topicKeyRefKey"/>
		<xsl:param name="topicKeyRefKeyText"/>
		<xsl:param name="priorClassification"/>
		<xsl:param name="renderWithTheLine" select="true()"/>
		<div>
			<xsl:if test="$renderWithTheLine">
				<xsl:attribute name="class">&fancyKeyciteContainerBottomClass;</xsl:attribute>
			</xsl:if>
			<div>
				<xsl:if test="$renderWithTheLine">
					<xsl:attribute name="class">&fancyKeyciteContainerClass;</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$generateRefKeyTable">
						<div class="&topicKeyContentTableClass;">
							<xsl:copy-of select="$topicKeyContents"/>
							<xsl:if test="string-length($topicKeyRefKey) &gt; 0 and string-length($topicKeyRefKeyText) &gt; 0">
								<div class="&headnoteKeyPairClass;">
									<xsl:attribute name="style">font-weight: bold</xsl:attribute>
									<span class="&headnoteRefNumberClass;">
										<xsl:copy-of select="$topicKeyRefKey"/>
									</span>
									<span class="&headnoteTopicKeyClass;">
										<xsl:copy-of select="$topicKeyRefKeyText"/>
									</span>
								</div>
							</xsl:if>
							<xsl:if test="string-length($priorClassification) &gt; 0">
								<div class="&headnoteKeyPairClass;">
									<span class="&headnoteTopicKeyClass;">
										<xsl:copy-of select="$priorClassification"/>
									</span>
								</div>
							</xsl:if>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="&popupHeadnoteClass;"></div>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>