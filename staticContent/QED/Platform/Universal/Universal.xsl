<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="CommonInlineTemplates.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:include href="CharFill.xsl"/>
	<xsl:include href="DocLinks.xsl"/>
	<xsl:include href="FormAssembleLink.xsl"/>
	<xsl:include href="Fraction.xsl"/>
	<xsl:include href="Image.xsl"/>
	<xsl:include href="List.xsl"/>
	<xsl:include href="NPrivateChar.xsl"/>
	<xsl:include href="OriginalImage.xsl"/>
	<xsl:include href="Para.xsl"/>
	<xsl:include href="SpecialCharacters.xsl"/>
	<xsl:include href="StarPages.xsl"/>
	<xsl:include href="Suppressed.xsl"/>
	<xsl:include href="Table.xsl"/>
	<xsl:include href="SearchTerms.xsl"/>
	<xsl:include href="InternalReferences.xsl"/>
	<xsl:include href="Publisher.xsl"/>
	<xsl:include href="KeyCiteSNTPlaceholder.xsl"/>
	<xsl:include href="Head.xsl"/>

	<xsl:include href="WrappingUtilities.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="Language"></xsl:variable>

	<xsl:template name="AddDocumentClasses">
		<xsl:param name="contentType"/>
		<xsl:attribute name="class">
			<xsl:text>&documentClass;</xsl:text>
			<xsl:if test="string-length($contentType) &gt; 0">
				<xsl:value-of select="concat(' ', $contentType)"/>
			</xsl:if>
			<xsl:if test="$DeliveryMode">
				<xsl:if test="string-length($DeliveryFormat) &gt; 0">
					<xsl:value-of select="concat(' co_', $DeliveryFormat)"/>
				</xsl:if>
				<xsl:if test="$LinkColor = '&linkColorBlack;'">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:text>&blackLinksClass;</xsl:text>
				</xsl:if>
				<xsl:if test="$LinkUnderline">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:text>&linkUnderlineClass;</xsl:text>
				</xsl:if>
				<xsl:if test="$FontSize = '&fontSizeLarge;'">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:text>&largeFontClass;</xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test="string-length($SourceSerial) &gt; 0 or string-length($Quotes) &gt; 0">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:text>&citingRefSearchTerms;</xsl:text>
			</xsl:if>
			<xsl:if test="$PreviewMode">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:text>&previewClass;</xsl:text>
			</xsl:if>
			<xsl:if test="$EasyEditMode">
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:text>&easyEditClass;</xsl:text>
			</xsl:if>
			<xsl:call-template name="AddProductDocumentClasses" />
		</xsl:attribute>
		<!-- This needs to be the first thing in the document from a transformation perspective. -->
		<xsl:call-template name="AddIconsforIPad"/>
		<!--If deliverymode and ListItemIdentifier present then add ex: <span id=DocGuid_ListItemIdentifier_x> _x appended to end of id comes from uniqueDocumentID - Website will include this in target links"-->
		<xsl:if test="$DeliveryMode and string-length($ListItemIdentifier) &gt; 0">
			<span>
				<xsl:attribute name="id">
					<xsl:value-of select="$Guid"/>
					<xsl:text>_</xsl:text>
					<xsl:value-of select="$ListItemIdentifier"/>
				</xsl:attribute>
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template name="AddProductDocumentClasses">
	</xsl:template>

	<xsl:template match="pinpoint.anchor">
		<a id="&pinpointIdPrefix;{@hashcode}">
			<xsl:comment>anchor</xsl:comment>
		</a>
	</xsl:template>

	<xsl:template match="anchor">
		<xsl:if test="not(preceding-sibling::node()[1][self::footnote.reference or self::table.footnote.reference or self::endnote.reference])">
			<a id="&internalLinkIdPrefix;{@ID}">
				<xsl:comment>anchor</xsl:comment>
			</a>
		</xsl:if>
	</xsl:template>

	<!-- Justified.Line -->
	<xsl:template match="justified.line" name="justifiedLine">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<xsl:choose>
				<xsl:when test="@quadding = 'l'">
					<div class="&alignHorizontalLeftClass;">
						<xsl:copy-of select="$contents"/>
					</div>
				</xsl:when>
				<xsl:when test="@quadding = 'c'">
					<div class="&alignHorizontalCenterClass;">
						<xsl:copy-of select="$contents"/>
					</div>
				</xsl:when>
				<xsl:when test="@quadding = 'r'">
					<div class="&alignHorizontalRightClass;">
						<xsl:copy-of select="$contents"/>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div>
						<xsl:copy-of select="$contents"/>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Message.Block -->
	<xsl:template match="message.block">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<div class="&messageBlockClass;">
				<xsl:copy-of select="$contents"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="message.line">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Predefined.Charstring -->
	<xsl:template match="predefined.charstring">
		<hr class="&horizontalRuleClass;" />
		<xsl:text>&#x200B;</xsl:text>
		<!-- HACK to make string-length evaluate to greater than 0 -->
	</xsl:template>

	<!-- TOC for Analysis or Reference-->
	<xsl:template name="Toc">
		<xsl:param name="rootClass"/>
		<div>
			<xsl:if test="$rootClass">
				<xsl:attribute name="class">
					<xsl:value-of select="$rootClass"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@ID">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="head"/>
			<ol class="&tocMainClass;">
				<xsl:apply-templates select="node()[not(self::head)]"/>
			</ol>
		</div>
	</xsl:template>

	<xsl:template name="TocEntryCharfill">
		<div>
			<xsl:for-each select="node()">
				<xsl:choose>
					<xsl:when test="self::charfill[following-sibling::charfill]">
						<xsl:apply-templates select="."/>
					</xsl:when>
					<xsl:when test="following-sibling::charfill or following-sibling::node()/charfill">
						<xsl:apply-templates select="."/>
					</xsl:when>
					<xsl:when test="self::cite.query[charfill]">
						<xsl:call-template name="citeQuery">
							<xsl:with-param name="linkContents">
								<xsl:apply-templates select="charfill/preceding-sibling::node()"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="./charfill">
						<xsl:apply-templates select="charfill/preceding-sibling::node()"/>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</div>

		<span>
			<span>
				<xsl:for-each select="node()">
					<xsl:choose>
						<xsl:when test="self::charfill"/>
						<!--suppress-->
						<xsl:when test="(preceding-sibling::charfill or preceding-sibling::node()/charfill) and not(following-sibling::charfill)">
							<xsl:apply-templates select="."/>
						</xsl:when>
						<xsl:when test="./charfill">
							<xsl:apply-templates select="charfill/following-sibling::node()"/>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
			</span>
		</span>
		<br/>
	</xsl:template>

	<xsl:template name="TocEntry" >
		<xsl:choose>
			<xsl:when test=".//charfill">
				<li>
					<xsl:call-template name="TocEntryCharfill"/>
				</li>
			</xsl:when>
			<xsl:otherwise>
				<li class="&tocCellWithoutLeadersClass;">
					<xsl:if test="@ID|@id">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&internalLinkIdPrefix;',@ID|@id)"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="internalReference">
						<xsl:with-param name="contents">
							<xsl:choose>
								<xsl:when test="justified.line">
									<xsl:apply-templates />
								</xsl:when>
								<xsl:when test="(analysis.ref|analysis.line) and analysis.entry">
									<xsl:apply-templates select="analysis.ref|analysis.line"/>
									<ol class="&tocMainClass;">
										<xsl:apply-templates select="analysis.entry"/>
									</ol>
								</xsl:when>
								<xsl:when test="analysis.ref or @refid">
									<xsl:apply-templates />
								</xsl:when>
								<xsl:otherwise>
									<xsl:variable name="innerContents">
										<xsl:apply-templates />
									</xsl:variable>
									<xsl:if test="string-length($innerContents) &gt; 0">
										<div>
											<xsl:copy-of select="$innerContents"/>
										</div>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- TOC for Analysis or Reference-->
	<xsl:template name="TocTable">
		<xsl:param name="insertTocTableClass" select="true()"/>
		<xsl:param name="rootClass"/>
		<div>
			<xsl:if test="$rootClass">
				<xsl:attribute name="class">
					<xsl:value-of select="$rootClass"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="@ID">
				<xsl:attribute name="id">
					<xsl:value-of select="concat('&internalLinkIdPrefix;', @ID)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="head"/>
			<table>
				<xsl:if test="$insertTocTableClass">
					<xsl:attribute name="class">&tocMainClass;</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="node()[not(self::head)]"/>
			</table>
		</div>
	</xsl:template>

	<xsl:template name="TocTableEntryCharfill">
		<td class="&alignHorizontalLeftClass;">
			<div class="&leaderTableFullWidthClass; &leaderDashesClass;">
				<div class="&leaderWrapperClass;">
					<xsl:for-each select="node()">
						<xsl:choose>
							<xsl:when test="self::charfill[following-sibling::charfill]">
								<xsl:apply-templates select="."/>
							</xsl:when>
							<xsl:when test="following-sibling::charfill or following-sibling::node()/charfill">
								<xsl:apply-templates select="."/>
							</xsl:when>
							<xsl:when test="self::cite.query[charfill]">
								<xsl:call-template name="citeQuery">
									<xsl:with-param name="linkContents">
										<xsl:apply-templates select="charfill/preceding-sibling::node()"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="./charfill">
								<xsl:apply-templates select="charfill/preceding-sibling::node()"/>
							</xsl:when>
						</xsl:choose>
					</xsl:for-each>
				</div>
			</div>
		</td>

		<td class="&alignHorizontalRightClass;">
			<div class="&leaderWrapperClass;">
				<xsl:for-each select="node()">
					<xsl:choose>
						<xsl:when test="self::charfill"/>
						<!--suppress-->
						<xsl:when test="(preceding-sibling::charfill or preceding-sibling::node()/charfill) and not(following-sibling::charfill)">
							<xsl:apply-templates select="."/>
						</xsl:when>
						<xsl:when test="./charfill">
							<xsl:apply-templates select="charfill/following-sibling::node()"/>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
			</div>
		</td>
	</xsl:template>

	<xsl:template name="TocTableEntry" >
		<xsl:choose>
			<xsl:when test=".//charfill">
				<tr>
					<xsl:call-template name="TocTableEntryCharfill"/>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<tr class="&tocCellWithoutLeadersClass;">
					<xsl:choose>
						<xsl:when test="justified.line">
							<td colspan="2">
								<xsl:apply-templates />
							</td>
						</xsl:when>
						<xsl:when test="analysis.ref and analysis.entry">
							<xsl:apply-templates select="analysis.ref"/>
							<td colspan="2">
								<table class="&tocMainClass;">
									<xsl:apply-templates select="analysis.entry"/>
								</table>
							</td>
						</xsl:when>
						<xsl:when test="analysis.ref">
							<xsl:apply-templates />
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="../analysis.entry//charfill">
									<td colspan="2">
										<xsl:apply-templates />
									</td>
								</xsl:when>
								<xsl:when test="../authority.reference//charfill">
									<td colspan="2">
										<xsl:apply-templates />
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td>
										<xsl:apply-templates />
									</td>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="text.line">
		<xsl:if test=".//text()">
			<div>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="EndOfDocumentCopyright">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:param name="endOfDocumentCopyrightTextVerbatim" select="false()"/>
		<tr>
			<td>
				<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&endOfDocumentTextKey;', '&endOfDocumentText;')"/>
			</td>
			<xsl:choose>
				<xsl:when test="$endOfDocumentCopyrightTextVerbatim">
					<td class="&endOfDocumentCopyrightClass;"><xsl:copy-of select="$endOfDocumentCopyrightText"/></td>
				</xsl:when>
				<xsl:otherwise>
					<td class="&endOfDocumentCopyrightClass;">&copy;<xsl:text><![CDATA[ ]]></xsl:text><xsl:value-of select="$currentYear"/><xsl:text><![CDATA[ ]]></xsl:text><xsl:copy-of select="$endOfDocumentCopyrightText"/></td>
				</xsl:otherwise>
			</xsl:choose>
		</tr>
	</xsl:template>
	
	<xsl:template name="EndOfDocument">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:param name="endOfDocumentCopyrightTextVerbatim" select="false()"/>
		<xsl:choose>
			<xsl:when test="$PreviewMode = 'True'">
				<xsl:call-template name="AdditionalContent" />
				<xsl:if test="$DeliveryMode = 'True' ">
					<xsl:call-template name="LinkBackToDocDisplay" />
				</xsl:if>
			</xsl:when>
			<xsl:when test="not($EasyEditMode)">
				<table>
					<xsl:choose>
						<!--Cannot use id for public records documents because we render and print multiple documents a-->
						<xsl:when test="$IsPublicRecords = true()">
							<xsl:attribute name="class">
								<xsl:text>&endOfDocumentId;</xsl:text>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="id">
								<xsl:text>&endOfDocumentId;</xsl:text>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					
					<xsl:call-template name="EndOfDocumentCopyright">
						<xsl:with-param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText" />
						<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="$endOfDocumentCopyrightTextVerbatim" />
					</xsl:call-template>
					
					<!-- #region Business Story 808223.This code will be removed as soon as LEO team finish the testing of the new content for California Code of Regulations on CI and DEMO environments.
							// !ATTENTION! No code except the one related to Business Story 808223 should be based on the logic below. It is implemented solely for the purpose of testing of the new content.-->
					<xsl:if test="DocumentExtension:ShouldDisplayEffectiveDates(//md.doctype.name)">
						<xsl:variable name="startEffectiveDate">
							<xsl:value-of select="//md.starteffective" />
						</xsl:variable>
						<xsl:variable name="endEffectiveDate">
							<xsl:value-of select="//md.endeffective" />
						</xsl:variable>
						<xsl:if test="$startEffectiveDate">
							<tr>
								<td>
									<strong>Start Effective Date:</strong>
								</td>
								<td>
									<xsl:value-of select="DocumentExtension:ReformatDate($startEffectiveDate, 'd', '', 'yyyyMMddHHmmss')"/>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="$endEffectiveDate">
							<tr>
								<td>
									<strong>End Effective Date:</strong>
								</td>
								<td>
									<xsl:value-of select="DocumentExtension:ReformatDate($endEffectiveDate, 'd', '', 'yyyyMMddHHmmss')"/>
								</td>
							</tr>
						</xsl:if>
					</xsl:if>
					<!-- #endregion-->
					
				</table>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="AdditionalContent">
		<div id="&additionalContentId;" class="&additionalContentClass;">&additionalContentText;</div>
	</xsl:template>

	<xsl:template name="LinkBackToDocDisplay">
		<div>
			<div class="&outOfPlanLabel;">
				<xsl:text>&thisDocumentOutOfPlanText;</xsl:text>
			</div>
			<a id="&linkIdPrefix;" class="&linkClass;">
				<xsl:attribute name="href">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name ="documentGuid" select="$Guid" />
					</xsl:call-template>
				</xsl:attribute>
				<span>
					<xsl:text>&viewOnLineDocument;</xsl:text>
				</span>
			</a>
		</div>
	</xsl:template>

	<xsl:template match="textrule">
		<xsl:choose>
			<xsl:when test="@position = 'baseline'">
				<xsl:choose>
					<xsl:when test="$DeliveryMode">
						<span>
							<xsl:variable name="length">
								<xsl:choose>
									<xsl:when test="@length">
										<xsl:value-of select="translate(@length, 'em', '')"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>1</xsl:text>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:call-template name="repeat">
								<xsl:with-param name="contents" select="'_'" />
								<xsl:with-param name="repetitions" select="number($length)" />
							</xsl:call-template>
						</span>
					</xsl:when>
					<xsl:otherwise>
						<span class="&baselineTextRuleClass;">
							<xsl:attribute name="style">
								<xsl:text>border-bottom-width:1pt;</xsl:text>
								<xsl:if test="@length">
									<xsl:value-of select="concat('width:', normalize-space(@length), ';')" />
								</xsl:if>
							</xsl:attribute>
							<xsl:text><![CDATA[ ]]></xsl:text>
						</span>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="@position = 'midline'">
				<hr class="&horizontalRuleClass;" />
				<xsl:text>&#x200B;</xsl:text>
				<!-- HACK to make string-length evaluate to greater than 0 -->
			</xsl:when>
			<xsl:otherwise>
				<!-- There are also potential @position values of 'ascender' and 'descender', but I can't find any live documents containing them -->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="display.quote">
		<xsl:if test=".//text()">
			<blockquote>
				<div>
					<xsl:apply-templates />
				</div>
			</blockquote>
		</xsl:if>
	</xsl:template>

	<xsl:template match="hide">
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&hiddenVisibilityClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hsi">
		<span class="&hiddenVisibilityClass;">
			<xsl:attribute name="style">
				<xsl:if test="@value">
					<xsl:value-of select="concat('width:', normalize-space(@value), 'px;')" />
				</xsl:if>
			</xsl:attribute>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</span>
	</xsl:template>

	<!--
	Matching on begin.quote and end.quote tags to write out processing instructions if ids match.  These 
	processing instructions will be read by DocumentSxltWriter to write out span or N-HIT tags in the markup
	-->
	<xsl:template match="begin.quote" name="beginQuote">
		<xsl:param name="additionalContent" />
		<xsl:if test="string-length(@ID) &gt; 0">
			<xsl:variable name="delimitedId" select="concat(';', concat(@ID, ';'))" />
			<xsl:if test="contains($Quotes, $delimitedId)">
				<xsl:copy-of select="$additionalContent"/>
				<xsl:processing-instruction name="start-highlighting"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="end.quote">
		<xsl:if test="string-length(@refid) &gt; 0">
			<xsl:variable name="delimitedRefId" select="concat(';', concat(@refid, ';'))" />
			<xsl:if test="contains($Quotes, $delimitedRefId)">
				<xsl:processing-instruction name="end-highlighting"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="begin.unpublished">
		<div class="&simpleContentBlockClass; &alignHorizontalCenterClass;">&unpublishedTextOpening;</div>
	</xsl:template>

	<xsl:template match="end.unpublished">
		<div class="&simpleContentBlockClass; &alignHorizontalCenterClass;">&unpublishedTextClosing;</div>
	</xsl:template>

	<!-- Suppress text nodes that contain 'UNPUBLISHED TEXT FOLLOWS' if the text node is the first text node preceding a "begin.unpublished" element -->
	<xsl:template match="text()[contains(normalize-space(.), 'UNPUBLISHED TEXT FOLLOWS') and generate-id(.) = generate-id(following::begin.unpublished[1]/preceding::text()[1])]" priority="1" />

	<!-- Suppress text nodes that contain 'END OF UNPUBLISHED TEXT' if the text node is the first text node following an "end.unpublished" element -->
	<xsl:template match="text()[contains(normalize-space(.), 'END OF UNPUBLISHED TEXT') and generate-id(.) = generate-id(preceding::end.unpublished[1]/following::text()[1])]" priority="1" />

	<xsl:template name="KeyCiteRegulatoryMessage">
		<div class="&seeCitingRefsClass;">&keyciteRegulatoryMessageText;</div>
	</xsl:template>

	<!-- For all Statues and Regulations documents -->
	<xsl:template match="ed.note.kitn">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paratextMainClass; &centerClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="AddIconsforIPad">
		<xsl:if test="not($IsPersisted)">
			<xsl:if test="$IsIpad and (string-length($StatusIndicatorHtml) &gt; 0)">
				<ul id="&iPadStatusId;">
					<xsl:value-of select="$StatusIndicatorHtml" disable-output-escaping="yes" />
				</ul>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Used for Easy Edit stylesheets -->
	<xsl:template name="EasyEditFlag">
		<xsl:if test="not($IsPersisted) and $DisplayEasyEditLink ">
			<input type="hidden" id="&easyEditLinkId;"></input>
		</xsl:if>
	</xsl:template>

	<xsl:template match="form | verdict.form | charge | head[preceding-sibling::section.body or following-sibling::section.body]" mode="EasyEdit">
		<xsl:apply-templates select="."/>
	</xsl:template>

	<xsl:template match="text()" mode="EasyEdit" />

	<!-- Used for Versioned Statutes to display effective dates in document -->
	<xsl:template name="DisplayEffectiveDates">
		<xsl:variable name="dateValue">
			<xsl:choose>
				<xsl:when test="string-length($EffectiveStartDate) &gt; 0">
					<xsl:text>Effective: </xsl:text>
					<xsl:value-of select="$EffectiveStartDate"/>
					<xsl:if test="string-length($EffectiveEndDate) &gt; 0">
						<xsl:text> to </xsl:text>
						<xsl:value-of select="$EffectiveEndDate"/>
					</xsl:if>
				</xsl:when>
				<xsl:when test="string-length($EffectiveEndDate) &gt; 0">
					<xsl:text>Effective: [See Text Amendments] to </xsl:text>
					<xsl:value-of select="$EffectiveEndDate"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="string-length($dateValue) &gt; 0">
			<div class="&effectiveDateClass;">
				<xsl:value-of select="$dateValue"/>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>