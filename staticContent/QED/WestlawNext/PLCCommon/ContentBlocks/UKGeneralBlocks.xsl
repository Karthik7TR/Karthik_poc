<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocLinks.xsl" forcePlatform="true"/>
	<xsl:include href="RecursiveTocBuilder.xsl" forcePlatform="true"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="UKStatuses.xsl"/>
	<xsl:include href="GeneralRestrictedContent.xsl"/>

	<xsl:variable name ="currentDocumentGuid" select="/Document/n-metadata/metadata.block/md.identifiers/md.uuid"/>
	<xsl:variable name ="contentType" select ="'&khContent;'"/>
	<xsl:variable name ="infoType" select="/Document/n-metadata/metadata.block/md.infotype"/>
	<xsl:variable name ="showLoading" select="false()"/>
	<xsl:variable name ="isAcademicUser" select="DocumentExtension:HasFacGranted('USER ACADEMIC')"/>

	<xsl:variable name="displayableCitesWithoutStatus" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info/md.display.primarycite[@display = 'Y' and not(@status)] | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite.wl/md.primarycite.info/md.display.primarycite[@display = 'Y' and not(@status)] | /Document/n-metadata/metadata.block/md.identifiers/md.cites/md.parallelcite/md.parallelcite.info/md.display.parallelcite[(@display = 'Y' or @userEntered = 'Y') and not(@status)]" />
	<xsl:variable name="eligibleWestlawCites" select="$displayableCitesWithoutStatus[@ID = $eligiblePagesets/@pageset ]" />

	<xsl:template name="BuildEndOfDocumentHead">
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template name="BuildLoggedOutGeneralDocumentHeader">
		<xsl:call-template name="BuildLoggedOutDocumentHeader"/>
		<xsl:call-template name="BuildEndOfDocumentHead"/>
	</xsl:template>

	<!--Trial button content-->
	<xsl:template name="TrialButtonContent">
		<a class="&trialButtonClass;" href="&wlukTrialFormLinkHref;" target="_blank">&loggedOutFreeTrialHeader;</a>
	</xsl:template>

	<xsl:template name="GeneralRestrictedDocument">
		<div id="&documentClass;" class="&documentClass; &ukWestlawContent;">
			<xsl:call-template name="BuildLoggedOutGeneralDocumentHeader"/>
			<div class="&coDocContentAll;">
				<div id="&coDocContentBody;">
					<xsl:choose>
						<xsl:when test="$IsAnonymousSession=false() and $PreviewMode = true()">
							<xsl:call-template name="GeneralUnsubscribedContent" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="GeneralLoggedOutContent" />
						</xsl:otherwise>
					</xsl:choose>
					<xsl:call-template name="BuildLoggedOutCopyright"/>
					<xsl:call-template name="BuildEndOfDocument"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<!--** General template for Document and inner common templates for custom overridings.-->
	<xsl:template match="Document" name="GeneralDocument">
		<div id="&documentClass;">
			<xsl:call-template name="BuildDocumentTypeAttribute" />
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="concat($contentType, ' &documentFixedHeaderView;')" />
			</xsl:call-template>
			<xsl:call-template name="BuildDocumentHeader"/>
			<xsl:call-template name="BuildDocumentToc"/>
			<div class="&coDocContentAll;">
				<xsl:if test="$contentType='&ukWestlawContent;'">
					<xsl:call-template name="BuildMetaInfoColumn"/>
				</xsl:if>
				<xsl:if test="$showLoading=true()">
					<span class="&loading;"></span>
				</xsl:if>
				<div id="&coDocContentBody;">
					<xsl:if test="not($DeliveryMode) and $showLoading = true()">
						<xsl:attribute name="class">
							<xsl:text>&hideStateClass;</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="BuildDocumentBody" />
					<xsl:call-template name="BuildFooter"/>
					<xsl:call-template name="BuildCopyright"/>
					<xsl:call-template name="BuildEndOfDocument"/>
				</div>
				<xsl:if test="$contentType!='&ukWestlawContent;'">
					<xsl:call-template name="BuildMetaInfoColumn"/>
				</xsl:if>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="Document" priority="1">
		<xsl:choose>
			<xsl:when test="$IsAnonymousSession=true() or $PreviewMode = true()">
				<xsl:call-template name="GeneralRestrictedDocument" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="BuildSpecificDocument" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--to override it in document specific stylesheet where it's necessary-->
	<xsl:template name="BuildSpecificDocument">
		<xsl:call-template name="GeneralDocument" />
	</xsl:template>

	<xsl:template name="OneColumnDocument">
		<div id="&documentClass;">
			<xsl:call-template name="BuildDocumentTypeAttribute" />
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="concat($contentType, ' &documentFixedHeaderView;')" />
			</xsl:call-template>
			<xsl:call-template name="BuildDocumentHeader"/>
			<xsl:call-template name="BuildDocumentToc"/>
			<div class="&coDocContentAll;">
				<xsl:if test="not($DeliveryMode) and $showLoading = true()">
					<span class="&loading;"></span>
				</xsl:if>
				<div id="&coDocContentBody;">
					<xsl:if test="not($DeliveryMode) and $showLoading = true()">
						<xsl:attribute name="class">
							<xsl:text>&hideStateClass;</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="BuildDocumentBody" />
					<xsl:call-template name="BuildFooter"/>
					<xsl:call-template name="BuildCopyright"/>
					<xsl:call-template name="BuildEndOfDocument"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="BuildDocumentTypeAttribute"/>
	<xsl:template name="BuildDocumentHeaderContent"/>
	<xsl:template name="BuildDocumentTocContent"/>
	<xsl:template name="BuildMetaInfoColumnContent"/>


	<xsl:template name="BuildDocumentHeader">
		<div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" >

			<xsl:call-template name="ShowWestlawUkLogo"/>
			<xsl:call-template name="ShowAcademicUserMark"/>

			<xsl:if test="not($DeliveryMode)">
				<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
			</xsl:if>

			<xsl:if test="not($IsQuickDraftDelivery)">
				<div id="&coDocHeaderContainer;">
					<!--Increase min-height for label-->
					<xsl:if test="$isAcademicUser = true()">
						<xsl:attribute name="class">
							<xsl:text>&academicUserClass;</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<div class="&paraMainClass;">
						<xsl:call-template name="BuildDocumentHeaderContent"/>
					</div>
				</div>
			</xsl:if>
		</div>
		<xsl:call-template name="BuildEndOfDocumentHead"/>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentHeaderContent" />

	<xsl:template name="BuildLoggedOutDocumentHeader">
		<div id="&coDocContentHeaderPrelim;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" >
			<xsl:call-template name="ShowWestlawUkLogo"/>
			<div id="&codocumentHeaderBorder;" class="&documentHeaderBorderClass;"></div>
			<div id="&coDocHeaderContainer;">
				<div class="&paraMainClass;">
					<xsl:call-template name="BuildLoggedOutDocumentHeaderContent"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="ShowWestlawUkLogo">
		<div class="&coFloatRight;">
			<div class="&ukWestlawLogo;">
				<a class="&linkToThisDocOnWestlawUk;" target="_blank" title="&westlawUkLinkTitle;" data-link-info ="{$currentDocumentGuid}">
					<img src="{$Images}&ukWestlawUkLogo;" alt="&ukProvidedBy; &ukWestlawUk;"/>
				</a>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="ShowAcademicUserMark">
		<xsl:if test="$isAcademicUser = true()">
			<div class="&coFloatRight; &clearClass;">&academicUserLabel;</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BuildDocumentToc">
		<xsl:call-template name="BuildDocumentTocInternal"/>
	</xsl:template>

	<xsl:template name="BuildDocumentBody"/>

	<xsl:template name="BuildMetaInfoColumn">
		<div id="&coDocContentMetaInfo;" >
			<xsl:attribute name="class">
				<xsl:text>&alignHorizontalLeftClass;</xsl:text>
				<xsl:if test="not($DeliveryMode) and $showLoading = true()">
					<xsl:text><![CDATA[ ]]>&hideStateClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:call-template name="BuildMetaInfoColumnContent"/>
		</div>
	</xsl:template>

	<xsl:template name="BuildFooter"/>
	<xsl:template name="BuildEndOfDocument"/>

	<xsl:template name="BuildCopyright">
		<xsl:param name="copyrightMessage" select="n-docbody/copyright-message"/>

		<xsl:if test="string-length($copyrightMessage) &gt; 0">
			<div class="&centerClass;">
				<xsl:call-template name="copyrightBlock">
					<xsl:with-param name="copyrightNode" select="$copyrightMessage" />
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BuildLoggedOutCopyright">
		<div class="&ukReferenceCopyrightWrap;">
			<xsl:call-template name="BuildCopyright"/>
		</div>
	</xsl:template>

	<!--** Table of content region-->

	<!-- Template to build TOC. Calls 'BuildDocumentTocContent' template for filling TOC items. 
	Can skip "Table of content" header.-->
	<xsl:template name="BuildDocumentTocInternal">
		<xsl:param name="includeHeader" select="true()"/>
		<xsl:param name="debug" select="false()"/>

		<xsl:if test="not($DeliveryMode or $IsMobile) or ($DisplayTableOfContents = 'true')">
			<div id="&coIntTocContainer;">
				<!--kh_toc-wrapper-->
				<xsl:attribute name="class">
					<xsl:text>&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;</xsl:text>
					<xsl:if test="not($DeliveryMode or $IsMobile)">
						<xsl:text> &hideState;</xsl:text>
					</xsl:if>
				</xsl:attribute>

				<div class="&coKhOverlayClass;">
					&nbsp;
				</div>

				<div id="&coKhTocContainer;" class="&coKhToc;">
					<div class="&coKhTocInner;">
						<xsl:if test="$includeHeader">
							<div class="&coKhTocHeader;">
								<xsl:if test="not($DeliveryMode)">
									<a class="&coMenuToggle;">
										<span class="&khIcon; &coMenuToggleIconMenu;">&coActionTogglerSpanText;</span>
										<span>
											<xsl:if test="not($IsMobile)">
												<xsl:attribute name="class">&hideState;</xsl:attribute>
											</xsl:if>
											<xsl:call-template name="AddTocHeadingText"/>
										</span>
									</a>
									<xsl:if test="not($IsMobile)">
										<a class="&coTocIconCross;" href="#"></a>
										<p class="&coAccessibilityLabel;">&coTocToggleHelpText;</p>
									</xsl:if>
								</xsl:if>
							</div>
						</xsl:if>
						<div>
							<xsl:attribute name="class">
								<xsl:text>&coKhTocContent;</xsl:text>
								<xsl:if test="not($DeliveryMode) and $showLoading = true()">
									<xsl:text><![CDATA[ ]]>&hideStateClass;</xsl:text>
								</xsl:if>
							</xsl:attribute>

							<xsl:if test="$DeliveryMode = 'True'">
								<xsl:attribute name="style">display: block;</xsl:attribute>
							</xsl:if>

							<xsl:if test="$debug">
								<xsl:text disable-output-escaping="yes">&lt;!--</xsl:text>
							</xsl:if>

							<xsl:call-template name ="BuildDocumentTocContent"/>

							<xsl:if test="$debug">
								<xsl:text disable-output-escaping="yes">--&gt;</xsl:text>
							</xsl:if>
						</div>
					</div>
				</div>
			</div>
		</xsl:if>

	</xsl:template>

	<xsl:template name="AddTocHeadingText">
		<xsl:text>&coTableOfContentsHeaderText;</xsl:text>
	</xsl:template>

	<!-- Basic template to write one toc item (li, ol, div, etc).
	Also, can write just "open" tag or just "close" (designed to be used for subitems). -->
	<xsl:template name="WriteTocItem">
		<xsl:param name="TocItemTag" select="string('li')"/>
		<xsl:param name="TocItemClass" select="''"/>
		<xsl:param name="TocItemInnerElement" select="''"/>
		<xsl:param name="TocItemAnchor" select="''"/>
		<xsl:param name="TocItemCaption" select="''"/>
		<xsl:param name="TocItemOpen" select="true()"/>
		<xsl:param name="TocItemClose" select="true()"/>
		<xsl:param name="DeliveryMode" select="$DeliveryMode" />
		<xsl:param name="IsAnchor" select="true()" />
		<xsl:param name="TocItemHideBody" select="false()"/>
		<xsl:param name="TocItemCategory" select="''"/>

		<xsl:if test="$TocItemOpen">
			<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
			<xsl:value-of select="$TocItemTag"/>
			<xsl:if test="string-length($TocItemClass) &gt; 0">
				<xsl:text> class="</xsl:text>
				<xsl:value-of select="$TocItemClass"/>
				<xsl:text>"</xsl:text>
			</xsl:if>
			<xsl:text disable-output-escaping="yes">&gt;</xsl:text>

			<xsl:if test="string-length($TocItemCaption)>0">
				<xsl:if test="$TocItemInnerElement">
					<xsl:text disable-output-escaping="yes">&lt;</xsl:text>
					<xsl:value-of select="$TocItemInnerElement"/>
					<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="(string-length($TocItemAnchor) > 0)">
						<a>
							<xsl:attribute name ="href">
								<xsl:if test="$IsAnchor='true'">
									<xsl:text>#</xsl:text>
								</xsl:if>
								<xsl:copy-of select="$TocItemAnchor"/>
							</xsl:attribute>

							<xsl:if test="$TocItemHideBody">
								<xsl:attribute name ="data-hideBody">
									<xsl:value-of select="$TocItemHideBody"/>
								</xsl:attribute>
							</xsl:if>

							<xsl:if test="$TocItemCategory and string-length($TocItemCategory) &gt; 0">
								<xsl:attribute name ="data-category">
									<xsl:value-of select="$TocItemCategory"/>
								</xsl:attribute>
							</xsl:if>

							<xsl:copy-of select="$TocItemCaption"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<span>
							<xsl:copy-of select="$TocItemCaption"/>
						</span>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="$TocItemInnerElement">
					<xsl:text disable-output-escaping="yes">&lt;&#47;</xsl:text>
					<xsl:value-of select="$TocItemInnerElement"/>
					<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:if>
		<xsl:if test="$TocItemClose">
			<xsl:text disable-output-escaping="yes">&lt;&#47;</xsl:text>
			<xsl:value-of select="$TocItemTag"/>
			<xsl:text disable-output-escaping="yes">&gt;</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Dev-friendly template to open 'ol' tag. -->
	<xsl:template name="WriteTocListOpen">
		<xsl:param name="includeClass" select="'&coKhTocOlList;'"/>

		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemTag" select="'ol'"/>
			<xsl:with-param name="TocItemClass" select="normalize-space($includeClass)" />
			<xsl:with-param name="TocItemClose" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Dev-friendly template to close 'ol' tag. -->
	<xsl:template name="WriteTocListClose">
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemTag" select="'ol'"/>
			<xsl:with-param name="TocItemOpen" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Dev-friendly template to close 'li' tag (designed to be used for subitems). -->
	<xsl:template name="WriteTocItemClose">
		<xsl:call-template name="WriteTocItem">
			<xsl:with-param name="TocItemOpen" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name ="GetAnchorId">
		<xsl:param name="section"></xsl:param>
		<xsl:value-of select ="concat('&internalLinkIdPrefix;', translate($section,' ',''))"/>
	</xsl:template>


	<!--** Date & time region-->

	<!-- Convert date from yyyymmdd format to dd Month yyyy format
		Example: 20150922 => 22 September 2015-->
	<xsl:template name="formatYearMonthDayToDDMMMYYYY">
		<xsl:param name="date" select="."/>
		<xsl:param name="dayFormatWithZero" select="true()"/>
		<xsl:if test="string-length($date) &gt; 7 and number($date) != 'NaN'">
			<xsl:variable name ="year" select ="substring($date,1,4)"/>
			<xsl:variable name ="month" select ="substring($date,5,2)"/>
			<xsl:variable name ="day" select ="substring($date,7,2)"/>

			<xsl:choose>
				<xsl:when test="$dayFormatWithZero!='true' and starts-with($day, '0')">
					<xsl:value-of select ="substring($day,2,1)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select ="$day"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:choose>
				<xsl:when test ="$month = 01">&JanuaryText;</xsl:when>
				<xsl:when test ="$month = 02">&FebruaryText;</xsl:when>
				<xsl:when test ="$month = 03">&MarchText;</xsl:when>
				<xsl:when test ="$month = 04">&AprilText;</xsl:when>
				<xsl:when test ="$month = 05">&MayText;</xsl:when>
				<xsl:when test ="$month = 06">&JuneText;</xsl:when>
				<xsl:when test ="$month = 07">&JulyText;</xsl:when>
				<xsl:when test ="$month = 08">&AugustText;</xsl:when>
				<xsl:when test ="$month = 09">&SeptemberText;</xsl:when>
				<xsl:when test ="$month = 10">&OctoberText;</xsl:when>
				<xsl:when test ="$month = 11">&NovemberText;</xsl:when>
				<xsl:when test ="$month = 12">&DecemberText;</xsl:when>
			</xsl:choose>
			<xsl:text><![CDATA[ ]]></xsl:text>
			<xsl:value-of select ="$year"/>
		</xsl:if>
	</xsl:template>


	<!--** Jurisdictions region-->
	<xsl:template name ="putJurisdictionName">
		<xsl:variable name="jurisdictionName">
			<xsl:call-template name="getJurisdictionName">
				<xsl:with-param name="countryCode" select="@application"/>
				<xsl:with-param name="hasPrecedingOtherApplication" select="preceding-sibling::fulltext/@application='&ukOtherApplicationCode;'"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:value-of select="$jurisdictionName"/>
	</xsl:template>

	<!--Universal template for Jurisdiction name retrieving-->
	<xsl:template name ="getJurisdictionName">
		<xsl:param name="countryCode"/>
		<xsl:param name="hasPrecedingOtherApplication"/>
		<xsl:param name="jurisdictionName"/>

		<xsl:choose>
			<xsl:when test ="$countryCode='&ukOtherApplicationCode;' and not ($hasPrecedingOtherApplication)">
				&ukOtherApplication;
			</xsl:when>
			<xsl:when test ="$countryCode='e'">
				<xsl:text>&ukEngland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='w'">
				<xsl:text>&ukWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='s'">
				<xsl:text>&ukScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='n'">
				<xsl:text>&ukNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='ew'">
				<xsl:text>&ukEnglandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='es'">
				<xsl:text>&ukEnglandAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='en'">
				<xsl:text>&ukEnglandAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='sw'">
				<xsl:text>&ukScotlandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='sn'">
				<xsl:text>&ukScotlandAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='se'">
				<xsl:text>&ukScotlandAndEngland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='ws'">
				<xsl:text>&ukWalesAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='wn'">
				<xsl:text>&ukWalesAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='we'">
				<xsl:text>&ukWalesAndEngland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='swn'">
				<xsl:text>&ukScotlandWalesAndNorthrenIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='swe'">
				<xsl:text>&ukScotlandWalesAndEngland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='snw'">
				<xsl:text>&ukScotlandNorthernIrelandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='sne'">
				<xsl:text>&ukScotlandNorthernIrelandAndEngland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='sew'">
				<xsl:text>&ukScotlandEnglandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='sen'">
				<xsl:text>&ukScotlandEnglandAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='ews'">
				<xsl:text>&ukEnglandWalesAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='ewn'">
				<xsl:text>&ukEnglandWalesAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='esw'">
				<xsl:text>&ukEnglandScotlandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='esn'">
				<xsl:text>&ukEnglandScotlandAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='enw'">
				<xsl:text>&ukEnglandNorthernIrelandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='ens'">
				<xsl:text>&ukEnglandNorthernIrelandAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='ewsn'">
				<xsl:text>&ukEnglandWalesScotlandAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='ewns'">
				<xsl:text>&ukEnglandWalesNorthernIrelandAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='eswn'">
				<xsl:text>&ukEnglandScotlandWalesAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='esnw'">
				<xsl:text>&ukEnglandScotlandNorthernIrelandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='enws'">
				<xsl:text>&ukEnglandNorthernIrelandWalesAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='ensw'">
				<xsl:text>&ukEnglandNorthernIrelandScotlandAndWales;</xsl:text>
			</xsl:when>

			<xsl:when test ="$countryCode='wesn'">
				<xsl:text>&ukWalesEnglandScotlandAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='wens'">
				<xsl:text>&ukWalesEnglandNorthernIrelandAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='wsen'">
				<xsl:text>&ukWalesScotlandEnglandAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='wsne'">
				<xsl:text>&ukWalesScotlandNorthernIrelandAndEngland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='wnes'">
				<xsl:text>&ukWalesNorthernIrelandEnglandAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='wnse'">
				<xsl:text>&ukWalesNorthernIrelandScotlandAndEngland;</xsl:text>
			</xsl:when>

			<xsl:when test ="$countryCode='sewn'">
				<xsl:text>&ukScotlandEnglandWalesAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='senw'">
				<xsl:text>&ukScotlandEnglandNorthernIrelandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='swen'">
				<xsl:text>&ukScotlandWalesEnglandAndNorthernIreland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='swne'">
				<xsl:text>&ukScotlandWalesNorthernIrelandAndEngland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='snew'">
				<xsl:text>&ukScotlandNorthernIrelandEnglandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='snwe'">
				<xsl:text>&ukScotlandNorthernIrelandWalesAndEngland;</xsl:text>
			</xsl:when>

			<xsl:when test ="$countryCode='news'">
				<xsl:text>&ukNorthernIrelandEnglandWalesAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='nesw'">
				<xsl:text>&ukNorthernIrelandEnglandScotlandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='nwes'">
				<xsl:text>&ukNorthernIrelandWalesEnglandAndScotland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='nwse'">
				<xsl:text>&ukNorthernIrelandWalesScotlandAndEngland;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='nsew'">
				<xsl:text>&ukNorthernIrelandScotlandEnglandAndWales;</xsl:text>
			</xsl:when>
			<xsl:when test ="$countryCode='nswe'">
				<xsl:text>&ukNorthernIrelandScotlandWalesAndEngland;</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>




	<!--** Links region-->
	<xsl:template match="caseref">
		<xsl:apply-templates select="link" />
	</xsl:template>

	<!--link (open page in the new window)-->
	<xsl:template name="LinkOpensInNewTab">
		<!-- optional parameters -->
		<xsl:param name="href"/>
		<xsl:param name="dataLinkInfo"/>
		<xsl:param name="text"/>
		<xsl:param name="id"/>
		<xsl:param name="class"/>
		<xsl:param name="title"/>
		<a target="&linkTargetNewWindow;">
			<xsl:if test="string-length($href) &gt; 0">
				<xsl:attribute name="href">
					<xsl:value-of select="$href"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($dataLinkInfo) &gt; 0">
				<xsl:attribute name="data-link-info">
					<xsl:value-of select="$dataLinkInfo"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($id) &gt; 0">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($class) &gt; 0">
				<xsl:attribute name="class">
					<xsl:value-of select="$class"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length($title) &gt; 0">
				<xsl:attribute name="title">
					<xsl:value-of select="$title"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="string-length($text) &gt; 0">
					<xsl:value-of select="$text"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="text()"/>
				</xsl:otherwise>
			</xsl:choose>
		</a>
	</xsl:template>

	<!--link (open page in the new window)-->
	<xsl:template name="LinkOnNewLineOpensInNewTab">
		<!--optional-->
		<xsl:param name="href" />
		<!--optional-->
		<xsl:param name="dataLinkInfo" />
		<!--optional-->
		<xsl:param name="text" />
		<!--optional-->
		<xsl:param name="id" />
		<!--optional-->
		<xsl:param name="class" />
		<li>
			<xsl:if test="$class">
				<xsl:attribute name="class">&hideState;</xsl:attribute>
			</xsl:if>

			<xsl:call-template name="LinkOpensInNewTab">
				<xsl:with-param name="href" select="$href" />
				<xsl:with-param name="dataLinkInfo" select="$dataLinkInfo" />
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="class" select="$class" />
				<xsl:with-param name="id" select="$id" />
			</xsl:call-template>
		</li>
	</xsl:template>

	<!--Annotation internal link (open page in the same window).-->
	<xsl:template match="link[@tuuid and not(@DMS) and not(cite.query)]">
		<a>
			<xsl:attribute name="href">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name="documentGuid" select="@tuuid"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:apply-templates select="child::node()" />
		</a>
	</xsl:template>

	<xsl:template match="link[@tuuid and cite.query]">
		<xsl:choose>
			<xsl:when test="(cite.query/@w-ref-type = 'UE') or (cite.query/@w-ref-type = 'UO')">
				<xsl:apply-templates select="./cite.query/node()"/>
			</xsl:when>
			<xsl:when test="cite.query/@w-ref-type = 'UW'">
				<xsl:call-template name="LinkOpensInNewTab">
					<xsl:with-param name="href" select="UrlBuilder:CreatePersistentUrl('Page.WlukSeemlessTransfer', concat('docGuid=', @tuuid),'originationContext=&docDisplayOriginationContext;')" />
					<xsl:with-param name="text">
						<xsl:apply-templates select="./cite.query/node()"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<a>
					<xsl:attribute name="href">
						<xsl:call-template name="GetDocumentUrl">
							<xsl:with-param name="documentGuid" select="@tuuid" />
						</xsl:call-template>
					</xsl:attribute>
					<xsl:apply-templates select="./cite.query/node()"/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="link[category.page.link]">
		<xsl:choose>
			<xsl:when test="category.page.link/@href">
				<xsl:element name="a">
					<xsl:attribute name="href">
						<xsl:call-template name="GetBrowseRootUrl">
							<xsl:with-param name="navigationPath" select="./category.page.link/@href" />
						</xsl:call-template>
					</xsl:attribute>
					<xsl:apply-templates select="./category.page.link"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="./category.page.link"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="GetBrowseRootUrl">
		<xsl:param name ="navigationPath" />
		<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.BrowseRoot', concat('catchall=',$navigationPath), 'contextData=&ContextDataCsDefault;', 'transitionType=&transitionTypeDefault;')"/>
	</xsl:template>

	<!--Annotation external link (open page in the new window).-->
	<xsl:template match="external-link | link[not(@tuuid) and not(@DMS) and not(cite.query)]">
		<xsl:call-template name="LinkOpensInNewTab">
			<xsl:with-param name="href" select="./@href" />
		</xsl:call-template>
	</xsl:template>

	<!--Links to other resources (bailii and etc.)-->
	<xsl:template match ="cmd.primary.source.uris">
		<xsl:for-each select="./cmd.primary.source.uri">
			<xsl:variable name ="prefix" select="translate(substring-before(. , '://' ), 'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
			<xsl:choose>
				<!--Bailii links-->
				<xsl:when test="$prefix = 'CASE'">
					<xsl:if test="DocumentExtension:HasFacGranted('ENABLE BAILII LINKS')">
						<xsl:call-template name="LinkOnNewLineOpensInNewTab">
							<!--href will be changed with js Cobalt.Document.Widget.ExternalReferences.PLCUK.js-->
							<xsl:with-param name="dataLinkInfo" select="substring-after(. , '://' )" />
							<xsl:with-param name="text" select="'&bailiiText;'"/>
							<xsl:with-param name="class" select="'&bailiiLink;'" />
						</xsl:call-template>
					</xsl:if>
				</xsl:when>
				<!--Celex Links-->
				<xsl:when test="$prefix = 'CELEX'">
					<xsl:variable name="celexId" select="substring-after(. , '://' )" />
					<xsl:call-template name="LinkOnNewLineOpensInNewTab">
						<!--href will be changed with js Cobalt.Document.Widget.ExternalReferences.PLCUK.js-->
						<xsl:with-param name="dataLinkInfo" select="$celexId" />
						<xsl:with-param name="text" select="'&eurLexText;'"/>
						<xsl:with-param name="class" select="'&celexLink;'" />
					</xsl:call-template>
				</xsl:when>
				<!--	legislation Links-->
				<xsl:when test="$prefix = 'LEGISLATION'">
					<xsl:if test="DocumentExtension:HasFacGranted('ENABLE-LEGI-GOV-LINKS')">
						<xsl:call-template name="LinkOnNewLineOpensInNewTab">
							<!--href will be changed with js Cobalt.Document.Widget.ExternalReferences.PLCUK.js-->
							<xsl:with-param name="dataLinkInfo" select="substring-after(. , '://' )" />
							<xsl:with-param name="text" select="'&legislationGovText;'"/>
							<xsl:with-param name="class" select="'&legislationGovLink;'" />
						</xsl:call-template>
					</xsl:if>
				</xsl:when>
				<!--	westlaw Links shouldn't be displayed-->
				<xsl:when test="$prefix = 'WESTLAW'" />
				<xsl:when test="starts-with(., '&linkJusticeToFind;')">
					<xsl:call-template name="LinkOnNewLineOpensInNewTab">
						<xsl:with-param name="href" select="." />
						<xsl:with-param name="text" select="'&linkJusticeToShow;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="starts-with(., '&linkWestlawChinaToFind;')">
					<xsl:call-template name="LinkOnNewLineOpensInNewTab">
						<xsl:with-param name="href" select="." />
						<xsl:with-param name="text" select="'&linkWestlawChinaToShow;'"/>
					</xsl:call-template>
				</xsl:when>
				<!--  Other hard coded links-->
				<xsl:otherwise>
					<xsl:call-template name="LinkOnNewLineOpensInNewTab">
						<xsl:with-param name="href" select="." />
						<xsl:with-param name="text">
							<xsl:if test="starts-with(., '&linkHttp;')">
								<xsl:value-of select="substring-before(substring-after(., '&linkHttp;'), '/')"/>
							</xsl:if>
							<xsl:if test="starts-with(., '&linkHttps;')">
								<xsl:value-of select="substring-before(substring-after(., '&linkHttps;'), '/')"/>
							</xsl:if>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<!--Links to westlaw Uk -->
	<xsl:template name="WestlawUKLinks">
		<xsl:for-each select="//md.doc.family.uuid">
			<xsl:if test="string-length(.) &gt; 0">
				<xsl:call-template name="LinkOnNewLineOpensInNewTab">
					<xsl:with-param name="dataLinkInfo" select="." />
					<!--href will be changed with js Cobalt.Document.Widget.WestlawUKLinks.PLCUK.js-->
					<xsl:with-param name="text" select="'&westlawUKText;'"/>
					<xsl:with-param name="class" select="'&westlawUKLink;'" />
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<!--** Content region-->

	<!--Template to build section header.
	Can be customized to have an anchor, horizontal rule, or wrapping tag ('h2' by default).-->
	<xsl:template name="BuildHeading">
		<xsl:param name="headingAnchorText"/>
		<xsl:param name="headingCaption"/>
		<xsl:param name="headingTag" select="string('h2')"/>
		<xsl:param name="useRule" select="false"/>
		<a>
			<xsl:attribute name ="id">
				<xsl:copy-of select="$headingAnchorText"/>
			</xsl:attribute>
		</a>
		<xsl:element name="{$headingTag}">
			<xsl:attribute name = "class">
				<xsl:value-of select = "'&printHeadingClass; '"/>
				<xsl:value-of select = "'&docHeadText; '"/>
				<xsl:if test="$useRule = 'true'">
					<xsl:value-of select = "'&borderTopClass; '"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:copy-of select="$headingCaption"/>
		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildMetaField">
		<xsl:param name="fieldClass"/>
		<xsl:param name="fieldCaption"/>
		<xsl:param name="fieldContent">
			<xsl:apply-templates />
		</xsl:param>
		<xsl:param name="captionOnNewLine" select="false()"/>
		<xsl:call-template name="RenderField">
			<xsl:with-param name="fieldClass" select="$fieldClass"/>
			<xsl:with-param name="fieldCaption" select="$fieldCaption"/>
			<xsl:with-param name="fieldContent" select="$fieldContent"/>
			<xsl:with-param name="captionWrapElement" select="'b'"/>
			<xsl:with-param name="captionOnNewLine" select="$captionOnNewLine"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="BuildBodyField">
		<xsl:param name="fieldCaption"/>
		<xsl:param name="fieldContent">
			<xsl:apply-templates />
		</xsl:param>
		<xsl:call-template name="RenderField">
			<xsl:with-param name="fieldClass" select="'&paraMainClass;'"/>
			<xsl:with-param name="fieldCaption" select="$fieldCaption"/>
			<xsl:with-param name="fieldContent" select="$fieldContent"/>
			<xsl:with-param name="captionWrapElement" select="'h3'"/>
			<xsl:with-param name="captionOnNewLine" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderField">
		<xsl:param name="fieldClass"/>
		<xsl:param name="fieldCaption"/>
		<xsl:param name="fieldContent">
			<xsl:apply-templates />
		</xsl:param>
		<xsl:param name="captionWrapElement"/>
		<xsl:param name="captionOnNewLine" select="false()"/>

		<xsl:variable name="wrapElement">
			<xsl:choose>
				<xsl:when test="$captionOnNewLine">
					<xsl:text>div</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>span</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string($fieldContent) != ''">
			<div class="{$fieldClass}">
				<xsl:if test="string($fieldCaption) != ''">
					<xsl:element name="{$captionWrapElement}">
						<xsl:value-of select = "$fieldCaption"/>
					</xsl:element>
				</xsl:if>
				<xsl:element name="{$wrapElement}">
					<xsl:copy-of select="$fieldContent"/>
				</xsl:element>
			</div>
		</xsl:if>
	</xsl:template>

	<!--Template to build resource type section -->
	<xsl:template match="resource.type">
		<xsl:call-template name="BuildMetaField">
			<xsl:with-param name="fieldClass" select="'&metaResourceType;'" />
			<xsl:with-param name="fieldCaption" select="'&docTypeText;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="md.title">
		<h1 class="&titleClass;">
			<xsl:apply-templates />
		</h1>
	</xsl:template>

	<xsl:template name="EmptyEndOfDocument">
		<xsl:param name="endOfDocumentCopyrightText" select="$endOfDocumentCopyrightText"/>

		<xsl:if test="not($DeliveryMode)">
			<xsl:choose>
				<xsl:when test="not($EasyEditMode)">
					<!--empty End of Document element-->
					<div id="&endOfDocumentId;"/>

					<!--back to the top button-->
					<xsl:if test="not($IsMobile)">
						<div id="&khBackToTop;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" style="display:none;">
							<xsl:choose>
								<xsl:when test="$IsIpad = 'true'">
									<a href="&hashTagText;&documentId;" title="&backToTopToolTip;">
										<span class="&khIcon; &khIconUpPointer;">&backToTop;</span>
									</a>
								</xsl:when>
								<xsl:otherwise>
									<a href="&hashTagText;&coPageContainer;" title="&backToTopToolTip;">
										<span class="&khIcon; &khIconUpPointer;">&backToTop;</span>
									</a>
								</xsl:otherwise>
							</xsl:choose>
						</div>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!--To break a line-->
	<xsl:template match="endline | //endline | */endline | br">
		<br/>
	</xsl:template>

	<xsl:template name="RightTrim">
		<xsl:param name="str"/>
		<xsl:param name="chars"/>

		<xsl:variable name="len" select="string-length($str)"/>
		<xsl:variable name="lastChar" select="substring($str, $len)"/>

		<xsl:choose>
			<xsl:when test="$len = 0"/>
			<xsl:when test="(not (contains($chars, $lastChar)))">
				<xsl:value-of select="$str"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RightTrim">
					<xsl:with-param name="str" select="substring($str, 1, $len - 1)"/>
					<xsl:with-param name="chars" select="$chars"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildImage">
		<xsl:param name="guid"/>
		<xsl:param name="styles"/>
		<xsl:param name="alt"/>
		<xsl:variable name="blobHref">
			<xsl:call-template name="createBlobLink">
				<xsl:with-param name="guid" select="$guid"/>
				<xsl:with-param name="mimeType" select="'&xPngMimeType;'"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:element name="img">
			<xsl:attribute name ="class">
				<xsl:value-of select="$styles"/>
			</xsl:attribute>
			<xsl:attribute name="src">
				<xsl:value-of select="$blobHref"/>
			</xsl:attribute>
			<xsl:attribute name="alt">
				<xsl:value-of select="$alt"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
