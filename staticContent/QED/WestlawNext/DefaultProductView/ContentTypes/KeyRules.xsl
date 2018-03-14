<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Date.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeKeyRulesClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template name="AddProductDocumentClasses">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:text>&khContent; &documentFixedHeaderView;</xsl:text>
	</xsl:template>
	
	<xsl:template match="keyrules">
		<xsl:choose>
				<xsl:when test="not($DeliveryMode)">
					<div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" >
						<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
						<div id="&coDocHeaderContainer;">
							<xsl:call-template name="wrapWithDiv">
								<xsl:with-param name="class" select="'&titleClass;'" />
								<xsl:with-param name="contents">
									<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.descriptions/md.longtitle"/>
								</xsl:with-param>
							</xsl:call-template>
							<div class="&coProductName;">
								<xsl:variable name="displayableCites" select="//cmd.cites/cmd.first.line.cite | //cmd.cites/cmd.second.line.cite | //cmd.cites/cmd.third.line.cite" />
								<xsl:if test="string-length($displayableCites) &gt; 0">
									<xsl:for-each select="$displayableCites">
										<xsl:call-template name="wrapContentBlockWithGenericClass"/>
									</xsl:for-each>
								</xsl:if>
							</div>
							<table class="&coDocContentExtras;">
								<tbody>
									<tr>
										<td>
											<xsl:if test="front.matter/date.line">
												<xsl:apply-templates select="front.matter/date.line"/>
											</xsl:if>
										</td>

										<td id="&coLastViewInfo;">
											<xsl:if test="$lastViewed != ''">
												<xsl:variable name="LastViewedLabel">
													&lastViewText;
													<xsl:call-template name="wrapWithSpan">
														<xsl:with-param name="contents" select="$lastViewed"/>
													</xsl:call-template>
												</xsl:variable>
												<xsl:call-template name="wrapWithDiv">
													<xsl:with-param name="contents" select="$LastViewedLabel" />
												</xsl:call-template>
											</xsl:if>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="BuildHeading" />
				</xsl:otherwise>
			</xsl:choose>														
		<div id="&coDocContentBody;">					
			<xsl:choose>
				<!--Apply the templates to everything except for the nodes in the heading which is already-->
				<!--taken care of above (to avoid having duplicate headings)-->
				<xsl:when test="$DeliveryMode and not($ShowKeyRuleChecklist) and not($ShowKeyRuleTiming) and not($ShowKeyRuleDocuments)">
					<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.hierarchies">
						<xsl:with-param name="fromDelivery" select="'true'"/>
					</xsl:apply-templates>
					<xsl:apply-templates select="./*[not(starts-with(name(), 'content.metadata.block') or starts-with(name(), 'front.matter') or starts-with(name(), 'document.type.block'))]" />
				</xsl:when>
				<xsl:when test="not($DeliveryMode)">
					<!--Apply the templates to everything except for the nodes in the heading which is already-->
					<!--taken care of above (to avoid having duplicate headings)-->
					<xsl:apply-templates select="./*[not(starts-with(name(), 'content.metadata.block') or starts-with(name(), 'front.matter') or starts-with(name(), 'document.type.block'))]" />
					<!--<xsl:apply-templates select="rules.block | timing.block | general.req.block | documents.block | format.block | filing.req.block | hearings.block | forms.block | checklist.block | misc.block" />-->
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="/Document/n-metadata/metadata.block/md.hierarchies">
						<xsl:with-param name="fromDelivery" select="'true'"/>
					</xsl:apply-templates>
          <xsl:if test="$DeliveryMode and $ShowKeyRuleChecklist">
            <xsl:apply-templates select="checklist.block" />
          </xsl:if>
					<xsl:if test="$DeliveryMode and $ShowKeyRuleTiming">
						<xsl:apply-templates select="timing.block" />
					</xsl:if>
					<xsl:if test="$DeliveryMode and $ShowKeyRuleDocuments">					
						<xsl:apply-templates select="documents.block" />
					</xsl:if>
				</xsl:otherwise>	
			</xsl:choose>
			<xsl:call-template name="EndOfDocument"/>
		</div>
	</xsl:template>

	<xsl:template name="BuildHeading">
		<xsl:variable name="title">
			<xsl:call-template name="wrapWithDiv">
				<xsl:with-param name="class" select="'&titleClass;'" />
				<xsl:with-param name="contents">
					<xsl:apply-templates select="front.matter/juris | document.type.block"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&documentHeadClass;'" />
			<xsl:with-param name="contents">
				<xsl:apply-templates select="//cmd.cites"/>
				<xsl:copy-of select="$title"/>
				<xsl:apply-templates select="front.matter/pub.name | front.matter/date.line"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>


	<!--Setup internal anchors : START-->	
	<xsl:template match="rules.block | timing.block | general.req.block | documents.block | format.block | filing.req.block | hearings.block | forms.block | checklist.block | misc.block">
		<div>
			<xsl:attribute name="class">
					<xsl:text>&khDivision;</xsl:text>
			</xsl:attribute>
			<xsl:if test="@ID">
				<a id="&internalLinkIdPrefix;{@ID}"></a>
			</xsl:if>
			<xsl:apply-templates />
		</div>
	</xsl:template>			
	<!--Setup internal anchors : END-->
	
	
	<!--TOC creation section : START -->
	<xsl:template match="/Document/n-metadata/metadata.block/md.hierarchies">
		<!-- This check is to prevent the TOC from rendering twice in delivered documents -->
		<xsl:param name="fromDelivery" />
		<xsl:if test="$fromDelivery = 'true' or not($DeliveryMode)">
			<div id="&coIntTocContainer;">
				<!--kh_toc-wrapper-->
				<xsl:attribute name="class">
					<xsl:text>&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;</xsl:text>
					<xsl:if test="not($DeliveryMode or $IsMobile)">
						<xsl:text> &hideState;</xsl:text>
					</xsl:if>
				</xsl:attribute>

				<xsl:call-template name="wrapWithTag">
					<xsl:with-param name="tag" select="string('div')" />
					<xsl:with-param name="class" select="'&coKhOverlayClass;'" />
					<xsl:with-param name ="contents" select="'&nbsp;'" />
				</xsl:call-template>

				<div id="&coKhTocContainer;" class="&coKhToc;">
					<div class="&coKhTocInner;">
						<div class="&coKhTocHeader;">
							<xsl:if test="not($DeliveryMode)">
								<a href="&hashTagText;" class="&coMenuToggle;">
									<span class="&khIcon; &coMenuToggleIconMenu;">&coActionTogglerSpanText;</span>
									<span>
										<xsl:if test="not($IsMobile)">
											<xsl:attribute name="class">&hideState;</xsl:attribute>
										</xsl:if>
										<xsl:text>&coDocumentOutlineHeaderText;</xsl:text>
									</span>
								</a>
								<xsl:if test="not($IsMobile)">
									<a class="&coTocIconCross;" href="#">&closeTOCText;</a>
									<p class="&coAccessibilityLabel;">&coTocToggleHelpText;</p>
								</xsl:if>
							</xsl:if>
						</div>
						<div class="&coKhTocContent;">
							<xsl:if test="$DeliveryMode = 'True'">
								<xsl:attribute name="style">display: block;</xsl:attribute>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="not($DeliveryMode)">
									<xsl:call-template name="TocBuilder">
										<xsl:with-param name="TocListTag" select="'ol'"/>
										<xsl:with-param name="TocListClass" select="'&coKhTocOlList;'"/>
										<xsl:with-param name="TocItemTag" select="'li'"/>
										<xsl:with-param name="TocItemClass" select="''"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="TocBuilder">
										<xsl:with-param name="TocListTag" select="'div'"/>
										<xsl:with-param name="TocListClass" select="'&coKhTocOlList;'"/>
										<xsl:with-param name="TocItemTag" select="'div'"/>
										<xsl:with-param name="TocItemClass" select="'&coKhTocGroup;'"/>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</div>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="TocBuilder">
		<xsl:param name="TocListTag" />
		<xsl:param name="TocListClass" />
		<xsl:param name="TocItemTag" />
		<xsl:param name="TocItemClass" />
		<!-- only output the list container if there are one or more nodes -->
		<xsl:if test="md.outline.block/md.entry">
			<xsl:element name="{$TocListTag}">
				<xsl:attribute name="class">
					<xsl:value-of select="$TocListClass"/>
				</xsl:attribute>
				<xsl:for-each select="md.outline.block/md.entry">
					<xsl:variable name="tocItem">
						<xsl:call-template name="lower-case">
							<xsl:with-param name="string" select="md.head" />
						</xsl:call-template>		
					</xsl:variable>
					
					<xsl:element name="{$TocItemTag}">
						<xsl:choose>							
							<xsl:when test="$DeliveryMode and ($ShowKeyRuleChecklist) and contains($tocItem, 'checklist')">
								<xsl:call-template name="BuildTocAnchor" />
							</xsl:when>
							<xsl:when test="$DeliveryMode and ($ShowKeyRuleTiming) and contains($tocItem, 'timing')">
								<xsl:call-template name="BuildTocAnchor" />
							</xsl:when>
							<xsl:when test="$DeliveryMode and ($ShowKeyRuleDocuments) and contains($tocItem, 'documents')">
								<xsl:call-template name="BuildTocAnchor" />
							</xsl:when>		
							<xsl:when test="$DeliveryMode and not($ShowKeyRuleChecklist or $ShowKeyRuleTiming or $ShowKeyRuleDocuments)">
								<xsl:call-template name="BuildTocAnchor" />
							</xsl:when>
							<xsl:when test="not($DeliveryMode)">
								<xsl:call-template name="BuildTocAnchor" />
							</xsl:when>					
						</xsl:choose>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="BuildTocAnchor">
		<a href="#co_anchor_{@refid}">
			<xsl:if test="$DeliveryMode">
				<xsl:text>&nbsp;</xsl:text>
			</xsl:if>
			<xsl:value-of select="md.head"/>
		</a>
	</xsl:template>
	
	<!--TOC creation section : END -->

	<xsl:template match="cmd.cites" priority="2">
		<xsl:variable name="displayableCites" select="cmd.first.line.cite | cmd.second.line.cite | cmd.third.line.cite" />
		<xsl:if test="string-length($displayableCites) &gt; 0">
			<div class="&citesClass;">
				<xsl:for-each select="$displayableCites">
					<xsl:call-template name="wrapContentBlockWithGenericClass"/>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="juris" priority="1">
		<xsl:variable name="xmlBasedClassName">
			<xsl:call-template name="escape-to-class" />
		</xsl:variable>
		<div class="&simpleContentBlockClass; {$xmlBasedClassName}">
			<xsl:text>&keyRules;</xsl:text>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="general.court | local.court">
		<xsl:if test="preceding-sibling::general.court | preceding-sibling::local.court">
			<xsl:text>&comma;</xsl:text>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="forms.doctype.code"/>
	
	<xsl:template match="label.name[preceding-sibling::label.designator and parent::para and count(../paratext) = 0]">
		<div class="&paratextMainClass;">
			<xsl:apply-templates select="." mode="para-label.designator" />
		</div>
	</xsl:template>

	<xsl:template match="label.name[../preceding-sibling::label.designator and parent::para and ../parent::list.item]">
		<xsl:apply-templates select="." mode="para-label.designator" />
	</xsl:template>
	
	<xsl:template match="label.name" mode="para-label.designator" priority="1">
		<xsl:apply-templates select="preceding-sibling::label.designator" mode="para-label.designator" />
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class" select="'&keyRulesLabelName;'"/>
			<xsl:with-param name="contents">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="date.line" priority="1">
		<xsl:call-template name="dateLine"/>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>
	
	<xsl:template match="list">
		<xsl:if test="list.item">
			<ul class="&listClass;">
				<xsl:apply-templates select="node()[not(self::list)]" />
			</ul>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="list/list.item | entity.link/list.item">		
			<li>
				<div class="&paraMainClass;">					
					<xsl:apply-templates select="./*[not(name() = 'list')]"/>
				</div>
				<xsl:apply-templates select="./*[name() = 'list']"/>			
			</li>
		
	</xsl:template>

	<xsl:template match="md.cmsids | md.cites" priority="2" />
	
	
	<xsl:template match="para | form.para | stat.para | codes.para | p" name="para">							
		<xsl:param name="divId">
		<xsl:if test="string-length(@id|@ID) &gt; 0">
			<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
		</xsl:if>
		</xsl:param>
		<xsl:param name="className" select="'&paraMainClass;'" />
		<xsl:if test="count(node()[not(self::label.name or self::label.designator)]) &gt; 0 and (.//text() or .//leader or .//image.block)">			
			<div>
					<xsl:attribute name="class">
						<xsl:value-of select="$className"/>
						<xsl:choose>
							<xsl:when test="@*">
								<xsl:call-template name="addParaClasses"/>
							</xsl:when>
						</xsl:choose>
					</xsl:attribute>
					<xsl:if test="string-length($divId) &gt; 0">
						<xsl:attribute name="id">
							<xsl:value-of select="$divId"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates select="./*[not(name() = 'para' or name() = 'list')]"/>
				</div>
			<xsl:apply-templates select="./*[name() = 'para' or name() = 'list']"/>			
		</xsl:if>			
	</xsl:template>	
	
	<!-- Document Footer : START -->
	<xsl:template name="EndOfDocument">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>
		<xsl:if test="not($DeliveryMode)">			
			<div id="&endOfDocumentId;">

				<xsl:variable name="DocumentText">
					<xsl:call-template name="wrapWithSpan">
						<xsl:with-param name="contents" select="'&endOfDocumentText;'" />
					</xsl:call-template>
				</xsl:variable>

				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&coEndDocHeader;'" />
					<xsl:with-param name="contents" select="$DocumentText" />
				</xsl:call-template>																	

				<xsl:variable name="CopyrightStr">
					<xsl:value-of select="concat('&copy;', '&nbsp;', $currentYear, '&nbsp;', $endOfDocumentCopyrightText)" />
				</xsl:variable>
				<xsl:call-template name="wrapWithTag">
					<xsl:with-param name="tag" select="string('div')" />
					<xsl:with-param name="class" select="'&endOfDocumentCopyrightClass;'"/>
					<xsl:with-param name="contents" select="$CopyrightStr" />
				</xsl:call-template>
			</div>

			<div class="&khEndOfDocumentWidgets; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></div>
			<xsl:if test="not($IsMobile)">
					<div id="&khBackToTop;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
						<xsl:choose>
							<xsl:when test="$IsIpad = 'true'">
								<a href="&hashTagText;&documentId;">
									<span class="&khIcon; &khIconUpPointer;"></span>
									<xsl:text> &backToTop;</xsl:text>
								</a>
							</xsl:when>
							<xsl:otherwise>
								<a href="&hashTagText;&coPageContainer;">
									<span class="&khIcon; &khIconUpPointer;"></span>
									<xsl:text> &backToTop;</xsl:text>
								</a>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</xsl:if>
		</xsl:if>
	</xsl:template>
	<!-- Document Footer : END -->
	
	<!--UTILITIES : START-->
	
	<xsl:template name="wrapWithTag">
		<xsl:param name="tag" select="div" />
		<xsl:param name="class"/>
		<xsl:param name="id"/>
		<xsl:param name="href"/>
		<xsl:param name="contents"/>
		<xsl:param name="style"/>
		<xsl:element name="{$tag}">
			<xsl:if test="$class">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($href) &gt; 0">
				<xsl:attribute name="href">
					<xsl:value-of select="$href"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($style) &gt; 0">
				<xsl:attribute name="style">
					<xsl:value-of select="$style"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>			
	</xsl:template>
	
	<!--UTILITIES : END-->

</xsl:stylesheet>
