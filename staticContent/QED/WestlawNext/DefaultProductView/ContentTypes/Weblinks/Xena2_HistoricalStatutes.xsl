<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="Leader.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="/Document/n-docbody/doc/cr[1]//d1[/Document/document-data/collection = 'w_codes_statxfl']" mode="cite"/>
			<xsl:apply-templates select="n-docbody"/>
			<xsl:apply-templates select="n-docbody/doc/mx"/>
			<xsl:call-template name="displayCopyright"/>
			<xsl:apply-templates select="n-docbody/doc/ex/d1"  mode="footerCitation"/>
			<xsl:apply-templates select="n-docbody/doc/rc.gen/d1[/Document/document-data/collection = 'w_codes_statxfl']" mode="footerCitation"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="//s518/d1" priority="2">
		<xsl:if test="internal.reference">
			<xsl:call-template name="wrapContentBlockWithCobaltClass">
				<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',internal.reference/@ID)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--	<xsl:template match="//s270//d6[child::internal.reference and //text() = 'Currentness' and contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]">
		<div class="&currentnessClass;">
			<xsl:apply-templates select="internal.reference"/>
		</div>
	</xsl:template> -->

	<xsl:template match="doc">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="gt"/>
			<xsl:apply-templates select="x_0.sb1[contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]"/>
			<xsl:apply-templates select="//md.cites"/>
			<xsl:choose>
				<xsl:when test="contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))">
					<xsl:apply-templates select="so | srnl | dl | ti | ti2 | dj | dj1 | cpr1 | */s277 | */s278 | */s279 | *//s040 | *//s050" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | hg10 | hg11 | so | srnl | dl | ti | ti2 | dj | dj1 | cpr1 | */s270 | */s271 | */s272 | */s273 | */s274 | */s275 | */s276 | */s277 | */s278 | */s279 | */s040[not(child::d9)]"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:apply-templates select="*[not(self::hg0 or self::hg1 or self::hg2 or self::hg3 or self::hg4 or self::hg5 or self::hg6 or self::hg7 or self::hg8 or self::hg9 or self::hg10 or self::hg11 or self::so or self::srnl or self::dl or self::ti or self::ti2 or self::dj or self::dj2 or self::mx or self::cpr1 or self::gt[child::d2] or self:: g1[child::d2] or self::g5[child::d2] or child::s270 or child::s271 or child::s272 or child::s273 or child::s274 or child::s275 or child::s276 or child::s277 or child::s278 or child::s279 or child::s040 or child::s050)]" />
	</xsl:template>

	<xsl:variable name="tocElements" select="'hg0|hg1|hg2|hg3|hg4|hg5|hg6|hg7'" />
	<xsl:variable name="dtagNames" select="'d2|d3|d4|d5|d6'"/>

	<!--<xsl:template match=" hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 " priority="2">
		-->
	<!--<xsl:template match=" hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | gt | g1 | g5" priority="2">-->
	<!--
		<xsl:variable name="numberOfPreceding" select="count(preceding-sibling::node()[contains($tocElements, local-name())])" />
		<xsl:choose>
			<xsl:when test=" descendant::node()[contains($dtagNames,local-name) and not(@lm)]">
				<xsl:call-template name="d6">
					<xsl:with-param name="lm" select="$numberOfPreceding" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>-->

	<xsl:template name="createTocLink">
		<xsl:param name="node" />
		<xsl:param name="anchorLevel" />

		<xsl:variable name="anchorLabel">
			<xsl:for-each select="$node/*">
				<xsl:apply-templates />
			</xsl:for-each>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="/Document/toc-data">
				<!--Special processing used for delivery to map toc urls to toc nodes-->

				<xsl:variable name="matchingTocAnchor" select="/Document/toc-data//ul[count(ancestor::ul) = $anchorLevel]/li/a" />

				<xsl:choose>
					<xsl:when test="$matchingTocAnchor">
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="$matchingTocAnchor/@href"/>
							</xsl:attribute>
							<xsl:value-of select="$anchorLabel"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$anchorLabel"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$anchorLabel"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="g1[child::d2] | g5[child::d2] | g7[child::d2]" priority="2" />

	<xsl:template match="d2[parent::gt and /Document/document-data/collection = 'w_codes_strules']" priority="2">
		<div class="&genericBoxClass;">
			<div class="&genericBoxHeaderClass;">
				<span></span>
			</div>
			<div class="&genericBoxContentClass;">
				<div class="&genericBoxContentRightClass;">
					<a class="&widgetCollapseIconClass;" href="#"></a>
					<div class="&simpleContentBlockClass; &prelimBlockClass; &headtextClass;">

						<xsl:call-template name="createTocLink">
							<xsl:with-param name="node" select="." />
							<xsl:with-param name="anchorLevel" select="0" />
						</xsl:call-template>

						<xsl:if test="/Document/n-docbody/doc/g1[child::d2]">

							<xsl:choose>
								<xsl:when test="/Document/n-docbody/doc/g5[child::d2] and (/Document/n-docbody/doc/g7[child::d2] or (not(/Document/n-docbody/doc/g7[child::d2]) and /Document/n-docbody/doc/g15))">
									<div id="co_prelimContainer">
										<div class="&simpleContentBlockClass; &prelimHeadClass; &headtextClass;">

											<xsl:call-template name="createTocLink">
												<xsl:with-param name="node" select="/Document/n-docbody/doc/g1/d2" />
												<xsl:with-param name="anchorLevel" select="1" />
											</xsl:call-template>

											<xsl:choose>
												<xsl:when test="/Document/n-docbody/doc/g7[child::d2] and /Document/n-docbody/doc/g15">
													<div id="co_prelimContainer">
														<div class="&simpleContentBlockClass; &prelimHeadClass; &headtextClass;">
															<xsl:call-template name="createTocLink">
																<xsl:with-param name="node" select="/Document/n-docbody/doc/g5/d2" />
																<xsl:with-param name="anchorLevel" select="2" />
															</xsl:call-template>

															<xsl:call-template name="prelimGoldenLeaf">
																<xsl:with-param name="text">
																	<xsl:call-template name="createTocLink">
																		<xsl:with-param name="node" select="/Document/n-docbody/doc/g7/d2" />
																		<xsl:with-param name="anchorLevel" select="3" />
																	</xsl:call-template>
																</xsl:with-param>
															</xsl:call-template>
														</div>
													</div>
												</xsl:when>
												<xsl:otherwise>
													<xsl:call-template name="prelimGoldenLeaf">
														<xsl:with-param name="text">
															<xsl:call-template name="createTocLink">
																<xsl:with-param name="node" select="/Document/n-docbody/doc/g5/d2" />
																<xsl:with-param name="anchorLevel" select="2" />
															</xsl:call-template>
														</xsl:with-param>
													</xsl:call-template>
												</xsl:otherwise>
											</xsl:choose>
										</div>
									</div>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="prelimGoldenLeaf">
										<xsl:with-param name="text">
											<xsl:call-template name="createTocLink">
												<xsl:with-param name="node" select="/Document/n-docbody/doc/g1/d2" />
												<xsl:with-param name="anchorLevel" select="1" />
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>

						</xsl:if>

					</div>
				</div>
			</div>
			<div class="&genericBoxFooterClass;">
				<span></span>
			</div>
		</div>
	</xsl:template>


	<!-- Display CAPTION field for State Provided collections. Elements for CAPTION field are s040 and s050 -->
	<xsl:template match="s040/d7/cite.query[contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]" priority="5" >
		<div class="&titleClass;">
			<xsl:for-each select="//s040/d7/cite.query/*">
				<xsl:apply-templates />
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="s050/d7/cite.query[contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]" priority="5" >
		<div class="&titleClass;">
			<xsl:for-each select="//s050/d7/cite.query/*">
				<xsl:apply-templates />
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template name ="tocHgStyle">
		<xsl:param name="hgNode" />
		<xsl:variable name="nodes" select="$hgNode/following-sibling::node()[starts-with(name(), 'x_') and not(child::s040) and not(child::s050)]" />

		<xsl:choose>
			<xsl:when test="count($nodes) > 0">
				<div id="co_prelimContainer">
					<div class="&simpleContentBlockClass; &prelimHeadClass; &headtextClass;">
						<xsl:for-each select="$hgNode/*">
							<xsl:apply-templates />
						</xsl:for-each>
						<!--		<xsl:copy-of select="$hgNode/*/*" /> -->
						<xsl:call-template name="tocHgStyle">
							<xsl:with-param name="hgNode" select="$nodes[1]" />
						</xsl:call-template>
					</div>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="prelimGoldenLeaf">
					<xsl:with-param name="text">
						<xsl:for-each select="$hgNode/*">
							<xsl:apply-templates />
						</xsl:for-each>
						<!--		<xsl:copy-of select="$hgNode/*/*" /> -->
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="x_0.sb1[contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]" priority="2">
		<div class="&genericBoxClass;">
			<div class="&genericBoxHeaderClass;">
				<span></span>
			</div>
			<div class="&genericBoxContentClass;">
				<div class="&genericBoxContentRightClass;">
					<a class="&widgetCollapseIconClass;" href="#"></a>
					<div class="&simpleContentBlockClass; &prelimBlockClass; &headtextClass;">
						<xsl:for-each select="*">
							<xsl:apply-templates />
						</xsl:for-each>
						<xsl:choose>
							<xsl:when test="/Document/n-docbody/doc/x_1.sb2">
								<xsl:call-template name="tocHgStyle">
									<xsl:with-param name="hgNode" select="/Document/n-docbody/doc/x_1.sb2" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="tocHgStyle" >
									<xsl:with-param name="hgNode" select="/Document/n-docbody/doc/x_1.sb1" />
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>

					</div>
				</div>
			</div>
			<div class="&genericBoxFooterClass;">
				<span></span>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="prelimGoldenLeaf">
		<xsl:param name="text" />
		<div class="&simpleContentBlockClass; &prelimHeadClass; &headtextClass;" id="&prelimGoldenLeafClass;">
			<xsl:copy-of select="$text" />
		</div>
	</xsl:template>

	<xsl:template match="justified.line[@quadding = 'l']" priority="2">
		<xsl:call-template name="justifiedLine" />
		<br/>
	</xsl:template>

	<xsl:template match="endline[@quadding = 'l']" priority="2">
		<xsl:text>&#160;</xsl:text>
		<br />
	</xsl:template>

	<xsl:template match="hstvr//d1" priority="2">
		<div>
			<xsl:text>&#160;</xsl:text>
			<xsl:call-template name="d6"/>
		</div>
	</xsl:template>

	<xsl:template match="tlw | tl1 | tl1w | t11w | tl2w | t32w | mp21 | mp21p" priority="1">
		<div>
			<xsl:apply-templates />
		</div>
		<br/>
	</xsl:template>

	<xsl:template match="md.cites" priority="3">
		<div class="&citesClass;">
			<xsl:choose>
				<xsl:when test="md.second.line.cite">
					<xsl:apply-templates select="md.second.line.cite"/>
				</xsl:when>
				<xsl:when test="md.third.line.cite">
					<xsl:apply-templates select="md.third.line.cite"/>
				</xsl:when>
				<xsl:when test="md.first.line.cite">
					<xsl:apply-templates select="md.first.line.cite"/>
				</xsl:when>
				<xsl:when test="/Document/n-docbody/doc/cr[1]//d1/text()">
					<br/>
					<xsl:apply-templates select="/Document/n-docbody/doc/cr[1]//d1/text()"/>
				</xsl:when>
			</xsl:choose>
		</div>
		<!-- Display CAPTION field for State Provided collections when caption is present else show the Title. Done to avoid duplicate display and fix bugs 442062, 447089 -->
		<xsl:choose>
			<xsl:when test="s040/d7/cite.query[contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="s050/d7/cite.query[contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="(//md.title[contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]) and (not(//s040/d7/cite.query)) and (not(//s050/d7/cite.query))">
				<div class="&titleClass;">
					<xsl:apply-templates select="//md.title"/>
				</div>
			</xsl:when>
			<xsl:when test="/Document/n-docbody/doc/g15/cite.query">
				<div class="&titleClass;">
					<xsl:apply-templates select="/Document/n-docbody/doc/g15/cite.query" mode="displayCaption"/>
				</div>
			</xsl:when>
			<xsl:when test="/Document/n-docbody/doc/g15w/cite.query">
				<div class="&titleClass;">
					<xsl:apply-templates select="/Document/n-docbody/doc/g15w/cite.query" mode="displayCaption" />
				</div>
			</xsl:when>
		</xsl:choose>
		<!--	<xsl:if test="//s270//d6/internal.reference[contains('|w_codes_stamt|w_codes_stane|w_codes_staks|' , concat('|', /Document/document-data/collection, '|'))]">
			<div class="&currentnessClass;">
				<xsl:apply-templates select="//s270//d6/internal.reference" />
			</div>
		</xsl:if> -->
	</xsl:template>

	<xsl:template match="g15/cite.query | g15w/cite.query" mode="displayCaption">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="/Document/n-docbody/doc/cr[1]//d1[/Document/document-data/collection = 'w_codes_statxfl']" mode ="cite">
		<xsl:if test="/Document/n-docbody/doc/cr[1]//d1[not(/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.second.line.cite/text()) and  not(/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.third.line.cite/text())
                         and not(/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.first.line.cite/text())]">
			<br/>
			<div class="&citesClass;">
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="md.cites[/Document/document-data/collection = 'w_codes_statxfl']" priority="6"/>


	<!-- Overrode these to avoid duplicate DHE -->
	<xsl:template match="ti | ti2 | til | hg0 | hg1 | hg2 | hg3 | hg4 | hg5 | hg6 | hg7 | hg8 | hg9 | hg10 | hg11 | hg12 | hg13 | hg14 | hg15 | hg16 | hg17 | hg18 | hg19 | snl | srnl | hc2" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<!--Supress the copyright message-->
	<!--<xsl:template match="cop | copmx | copsd | /Document/n-docbody/doc/message.block/include.message | /Document/n-docbody/doc/g15/cite.query"/>-->
	<xsl:template match="cop | copmx | copsd | /Document/n-docbody/doc/g15/cite.query | /Document/n-docbody/doc/g15w/cite.query"/>

	<xsl:template name="displayCopyright">
		<xsl:variable name="copyright_node" select="//cop | //copmx | //copsd"/>
		<xsl:call-template name="copyrightBlock">
			<xsl:with-param name="copyrightNode" select="$copyright_node"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="sf10 | sf00 | xhsu.shfn | xhsu.gnp | xhsu.gnp21 | xhsu.gnp32">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="nd.gnp | xcr.gnp">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!--Supress ADDTERM -->
	<xsl:template match="adt.gen"/>

	<!-- Suppress other irrelevant content -->
	<xsl:template match="prtid | prtrv | tocid | tlrg | tlep | tlan | tlrp | tlftc | rctax | plcrpe | ce1 | ssrh21 | ssrh23 | ssrh1 | ssrh2 | g1 | g5"/>

	<!-- bug  fix for 298223-->
	<xsl:template match="snl" priority="1">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="nw" priority="1">
		<div>
			<br/>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="sl//d9 | sg2w[child::leader] | mp32/d6[child::leader] | smp/d9[child::leader] | smpp/d9[child::leader] | pb2/d8[child::leader]" priority="1">
		<div>
			<xsl:call-template name="leaderContent">
				<xsl:with-param name="parent" select="."/>
			</xsl:call-template>
		</div>
		<br/>
	</xsl:template>

	<xsl:template match="rc.gen/d1" priority="1" />

	<!--<xsl:template match="rc.gen" priority="1">
		<xsl:value-of select="d1"/>
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>-->

	<!-- We need to suppress any CM cite.query elements in Refs & Annos documents because they will not resolve -->
	<xsl:template match="cite.query[@w-ref-type = 'CM']" priority="1" >
		<xsl:value-of select="."></xsl:value-of>
	</xsl:template>

