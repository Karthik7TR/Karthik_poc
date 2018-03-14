<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited.-->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="NotesOfDecisions.xsl"/>
	<xsl:include href="Credit.xsl"/>
	<xsl:include href="ContextAndAnalysis.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="LinkedToc.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="Annotations.xsl"/>
	<xsl:include href="HistoryNotes.xsl"/>
	<xsl:include href="LeaderWLN.xsl"/>
	<xsl:include href="RuleBookMode.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeCodesStatutesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata"/>
			<xsl:apply-templates/>
			<xsl:if test="not($IsRuleBookMode)">
				<xsl:apply-templates select="n-docbody//content.metadata.block" mode="footerCustomCitation" />
				<!--Adding a Div so the a separate Fo block is made for the currencyblock  Fix for Bug303578-->
				<div>
					<xsl:apply-templates select="n-docbody//include.currency.block/include.currency" mode="currency"/>
				</div>
				<xsl:call-template name="EndOfDocument" />
			</xsl:if>
			<xsl:if test="$IsRuleBookMode">
				<xsl:apply-templates select="/" mode="Custom"/>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="AddProductDocumentClasses">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:text>&documentFixedHeaderView;</xsl:text>
	</xsl:template>

	<!--Bug#700820,For Missing whitespace above some paragraphs-->
	<xsl:template match="para[preceding-sibling::subsection]/paratext" priority="1">
		<br/>
		<xsl:call-template name="renderParagraphTextDiv"/>
	</xsl:template>

	<!--Supress this-->
	<xsl:template match="include.currency"/>

	<xsl:template match="content.metadata.block" mode="footerCustomCitation">
		<div class="&alignHorizontalLeftClass;">
			<xsl:apply-templates select ="cmd.identifiers/cmd.cites/cmd.expandedcite"/>
		</div>
	</xsl:template>

	<xsl:template match="subsection//headtext">
		<div class="&headtextClass; &alignHorizontalCenterClass;">
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="include.copyright.block/include.copyright[@n-include_collection = 'w_codes_stamsgp']" priority="1">
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="." />
		</xsl:call-template>
	</xsl:template>

	<!-- override the cite.query match for only cite.query elements that are "Refs and Annos" links (in the TOC). -->
	<xsl:template match ="cite.query[not(./@w-pub-number) and ./@w-ref-type = 'CM' and /Document/document-data/doc-type-id = '48' and contains(text(),'(Refs &amp; Annos)')]">
		<xsl:call-template name ="citeQuery">
			<xsl:with-param name="originationPubNum" select="/Document/n-metadata/metadata.block/md.publications/md.publication/md.pubid" />
		</xsl:call-template>
	</xsl:template>

	<!-- override the cite.query match for the new PP ref-type and check for IAC. -->
	<xsl:template match ="cite.query[./@w-ref-type = 'PP']">
		<xsl:choose>
			<xsl:when test="$IAC-LIGER-POPULARNAME">
				<xsl:call-template name ="citeQuery" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- override the cite.query match for the new PS ref-type. -->
	<xsl:template match ="cite.query[./@w-ref-type = 'PS']">
		<xsl:variable name="href">
			<xsl:call-template name="CreateUrl">
				<xsl:with-param name ="pubNum" select="@w-pub-number" />
				<xsl:with-param name ="cite" select="@w-normalized-cite" />
				<xsl:with-param name ="originatingDoc" select="@ID" />
				<xsl:with-param name ="refType" select="@w-ref-type" />
			</xsl:call-template>
		</xsl:variable>
		<a class="&linkClass;">
			<xsl:attribute name="href">
				<xsl:copy-of select="$href"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template name="CreateUrl">
		<xsl:param name="pubNum"/>
		<xsl:param name="cite"/>
		<xsl:param name="originatingDoc"/>
		<xsl:param name="refType"/>
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentByLookup', 'viewType=FullText', 'findType=L', concat('pubNum=',$pubNum),concat('cite=',$cite),concat('originatingDoc=',$originatingDoc), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
	</xsl:template>

	<!-- Suppress these two elements since they look weird. -->
	<xsl:template match="md.secondary.cites | popular.name.doc.title" />

	<xsl:template match="include.head.block">
		<div class="&headtextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Message.Block/Message -->
	<xsl:template match="message.block">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="message.block/message">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="signature.block">
		<div>
			<xsl:for-each select="signature.line/signature">
				<xsl:apply-templates/>
				<br/>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="body.footnote.block" />

	<xsl:template match="abbreviations.reference | abbreviations">
		<xsl:variable name="refid" select="translate(@refid, '?', 'Þ')" />
		<xsl:variable name="id" select="translate(@ID, '?', 'Þ')" />
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<div>
			<xsl:if test="string-length($contents) &gt; 0 or string-length($id) &gt; 0">
				<xsl:choose>
					<xsl:when test="key('allElementIds', $refid)">
						<a href="{concat('#&internalLinkIdPrefix;', $refid)}" class="&internalLinkClass; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
							<xsl:if test="string-length($id) &gt; 0">
								<xsl:attribute name="id">
									<xsl:value-of select="concat('&internalLinkIdPrefix;', $id)"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:copy-of select="$contents"/>
							<xsl:comment>anchor</xsl:comment>
						</a>
					</xsl:when>
					<xsl:when test="string-length($id) &gt; 0">
						<a id="{concat('#&internalLinkIdPrefix;', $id)}">
							<xsl:comment>anchor</xsl:comment>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy-of select="$contents"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="centdol"/>

	<!--Fix for the missing content in WLN(Collection : w_codesstailnvdp)-->
	<xsl:template match="ed.note.grade">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="annotations[/Document/document-data/collection = 'w_codesstailnvdp']">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and $StatutoryTextOnly" />
			<xsl:otherwise>
				<xsl:call-template name="wrapContentBlockWithGenericClass">
					<xsl:with-param name="contents">
						<xsl:variable name="editorsNotes">
							<xsl:apply-templates select="node()[not(self::nod.block)]" />
						</xsl:variable>
						<xsl:if test="string-length($editorsNotes) &gt; 0">
							<xsl:copy-of select="$editorsNotes"/>
						</xsl:if>
						<xsl:apply-templates select="nod.block[1]"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="annotations[contains('|w_codesstaohnvdp|w_codesstausnvdp|' , concat('|', /Document/document-data/collection, '|'))]">
		<xsl:choose>
			<xsl:when test="$DeliveryMode and $StatutoryTextOnly" />
			<xsl:otherwise>
				<!--international collections -->
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Supress tgroup tags that have warning attribute. bug 339115)-->
	<xsl:template match="tgroup[@warning]" priority="2"/>

	<!--call tgroup template passing in the column width check parameter.-->
	<xsl:template match="tgroup" priority="1">
		<xsl:call-template name ="TGroupTemplate">
			<xsl:with-param name ="checkNoColWidthExists" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="tbody/row/entry" priority="1">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<xsl:if test="not($colposition &gt; count($columnInfo))">
			<td>
				<xsl:call-template name="RenderTableCell">
					<xsl:with-param name="columnInfo" select="$columnInfo"/>
					<xsl:with-param name="colalign" select="$colalign" />
					<xsl:with-param name="colposition" select="$colposition" />
					<xsl:with-param name="colwidth" select="$colwidth" />
				</xsl:call-template>
			</td>
		</xsl:if>
	</xsl:template>

	<!-- Credits-->
	<xsl:template match="credit[contains('|w_codesstaohnvdp|w_codesstausnvdp|' , concat('|', /Document/document-data/collection, '|'))]" >
		<!--international collections -->
		<div class="&paraIndentClass; &paraMainClass;">
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', '&credits;')" />
			</xsl:attribute>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="head//headtext[contains('|w_codesstaohnvdp|w_codesstausnvdp|' , concat('|', /Document/document-data/collection, '|')) and (ancestor::reference.block | ancestor::credit)]" priority="2">
		<!--international collections-->
		<div class="&headtextClass;">
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="subsection.hovertext">
		<!--<xsl:variable name="originalText" select="'26 CFR Â§â€‚1.0-1(b)'"></xsl:variable>-->
		<xsl:variable name="originalText" select="@text"></xsl:variable>
		<span class="&hoverText;" title="{DocumentExtension:ToXmlEncodedString($originalText)}">
			<xsl:apply-templates />
		</span>

		<!--Setup internal anchors for the hidden TOC-->
		<xsl:if test="$IsRuleBookMode">
			<xsl:variable name="hashValue">
				<xsl:value-of select="ancestor::subsection[1]/@ID"/>
			</xsl:variable>

			<xsl:if test="$hashValue">
				<a id="&internalLinkIdPrefix;{$hashValue}"></a>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="parseYearMonthDayDateFormat">
		<xsl:param name="date" select="."/>
		<xsl:param name="displayDay" />
		<xsl:param name="displayTime" />
		<xsl:if test="string-length($date) &gt; 7 and number($date) != 'NaN'">
			<xsl:variable name ="year" select ="substring($date,1,4)"/>
			<xsl:variable name ="month" select ="substring($date,5,2)"/>
			<xsl:variable name ="day" select ="substring($date,7,2)"/>
			<xsl:choose>
				<xsl:when test ="$month = 01">January</xsl:when>
				<xsl:when test ="$month = 02">February</xsl:when>
				<xsl:when test ="$month = 03">March</xsl:when>
				<xsl:when test ="$month = 04">April</xsl:when>
				<xsl:when test ="$month = 05">May</xsl:when>
				<xsl:when test ="$month = 06">June</xsl:when>
				<xsl:when test ="$month = 07">July</xsl:when>
				<xsl:when test ="$month = 08">August</xsl:when>
				<xsl:when test ="$month = 09">September</xsl:when>
				<xsl:when test ="$month = 10">October</xsl:when>
				<xsl:when test ="$month = 11">November</xsl:when>
				<xsl:when test ="$month = 12">December</xsl:when>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:if test="$displayDay">
				<xsl:value-of select ="$day"/>
				<xsl:text>,<![CDATA[ ]]></xsl:text>
			</xsl:if>

			<xsl:value-of select ="$year"/>

			<xsl:if test="$displayTime">
				<xsl:if test="string-length($date) &gt; 13">
					<xsl:variable name ="hour" select ="substring($date,9,2)"/>
					<xsl:variable name ="minute" select ="substring($date,11,2)"/>
					<xsl:variable name ="second" select ="substring($date,13,2)"/>

					<xsl:text><![CDATA[ ]]></xsl:text>


					<xsl:choose>
						<xsl:when test="number($hour) = 00">12</xsl:when>
						<xsl:when test="number($hour) &gt; 12">
							<xsl:value-of select="number($hour) - 12"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="number($hour)"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:value-of select ="concat(':', $minute, ':', $second)"/>
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:choose>
						<xsl:when test="$hour &gt; 11">PM</xsl:when>
						<xsl:otherwise>AM</xsl:otherwise>
					</xsl:choose>
				</xsl:if>

			</xsl:if>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
