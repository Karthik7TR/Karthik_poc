<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:key name="pinpoint-anchor" match="pinpoint.anchor" use="@hashcode"/>
	
	<xsl:template match="n-metadata"/>
	<xsl:template match="cmd.first.line.cite" />
	<xsl:template match="cmd.alternative.cite/internal.reference"/>
	<xsl:template match="cmd.expandedcite"/>
	<xsl:template match="cmd.tax.expandedcite"/>
	<xsl:template match="search.link"/>
	
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeBlackLawDictionaryClass;'"/>
			</xsl:call-template>
			<xsl:comment> </xsl:comment>
			<xsl:apply-templates />
			<xsl:call-template name="EndOfDocument" />
		</div>		
	</xsl:template>
	
	<xsl:template match="headtext" priority="1">
			<xsl:param name="divId"/>
			<xsl:param name="foreignOrigin">
				<xsl:value-of select="//blacks.entry/term/@foreign.origin"/>
			</xsl:param>
			<xsl:param name="contents">
				<xsl:choose>
					<xsl:when test="$foreignOrigin = 'y'">
						<i>
							<xsl:value-of select="//md.shorttitle"/>
						</i>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="//md.shorttitle"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:param>
			<xsl:choose>
				<xsl:when test="../@toc-guid = ''">
					<xsl:call-template name="wrapWithDiv">
						<xsl:with-param name="class" select="'&titleClass;'"/>
						<xsl:with-param name="contents" select="$contents"/>
					</xsl:call-template>
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="renderHeadTextDiv">
					<xsl:with-param name="divId" select="$divId"/>
				</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	
	<xsl:template match="doc.title" priority="1">
		<xsl:if test="not(name(parent::*) = 'appendix')">
			<xsl:apply-templates />
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="cmd.cites">
			<xsl:variable name="contents">
				<xsl:call-template name="concatCites" />
			</xsl:variable>
			<xsl:if test="string-length($contents) &gt; 0">
				<div class="&citesClass;">
					<xsl:copy-of select="$contents"/>
				</div>
			</xsl:if>
	</xsl:template>

	<xsl:template name="concatCites">
		<xsl:variable name="pertinentCites" select="//dictionary/content.metadata.block/cmd.identifiers/cmd.cites/cmd.second.line.cite | //dictionary/content.metadata.block/cmd.identifiers/cmd.cites/cmd.alternative.cite"/>
		<xsl:choose>
				<xsl:when test="string-length($pertinentCites) &gt; 0">
					<xsl:for-each select="$pertinentCites">
						<xsl:apply-templates select="." />
						<xsl:if test="position() != last()">
							<xsl:text>,<![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="//n-metadata/metadata.block/md.descriptions/md.longtitle" />
				</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="nav.links" priority="1">
		<xsl:variable name="contents">
			<xsl:variable name="citeQueries" select=".//cite.query"/>
			<xsl:for-each select="$citeQueries">
			<xsl:apply-templates select="."/>
			<xsl:if test="position() != last()">
				<xsl:text>|<![CDATA[ ]]></xsl:text>
			</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents" select="$contents"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="blacks.entry">
		<xsl:variable name="contents">
			<xsl:apply-templates select="term"/>
			<xsl:apply-templates select="pronunciation"/>
			<xsl:apply-templates select="part.of.speech"/>
			<xsl:apply-templates select="term.type"/>
			<xsl:apply-templates select="etymology"/>
			<xsl:apply-templates select="usage"/>
			<xsl:apply-templates select="date"/>
			<xsl:apply-templates select="subject.matter"/>
			<xsl:apply-templates select="sense"/>
		</xsl:variable>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents" select="$contents"/>
		</xsl:call-template>
		<xsl:call-template name="copyright"/>
	</xsl:template>
	
	<xsl:template match="sense">
		<xsl:variable name="contents">
			<xsl:if test="string-length(@designator) &gt; 0">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:value-of select="@designator"/>
				<xsl:text>.</xsl:text>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<strong>
				<xsl:value-of select="$contents"/>
			</strong>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="term | name | pronunciation | part.of.speech | term.type | date | etymology | usage | subject.matter">
		<xsl:apply-templates/>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>
	
	<xsl:template match="usage">
		<i>
			<xsl:value-of select="@usage.type"/>
		</i>
		<xsl:text>.&#160;</xsl:text>
	</xsl:template>
	
	<xsl:template match="subentry">
		<xsl:variable name="contents">
			<xsl:text>-<![CDATA[ ]]></xsl:text>
			<xsl:apply-templates/>
		</xsl:variable>
		<xsl:call-template name="wrapContentBlockWithCobaltClass">
			<xsl:with-param name="contents" select="$contents"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="cmd.copyright">
		<xsl:variable name="copy-right">
		<xsl:value-of select="//cmd.copyright"/>
		</xsl:variable>
	</xsl:template>
	
	<xsl:template name="copyright">
		<br/>
		<div class="&copyrightClass;">
			<xsl:value-of select="//cmd.copyright"/>
		</div>
	</xsl:template>
	
	<xsl:template match="pinpoint.anchor" priority="1">
		<xsl:variable name="pubNumber">
			<xsl:value-of select="//md.publications/md.publication[1]/md.pubid"/>
		</xsl:variable>
		<a id="&pinpointIdPrefix;{concat('sp_', $pubNumber, '_', translate(@hashcode, ' ', ''))}">
			<xsl:comment>anchor</xsl:comment>
		</a>
	</xsl:template>
	
	<xsl:template match="address/para/paratext">
		<xsl:value-of select="."/>
	</xsl:template>
	
	<xsl:template match="cite.query" name="citeQuery" priority="1">
		<xsl:param name="citeQueryElement" select="."/>
		<xsl:param name="linkContents">
			<xsl:apply-templates select="$citeQueryElement/node()[not(self::starpage.anchor)]" />
		</xsl:param>
		<xsl:param name="transitionType" select="'&transitionTypeDocumentItem;'" />
		<xsl:param name="originationContext" select="'&docDisplayOriginationContext;'"/>
		<xsl:param name ="originationPubNum"/>
		<xsl:variable name="fullLinkContents">
			<xsl:choose>
				<xsl:when test="string-length($SourceSerial) &gt; 0 and ($citeQueryElement/@w-serial-number = $SourceSerial or $citeQueryElement/@w-normalized-cite = $SourceSerial)">
					<xsl:call-template name="markupSourceSerialSearchTerm">
						<xsl:with-param name="linkContents" select="$linkContents"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$linkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="isDraggable">
			<xsl:choose>
				<xsl:when test="$citeQueryElement/@w-ref-type = 'KD' or $citeQueryElement/@w-ref-type = 'KW'">
					<xsl:text>false</xsl:text>
				</xsl:when>
				<xsl:when test="not($AllowLinkDragAndDrop)">
					<xsl:text>false</xsl:text>					
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>true</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
    
		<xsl:apply-templates select="$citeQueryElement/starpage.anchor"/>

		<xsl:variable name="sourceCite">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite[not(/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite)]" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="string-length($fullLinkContents) &gt; 0">

      <xsl:variable name="infoType" select="/Document/n-metadata/metadata.block/md.infotype"/>

      <xsl:variable name="persistentUrl">
        <xsl:choose>
          <xsl:when test="$citeQueryElement/@w-ref-type = 'WK'">
            <xsl:value-of select="CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', concat('typecode=', $infoType/@typecode), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&transitionTypeParamName;=', $transitionType))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="CiteQuery:GetCiteQueryLink($citeQueryElement, $Guid, '', $sourceCite, concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, concat('&transitionTypeParamName;=', $transitionType), concat('pubNum=', $originationPubNum))"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
			
			<xsl:variable name="wpinpointpage">
				<xsl:if test="string-length($citeQueryElement/@w-pinpoint-page) &gt; 0">
					<xsl:value-of select="$citeQueryElement/@w-pinpoint-page"/>
				</xsl:if>
			</xsl:variable>
			
			<xsl:variable name="internalReference">
				<xsl:variable name="pubNumber">
					<xsl:value-of select="//md.publications/md.publication[1]/md.pubid"/>
				</xsl:variable>
				<xsl:for-each select="key('pinpoint-anchor', $wpinpointpage)">
					<xsl:variable name="concatprefix">
						<xsl:text>&pinpointIdPrefix;</xsl:text>
						<xsl:value-of select="concat('sp_', $pubNumber, '_', translate($wpinpointpage, ' ', ''))"/>
					</xsl:variable>
					<xsl:value-of select="concat('#', $concatprefix)"/>
				</xsl:for-each>
			</xsl:variable>
			
			<xsl:choose>
				<xsl:when test="string-length($persistentUrl) &gt; 0 and $DisplayLinksInDocument">
					<a>
						<xsl:attribute name="id">
							<xsl:text>&linkIdPrefix;</xsl:text>
							<xsl:choose>
								<xsl:when test="string-length($citeQueryElement/@ID) &gt; 0">
									<xsl:value-of select="$citeQueryElement/@ID"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="generate-id($citeQueryElement)"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:text>&linkClass;</xsl:text>
							<xsl:if test="$isDraggable = 'true'">
								<xsl:text><![CDATA[ ]]>&linkDraggableClass;</xsl:text>
							</xsl:if>
						</xsl:attribute>
						<xsl:attribute name="href">
							<xsl:choose>
									<xsl:when test="string-length($internalReference) &gt; 0">
										<xsl:value-of select="$internalReference"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:copy-of select="$persistentUrl"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:if test="$citeQueryElement/@w-ref-type = 'WM'">
							<xsl:attribute name="target">
								<xsl:text>_blank</xsl:text>
							</xsl:attribute>
						</xsl:if>
						<xsl:copy-of select="$fullLinkContents"/>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$fullLinkContents"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>

		 <!--Add a space if the following sibling is a cite.query--> 
		<xsl:if test="$citeQueryElement/following-sibling::node()[1]/self::cite.query">
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
