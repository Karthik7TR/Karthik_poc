<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="N-HIT" name="nHit">
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>

		<xsl:choose>
			<xsl:when test="parent::N-HIT">
				<xsl:call-template name="RenderSearchTermJumpPoint">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:when>
			<!-- hack for RI -->
			<xsl:when test="string-length($SourceSerial) &gt; 0 and (ancestor::cite.query/@w-serial-number = $SourceSerial or ancestor::cite.query/@w-normalized-cite = $SourceSerial)">
				<xsl:call-template name="RenderSearchTermJumpPoint">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:when>
			<!-- end hack -->
			<xsl:when test="(@n-wordset = $SearchWithinTermsWordset) or (ancestor::N-HIT[@n-wordset = $SearchWithinTermsWordset])">
				<xsl:call-template name="nWithin">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="(@n-wordset = $SecondaryTermsWordset) or (ancestor::N-HIT[@n-wordset = $SecondaryTermsWordset])">
				<xsl:call-template name="nLocate">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderTerm">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="N-LOCATE" name="nLocate">
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:choose>
			<xsl:when test="(@n-wordset = $SecondaryTermsWordset) or (ancestor::N-HIT[@n-wordset = $SecondaryTermsWordset]) or ((not(@n-wordset) or not(ancestor::N-HIT[@n-wordset])) and $SecondaryTermsWordset &gt; -1)">
				<xsl:call-template name="RenderTerm">
					<xsl:with-param name="contents" select="$contents" />
					<xsl:with-param name="secondaryTerm" select="true()" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderTerm">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="N-WITHIN" name="nWithin">
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:choose>
			<xsl:when test="(@n-wordset = $SearchWithinTermsWordset) or (ancestor::N-HIT[@n-wordset = $SearchWithinTermsWordset]) or ((not(@n-wordset) or not(ancestor::N-HIT[@n-wordset])) and $SearchWithinTermsWordset = 0)">
				<xsl:call-template name="RenderTerm">
					<xsl:with-param name="contents" select="$contents" />
					<xsl:with-param name="searchWithinTerm" select="true()" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderTerm">
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderTerm">
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:param name="secondaryTerm" select="false()" />
		<xsl:param name="searchWithinTerm" select="false()" />
		<xsl:param name="wordPos" select="@n-wordpos" />
		<xsl:param name="offset" select="@offset" />

		<xsl:if test="$EnableBestPortionNavigation and not($DeliveryMode) and not($searchWithinTerm)">
			<xsl:processing-instruction name="renderingSearchTerm" />
		</xsl:if>

		<span>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="$searchWithinTerm">
						<xsl:text>&searchWithinTermClass;</xsl:text>
					</xsl:when>
					<xsl:when test="($DeliveryMode) and ($secondaryTerm)">
						<xsl:text>&locateTermClass;</xsl:text>
					</xsl:when>
					
					<xsl:when test="ancestor::opinion.concurrance">
						<xsl:text>&searchTermClass; opinionConcurrance</xsl:text>
						<xsl:if test="$secondaryTerm">
							<xsl:text> &locateTermClass;</xsl:text>
						</xsl:if>
						<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
							<xsl:text> &searchTermNoHighlightClass;</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:when test="ancestor::opinion.dissent">
						<xsl:text>&searchTermClass; opinionDissent</xsl:text>
						<xsl:if test="$secondaryTerm">
							<xsl:text> &locateTermClass;</xsl:text>
						</xsl:if>
						<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
							<xsl:text> &searchTermNoHighlightClass;</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:when test="ancestor::opinion.cipdip">
						<xsl:text>&searchTermClass; opinionCipdip</xsl:text>
						<xsl:if test="$secondaryTerm">
							<xsl:text> &locateTermClass;</xsl:text>
						</xsl:if>
						<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
							<xsl:text> &searchTermNoHighlightClass;</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&searchTermClass;</xsl:text>
						<xsl:if test="$secondaryTerm">
							<xsl:text> &locateTermClass;</xsl:text>
						</xsl:if>
						<xsl:if test="$DisplayOnlyPagesWithSearchTerms and not($DisplayTermHighlighting)">
							<xsl:text> &searchTermNoHighlightClass;</xsl:text>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="not($DeliveryMode) and (string-length($wordPos) &gt; 0 or string-length($offset) &gt; 0)">
				<xsl:attribute name="id">
					<xsl:text>&termIdPrefix;</xsl:text>
					<xsl:choose>
						<xsl:when test="string-length($wordPos) &gt; 0">
							<xsl:value-of select="$wordPos"/>
						</xsl:when>
						<xsl:when test="string-length($offset) &gt; 0">
							<xsl:value-of select="$offset"/>
						</xsl:when>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:copy-of select="$contents" />
		</span>
	</xsl:template>

	<xsl:template name="RenderSearchTermJumpPoint">
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:param name="wordPos" select="@n-wordpos" />
		<xsl:param name="offset" select="@offset" />

		<span>
			<xsl:if test="not($DeliveryMode) and (string-length($wordPos) &gt; 0 or string-length($offset) &gt; 0)">
				<xsl:attribute name="id">
					<xsl:text>&termIdPrefix;</xsl:text>
					<xsl:choose>
						<xsl:when test="string-length($wordPos) &gt; 0">
							<xsl:value-of select="$wordPos"/>
						</xsl:when>
						<xsl:when test="string-length($offset) &gt; 0">
							<xsl:value-of select="$offset"/>
						</xsl:when>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
			<xsl:copy-of select="$contents" />
		</span>
	</xsl:template>

	<!-- This is here to handle SNAP snippets from search and other Novus result lists. -->
	<xsl:template match="bos[@snipid]" priority="1" name="SnapSnippetAnchor">
		<xsl:if test="not($DeliveryMode)">
			<xsl:if test="$IAC-SNAP-SNIPPETS-DEBUGLOG">
				<xsl:processing-instruction name="logSnippetStart"/>
			</xsl:if>
			<a>
				<xsl:attribute name="id">
					<xsl:text>&snapSnippetMarkerIdPrefix;</xsl:text>
					<xsl:value-of select="@snipid"/>
				</xsl:attribute>
				<xsl:call-template name="RenderSnapSnippetAnchorAttributes" />
			</a>
		</xsl:if>
	</xsl:template>

	<!-- Best Portion Navigation feature - Renders additional Snap Snippet anchor attributes needed for navigation. -->
	<xsl:template name="RenderSnapSnippetAnchorAttributes">
		<xsl:if test="$EnableBestPortionNavigation and not($DeliveryMode)">
			<xsl:processing-instruction name="startSnapSnippet">
				<xsl:value-of select="@snipid"/>
			</xsl:processing-instruction>
			<xsl:if test="ancestor::head | ancestor::mv.source.head | ancestor::prop.head | ancestor::form.head">
				<xsl:processing-instruction name="snapSnippetIsInHeading"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Best Portion Navigation feature - Renders Snap Snippet end marker. -->
	<xsl:template match="eos" priority="1">
		<xsl:if test="$EnableBestPortionNavigation and not($DeliveryMode)">
			<xsl:processing-instruction name="endSnapSnippet" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
