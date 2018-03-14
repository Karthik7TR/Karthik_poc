<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:param name="SponsorId" />
	<xsl:param name="LogoUrl" />
	<xsl:param name="AlertGuid" />
	<xsl:param name="AlertHistoryGuid" />
	<xsl:param name="AlertHistoryRank" />
	
	<xsl:variable name="SponsorHash">
		<xsl:if test="string-length($SponsorId) &gt; 0">
			<xsl:variable name="docFamilyGuid" select="/Document/n-metadata/metadata.block/md.descriptions/md.doc.family.uuid/text()" />
			<xsl:value-of select="DocumentExtension:GenerateSponsorHash($SponsorId, $docFamilyGuid)" />
		</xsl:if>
	</xsl:variable>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&predocketsClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="CustomLogo" />
			<xsl:call-template name="TitleLink" />
			<xsl:call-template name="Source"/>
			<br/>
			<xsl:apply-templates select="n-docbody/r/case.information.block/docket.block/docket.number | n-docbody/r/docket.block/docket.number"/>
			<br/>
			<xsl:apply-templates select="n-docbody/r/case.information.block/filing.date.block | n-docbody/r/filing.date.block"/>
			<xsl:apply-templates select="n-docbody/r/case.information.block/case.type.block | n-docbody/r/case.type"/>
			<xsl:apply-templates select="n-docbody/r/case.information.block/case.subtype.block | n-docbody/r/case.subtype"/>
			<xsl:apply-templates select="n-docbody/r/summary/nature.of.suit.block"/>
			<xsl:apply-templates select="n-docbody/r/summary/key.nature.of.suit.block | n-docbody/r/case.information.block/key.nature.of.suit.block | n-docbody/r/key.nature.of.suit.block"/>

			<xsl:for-each select="n-docbody/r/matched.block/matched.party.block/matched.party.type.block">
				<xsl:call-template name="PlaintiffDefendantInformation">
					<xsl:with-param name="isPlaintiff" select="true()" />
					<xsl:with-param name="partyCriteria" select="normalize-space(matched.party.type)='PLAINTIFF' or normalize-space(matched.party.type)='PLAINTIFF/PETITIONER'" />
					<xsl:with-param name="partyName" select="../../matched.party.block/matched.party.name.block/matched.party.name" />
					<xsl:with-param name="attorneyName" select="../../matched.attorney.block/matched.attorney.name.block/matched.attorney.name" />
					<xsl:with-param name="attorneyFirmName" select="../../matched.attorney.block/matched.firm.block/matched.firm.name.block/matched.firm.name" />
				</xsl:call-template>
			</xsl:for-each>

			<xsl:for-each select="n-docbody/r/party.block/party.type.block">
				<xsl:call-template name="PlaintiffDefendantInformation">
					<xsl:with-param name="isPlaintiff" select="true()" />
					<xsl:with-param name="partyCriteria" select="contains(normalize-space(party.type), 'PLAINTIFF') or normalize-space(party.type)='PLAINTIFF/PETITIONER'" />
					<xsl:with-param name="partyName" select="../party.name.block/party.name" />
				</xsl:call-template>
			</xsl:for-each>

			<xsl:for-each select="n-docbody/r/party.block/plaintiff.party">
				<xsl:call-template name="PlaintiffDefendantInformation">
					<xsl:with-param name="isPlaintiff" select="true()" />
					<xsl:with-param name="partyCriteria" select="true()" />
					<xsl:with-param name="partyName" select="party.name.block/party.name" />
					<xsl:with-param name="attorneyName" select="party.attorney.block/attorney.name" />
					<xsl:with-param name="attorneyFirmName" select="party.attorney.block/firm.name.block/firm.name" />
				</xsl:call-template>
			</xsl:for-each>

			<xsl:for-each select="n-docbody/r/matched.block/matched.party.block/matched.plaintiff.party.block">
				<xsl:call-template name="PlaintiffDefendantInformation">
					<xsl:with-param name="isPlaintiff" select="true()" />
					<xsl:with-param name="partyCriteria" select="true()" />
					<xsl:with-param name="partyName" select="matched.party.name.block/matched.party.name" />
					<xsl:with-param name="attorneyName" select="../../matched.attorney.block/matched.plaintiff.attorney.block/matched.attorney.name.block/matched.attorney.name" />
					<xsl:with-param name="attorneyFirmName" select="../../matched.attorney.block/matched.plaintiff.attorney.block/matched.firm.block/matched.firm.name.block/matched.firm.name" />
				</xsl:call-template>
			</xsl:for-each>

			<xsl:for-each select="n-docbody/r/matched.block/matched.party.block/matched.defendant.party.block">
				<xsl:call-template name="PlaintiffDefendantInformation">
					<xsl:with-param name="isPlaintiff" select="false()" />
					<xsl:with-param name="partyCriteria" select="true()" />
					<xsl:with-param name="partyName" select="matched.party.name.block/matched.party.name" />
					<xsl:with-param name="attorneyName" select="../../matched.attorney.block/matched.defendant.attorney.block/matched.attorney.name.block/matched.attorney.name" />
					<xsl:with-param name="attorneyFirmName" select="../../matched.attorney.block/matched.defendant.attorney.block/matched.firm.block/matched.firm.name.block/matched.firm.name" />
				</xsl:call-template>
			</xsl:for-each>

			<xsl:for-each select="n-docbody/r/matched.block/matched.party.block/matched.party.type.block">
				<xsl:call-template name="PlaintiffDefendantInformation">
					<xsl:with-param name="isPlaintiff" select="false()" />
					<xsl:with-param name="partyCriteria" select="contains(normalize-space(matched.party.type), 'DEFENDANT')" />
					<xsl:with-param name="partyName" select="../../matched.party.block/matched.party.name.block/matched.party.name" />
					<xsl:with-param name="attorneyName" select="../../matched.attorney.block/matched.attorney.name.block/matched.attorney.name" />
					<xsl:with-param name="attorneyFirmName" select="../../matched.attorney.block/matched.firm.block/matched.firm.name.block/matched.firm.name" />
				</xsl:call-template>
			</xsl:for-each>

			<xsl:for-each select="n-docbody/r/party.block/party.type.block">
				<xsl:call-template name="PlaintiffDefendantInformation">
					<xsl:with-param name="isPlaintiff" select="false()" />
					<xsl:with-param name="partyCriteria" select="contains(normalize-space(party.type), 'DEFENDANT') or contains(normalize-space(party.type), 'DEFENDANT/RESPONDENT')" />
					<xsl:with-param name="partyName" select="../party.name.block/party.name" />
				</xsl:call-template>
			</xsl:for-each>

			<xsl:for-each select="n-docbody/r/party.block/defendant.party">
				<xsl:call-template name="PlaintiffDefendantInformation">
					<xsl:with-param name="isPlaintiff" select="false()" />
					<xsl:with-param name="partyCriteria" select="true()" />
					<xsl:with-param name="partyName" select="party.name.block/party.name" />
					<xsl:with-param name="attorneyName" select="party.attorney.block/attorney.name" />
					<xsl:with-param name="attorneyFirmName" select="party.attorney.block/firm.name.block/firm.name" />
				</xsl:call-template>
			</xsl:for-each>
			
			<xsl:for-each select="n-docbody/r/attorney.block">
				<xsl:if test="attorney.name.block/attorney.name!='null' 
					and normalize-space(attorney.name.block/attorney.name)!=''">
					<xsl:call-template name="DocketInfoDiv">
						<xsl:with-param name="label">
							<xsl:value-of select="attorney.name.block/label" />
						</xsl:with-param>
						<xsl:with-param name="text">
							<xsl:value-of select="attorney.name.block/attorney.name"/>
						</xsl:with-param>
					</xsl:call-template>

					<xsl:if test="attorney.status.block/attorney.status!='null' 
						and normalize-space(attorney.status.block/attorney.status)!=''">
						<xsl:call-template name="DocketInfoDiv">
							<xsl:with-param name="label">
								<xsl:value-of select="attorney.status.block/label" />
							</xsl:with-param>
							<xsl:with-param name="text">
								<xsl:value-of select="attorney.status.block/attorney.status"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
			<br/>

			<xsl:apply-templates select="n-docbody/r/synopsis.block"/>
			<xsl:if test="not($IsMobile)">
				<xsl:variable name="index">
					<xsl:choose>
						<xsl:when test="send.runner.link">
							<xsl:value-of select="send.runner.link/@indexvalue" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="0"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="n-docbody/r/complaint.block/image.block | n-docbody/r/wcn.complaint.block/image.block">
						<xsl:apply-templates select="n-docbody/r/wcn.complaint.block/image.block | n-docbody/r/complaint.block/image.block"/>
					</xsl:when>
					<xsl:when test="$index and $HasDocketOrdersAccess">
						<xsl:call-template name="RenderSendRunnerLink">
							<xsl:with-param name="index" select="$index" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text><![CDATA[ ]]></xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>

			<xsl:call-template name="newline" />

			<xsl:apply-templates select="document-data/alertNote" />
			
		</div>
	</xsl:template>

	<xsl:template name="PlaintiffDefendantInformation">
		<xsl:param name="isPlaintiff" />
		<xsl:param name="partyCriteria" />
		<xsl:param name="partyName" />
		<xsl:param name="attorneyName" />
		<xsl:param name="attorneyFirmName" />

		<xsl:variable name="partyTitle">
			<xsl:choose>
				<xsl:when test='$isPlaintiff'>
					<xsl:text>&docketsPlaintiffTitle;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&docketsDefendantTitle;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="attorneyTitle">
			<xsl:choose>
				<xsl:when test='$isPlaintiff'>
					<xsl:text>&docketsPlaintiffAttorneys;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&docketsDefendantAttorneys;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:if test="$partyCriteria">
			<xsl:if test="string-length($partyName) &gt; 0">
				<xsl:call-template name="DocketInfoDiv">
					<xsl:with-param name="label">
						<xsl:value-of select="$partyTitle"/>
					</xsl:with-param>
					<xsl:with-param name="text">
						<xsl:value-of select="$partyName"/>
					</xsl:with-param>
				</xsl:call-template>

				<xsl:if test="string-length($attorneyName) &gt; 0">
					<xsl:call-template name="DocketInfoDiv">
						<xsl:with-param name="label">
							<xsl:value-of select="$attorneyTitle"/>
						</xsl:with-param>
						<xsl:with-param name="text">
							<xsl:value-of select="$attorneyName"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>

				<xsl:if test="string-length($attorneyFirmName) &gt; 0">
					<xsl:call-template name="DocketInfoDiv">
						<xsl:with-param name="label">
							<xsl:text>&docketsFirmLabel;</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="text">
							<xsl:value-of select="$attorneyFirmName"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="CustomLogo">
		<xsl:if test="string-length($LogoUrl) &gt; 0">
			<div id="&searchAlertLogoId;" class="&searchAlertLogoClass;">
				<img src="{$LogoUrl}"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="TitleLink" >
		<xsl:variable name="docFamilyGuid" select="/Document/n-metadata/metadata.block/md.descriptions/md.doc.family.uuid/text()" />
		<h3>
			<a>
				<xsl:attribute name="href">
					<xsl:choose>
						<xsl:when test="string-length($SponsorId) &gt; 0">
							<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBasicByLookup', concat('docFamilyGuid=', $docFamilyGuid), concat('pubNum=', &cwPubNumb;), 'maxResults=1', concat('sp=', $SponsorId), concat('hash=', $SponsorHash), 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentItem;', 'contextData=(sc.CourtWire)')"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentByLookup', concat('docFamilyGuid=', $docFamilyGuid), concat('pubNum=', &cwPubNumb;), 'maxResults=1', 'viewType=FullText', 'originationContext=&docDisplayOriginationContext;', $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentItem;', 'contextData=(sc.CourtWire)')"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="string-length($AlertHistoryGuid) &gt; 0 and string-length($AlertHistoryRank) &gt; 0">
						<xsl:variable name="navigationPath">
							<xsl:call-template name="url-encode">
								<xsl:with-param name="str">
									<xsl:value-of select="concat('Alert/v1/listNavigation/CourtWire/', $AlertHistoryGuid, '?alertGuid=', $AlertGuid, '&amp;rank=', $AlertHistoryRank)"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:variable>
						<xsl:value-of select="concat('&amp;listSource=Alert&amp;list=CourtWire&amp;rank=', $AlertHistoryRank, '&amp;navigationPath=', $navigationPath)"/>
					</xsl:if>
					<xsl:if test="string-length($AlertGuid) &gt; 0">
						<xsl:value-of select="concat('&amp;alertGuid=', $AlertGuid)"/>
					</xsl:if>
				</xsl:attribute>
				<xsl:value-of select="/Document/n-metadata/metadata.block/md.descriptions/md.title"/>
			</a>
		</h3>
	</xsl:template>

	<xsl:template name="DocketsDate">
		<xsl:param name="date" />
		<xsl:choose>
			<xsl:when test="$date/N-HIT">
				<xsl:call-template name="nHit">
					<xsl:with-param name="contents">
						<xsl:call-template name="DocketsDate">
							<xsl:with-param name="date" select="$date/N-HIT" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$date/N-LOCATE">
				<xsl:call-template name="nLocate">
					<xsl:with-param name="contents">
						<xsl:call-template name="DocketsDate">
							<xsl:with-param name="date" select="$date/N-LOCATE" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$date/N-WITHIN">
				<xsl:call-template name="nWithin">
					<xsl:with-param name="contents">
						<xsl:call-template name="DocketsDate">
							<xsl:with-param name="date" select="$date/N-WITHIN" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name ="year" select="substring($date,1,4)"/>
				<xsl:variable name ="month" select="substring($date,5,2)"/>
				<xsl:variable name ="day" select="substring($date,7,2)"/>
				<xsl:value-of select="$month"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$day"/>
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$year"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="DocketInfoDiv">
		<xsl:param name="label" />
		<xsl:param name="text" />
		<div>
			<strong>
				<xsl:copy-of select="$label"/>&nbsp;
			</strong>
			<xsl:copy-of select="$text"/>
		</div>
	</xsl:template>

	<xsl:template match="synopsis.block">
		<span id="&docketsSynopsisInformationId;">
			<strong>
				<xsl:text>&docketsCaseSynopsis;</xsl:text>
			</strong>
		</span>
		<br/>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template name="Source">
		<xsl:variable name="court">
			<xsl:value-of select="/Document/n-docbody/r/case.information.block/court.block/court.linking | /Document/n-docbody/r/court.block/court | /Document/n-docbody/r/court.block/court.linking | /Document/n-docbody/r/court.info.block/court.block/court.linking"/>
		</xsl:variable>
		<xsl:variable name="county">
			<xsl:value-of select="/Document/n-docbody/r/case.information.block/court.block/filing.county"/>
		</xsl:variable>
		<xsl:copy-of select="$court"/>
		<xsl:if test="string-length($county) &gt; 0">
			<xsl:text> (</xsl:text>
			<xsl:copy-of select="$county"/>
			<xsl:text>)</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="filing.date.block">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:value-of select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="filing.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.type.block">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:value-of select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:value-of select="case.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.type">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:text>&docketsCaseType;</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:value-of select="self::node()" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.subtype.block">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:value-of select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:value-of select="case.subtype" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.subtype">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:text>&docketsCaseSubtype;</xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:value-of select="self::node()" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="nature.of.suit.block">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:value-of select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:value-of select="nature.of.suit" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="key.nature.of.suit.block">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:apply-templates select="knos.level1.block/label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="knos.level1.block/knos.level1" />
				<xsl:if test="knos.level2.block">
					<xsl:text>&#59;&nbsp;</xsl:text>
					<xsl:apply-templates select="knos.level2.block/knos.level2" />
				</xsl:if>
				<xsl:if test="knos.level3.block">
					<xsl:text>&#59;&nbsp;</xsl:text>
					<xsl:apply-templates select="knos.level3.block/knos.level3" />
				</xsl:if>
				<xsl:if test="knos.code">
					<xsl:text>&nbsp;&#40;</xsl:text>
					<xsl:apply-templates select="knos.code" />
					<xsl:text>&#41;</xsl:text>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="facts.block">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:value-of select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:value-of select="facts" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="damages.block">
		<xsl:call-template name="DocketInfoDiv">
			<xsl:with-param name="label">
				<xsl:value-of select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:value-of select="damages" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="wcn.complaint.block/image.block">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&imageBlockClass;</xsl:text>
				<xsl:if test="$documentType = 'Analytical - EForms'">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:text>&centerClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates select="image.link">
				<xsl:with-param name="text">
					<xsl:text>&docketsComplaintLink;</xsl:text>
				</xsl:with-param>
				<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
				<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
				<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template match="complaint.block/image.block">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&imageBlockClass;</xsl:text>
				<xsl:if test="$documentType = 'Analytical - EForms'">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:text>&centerClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates select="image.link">
				<xsl:with-param name="text">
					<xsl:text>&docketsComplaintLink;</xsl:text>
				</xsl:with-param>
				<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
				<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
				<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template name="RenderSendRunnerLink">
		<xsl:param name="index" />
		<xsl:variable name="sendRunnerHref">
			<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentOrderReview', concat('orderedIndex=', $index), concat('documentGuid=', $Guid), concat('isCourtWireDocument=', 'true'), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), 'originationContext=&docDisplayOriginationContext;', '&transitionTypeParamName;=&transitionTypeDocument;', 'contextData=(sc.CourtWire)', $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
			<xsl:if test="string-length($AlertGuid) &gt; 0">
				<xsl:value-of select="concat('&amp;alertGuid=', $AlertGuid)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:if test="string-length($sendRunnerHref) &gt; 0">
			<a id="&docketSendRunnerLinkId;{$index}" class="&docketProceedingsButtonClass; &docketSendRunnerLinkClass;" href="{$sendRunnerHref}">
				<i></i>
				<xsl:text>&docketSendRunnerText;</xsl:text>
			</a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="newline">
		<div>&nbsp;</div>
	</xsl:template>

	<xsl:template match="image.link">
		<xsl:param name="text"/>
		<xsl:param name="className" />
		<xsl:param name="displayIcon"/>
		<xsl:param name="displayIconClassName"/>
		<xsl:param name="displayIconAltText"/>
		<xsl:variable name="countImage" select="count(preceding::image.link) + 1"/>
		<xsl:variable name="guid">
			<xsl:choose>
				<xsl:when test="string-length(@target) &gt; 30">
					<xsl:value-of select="@target" />
				</xsl:when>
				<xsl:when test="string-length(@tuuid) &gt; 30">
					<xsl:value-of select="@tuuid" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>NotValidImage</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($guid) &gt; 0">
			<xsl:variable name ="displayIconPath">
				<xsl:choose>
					<xsl:when test="string-length($ImagesPermaLink) &gt; 0">
						<xsl:variable name="fileName">
							<xsl:call-template name="getPureFileName">
								<xsl:with-param name="path" select="$displayIcon"/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:value-of select="concat($ImagesPermaLink, '/permalink/', $fileName)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$displayIcon"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:choose>
				<xsl:when test="/*/ImageMetadata/n-metadata[md.blobref.guid = $guid]">
					<xsl:apply-templates select="/*/ImageMetadata/n-metadata[md.blobref.guid = $guid]" mode="MakeImageLink">
						<xsl:with-param name="guid" select="$guid"/>
						<xsl:with-param name="className" select="$className" />
						<xsl:with-param name="imageNumInDoc" select="$countImage"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="/*/ImageMetadata/n-metadata/@guid = $guid">
				<xsl:variable name="renderType" select="/*/ImageMetadata/n-metadata/md.image.renderType"/>
				<xsl:choose>
					<xsl:when test="$renderType = 'BlobError'">
						<xsl:call-template name="createPreDocketComplaintLink">
							<xsl:with-param name="guid" select="$guid"/>
							<xsl:with-param name="text" select="$text"/>
							<xsl:with-param name="className" select="$className" />
							<xsl:with-param name="displayIcon" select="$displayIconPath"/>
							<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
							<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
							<xsl:with-param name="countImage" select="$countImage"/>
							<xsl:with-param name="targetType" select="@ttype"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="/*/ImageMetadata/n-metadata[@guid = $guid]" mode="MakeImageLink">
							<xsl:with-param name="guid" select="$guid"/>
							<xsl:with-param name="text" select="$text"/>
							<xsl:with-param name="className" select="$className" />
							<xsl:with-param name="displayIcon" select="$displayIconPath"/>
							<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
							<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
							<xsl:with-param name="imageNumInDoc" select="$countImage"/>
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createPreDocketComplaintLink">
						<xsl:with-param name="guid" select="$guid"/>
						<xsl:with-param name="text" select="$text"/>
						<xsl:with-param name="className" select="$className" />
						<xsl:with-param name="displayIcon" select="$displayIconPath"/>
						<xsl:with-param name="displayIconClassName" select="$displayIconClassName"/>
						<xsl:with-param name="displayIconAltText" select="$displayIconAltText"/>
						<xsl:with-param name="countImage" select="$countImage"/>
						<xsl:with-param name="targetType" select="@ttype"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="alertNote">
		<div class="&alertNoteClass;">
			<h3>&alertNotes;</h3>
			<xsl:apply-templates />
		</div>
		
		<xsl:call-template name="newline" />
	</xsl:template>
	
	<xsl:template name="createBlobLink">
		<xsl:param name="guid" />
		<xsl:param name="highResolution" />
		<xsl:param name="targetType" />
		<xsl:param name="mimeType" />
		<xsl:param name="maxHeight" />
		<xsl:param name="forImgTag" />
		<xsl:param name="originationContext" />
		<xsl:param name="prettyName" />
		<xsl:param name="hash" />

		<xsl:variable name="extension">
			<xsl:text>pdf</xsl:text>
		</xsl:variable>

		<!-- Need to skip BrowserHawk validation for firms: Neil Gerber, Quinn Emmanuel - see bug 710656 -->
		<xsl:variable name ="bhskip">
			<xsl:if test="string-length($SponsorId) &gt; 0">
				<xsl:value-of select="1"/>
			</xsl:if>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="string-length($prettyName) &gt; 0">
				<xsl:choose>
					<xsl:when test="$UseBlobRoyaltyId">
						<xsl:variable name="originatingDocGuid" select="$Guid" />
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), concat('bhskip=', $bhskip))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), concat('bhskip=', $bhskip))"/>
							</xsl:otherwise>
						</xsl:choose>								
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), concat('bhskip=', $bhskip))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlob', concat('imageGuid=', $guid), concat('imageFileName=', $prettyName), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), concat('bhskip=', $bhskip))"/>
							</xsl:otherwise>
						</xsl:choose>								
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$UseBlobRoyaltyId">
						<xsl:variable name="originatingDocGuid" select="$Guid" />
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), concat('bhskip=', $bhskip))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originatingDocGuid=', $originatingDocGuid), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), concat('bhskip=', $bhskip))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$UseRelativePathForImages">
								<xsl:value-of select="UrlBuilder:CreateRelativePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), concat('bhskip=', $bhskip))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.DocumentBlobV1', concat('imageGuid=', $guid), concat('extension=', $extension), concat('maxHeight=', $maxHeight), concat('targetType=', $targetType), concat('originationContext=', $originationContext), $specialVersionParamVariable, $specialRequestSourceParamVariable, '&transitionTypeParamName;=&transitionTypeDocumentImage;', concat('&uniqueIdParamName;=', $UniqueIdForBlobs), concat('&hashParamName;=', $hash), concat('sp=', $SponsorId), concat('hash=', $SponsorHash), concat('bhskip=', $bhskip))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:if test="string-length($AlertGuid) &gt; 0">
			<xsl:value-of select="concat('&amp;alertGuid=', $AlertGuid)"/>
		</xsl:if>
	</xsl:template>
  
	<xsl:template name="getPureFileName">
		<xsl:param name="path" />
		<xsl:choose>
			<xsl:when test="contains($path,'/')">
				<xsl:variable name="temp">
					<xsl:value-of select="substring-after($path,'/')" />
				</xsl:variable>
				<xsl:call-template name="getPureFileName">
					<xsl:with-param name="path" select="$temp" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$path"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>