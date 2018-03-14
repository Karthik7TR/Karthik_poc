<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2009: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet [
	<!ENTITY link "link">
	<!ENTITY document "document">
	<!ENTITY relatedInfo "relatedinformation">
	<!ENTITY flag "flag">
	<!ENTITY blob "blob">
	<!ENTITY notesOfDecision "Annotations.NOD">
	<!ENTITY originationContextUrlParam "originationContext">
	<!ENTITY fullTextPath "/View/FullText.html">
	<!ENTITY citingReferencesHtml "/citingreferences.html">
	<!ENTITY docFamilyGuid "familyGuid">
	<!ENTITY docGuid "docGuid">
	<!ENTITY findType "findType">
	<!ENTITY serialNumber "serNum">
	<!ENTITY pubNumber "pubNum">
	<!ENTITY cite "cite">
	<!ENTITY pinPoint "pinPoint">
	<!ENTITY originatingDoc "originatingDoc">
	<!ENTITY fullText "fullText">
	<!ENTITY targetType "targetType">
	<!ENTITY maxHeight "maxHeight">
	<!ENTITY mimeType "mimeType">
	<!ENTITY highResolutionSegment "highResolution">
	<!ENTITY docHeadnoteLinkSegment "docheadnotelink">
	<!ENTITY headnoteIdUrlParam "headnoteId">
	<!ENTITY searchTypeValueSearch "Search">
	<!ENTITY listSourceUrlParam "ListSource">
	<!ENTITY navigationPathUrlParam "NavigationPath">
	<!ENTITY listNameUrlParam "List">
	<!ENTITY rankInListUrlParam "Rank">
	<!ENTITY sourceSerialUrlParam "sourceSerial">
	<!ENTITY destinationSerialUrlParam "destinationSerial">
	<!ENTITY pinpointIdPrefix "co_pp_">
	<!ENTITY database "database">
	<!ENTITY signon "signon">
	<!ENTITY refType "refType">
	<!ENTITY customDigest "customDigest">
	<!ENTITY keyNumber "keyNumber">
	<!ENTITY keytext "keytext">

	<!ENTITY xslParamLinkHost "linkHost">
	<!ENTITY xslParamDocGuid "docGuid">
	<!ENTITY xslParamDocFamilyGuid "docFamilyGuid">
	<!ENTITY xslParamBlobGuid "blobGuid">
	<!ENTITY xslParamOriginationContext "originationContext">
	<!ENTITY xslParamListSource "listSource">
	<!ENTITY xslParamNavigationPath "navigationPath">
	<!ENTITY xslParamListName "list">
	<!ENTITY xslParamRank "rank">
	<!ENTITY xslParamSourceSerial "sourceSerial">
	<!ENTITY xslParamDestinationSerial "destinationSerial">
	<!ENTITY xslParamHeadnoteId "headnoteId">
	<!ENTITY xslParamHighResolution "highResolution">
	<!ENTITY xslParamTargetType "targetType">
	<!ENTITY xslParamMimeType "mimeType">
	<!ENTITY xslParamMaxHeight "maxHeight">
	<!ENTITY xslParamLinkElement "linkElement">
	<!ENTITY xslParamOriginatingDoc "originatingDoc">
	<!ENTITY xslParamRefType "refType">
	<!ENTITY xslParamPubNumber "pubNumber">
	<!ENTITY xslParamSerialNumber "serialNumber">
	<!ENTITY xslParamNormalizedCite "normalizedCite">
	<!ENTITY xslParamPinpointPage "pinpointPage">
	<!ENTITY xslParamText "text">
	<!ENTITY xslParamKeyNumber "keyNumber">
	<!ENTITY xslParamAddAmpersand "addAmpersand">
]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Global variables for Cite.Query parsing (located at the top for ease of updating) -->
	<xsl:variable name="missingRefTypes" select="';AB;AD;AE;AG;AM;BD;CD;CH;CL;CO;CR;CV;CX;C1;DD;DF;DI;DJ;DK;DM;DN;DO;DQ;DS;DU;DV;D1;EA;EC;EG;EI;EM;EV;FE;FM;FN;FT;FW;GB;GC;GD;GG;GK;GL;GS;G1;HD;HI;IA;IE;IJ;IL;IM;IN;IP;IQ;JI;JR;KE;KP;KS;LD;LE;LH;LJ;LL;LM;LN;LV;MD;MJ;MP;MR;MU;M1;NE;NF;NG;NH;NJ;NO;NS;NT;NV;OP;PE;PF;PH;PI;PJ;PL;PR;PT;PW;QH;QT;RA;RR;RU;RX;R9;SD;SE;SG;SS;SU;SV;TB;TC;TD;TF;TJ;TK;TL;TM;TQ;UA;UC;UD;UE;UJ;UL;UM;UO;UT;UX;UY;UZ;U1;VO;VS;VV;WD;WK;XX;ZA;ZZ;Z9;'" />
	<xsl:variable name="approvedRefTypes" select="';AA;AN;BR;CA;CC;DA;DE;EW;GA;HN;JV;KD;KW;LQ;LR;NA;NR;PA;PD;PS;RB;RC;RE;RM;RP;SA;SP;ST;TG;TR;TS;VB;VE;VP;VQ;'" />
	<xsl:variable name="dbOverrideRefTypes" select="';CL;CM;CN;CP;DA;DN;ES;EW;FS;GA;GK;HI;LB;LD;LF;LK;LN;LQ;MC;NE;NF;NG;NH;QH;RQ;RW;SB;SG;SP;TC;TG;TM;TQ;UA;'" />

	<!--
	Externally called Persistent and Transient Link Builder Templates
	-->

	<!-- Create persistent Document Service FullText URIs -->
	<xsl:template name="createDocumentRequestPersistentUrl">
		<xsl:param name="host" /> <!-- should be: &xslParamLinkHost; -->
		<xsl:param name="&xslParamDocGuid;" />
		<xsl:param name="&xslParamDocFamilyGuid;"/>
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:if test="string-length($&xslParamDocGuid;) &gt; 0">
			<xsl:value-of select="$host"/>
			<xsl:text>/&document;/</xsl:text>
			<xsl:value-of select="$&xslParamDocGuid;"/>
			<xsl:text>&fullTextPath;</xsl:text>
			<xsl:variable name="queryString">
				<xsl:if test="string-length($&xslParamDocFamilyGuid;) &gt; 0">
					<xsl:text>&docFamilyGuid;=</xsl:text>
					<xsl:value-of select="$&xslParamDocFamilyGuid;"/>
				</xsl:if>
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamAddAmpersand;" select="string-length($&xslParamDocFamilyGuid;) &gt; 0" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($queryString) &gt; 0 and not(contains($queryString, '?'))">
				<xsl:text>?</xsl:text>
			</xsl:if>
			<xsl:value-of select="$queryString"/>
		</xsl:if>
	</xsl:template>

	<!-- Make transient Document Service FullText URIs from any list (e.g. Search Results) -->
	<xsl:template name="createListNavigableDocumentRequestUrl">
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamDocGuid;" />
		<xsl:param name="&xslParamListSource;"/>
		<xsl:param name="&xslParamNavigationPath;" />
		<xsl:param name="&xslParamListName;" />
		<xsl:param name="&xslParamRank;" />
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:variable name="urlEncodedNavigationPath">
			<xsl:call-template name="url-encode">
				<xsl:with-param name="str" select="$&xslParamNavigationPath;" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Only construct this is ALL pieces of navigation information are available -->
		<xsl:if test="string-length($&xslParamDocGuid;) &gt; 0 and string-length($&xslParamListSource;) &gt; 0 and string-length($urlEncodedNavigationPath) &gt; 0 and string-length($&xslParamListName;) &gt; 0 and string-length($&xslParamRank;) &gt; 0">
			<xsl:value-of select="$&xslParamLinkHost;"/>
			<xsl:text>/&document;/</xsl:text>
			<xsl:value-of select="$&xslParamDocGuid;"/>
			<xsl:text>&fullTextPath;</xsl:text>
			<xsl:variable name="queryString">
				<xsl:text>&listSourceUrlParam;=</xsl:text>
				<xsl:value-of select="$&xslParamListSource;"/>
				<xsl:text>&amp;</xsl:text>
				<xsl:text>&navigationPathUrlParam;=</xsl:text>
				<xsl:value-of select="$urlEncodedNavigationPath"/>
				<xsl:text>&amp;</xsl:text>
				<xsl:text>&listNameUrlParam;=</xsl:text>
				<xsl:value-of select="$&xslParamListName;"/>
				<xsl:text>&amp;</xsl:text>
				<xsl:text>&rankInListUrlParam;=</xsl:text>
				<xsl:value-of select="$&xslParamRank;"/>
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamAddAmpersand;" select="true()" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($queryString) &gt; 0 and not(contains($queryString, '?'))">
				<xsl:text>?</xsl:text>
			</xsl:if>
			<xsl:value-of select="$queryString"/>
		</xsl:if>
	</xsl:template>

	<!-- Create persistent URIs for Pinpoint links to cited documents -->
	<xsl:template name="createCitedDocumentRequestPersistentUrl">
		<xsl:param name="host" />
		<!-- should be: &xslParamLinkHost; -->
		<xsl:param name="&xslParamDocGuid;" />
		<xsl:param name="&xslParamDocFamilyGuid;"/>
		<xsl:param name="&xslParamOriginationContext;" />
		<xsl:param name="&xslParamSourceSerial;"/>
		<xsl:param name="&xslParamDestinationSerial;"/>

		<xsl:if test="string-length($&xslParamDocGuid;) &gt; 0">
			<xsl:value-of select="$host"/>
			<xsl:text>/&document;/</xsl:text>
			<xsl:value-of select="$&xslParamDocGuid;"/>
			<xsl:text>&fullTextPath;</xsl:text>
			<xsl:variable name="queryString">
				<xsl:variable name="optionalParams">
					<xsl:if test="string-length($&xslParamDocFamilyGuid;) &gt; 0">
						<xsl:text>&docFamilyGuid;=</xsl:text>
						<xsl:value-of select="$&xslParamDocFamilyGuid;"/>
					</xsl:if>
					<xsl:if test="string-length($&xslParamSourceSerial;) &gt; 0">
						<xsl:if test="string-length($&xslParamDocFamilyGuid;) &gt; 0">
							<xsl:text>&amp;</xsl:text>
						</xsl:if>
						<xsl:text>&sourceSerialUrlParam;=</xsl:text>
						<xsl:value-of select="$&xslParamSourceSerial;"/>
					</xsl:if>
					<xsl:if test="string-length($&xslParamDestinationSerial;) &gt; 0">
						<xsl:if test="string-length($&xslParamDocFamilyGuid;) &gt; 0 or string-length($&xslParamSourceSerial;) &gt; 0">
							<xsl:text>&amp;</xsl:text>
						</xsl:if>
						<xsl:text>&destinationSerialUrlParam;=</xsl:text>
						<xsl:value-of select="$&xslParamDestinationSerial;"/>
					</xsl:if>
				</xsl:variable>
				<xsl:value-of select="$optionalParams" />
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamAddAmpersand;" select="string-length($optionalParams) &gt; 0" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($queryString) &gt; 0 and not(contains($queryString, '?'))">
				<xsl:text>?</xsl:text>
			</xsl:if>
			<xsl:value-of select="$queryString"/>
		</xsl:if>
	</xsl:template>

	<!-- Create persistent URIs for Headnote links to cited case documents -->
	<xsl:template name="createHeadnotesCitedCaseRefPersistentUrl">
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="guid" />
		<!-- should be: &xslParamDocGuid; -->
		<xsl:param name="&xslParamHeadnoteId;" />
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:if test="string-length($guid) &gt; 0 and string-length($&xslParamHeadnoteId;) &gt; 0">
			<xsl:value-of select="$&xslParamLinkHost;"/>
			<xsl:text>/&link;/&relatedInfo;/&docHeadnoteLinkSegment;</xsl:text>
			<xsl:variable name="queryString">
				<xsl:text>&docGuid;=</xsl:text>
				<xsl:value-of select="$guid"/>
				<xsl:text>&amp;</xsl:text>
				<xsl:text>&headnoteIdUrlParam;=</xsl:text>
				<xsl:value-of select="$&xslParamHeadnoteId;"/>
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamAddAmpersand;" select="true()" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($queryString) &gt; 0 and not(contains($queryString, '?'))">
				<xsl:text>?</xsl:text>
			</xsl:if>
			<xsl:value-of select="$queryString"/>
		</xsl:if>
	</xsl:template>

	<!-- Create persistent URIs for links to images in a document -->
	<xsl:template name="createBlobPersistentUrl">
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="guid" />
		<!-- should be: &xslParamBlobGuid; -->
		<xsl:param name="&xslParamHighResolution;" select="false()" />
		<xsl:param name="&xslParamTargetType;" />
		<xsl:param name="&xslParamMimeType;" />
		<xsl:param name="&xslParamMaxHeight;" />
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:if test="string-length($guid) &gt; 0 and string-length($&xslParamMimeType;) &gt; 0">
			<xsl:value-of select="$&xslParamLinkHost;"/>
			<xsl:text>/&link;/&document;/&blob;/</xsl:text>
			<xsl:if test="$&xslParamHighResolution;">
				<xsl:text>&highResolutionSegment;/</xsl:text>
			</xsl:if>
			<xsl:value-of select="$guid"/>
			<xsl:variable name="extensionAndQueryString">
				<xsl:choose>
					<xsl:when test="$&xslParamMimeType; = 'application/pdf'">
						<xsl:text>.pdf</xsl:text>
						<xsl:if test="string-length($&xslParamTargetType;) &gt; 0">
							<xsl:text>?&targetType;=</xsl:text>
							<xsl:value-of select="$&xslParamTargetType;"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="$&xslParamMimeType; = 'image/jpeg'">
						<xsl:text>.jpg</xsl:text>
						<xsl:if test="string-length($&xslParamMaxHeight;) &gt; 0">
							<xsl:text>?&maxHeight;=</xsl:text>
							<xsl:value-of select="$&xslParamMaxHeight;"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="$&xslParamMimeType; = 'application/x-wgsl'">
						<xsl:text>.amz</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="$extensionAndQueryString" />
			<xsl:call-template name="addOriginationContextQueryParam">
				<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
				<xsl:with-param name="&xslParamAddAmpersand;" select="contains($extensionAndQueryString, '?')" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Create a persistent URI for the KeyCite flag link -->
	<xsl:template name="createKeyCiteFlagPersistentUrl">
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamDocGuid;" />
		<xsl:param name="&xslParamDocFamilyGuid;" />
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:if test="string-length($&xslParamDocGuid;) &gt; 0">
			<xsl:variable name="queryString">
				<xsl:variable name="optionalParams">
					<xsl:if test="string-length($&xslParamDocFamilyGuid;) &gt; 0">
						<xsl:text>&docFamilyGuid;=</xsl:text>
						<xsl:value-of select="$&xslParamDocFamilyGuid;"/>
					</xsl:if>
					<xsl:if test="string-length($&xslParamDocFamilyGuid;) &gt; 0 and string-length($&xslParamDocGuid;) &gt; 0">
						<xsl:text>&amp;</xsl:text>
					</xsl:if>
					<xsl:if test="string-length($&xslParamDocGuid;) &gt; 0">
						<xsl:text>&docGuid;=</xsl:text>
						<xsl:value-of select="$&xslParamDocGuid;"/>
					</xsl:if>
				</xsl:variable>
				<xsl:value-of select="$optionalParams" />
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamAddAmpersand;" select="string-length($optionalParams) &gt; 0" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:value-of select="$&xslParamLinkHost;"/>
			<xsl:text>/&link;/&relatedInfo;/&flag;</xsl:text>
			<xsl:if test="string-length($queryString) &gt; 0 and not(contains($queryString, '?'))">
				<xsl:text>?</xsl:text>
			</xsl:if>
			<xsl:value-of select="$queryString"/>
		</xsl:if>
	</xsl:template>

	<!-- Create a persistent URI for the NotesOfDecisions link to Related Info -->
	<xsl:template name="createNotesOfDecisionsPersistentUrl">
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamDocGuid;" />
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:if test="string-length($&xslParamDocGuid;) &gt; 0">
			<xsl:value-of select="$&xslParamLinkHost;"/>
			<xsl:text>/&link;/&relatedInfo;/&notesOfDecision;</xsl:text>
			<xsl:variable name="queryString">
				<xsl:text>&docGuid;=</xsl:text>
				<xsl:value-of select="$&xslParamDocGuid;"/>
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamAddAmpersand;" select="true()" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($queryString) &gt; 0 and not(contains($queryString, '?'))">
				<xsl:text>?</xsl:text>
			</xsl:if>
			<xsl:value-of select="$queryString"/>
		</xsl:if>
	</xsl:template>

	<!-- Create persistent URIs for Cite.Query links.
	These links are resolved at a later time through the link resolver service. There are two formats of the Cite.Query element, both of which are handled here. -->
	<xsl:template name="createCiteQueryPersistentUrl">
		<xsl:param name="&xslParamLinkElement;" />
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamOriginatingDoc;" />
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:choose>
			<xsl:when test="$&xslParamLinkElement;[self::cite.query] and $&xslParamLinkElement;/@target">
				<xsl:call-template name="parseCentRCiteQuery">
					<xsl:with-param name="&xslParamLinkElement;" select="$&xslParamLinkElement;" />
					<xsl:with-param name="&xslParamLinkHost;" select="$&xslParamLinkHost;" />
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamOriginatingDoc;" select="$&xslParamOriginatingDoc;" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="($&xslParamLinkElement;[self::cite.query] or $&xslParamLinkElement;[self::md.toggle.link]) and $&xslParamLinkElement;/@w-ref-type">
				<xsl:call-template name="parseStandardCiteQuery">
					<xsl:with-param name="&xslParamLinkElement;" select="$&xslParamLinkElement;" />
					<xsl:with-param name="&xslParamLinkHost;" select="$&xslParamLinkHost;" />
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamOriginatingDoc;" select="$&xslParamOriginatingDoc;" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>


	<!--
	Internal content-specific sub-templates
	-->

	<!-- Parse the standard format of Cite.Query based links -->
	<xsl:template name="parseStandardCiteQuery">
		<xsl:param name="&xslParamLinkElement;" />
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamOriginationContext;" />
		<xsl:param name="&xslParamOriginatingDoc;" />

		<xsl:call-template name="createCiteQueryBasedUrl">
			<xsl:with-param name="&xslParamRefType;" select="$&xslParamLinkElement;/@w-ref-type" />
			<xsl:with-param name="&xslParamPubNumber;" select="$&xslParamLinkElement;/@w-pub-number" />
			<xsl:with-param name="&xslParamSerialNumber;" select="$&xslParamLinkElement;/@w-serial-number" />
			<xsl:with-param name="&xslParamNormalizedCite;" select="$&xslParamLinkElement;/@w-normalized-cite" />
			<xsl:with-param name="&xslParamPinpointPage;" select="$&xslParamLinkElement;/@w-pinpoint-page" />
			<xsl:with-param name="&xslParamLinkHost;" select="$&xslParamLinkHost;" />
			<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
			<xsl:with-param name="&xslParamOriginatingDoc;" select="$&xslParamOriginatingDoc;" />
			<xsl:with-param name="&xslParamText;">
				<xsl:value-of select="$&xslParamLinkElement;"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Parse the semi-colon delimited, single-attribute based format of Cite.Query based links -->
	<xsl:template name="parseCentRCiteQuery">
		<xsl:param name="&xslParamLinkElement;" />
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamOriginationContext;" />
		<xsl:param name="&xslParamOriginatingDoc;" />

		<xsl:variable name="sourceSerialNumber">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$&xslParamLinkElement;/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="0" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetSeqNumber">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$&xslParamLinkElement;/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetSerialNumber">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$&xslParamLinkElement;/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetRefType">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$&xslParamLinkElement;/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetCite">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$&xslParamLinkElement;/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetPubNumber">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$&xslParamLinkElement;/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetPinPointPage">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$&xslParamLinkElement;/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="createCiteQueryBasedUrl">
			<xsl:with-param name="&xslParamSerialNumber;" select="$targetSerialNumber" />
			<xsl:with-param name="&xslParamRefType;" select="$targetRefType" />
			<xsl:with-param name="&xslParamNormalizedCite;" select="$targetCite" />
			<xsl:with-param name="&xslParamPubNumber;" select="$targetPubNumber" />
			<xsl:with-param name="&xslParamPinpointPage;" select="$targetPinPointPage" />
			<xsl:with-param name="&xslParamLinkHost;" select="$&xslParamLinkHost;" />
			<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
			<xsl:with-param name="&xslParamOriginatingDoc;" select="$&xslParamOriginatingDoc;" />
		</xsl:call-template>
	</xsl:template>

	<!-- Build the Cite.Query based link -->
	<xsl:template name="createCiteQueryBasedUrl">
		<xsl:param name="&xslParamRefType;" />
		<xsl:param name="&xslParamPubNumber;" />
		<xsl:param name="&xslParamSerialNumber;" />
		<xsl:param name="&xslParamNormalizedCite;" />
		<xsl:param name="&xslParamPinpointPage;" />
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamOriginationContext;" />
		<xsl:param name="&xslParamOriginatingDoc;" />
		<xsl:param name="&xslParamText;" />

		<!-- Anything with a refType is not valid -->
		<xsl:if test="string-length($&xslParamRefType;) &gt; 0">
			<xsl:variable name="encapsulatedRefType" select="concat(concat(';', $&xslParamRefType;), ';')" />
			<xsl:choose>
				<xsl:when test="not(contains($approvedRefTypes, $encapsulatedRefType) or contains($missingRefTypes, $encapsulatedRefType))">
					<!-- Swallow these as they are not turned on -->
				</xsl:when>
				<xsl:when test="$&xslParamRefType; = 'DB'">
					<xsl:call-template name="createDBRefTypeUrl">
						<xsl:with-param name="&xslParamLinkHost;" select="$&xslParamLinkHost;" />
						<xsl:with-param name="&xslParamNormalizedCite;" select="$&xslParamNormalizedCite;" />
						<xsl:with-param name="&xslParamText;" select="$&xslParamText;" />
						<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$&xslParamRefType; = 'KD' or $&xslParamRefType; = 'KW'">
					<xsl:call-template name="createCustomDigestPersistentUrl">
						<xsl:with-param name="&xslParamLinkHost;" select="$&xslParamLinkHost;" />
						<xsl:with-param name="&xslParamDocGuid;" select="$&xslParamOriginatingDoc;"/>
						<xsl:with-param name="&xslParamKeyNumber;">
							<xsl:choose>
								<xsl:when test="contains($&xslParamNormalizedCite;, 'TT_')">
									<xsl:value-of select="substring-before($&xslParamNormalizedCite;, 'TT_')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$&xslParamNormalizedCite;"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
						<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="findType">
						<xsl:call-template name="mapRefTypeToFindType">
							<xsl:with-param name="&xslParamRefType;" select="$&xslParamRefType;" />
							<xsl:with-param name="&xslParamNormalizedCite;" select="$&xslParamNormalizedCite;" />
						</xsl:call-template>
					</xsl:variable>

					<xsl:if test="string-length($findType) &gt; 0">
						<xsl:variable name="queryString">
							<xsl:if test="$findType">
								<xsl:text>&findType;=</xsl:text>
								<xsl:value-of select="$findType"/>
							</xsl:if>
							<xsl:if test="string-length($&xslParamSerialNumber;) &gt; 0">
								<xsl:text>&amp;&serialNumber;=</xsl:text>
								<xsl:call-template name="padSerialNumberWithPreceedingZeros">
									<xsl:with-param name="&xslParamSerialNumber;" select="$&xslParamSerialNumber;" />
								</xsl:call-template>
							</xsl:if>
							<xsl:variable name="outputPubNumber">
								<xsl:call-template name="processSpecialRulesForPubNumber">
									<xsl:with-param name="&xslParamRefType;" select="$&xslParamRefType;"/>
									<xsl:with-param name="&xslParamPubNumber;" select="$&xslParamPubNumber;"/>
								</xsl:call-template>
							</xsl:variable>
							<xsl:if test="string-length($outputPubNumber) &gt; 0">
								<xsl:text>&amp;&pubNumber;=</xsl:text>
								<xsl:value-of select="$outputPubNumber"/>
							</xsl:if>
							<xsl:if test="string-length($&xslParamNormalizedCite;) &gt; 0">
								<xsl:text>&amp;&cite;=</xsl:text>
								<xsl:call-template name="processSpecialRulesForNormalizedCite">
									<xsl:with-param name="&xslParamNormalizedCite;" select="$&xslParamNormalizedCite;" />
									<xsl:with-param name="&xslParamRefType;" select="$&xslParamRefType;" />
								</xsl:call-template>
							</xsl:if>
							<xsl:if test="string-length($&xslParamPinpointPage;) &gt; 0 and $&xslParamRefType; = 'CN'">
								<xsl:text>&amp;&sourceSerialUrlParam;=</xsl:text>
								<xsl:value-of select="$&xslParamPinpointPage;"/>
							</xsl:if>
							<xsl:if test="string-length($&xslParamOriginatingDoc;) &gt; 0">
								<xsl:text>&amp;&originatingDoc;=</xsl:text>
								<xsl:value-of select="$&xslParamOriginatingDoc;"/>
							</xsl:if>
							<xsl:if test="string-length($&xslParamRefType;) &gt; 0 and contains($dbOverrideRefTypes, $encapsulatedRefType)">
								<xsl:text>&amp;&refType;=</xsl:text>
								<xsl:value-of select="$&xslParamRefType;"/>
							</xsl:if>
							<xsl:call-template name="addOriginationContextQueryParam">
								<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
								<xsl:with-param name="&xslParamAddAmpersand;" select="true()" />
							</xsl:call-template>
						</xsl:variable>

						<xsl:value-of select="$&xslParamLinkHost;"/>
						<xsl:text>/&link;/&document;/&fullText;</xsl:text>
						<xsl:if test="string-length($queryString) &gt; 0 and not(contains($queryString, '?'))">
							<xsl:text>?</xsl:text>
						</xsl:if>
						<xsl:value-of select="$queryString"/>
						<xsl:variable name="fragmentIdentifier">
							<xsl:if test="$&xslParamPinpointPage; and string-length($&xslParamPinpointPage;) &gt; 0">
								<xsl:choose>
									<xsl:when test="$&xslParamRefType; = 'CN'" />
									<xsl:when test="$&xslParamRefType; = 'HN'">
										<xsl:text>&pinpointIdPrefix;</xsl:text>
										<xsl:value-of select="translate($&xslParamPinpointPage;, ';', '')"/>
									</xsl:when>
									<xsl:when test="$&xslParamRefType; = 'RB' or $&xslParamRefType; = 'RE' or $&xslParamRefType; = 'SP' or $&xslParamRefType; = 'VB' or $&xslParamRefType; = 'VE' or $&xslParamRefType; = 'VS'">
										<xsl:text>&pinpointIdPrefix;</xsl:text>
										<xsl:value-of select="$&xslParamPinpointPage;"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>&pinpointIdPrefix;</xsl:text>
										<xsl:text>sp_</xsl:text>
										<xsl:value-of select="number($&xslParamPubNumber;)"/>
										<xsl:text>_</xsl:text>
										<xsl:value-of select="$&xslParamPinpointPage;"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:if>
						</xsl:variable>
						<xsl:if test="string-length($fragmentIdentifier) &gt; 0">
							<xsl:text>#</xsl:text>
							<xsl:value-of select="$fragmentIdentifier"/>
						</xsl:if>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Build a DB link -->
	<xsl:template name="createDBRefTypeUrl">
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamNormalizedCite;" />
		<xsl:param name="&xslParamText;" />
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:variable name="dbName">
			<xsl:choose>
				<xsl:when test="string-length($&xslParamNormalizedCite;) &gt; 0">
					<xsl:value-of select="$&xslParamNormalizedCite;" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getDBNameFromText">
						<xsl:with-param name="&xslParamText;" select="$&xslParamText;" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($dbName) &gt; 0">
			<xsl:value-of select="$&xslParamLinkHost;"/>
			<xsl:text>/&link;/&database;/&signon;/</xsl:text>
			<xsl:value-of select="$dbName"/>
			<xsl:call-template name="addOriginationContextQueryParam">
				<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
				<xsl:with-param name="&xslParamAddAmpersand;" select="false()" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Create persistent URIs for Custom Digest -->
	<xsl:template name="createCustomDigestPersistentUrl">
		<xsl:param name="&xslParamLinkHost;" />
		<xsl:param name="&xslParamDocGuid;" />
		<xsl:param name="&xslParamKeyNumber;" />
		<xsl:param name="&xslParamOriginationContext;" />

		<xsl:variable name="urlEncodedKeyText">
			<xsl:if test="string-length(../../keytext) &gt; 0">
				<xsl:text>&amp;&keytext;=</xsl:text>
				<xsl:call-template name="url-encode">
					<xsl:with-param name="str">
						<xsl:value-of select="../../keytext"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>

		<xsl:if test="string-length($&xslParamDocGuid;) &gt; 0 and string-length($&xslParamKeyNumber;) &gt; 0">
			<xsl:value-of select="$&xslParamLinkHost;"/>
			<xsl:text>/&link;/&searchTypeValueSearch;/&customDigest;</xsl:text>
			<xsl:variable name="queryString">
				<xsl:text>&docGuid;=</xsl:text>
				<xsl:value-of select="$&xslParamDocGuid;"/>
				<xsl:text>&amp;</xsl:text>
				<xsl:text>&keyNumber;=</xsl:text>
				<xsl:value-of select="$&xslParamKeyNumber;"/>
				<xsl:value-of select="$urlEncodedKeyText"/>
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="&xslParamOriginationContext;" select="$&xslParamOriginationContext;" />
					<xsl:with-param name="&xslParamAddAmpersand;" select="true()" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($queryString) &gt; 0 and not(contains($queryString, '?'))">
				<xsl:text>?</xsl:text>
			</xsl:if>
			<xsl:value-of select="$queryString"/>
		</xsl:if>
	</xsl:template>

	<!-- Template containing RefTypes to FindTypes mapping rules -->
	<xsl:template name="mapRefTypeToFindType">
		<xsl:param name="&xslParamRefType;" />
		<xsl:param name="&xslParamNormalizedCite;" />
		<xsl:variable name="refTypesMappedToaFindTypes" select="';KP;'" />
		<xsl:variable name="refTypesMappedToHashFindTypes" select="';GL;'" />
		<xsl:variable name="refTypesMappedTogFindTypes" select="';UD;UX;'" />
		<xsl:variable name="refTypesMappedTohFindTypes" select="';EW;MC;ME;RQ;'" />
		<xsl:variable name="refTypesMappedToiFindTypes" select="';WK;'" />
		<xsl:variable name="refTypesMappedTolFindTypes" select="';AG;CM;CN;CP;DN;LK;LL;SB;SL;'" />
		<xsl:variable name="refTypesMappedToLFindTypes" select="';DU;GA;IQ;LQ;LV;PS;RB;RE;SP;VB;VE;VP;VS;'" />
		<xsl:variable name="refTypesMappedToMFindTypes" select="';RR;'" />
		<xsl:variable name="refTypesMappedTo0FindTypes" select="';DJ;'" />
		<xsl:variable name="refTypesMappedToVFindTypes" select="';TC;'" />
		<xsl:variable name="refTypesMappedToVQFindTypes" select="';VQ;'" />
		<xsl:variable name="refTypesMappedToYFindTypes" select="';AA;AN;BR;CA;CC;DA;DE;DM;DS;EA;FR;FS;FT;GC;GG;HN;IA;IC;IP;JR;JV;LR;NA;NR;OP;PA;PD;PE;PH;PJ;PL;PW;RA;RC;RM;RN;RP;R9;SA;SD;SS;ST;SU;TG;TR;TS;TV;UA;UC;UJ;UL;UT;UY;UZ;VO;VV;'" />
		<xsl:variable name="refTypesMappedToUNKNOWNFindTypes" select="';AB;AD;AE;AM;BD;CD;CH;CL;CO;CR;CV;CX;C1;DD;DF;DI;DK;DO;DQ;DV;D1;EC;EG;EI;EM;EV;FE;FM;FN;FW;GB;GD;GK;GS;G1;HD;HI;IE;IJ;IL;IM;IN;JI;KE;KS;LD;LE;LH;LJ;LM;LN;MD;MJ;MP;MR;MU;M1;NE;NF;NG;NH;NJ;NO;NS;NT;NV;PF;PI;P;PT;QH;QT;RU;RX;SE;SG;SV;TB;TD;TF;TJ;TK;TL;TM;TQ;UE;UM;UO;U1;WD;XX;ZA;ZZ;Z9;'" />

		<xsl:if test="string-length($&xslParamRefType;) &gt; 0">
			<xsl:variable name="encapsulatedRefType" select="concat(concat(';', $&xslParamRefType;), ';')" />
			<xsl:choose>
				<xsl:when test="contains($refTypesMappedToYFindTypes, $encapsulatedRefType)">
					<xsl:text>Y</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedToLFindTypes, $encapsulatedRefType)">
					<xsl:choose>
						<xsl:when test="$&xslParamRefType; = 'GA' and string-length($&xslParamNormalizedCite;) = 0">
							<!--Special Rule for handling GA refType with empty normalizedCite -->
							<xsl:text>Y</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>L</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedToVQFindTypes, $encapsulatedRefType)">
					<xsl:text>VQ</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedTolFindTypes, $encapsulatedRefType)">
					<xsl:text>l</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedTo0FindTypes, $encapsulatedRefType)">
					<xsl:text>0</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedToaFindTypes, $encapsulatedRefType)">
					<xsl:text>a</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedTogFindTypes, $encapsulatedRefType)">
					<xsl:text>g</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedToHashFindTypes, $encapsulatedRefType)">
					<xsl:text>#</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedTohFindTypes, $encapsulatedRefType)">
					<xsl:text>h</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedToVFindTypes, $encapsulatedRefType)">
					<xsl:text>V</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedToMFindTypes, $encapsulatedRefType)">
					<xsl:text>M</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedToiFindTypes, $encapsulatedRefType)">
					<xsl:text>i</xsl:text>
				</xsl:when>
				<xsl:when test="contains($refTypesMappedToUNKNOWNFindTypes, $encapsulatedRefType)">
					<xsl:text>UNKNOWN</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<!-- Template for processing RefType-based special rules for NormalizedCite attribute of Cite.Query element -->
	<xsl:template name="processSpecialRulesForNormalizedCite">
		<xsl:param name="&xslParamRefType;" />
		<xsl:param name="&xslParamNormalizedCite;" />
		<xsl:choose>
			<xsl:when test="$&xslParamRefType; = 'AG'">
				<xsl:text>ARCHIVE-GUID(</xsl:text>
				<xsl:value-of select="$&xslParamNormalizedCite;" />
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="($&xslParamRefType; = 'CN' or $&xslParamRefType; = 'CP' or $&xslParamRefType; = 'DN' or $&xslParamRefType; = 'SL') and not(contains($&xslParamNormalizedCite;, 'UUID('))">
				<xsl:text>UUID(</xsl:text>
				<xsl:value-of select="$&xslParamNormalizedCite;" />
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="$&xslParamRefType; = 'LK'">
				<xsl:text>LK("</xsl:text>
				<xsl:value-of select="$&xslParamNormalizedCite;" />
				<xsl:text>")</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$&xslParamNormalizedCite;" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Template for processing RefType-based special rules for PubNumber attribute of Cite.Query element -->
	<xsl:template name="processSpecialRulesForPubNumber">
		<xsl:param name="&xslParamRefType;" />
		<xsl:param name="&xslParamPubNumber;" />

		<xsl:choose>
			<xsl:when test="string-length($&xslParamPubNumber;) = 0 and ($&xslParamRefType; = 'EQ' or $&xslParamRefType; = 'EW')">
				<xsl:text>176285</xsl:text>
			</xsl:when>
			<xsl:when test="string-length($&xslParamPubNumber;) = 0 and $&xslParamRefType; = 'RQ'">
				<xsl:text>176284</xsl:text>
			</xsl:when>
			<xsl:when test="$&xslParamPubNumber; = 1037 and ($&xslParamRefType; = 'CN' or $&xslParamRefType; = 'CP')">
				<xsl:text>184736</xsl:text>
			</xsl:when>
			<xsl:when test="$&xslParamRefType; = 'TG'">
				<xsl:choose>
					<xsl:when test="$&xslParamPubNumber; = 220 or $&xslParamPubNumber; = 221 or $&xslParamPubNumber; = 222 or $&xslParamPubNumber; = 223 or $&xslParamPubNumber; = 224 or $&xslParamPubNumber; = 225 or $&xslParamPubNumber; = 226 or $&xslParamPubNumber; = 231 or $&xslParamPubNumber; = 233 or $&xslParamPubNumber; = 4040 or $&xslParamPubNumber; = 4041 or $&xslParamPubNumber; = 4083">
						<xsl:text>CA-ORCS</xsl:text>
					</xsl:when>
					<xsl:when test="$&xslParamPubNumber; = 154 or $&xslParamPubNumber; = 155 or $&xslParamPubNumber; = 550 or $&xslParamPubNumber; = 551 or $&xslParamPubNumber; = 596 or $&xslParamPubNumber; = 605 or $&xslParamPubNumber; = 7048 or $&xslParamPubNumber; = 7049 or $&xslParamPubNumber; = 7050 or $&xslParamPubNumber; = 7196">
						<xsl:text>NY-ORCS</xsl:text>
					</xsl:when>
					<xsl:when test="$&xslParamPubNumber; = 251 or $&xslParamPubNumber; = 799 or $&xslParamPubNumber; = 800 or $&xslParamPubNumber; = 804">
						<xsl:text>WA-ORCS</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$&xslParamPubNumber;"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$&xslParamPubNumber;"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Get the DB name from a text string. If there is no params, use the text. If there are params, use the text in the last set. -->
	<xsl:template name="getDBNameFromText">
		<xsl:param name="&xslParamText;" />
		<xsl:choose>
			<xsl:when test="contains($&xslParamText;, '(')">
				<xsl:call-template name="getDBNameFromText">
					<xsl:with-param name="&xslParamText;" select="substring-after($&xslParamText;,'(')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains($&xslParamText;, ')')">
				<xsl:call-template name="getDBNameFromText">
					<xsl:with-param name="&xslParamText;" select="substring-before($&xslParamText;,')')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$&xslParamText;"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Pad value of SerialNumber attribute in Cite.Query element so it is a 10 digit number with prepended Zeros -->
	<xsl:template name="padSerialNumberWithPreceedingZeros">
		<xsl:param name="&xslParamSerialNumber;" />
		<xsl:call-template name="prependZeros">
			<xsl:with-param name="length" select="10" />
			<xsl:with-param name="padString" select="$&xslParamSerialNumber;" />
		</xsl:call-template>
	</xsl:template>


	<!--
	Internal utility/helper sub-templates
	-->

	<!-- Prepend Zeros to a string -->
	<xsl:template name="prependZeros">
		<!-- recursive template to right justify and prepend-->
		<xsl:param name="padString"/>
		<xsl:param name="length"/>
		<xsl:choose>
			<xsl:when test="string-length($padString) &lt; $length">
				<xsl:call-template name="prependZeros">
					<xsl:with-param name="padString" select="concat(0,$padString)"/>
					<xsl:with-param name="length" select="$length"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$padString"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Replace all instances of a substring with another string -->
	<xsl:template name="replaceString">
		<xsl:param name="string" select="." />
		<xsl:param name="pattern" select="''" />
		<xsl:param name="replacement" select="''" />
		<xsl:choose>
			<xsl:when test="contains($string, $pattern)">
				<xsl:value-of select="substring-before($string, $pattern)" />
				<xsl:copy-of select="$replacement" />
				<xsl:call-template name="replaceString">
					<xsl:with-param name="string" select="substring-after($string, $pattern)" />
					<xsl:with-param name="pattern" select="$pattern" />
					<xsl:with-param name="replacement" select="$replacement" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Split and pick segments of a delimited string by index -->
	<xsl:template name="subStringByIndex">
		<xsl:param name="string" />
		<xsl:param name="pattern" />
		<xsl:param name="index" />
		<xsl:choose>
			<xsl:when test="$index = 0">
				<xsl:value-of select="substring-before($string, $pattern)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="subStringByIndex">
					<xsl:with-param name="index" select="$index - 1"/>
					<xsl:with-param name="string"	select="substring-after($string, $pattern)" />
					<xsl:with-param name="pattern" select="$pattern" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Global variables for URL encoding -->
	<xsl:variable name="ascii"> !"#$%&amp;'()*+,-./0123456789:;&lt;=&gt;?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~</xsl:variable>
	<xsl:variable name="latin1">&#160;&#161;&#162;&#163;&#164;&#165;&#166;&#167;&#168;&#169;&#170;&#171;&#172;&#173;&#174;&#175;&#176;&#177;&#178;&#179;&#180;&#181;&#182;&#183;&#184;&#185;&#186;&#187;&#188;&#189;&#190;&#191;&#192;&#193;&#194;&#195;&#196;&#197;&#198;&#199;&#200;&#201;&#202;&#203;&#204;&#205;&#206;&#207;&#208;&#209;&#210;&#211;&#212;&#213;&#214;&#215;&#216;&#217;&#218;&#219;&#220;&#221;&#222;&#223;&#224;&#225;&#226;&#227;&#228;&#229;&#230;&#231;&#232;&#233;&#234;&#235;&#236;&#237;&#238;&#239;&#240;&#241;&#242;&#243;&#244;&#245;&#246;&#247;&#248;&#249;&#250;&#251;&#252;&#253;&#254;&#255;</xsl:variable>
	<xsl:variable name="safe">!'()*-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~</xsl:variable>
	<xsl:variable name="hex" >0123456789ABCDEF</xsl:variable>

	<xsl:template name="url-encode">
		<xsl:param name="str"/>
		<xsl:if test="$str">
			<xsl:variable name="first-char" select="substring($str,1,1)"/>
			<xsl:choose>
				<xsl:when test="contains($safe,$first-char)">
					<xsl:value-of select="$first-char"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="codepoint">
						<xsl:choose>
							<xsl:when test="contains($ascii,$first-char)">
								<xsl:value-of select="string-length(substring-before($ascii,$first-char)) + 32"/>
							</xsl:when>
							<xsl:when test="contains($latin1,$first-char)">
								<xsl:value-of select="string-length(substring-before($latin1,$first-char)) + 160"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:message terminate="no">Warning: string contains a character that is out of range! Substituting " ".</xsl:message>
								<xsl:text>63</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="hex-digit1" select="substring($hex,floor($codepoint div 16) + 1,1)"/>
					<xsl:variable name="hex-digit2" select="substring($hex,($codepoint mod 16) + 1,1)"/>
					<xsl:value-of select="concat('%',$hex-digit1,$hex-digit2)"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="string-length($str) &gt; 1">
				<xsl:call-template name="url-encode">
					<xsl:with-param name="str" select="substring($str,2)"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Helper method to add OriginationContext as a Query Param -->
	<xsl:template name="addOriginationContextQueryParam">
		<xsl:param name="&xslParamOriginationContext;" />
		<xsl:param name="&xslParamAddAmpersand;" />

		<xsl:if test="string-length($&xslParamOriginationContext;) &gt; 0">
			<xsl:choose>
				<xsl:when test="$&xslParamAddAmpersand;">
					<xsl:text>&amp;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>?</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>&originationContextUrlParam;=</xsl:text>
			<xsl:value-of select="$&xslParamOriginationContext;"/>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>