<xsl:template match="internal.reference" priority="2">
		<xsl:param name="id" select="translate(@ID, ';', '')"/>
		<xsl:param name="refid" select="@refid" />
		<xsl:param name="additionalClass"/>
		<xsl:param name="contents" />
		<xsl:choose>
			<xsl:when test="key('allElementIds', $refid)">
				<span>
					<xsl:if test="string-length($id) &gt; 0">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&internalLinkIdPrefix;', translate($id, ';', ''))"/>
						</xsl:attribute>
					</xsl:if>
					<a href="{concat('#&internalLinkIdPrefix;', translate($refid, ';', ''))}">
						<xsl:attribute name="class">
							<xsl:text>&internalLinkClass;</xsl:text>
							<xsl:if test="string-length($additionalClass) &gt; 0">
								<xsl:text><![CDATA[ ]]></xsl:text>
								<xsl:value-of select="$additionalClass"/>
							</xsl:if>
						</xsl:attribute>
						<xsl:choose>
							<xsl:when test="string-length($contents) &gt; 0">
								<xsl:copy-of select="$contents"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates />
							</xsl:otherwise>
						</xsl:choose>
					</a>
				</span>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string-length($contents) &gt; 0">
						<xsl:copy-of select="$contents"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="tgroup" priority="1">
		<xsl:call-template name ="TGroupTemplate">
			<xsl:with-param name ="checkNoColWidthExists" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Fix for the Bug 346157-->
	<xsl:template match="n-docbody/doc/ex/d1" mode="footerCitation">
		<div>
			<br/>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- Fix for the Bug 346157-->
	<xsl:template match="n-docbody/doc/rc.gen/d1[/Document/document-data/collection = 'w_codes_statxfl']" mode="footerCitation">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="row" priority="1">
		<xsl:param name="columnInfo" />
		<xsl:param name="header"/>
		<xsl:param name="proportionalTotal"/>
		<xsl:variable  select ="ancestor::tbl/@row-shade" name="alternate"/>
		<xsl:choose>
			<!-- To avoid chunking until after a header row -->
			<xsl:when test="parent::thead and count(preceding-sibling::row) = 0">
				<xsl:call-template name="startUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_header')" />
				</xsl:call-template>
			</xsl:when>
			<!-- To avoid chunking after the first row if there isn't a header row -->
			<xsl:when test="not(ancestor::tgroup[1]/thead) and parent::tbody and count(preceding-sibling::row) = 0">
				<xsl:call-template name="startUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_header')" />
				</xsl:call-template>
			</xsl:when>
			<!-- To avoid chunking between the last table row and the end of the table -->
			<xsl:when test="parent::tbody and count(following-sibling::row) = 1">
				<xsl:call-template name="startUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_trailer')" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>

		<tr>
			<xsl:if test ="$alternate = 'alt'">
				<xsl:attribute name="class">
					<xsl:value-of select="concat('co_stripeRow',(position() mod 2)+1)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$columnInfo">
					<xsl:variable name="row" select="." />
					<xsl:variable name="entries" select="entry" />
					<xsl:variable name="colspans" select="entry[@namest]" />
					<xsl:for-each select="$columnInfo">
						<xsl:variable name="colname" select="@colname" />
						<xsl:variable name="colnumber" select="@colname" />
						<xsl:variable name="colposition" select="position()" />
						<xsl:variable name="colalign" select="@align" />
						<xsl:variable name="colwidth">
							<xsl:choose>
								<xsl:when test="$proportionalTotal &gt; 0">
									<xsl:value-of select="concat(round(substring-before(@colwidth, '*') div $proportionalTotal * 100), '%')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="@colwidth"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:variable name="render">
							<!-- Will stay empty if the column can render -->
							<xsl:choose>
								<xsl:when test="$colspans">
									<xsl:for-each select="$colspans">
										<xsl:variable name="startCol">
											<xsl:call-template name="GetColNumber">
												<xsl:with-param name="columnInfo" select="$columnInfo" />
												<xsl:with-param name="text" select="@namest" />
											</xsl:call-template>
										</xsl:variable>
										<xsl:variable name="endCol">
											<xsl:call-template name="GetColNumber">
												<xsl:with-param name="columnInfo" select="$columnInfo" />
												<xsl:with-param name="text" select="@nameend" />
											</xsl:call-template>
										</xsl:variable>
										<xsl:choose>
											<xsl:when test="number($colposition) &lt; number($startCol) or number($colposition) = number($startCol)" />
											<xsl:when test="number($colposition) &gt; number($endCol)" />
											<xsl:otherwise>
												<xsl:value-of select="false()"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:for-each>
								</xsl:when>
							</xsl:choose>
						</xsl:variable>
						<xsl:if test="string-length($render) = 0">
							<xsl:choose>
								<xsl:when test="$colname and $entries[(@colname=$colname and not(@namest)) or (@colname and @namest=$colname)]">
									<xsl:variable name="thisEntry" select="$entries[(@colname=$colname and not(@namest)) or (@colname and @namest=$colname)]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$colnumber and $entries[@colnum=$colnumber]">
									<xsl:variable name="thisEntry" select="$entries[@colnum=$colnumber]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:when test="$colspans and count($entries) &lt; count($columnInfo) and not($entries[@colname] or $entries[@colnum])">
									<xsl:call-template name="findEntryAndRender">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="entries" select="$entries"/>
										<xsl:with-param name="index" select="1"/>
										<xsl:with-param name="colposition" select="$colposition"/>
										<xsl:with-param name="total" select="1"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="$entries[position()=$colposition] and not($entries[position()=$colposition]/@colname) and not($entries[position()=$colposition]/@colnum)">
									<xsl:variable name="thisEntry" select="$entries[position()=$colposition]"/>
									<xsl:apply-templates select="$thisEntry">
										<xsl:with-param name="columnInfo" select="$columnInfo"/>
										<xsl:with-param name="colalign" select="$colalign" />
										<xsl:with-param name="colposition" select="$colposition" />
										<xsl:with-param name="colwidth" select="$colwidth" />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:otherwise>
									<!-- this is an empty cell -->
									<xsl:choose>
										<xsl:when test="$row/parent::thead">
											<th>
												<xsl:call-template name="RenderTableCellXena">
													<xsl:with-param name="columnInfo" select="$columnInfo"/>
													<xsl:with-param name="colalign" select="$colalign" />
													<xsl:with-param name="colposition" select="$colposition" />
													<xsl:with-param name="colwidth" select="$colwidth" />
													<xsl:with-param name="row" select="$row" />
												</xsl:call-template>
												<xsl:text><![CDATA[ ]]></xsl:text>
											</th>
										</xsl:when>
										<xsl:otherwise>
											<td>
												<xsl:call-template name="RenderTableCellXena">
													<xsl:with-param name="columnInfo" select="$columnInfo"/>
													<xsl:with-param name="colalign" select="$colalign" />
													<xsl:with-param name="colposition" select="$colposition" />
													<xsl:with-param name="colwidth" select="$colwidth" />
													<xsl:with-param name="row" select="$row" />
												</xsl:call-template>
												<xsl:text><![CDATA[ ]]></xsl:text>
											</td>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates>
						<xsl:with-param name="columnInfo" select="$columnInfo"/>
						<xsl:with-param name="header" select="$header" />
						<xsl:with-param name="proportionalTotal" select="$proportionalTotal"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</tr>

		<xsl:choose>
			<!-- To avoid chunking immediately after a header row (when combined with ancestoral <xsl:if test="parent::tbody">) -->
			<xsl:when test="parent::tbody and count(preceding-sibling::row) = 0">
				<xsl:call-template name="endUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_header')" />
				</xsl:call-template>
			</xsl:when>
			<!-- To avoid chunking between the last table row and the end of the table (see template match for "row" to see the corresponding startUnchunkableBlock) -->
			<xsl:when test="parent::tbody and count(following-sibling::row) = 0">
				<xsl:call-template name="endUnchunkableBlock">
					<xsl:with-param name="text" select="concat(generate-id(ancestor::tgroup[1]), '_trailer')" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="tbody/row/entry" priority="1">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<td>
			<xsl:call-template name="RenderTableCellXena">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
			</xsl:call-template>
		</td>
	</xsl:template>

	<xsl:template match="thead/row/entry" priority="1">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<th>
			<xsl:call-template name="RenderTableCellXena">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
			</xsl:call-template>
		</th>
	</xsl:template>

	<xsl:template name="RenderTableCellXena">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<xsl:param name="row" />
		<xsl:param name="contents">
			<xsl:choose>
				<xsl:when test="leader">
					<xsl:call-template name="leaderContent">
						<xsl:with-param name="parent" select="." />
					</xsl:call-template>
				</xsl:when>

				<!-- This is needed to wrap not leader columns with same div as leader columns if one exist in the row. 
					 It will align text in all columns vertically.-->
				<xsl:when test="..//leader and not(child::node()//leader) and string(.)">
					<xsl:call-template name="leaderContent">
						<xsl:with-param name="parent" select="." />
					</xsl:call-template>
				</xsl:when>

				<xsl:otherwise>
					<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:param>
		<xsl:attribute name="class">
			<xsl:choose>
				<!-- if the table is a leader table, left align the headers, because for the leader tables, the columns 
				     are all left algin by definition -->
				<xsl:when test="ancestor::thead and ancestor::table//leader">
					<xsl:text>&alignHorizontalLeftClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@align = 'right'">
					<xsl:text>&alignHorizontalRightClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@align = 'left'">
					<xsl:text>&alignHorizontalLeftClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@align = 'center'">
					<xsl:text>&alignHorizontalCenterClass;</xsl:text>
				</xsl:when>
				<xsl:when test="$colalign = 'right'">
					<xsl:text>&alignHorizontalRightClass;</xsl:text>
				</xsl:when>
				<xsl:when test="$colalign = 'left'">
					<xsl:text>&alignHorizontalLeftClass;</xsl:text>
				</xsl:when>
				<xsl:when test="$colalign = 'center'">
					<xsl:text>&alignHorizontalCenterClass;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&alignHorizontalLeftClass;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:choose>
				<xsl:when test="..//leader and not(child::node()//leader) and string(.)">
					<xsl:text>&alignVerticalBottomClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@valign = 'bottom'">
					<xsl:text>&alignVerticalBottomClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@valign = 'top'">
					<xsl:text>&alignVerticalTopClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@valign">
					<xsl:value-of select="concat('vAlignError_', @valign)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&alignVerticalTopClass;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:if test="@rowsep = '1' or parent::row/@rowsep = '1' or ($row and $row/@rowsep = '1') or ancestor::table[1]/@rowsep = '1'">
				<xsl:text>&borderBottomClass;</xsl:text>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
			<xsl:if test="@colsep = '1'">
				<xsl:text>&borderRightClass;</xsl:text>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</xsl:if>
		</xsl:attribute>
		<xsl:variable name="colspan">
			<xsl:if test="@namest">
				<xsl:variable name="startCol">
					<xsl:call-template name="GetColNumber">
						<xsl:with-param name="columnInfo" select="$columnInfo" />
						<xsl:with-param name="text" select="@namest" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:if test="number($startCol) = $colposition">
					<xsl:variable name="endCol">
						<xsl:call-template name="GetColNumber">
							<xsl:with-param name="columnInfo" select="$columnInfo" />
							<xsl:with-param name="text" select="@nameend" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:for-each select="$columnInfo">
						<xsl:if test="number($endCol) = position()">
							<xsl:value-of select="position() - $colposition + 1"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="string-length($colspan) &gt; 0">
			<xsl:attribute name="colspan">
				<xsl:value-of select="$colspan"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="not(string-length($colspan) &gt; 0) and $colwidth and string-length($colwidth) &gt; 0">
			<xsl:attribute name="style">
				<xsl:value-of select="concat('width:', $colwidth)"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:variable  select ="ancestor::tbl/@row-shade" name="alternateCheck"/>
		<xsl:choose>
			<xsl:when test ="$alternateCheck = 'alt'">
				<xsl:choose>
					<xsl:when test="normalize-space()">
						<xsl:copy-of select="$contents"/>
					</xsl:when>
					<xsl:otherwise>
						<br />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$contents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
