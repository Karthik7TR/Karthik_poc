<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>
	<xsl:include href="ProvisionsTable.xsl" />
	<xsl:include href="FootnotesCommon.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="primarycite" select="Document/n-docbody/document/metadata.block/md.identifiers/md.cites/md.primarycite/text()" />

	<xsl:variable name ="appType" select ="descendant::fulltext/@application"/>

	<xsl:variable name="statusCode" select="//n-metadata/metadata.block/md.history/md.keycite/md.flag.color.code" />

	<xsl:variable name="analysisDocGuid" select="Document/n-metadata/metadata.block/md.references/md.locatordoc/@href"/>

	<!-- Document -->
	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<!-- render the CONTENT -->
			<div class="&paraMainClass;">&#160;</div>
			<xsl:apply-templates select="*[not(self::footnote-text or self::updatenote)]"/>

			<xsl:call-template name="RenderFootnotes">
				<xsl:with-param name="footNoteTitle" select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')" />
			</xsl:call-template>
			
			<!-- Render the provisions link -->
			<xsl:apply-templates select="descendant::fulltext_metadata/provisions" />

			<!-- Render copyright -->
			<xsl:apply-templates select="descendant::copyright-message" mode="copyright" />

			<div class="&alignHorizontalLeftClass;">
				<div class="&paratextMainClass;">&#160;</div>
				<xsl:value-of select="$primarycite" />
			</div>
			
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>
	
	<!-- Document Citation at top of document -->
	<xsl:template match="n-docbody/document/metadata.block">
		<div class="&centerClass; &paraMainClass;">
			<xsl:apply-templates select="md.identifiers/md.cites/md.parallelcite" />
		</div>
	</xsl:template>

	<xsl:template match="fulltext_metadata">
		<div class="&paraMainClass;">
			<xsl:apply-templates select="headings" />
			<xsl:call-template name="AnalysisLink" />
			<div class="&paraMainClass;">
				<xsl:apply-templates select="commencement" />
			</div>
			<xsl:if test="image.block/image.link">
				<div class="&centerClass; &paraMainClass;">
					<xsl:apply-templates select="image.block/image.link"/>
				</div>
			</xsl:if>

			<!-- UK Parallel Text -->
			<xsl:if test ="ancestor::n-docbody/descendant::fulltext/@application">
				<div class="&centerClass; &paraMainClass;">
					<xsl:if test ="count(ancestor::n-docbody/descendant::fulltext) > 1">
						<xsl:call-template name ="parallelHeading"/>
					</xsl:if>
				</div>
			</xsl:if>

		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<!-- Centered Headings -->
	<xsl:template match="headings">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="heading">
		<xsl:apply-templates />
		<xsl:if test="following-sibling::heading">
			<br/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="AnalysisLink">
			<div class="&centerClass;">
				<xsl:text>&ukSupersededLegis;</xsl:text>
				<br></br>
				<!--Analysis Link within the document, instead of an RI tab-->
				<xsl:if test="string-length($analysisDocGuid) &gt; 0">
					<a id="&linkIdPrefix;" class="&linkClass;">
						<xsl:attribute name="href">
							<xsl:call-template name="GetDocumentUrl">
								<xsl:with-param name ="documentGuid" select="$analysisDocGuid" />
							</xsl:call-template>
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:text>&ukAnalysisLinkToolTip;</xsl:text>
						</xsl:attribute>
						<xsl:text>&ukAnalysisLinkText;</xsl:text>
					</a>
				</xsl:if>
			</div>
			<div>&#160;</div>
	</xsl:template>

	<xsl:template match="provisions">
		<div class="&paraMainClass;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="link">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="commencement">
		<xsl:choose>
			<xsl:when test="start-date">
				<div class="&centerClass; &paraMainClass;">
					<xsl:choose>
						<xsl:when test ="$statusCode='R' and @type='sif'">
							<strong>
								<xsl:text>&ukRepealed;</xsl:text>
							</strong>
						</xsl:when>
						<xsl:when test ="$statusCode='R'">
							<xsl:text>&ukRepealedOn;</xsl:text>
						</xsl:when>
						<xsl:when test ="@type='sda'">
							<xsl:text>&ukThisVersionInForceOn;</xsl:text>
						</xsl:when>
						<xsl:when test ="@type='partial'">
							<xsl:text>&ukThisVersionPartiallyInForceFrom;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&ukVersionInForceFrom;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="@type='sif'">
							<strong>
								<xsl:text>&ukDateNotAvailable;</xsl:text>
							</strong>
							<xsl:if test ="end-date or (@type='lif' or @type='sif' or @type='sif_dateknown') and $statusCode != 'R'">
								<xsl:text>&ukTo;</xsl:text>
							</xsl:if>
						</xsl:when>
						<xsl:when test="start-date = '19910201'">
							<strong>
								<xsl:text>&ukDateNotAvailable;</xsl:text>
							</strong>
							<xsl:if test ="end-date or (@type='lif' or @type='sif' or @type='sif_dateknown') and $statusCode != 'R'">
								<xsl:text>&ukTo;</xsl:text>
							</xsl:if>
						</xsl:when>
						<!-- Render 'date to be appointed' when the start date is 20990101-->
						<xsl:when test ="start-date = '20990101'">
							<strong>
								<xsl:text>&ukDateToBeAppointed;</xsl:text>
							</strong>
						</xsl:when>
						<xsl:otherwise>
							<strong>
								<xsl:value-of select="start-date/@date"/>
							</strong>
							<xsl:if test ="end-date or (@type='lif' or @type='sif' or @type='sif_dateknown') and $statusCode != 'R'">
								<xsl:text>&ukTo;</xsl:text>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>

					<strong>
						<xsl:choose>
							<xsl:when test ="$statusCode = 'R'"/>
							<xsl:when test="end-date = '20990101'">
								<xsl:text>&ukDateToBeAppointed;</xsl:text>
							</xsl:when>
							<xsl:when test="(@type='basic' or (@type='sif' and end-date) or (@type='sif_dateknown' and end-date) or (@type='nyif' and end-date)) and start-date">
								<xsl:value-of select="end-date/@date"/>
							</xsl:when>
							<xsl:when test="@type='lif' or @type='sif' or @type='sif_dateknown'">
								<xsl:text>&ukPresent;</xsl:text>
							</xsl:when>
							<!--<xsl:when test="$haveSettings and settings:IsDocumentFutureVersion(end-date)">
								<xsl:text>&ukPresent;</xsl:text>
							</xsl:when>-->
							<xsl:otherwise>
								<xsl:value-of select="end-date/@date"/>
							</xsl:otherwise>
						</xsl:choose>
					</strong>
				</div>
			</xsl:when>
			<xsl:when test="start-date">
				<div class="&centerClass;">
					<xsl:text>&ukVersionInForceFrom;</xsl:text>
					<strong>
						<xsl:choose>
							<xsl:when test="@type='sif'">
								<xsl:text>&ukDateNotAvailable;</xsl:text>
								<xsl:text>&#160;</xsl:text>
							</xsl:when>
							<xsl:when test="start-date = '19910201'">
								<xsl:text>&ukDateNotAvailable;</xsl:text>
								<xsl:text>&#160;</xsl:text>
							</xsl:when>
							<xsl:when test ="@type='nyif'">
								<xsl:text>&ukDateToBeAppointed;</xsl:text>
								<xsl:text>&#160;</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="start-date/@date"/>
							</xsl:otherwise>
						</xsl:choose>
					</strong>
					<xsl:if test ="not(@type='nyif')">
						<xsl:text>&ukTo;</xsl:text>
						<strong>
							<xsl:text>&ukPresent;</xsl:text>
						</strong>
					</xsl:if>
				</div>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Dates -->
	<xsl:template match="dates">
		<div class="&paraMainClass; &centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="made|laid|in-force-text|scotlaid|sub-in-force-text">
		<div class="&paraMainClass; &centerClass;">
			<em>
				<xsl:apply-templates />
				<xsl:if test="@date != '00000000'">
					<xsl:text>: </xsl:text>
					<xsl:call-template name="formatDate">
						<xsl:with-param name="dateTime" select="@date" />
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="parent::in-force/@date != '00000000'">
					<xsl:text>: </xsl:text>
					<xsl:call-template name="formatDate">
						<xsl:with-param name="dateTime" select="parent::in-force/@date" />
					</xsl:call-template>
				</xsl:if>
			</em>
		</div>
	</xsl:template>

	<xsl:template match="enacting-words">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="approval|laid-draft">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- **********************************************************************************************
	     * Section (C) - Sections                                                                     *
		 ********************************************************************************************** -->

	<xsl:template match="section-group">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="para-group/title">
		<br/>
		<br/>
		<div class="&centerClass;">
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>			
	</xsl:template>

	<xsl:template match="number">
		<xsl:choose>
			<xsl:when test="parent::section or parent::rule or parent::regulation or parent::article or parent::paragraph or parent::table">
				<br/>				
				<strong>
					<xsl:apply-templates />
					<xsl:text>&#160;</xsl:text>
				</strong>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
				<xsl:text>&#160;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="sub1">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<p>
					<xsl:apply-templates />
				</p>
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="sub2">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&indentLeft2Class;</xsl:text>
				<xsl:if test="not(parent::update)">
					<xsl:text> &paraMainClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::text() and not(following-sibling::sub2)">
				<div class="&paraMainClass;">&#160;</div>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template match="sub3">
		<xsl:element name="div">
			<xsl:attribute name="class">
				<xsl:text>&indentLeft3Class;</xsl:text>
				<xsl:if test="not(parent::update)">
					<xsl:text> &paraMainClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::text()">
				<div class="&paraMainClass;">&#160;</div>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template match="longquote">
		<blockquote>
			<div>
			<xsl:apply-templates />
			</div>
		</blockquote>
	</xsl:template>

	<xsl:template match="enacting-text">
		<xsl:if test="preceding-sibling::enacting-text">
			<div class="&paraMainClass;">&#160;</div>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="defnlist">
		<div class="&paraMainClass; &paraIndentLeftClass;">
			<div>&#160;</div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="defnlist-item">
		<div class="&paraMainClass; &paraIndentLeftClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="para-text">
		<xsl:choose>
			<xsl:when test="preceding-sibling::number and (parent::sub1 or parent::sub2 or parent::sub3 or parent::paragraph)">
				<xsl:text>&#160;</xsl:text>
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="preceding-sibling::number and not(parent::sub1 or parent::sub2 or parent::sub3 or parent::paragraph)">
				<div class="&paraMainClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:when test="parent::entry and ancestor::ins">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="parent::entry and not(preceding-sibling::para-text)">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<div class="&paraMainClass;">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<div class="&paraMainClass;">&#160;</div>
					<xsl:apply-templates />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="text()" priority="5">
		<xsl:value-of select="translate(.,'&#10;', ' ')"/>
	</xsl:template>

	<xsl:template match="list">
		<div class="&paraMainClass; &indentLeft2Class;">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:apply-templates />
				</xsl:when>
				<xsl:otherwise>
					<ul>
						<xsl:apply-templates />
					</ul>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

	<xsl:template match="item">
		<xsl:choose>
			<xsl:when test="$DeliveryMode">
				<div>
					<xsl:apply-templates />
					<xsl:if test="following-sibling::node()[1][self::list]">
						<xsl:apply-templates select="following-sibling::node()[1]" />
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<li>
					<xsl:if test="node()">
						<xsl:apply-templates select="*[not(self::text())]" />
					</xsl:if>
					<xsl:if test="text()">
						&#160;<xsl:apply-templates select="text()" />
					</xsl:if>
					<xsl:if test="following-sibling::node()[1][self::list]">
						<xsl:apply-templates select="following-sibling::node()[1]" />
					</xsl:if>
				</li>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="longtitle">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="assent">
		<div class="&paraIndentRightClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="number[parent::form]">
		<div class="&centerClass;">
			<strong>
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="form">
		<xsl:apply-templates />
		<div class="&centerClass;">
			<xsl:text>&ukFormNotAvailable;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template match="links[parent::form]">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="links[parent::footnote-text]">
		<xsl:text>&#160;</xsl:text>
		<xsl:apply-templates />
		<xsl:text>&#160;</xsl:text>
	</xsl:template>

	<xsl:template match="amendnotes" />

	<xsl:template match="signee">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="signee-name|signee-title|signee-department">
		<div class="&paraIndentRightClass;">
			<xsl:if test="not(preceding-sibling::*)">
				<div class="&paraMainClass;">&#160;</div>
			</xsl:if>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="signee-text|date-signed">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="signee-address">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="address-line">
		<xsl:if test="preceding-sibling::address-line">
			<div class="&paraMainClass;">&#160;</div>
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<!-- Common formatting templates -->
	<xsl:template match="emphasis">
		<xsl:choose>
			<xsl:when test="ancestor::thead">
				<em>
					<strong>
						<xsl:apply-templates />
					</strong>
				</em>
			</xsl:when>
			<xsl:otherwise>
				<em>
					<xsl:apply-templates />
				</em>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name ="parallelHeading">
		<div class="&centerClass; &paraMainClass;">
			<h2>
				<strong>
					<xsl:choose>
						<xsl:when test ="$appType='o'">
							<xsl:text>&ukTextProvisionVariesOnOtherApplication;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&ukParallelTextsRelatingTo;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:for-each select ="ancestor::n-docbody/descendant::fulltext">
						<xsl:if test ="preceding-sibling::fulltext[1]">
							<xsl:choose>
								<xsl:when test ="@application='o'">
									&#160;
								</xsl:when>
								<xsl:otherwise>
									<xsl:text> | </xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<a>
							<xsl:attribute name ="href">
								<xsl:value-of select ="concat('#', ./@application)"/>
							</xsl:attribute>
							<xsl:choose>
								<xsl:when test ="@application='o' and not (preceding-sibling::fulltext/@application='o')">
									<xsl:text>&ukOtherApplication;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='e'">
									<xsl:text>&ukEngland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='w'">
									<xsl:text>&ukWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='s'">
									<xsl:text>&ukScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='n'">
									<xsl:text>&ukNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='ew'">
									<xsl:text>&ukEnglandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='es'">
									<xsl:text>&ukEnglandAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='en'">
									<xsl:text>&ukEnglandAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='sw'">
									<xsl:text>&ukScotlandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='sn'">
									<xsl:text>&ukScotlandAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='se'">
									<xsl:text>&ukScotlandAndEngland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='ws'">
									<xsl:text>&ukWalesAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='wn'">
									<xsl:text>&ukWalesAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='we'">
									<xsl:text>&ukWalesAndEngland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='swn'">
									<xsl:text>&ukScotlandWalesAndNorthrenIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='swe'">
									<xsl:text>&ukScotlandWalesAndEngland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='snw'">
									<xsl:text>&ukScotlandNorthernIrelandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='sne'">
									<xsl:text>&ukScotlandNorthernIrelandAndEngland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='sew'">
									<xsl:text>&ukScotlandEnglandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='sen'">
									<xsl:text>&ukScotlandEnglandAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='ews'">
									<xsl:text>&ukEnglandWalesAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='ewn'">
									<xsl:text>&ukEnglandWalesAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='esw'">
									<xsl:text>&ukEnglandScotlandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='esn'">
									<xsl:text>&ukEnglandScotlandAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='enw'">
									<xsl:text>&ukEnglandNorthernIrelandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='ens'">
									<xsl:text>&ukEnglandNorthernIrelandAndScotland;</xsl:text>
								</xsl:when>

								<xsl:when test ="@application='ewsn'">
									<xsl:text>&ukEnglandWalesScotlandAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='ewns'">
									<xsl:text>&ukEnglandWalesNorthernIrelandAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='eswn'">
									<xsl:text>&ukEnglandScotlandWalesAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='esnw'">
									<xsl:text>&ukEnglandScotlandNorthernIrelandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='enws'">
									<xsl:text>&ukEnglandNorthernIrelandWalesAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='ensw'">
									<xsl:text>&ukEnglandNorthernIrelandScotlandAndWales;</xsl:text>
								</xsl:when>

								<xsl:when test ="@application='wesn'">
									<xsl:text>&ukWalesEnglandScotlandAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='_wens'">
									<xsl:text>&ukWalesEnglandNorthernIrelandAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='wsen'">
									<xsl:text>&ukWalesScotlandEnglandAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='wsne'">
									<xsl:text>&ukWalesScotlandNorthernIrelandAndEngland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='wnes'">
									<xsl:text>&ukWalesNorthernIrelandEnglandAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='wnse'">
									<xsl:text>&ukWalesNorthernIrelandScotlandAndEngland;</xsl:text>
								</xsl:when>
								
								<xsl:when test ="@application='sewn'">
									<xsl:text>&ukScotlandEnglandWalesAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='senw'">
									<xsl:text>&ukScotlandEnglandNorthernIrelandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='swen'">
									<xsl:text>&ukScotlandWalesEnglandAndNorthernIreland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='swne'">
									<xsl:text>&ukScotlandWalesNorthernIrelandAndEngland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='snew'">
									<xsl:text>&ukScotlandNorthernIrelandEnglandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='snwe'">
									<xsl:text>&ukScotlandNorthernIrelandWalesAndEngland;</xsl:text>
								</xsl:when>
								
								<xsl:when test ="@application='news'">
									<xsl:text>&ukNorthernIrelandEnglandWalesAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='nesw'">
									<xsl:text>&ukNorthernIrelandEnglandScotlandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='nwes'">
									<xsl:text>&ukNorthernIrelandWalesEnglandAndScotland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='nwse'">
									<xsl:text>&ukNorthernIrelandWalesScotlandAndEngland;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='nsew'">
									<xsl:text>&ukNorthernIrelandScotlandEnglandAndWales;</xsl:text>
								</xsl:when>
								<xsl:when test ="@application='nswe'">
									<xsl:text>&ukNorthernIrelandScotlandWalesAndEngland;</xsl:text>
								</xsl:when>
							</xsl:choose>
						</a>
					</xsl:for-each>
				</strong>
			</h2>
		</div>
	</xsl:template>

	<xsl:template match ="fulltext">
		<xsl:if test ="count(ancestor::n-docbody/descendant::fulltext) > 1">
			<div class="&paraMainClass; &underlineClass;">
				<h2>
					<strong>
						<a>
							<xsl:attribute name ="id">
								<xsl:value-of select="@application"/>
							</xsl:attribute>
						</a>
						<xsl:choose>
							<xsl:when test ="@application='o' and not (preceding-sibling::fulltext/@application='o')">
								<xsl:text>&ukOtherApplication;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='e'">
								<xsl:text>&ukEngland; </xsl:text>
							</xsl:when>
							<xsl:when test ="@application='w'">
								<xsl:text>&ukWales; </xsl:text>
							</xsl:when>
							<xsl:when test ="@application='s'">
								<xsl:text>&ukScotland; </xsl:text>
							</xsl:when>
							<xsl:when test ="@application='n'">
								<xsl:text>&ukNorthernIreland; </xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ew'">
								<xsl:text>&ukEnglandAndWales; </xsl:text>
							</xsl:when>
							<xsl:when test ="@application='es'">
								<xsl:text>&ukEnglandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='en'">
								<xsl:text>&ukEnglandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sw'">
								<xsl:text>&ukScotlandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sn'">
								<xsl:text>&ukScotlandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='se'">
								<xsl:text>&ukScotlandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ws'">
								<xsl:text>&ukWalesAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wn'">
								<xsl:text>&ukWalesAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='we'">
								<xsl:text>&ukWalesAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='swn'">
								<xsl:text>&ukScotlandWalesAndNorthrenIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='swe'">
								<xsl:text>&ukScotlandWalesAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='snw'">
								<xsl:text>&ukScotlandNorthernIrelandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sne'">
								<xsl:text>&ukScotlandNorthernIrelandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sew'">
								<xsl:text>&ukScotlandEnglandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sen'">
								<xsl:text>&ukScotlandEnglandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ews'">
								<xsl:text>&ukEnglandWalesAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ewn'">
								<xsl:text>&ukEnglandWalesAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='esw'">
								<xsl:text>&ukEnglandScotlandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='esn'">
								<xsl:text>&ukEnglandScotlandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='enw'">
								<xsl:text>&ukEnglandNorthernIrelandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ens'">
								<xsl:text>&ukEnglandNorthernIrelandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ewsn'">
								<xsl:text>&ukEnglandWalesScotlandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ewns'">
								<xsl:text>&ukEnglandWalesNorthernIrelandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='eswn'">
								<xsl:text>&ukEnglandScotlandWalesAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='esnw'">
								<xsl:text>&ukEnglandScotlandNorthernIrelandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='enws'">
								<xsl:text>&ukEnglandNorthernIrelandWalesAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ensw'">
								<xsl:text>&ukEnglandNorthernIrelandScotlandAndWales;</xsl:text>
							</xsl:when>
							
							<xsl:when test ="@application='wesn'">
								<xsl:text>&ukWalesEnglandScotlandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='_wens'">
								<xsl:text>&ukWalesEnglandNorthernIrelandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wsen'">
								<xsl:text>&ukWalesScotlandEnglandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wsne'">
								<xsl:text>&ukWalesScotlandNorthernIrelandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wnes'">
								<xsl:text>&ukWalesNorthernIrelandEnglandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wnse'">
								<xsl:text>&ukWalesNorthernIrelandScotlandAndEngland;</xsl:text>
							</xsl:when>
							
							<xsl:when test ="@application='sewn'">
								<xsl:text>&ukScotlandEnglandWalesAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='senw'">
								<xsl:text>&ukScotlandEnglandNorthernIrelandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='swen'">
								<xsl:text>&ukScotlandWalesEnglandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='swne'">
								<xsl:text>&ukScotlandWalesNorthernIrelandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='snew'">
								<xsl:text>&ukScotlandNorthernIrelandEnglandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='snwe'">
								<xsl:text>&ukScotlandNorthernIrelandWalesAndEngland;</xsl:text>
							</xsl:when>
							
							<xsl:when test ="@application='news'">
								<xsl:text>&ukNorthernIrelandEnglandWalesAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nesw'">
								<xsl:text>&ukNorthernIrelandEnglandScotlandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nwes'">
								<xsl:text>&ukNorthernIrelandWalesEnglandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nwse'">
								<xsl:text>&ukNorthernIrelandWalesScotlandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nsew'">
								<xsl:text>&ukNorthernIrelandScotlandEnglandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nswe'">
								<xsl:text>&ukNorthernIrelandScotlandWalesAndEngland;</xsl:text>
							</xsl:when>
						</xsl:choose>
					</strong>
				</h2>
			</div>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>

	<!--
	  ******************************************************************************************************
		* Backlog Item 506268: 
		* Remove all logos from International content. 
		* Add copyright message from royality block and message block centered at the bottom of the document.
		******************************************************************************************************
	-->
	<xsl:template match="copyright-message" mode="copyright">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

	<!--
	  ******************************************************************************************************
		* EQUATIONS
		* Two types of equations: display-equation and inline-equation
		*
		* display-equation contains a dformula element and should be displayed centered with space above
		* and below the equation
		*
		* inline-equation contains a formula element and should be displayed inline with surrounding
		* elements (primarily text)
		*
		* Need to consider:
		* - Nested Equations (e.g. <display-equation> inside <inline-equation>)
		*	- Nested Fractions, fractions within subscripts and superscripts
		*	- Nested subscripts and superscripts, subscripts within superscipts and vice-versa
		*	- Block level elements cannot be placed inside of inline-only containers (e.g. no <div> or <table> inside a <span> or <sup>)
		* - Need to remain XHMTL 1.0 Compliant
		* - Delivery is quite a strict and the XHTML output needs to get converted to valid XLS:FO.
		* - Delivery needs to look good enough.
		******************************************************************************************************
	-->

	<xsl:template match="display-equation">
		<div>&#160;</div>
		<div style="white-space: nowrap;">
			<xsl:attribute name="id">
				<xsl:value-of select="concat('equation_',@id)"/>
			</xsl:attribute>

			<xsl:apply-templates />
		</div>
		<div>&#160;</div>
	</xsl:template>

	<xsl:template match="dformula">
		<div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="inline-equation">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="formula">
		<div style="display: inline-block; white-space: nowrap">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="fraction">
		<xsl:choose>
			<xsl:when test="$DeliveryMode or parent::superscript or parent::sup or parent::subscript or parent::inf">
				<xsl:text> ( </xsl:text>
				<xsl:apply-templates select="num" mode="inline"/>
				<xsl:text> ) / ( </xsl:text>
				<xsl:apply-templates select="den" mode="inline"/>
				<xsl:text> ) </xsl:text>
			</xsl:when>

			<xsl:otherwise>
				<div style="display: inline-block; vertical-align: middle; text-align: center;">
					<xsl:apply-templates/>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="num">
		<div style="display: block; border-bottom: 1px solid;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="num" mode="inline">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="den">
		<div style="display: inline-block; vertical-align:top;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="den" mode="inline">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="wheretext">
		<div>
			<xsl:apply-templates/>
		</div>
		<div>&#160;</div>
	</xsl:template>

	<xsl:template match="parameter">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="parameter/para-text">
		<div class="&indentLeft2Class;">
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="inline-equation//subscript | inline-equation//inf | display-equation//subscript | display-equation//inf">
		<sub>
			<xsl:apply-templates/>
		</sub>
	</xsl:template>

	<xsl:template match="inline-equation//superscript | inline-equation//sup | display-equation//superscript | display-equation//sup">
		<sup>
			<xsl:apply-templates/>
		</sup>
	</xsl:template>

	<!-- Helper classes -->
	<!-- 
		Date:
			Sample output format = 21 July 1999 (DD month YYYY)
			Sample input format = 19990721 (YYYY MM DD)
	-->
	<xsl:template name="formatDate">
		<xsl:param name="dateTime" />
		<xsl:variable name="displayYear" select="substring($dateTime,1,4)" />
		<xsl:variable name="month" select="substring($dateTime,5,2)" />
		<xsl:variable name="displayMonth">
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
		</xsl:variable>
		<xsl:variable name="displayDay" select="substring($dateTime,7,2)" />
		<xsl:value-of select="concat($displayDay, ' ', $displayMonth, ' ', $displayYear)" />
	</xsl:template>



	<!-- remove these -->
	<xsl:template match="toch|tndx|crx|space|si-begin/subject|si-begin/title|si-numbers|act-number|shorttitle|department-code|fulltext/si-part|fulltext/act-part|schedule/title[not(ancestor::longquote)]|fulltext/schedule-part|fulltext/schedule-subpart|fulltext/schedule-subsubpart|fulltext/chapter" />
	<xsl:template match="md.previous.doc.in.sequence | md.next.doc.in.sequence | copyright-message | md.primarycite" />
	<xsl:template match="md.parallelcite[ancestor::n-metadata]"/>
</xsl:stylesheet>
