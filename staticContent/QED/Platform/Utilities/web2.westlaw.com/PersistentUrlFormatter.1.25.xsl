<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2009: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet [
	<!ENTITY link "link">
	<!ENTITY document "document">
	<!ENTITY relatedInfo "relatedinformation">
	<!ENTITY flag "flag">
	<!ENTITY noGuidFound "NO_GUID_FOUND">
	<!ENTITY contentTypeUrlParam "contentType">
	<!ENTITY searchResultIdUrlParam "result">
	<!ENTITY searchTypeUrlParam "searchType">
	<!ENTITY rankUrlParam "rank">
	<!ENTITY originationContextUrlParam "originationContext">
	<!ENTITY fullTextPath "/View/FullText.html">
	<!ENTITY citingReferencesHtml "/citingreferences.html">
	<!ENTITY docFamilyGuid "familyGuid">
	<!ENTITY docGuid "docGuid">
	<!ENTITY findType "findtype">
	<!ENTITY serialNumber "serialnum">
	<!ENTITY pubNumber "db">
	<!ENTITY cite "cite">
	<!ENTITY pinPoint "pinPoint">
	<!ENTITY originatingDoc "ordoc">
	<!ENTITY fullText "fullText">
	<!ENTITY targetType "targetType">
	<!ENTITY maxHeight "maxHeight">
	<!ENTITY mimeType "mimeType">
	<!ENTITY highResolutionSegment "highResolution">
	<!ENTITY docHeadnoteLinkSegment "docheadnotelink">
	<!ENTITY headnoteId "headnoteId">
	<!ENTITY searchTypeValueSearch "Search">
	<!ENTITY searchTypeValueBrowse "Browse">
	<!ENTITY sourceSerial "sourceSerial">
	<!ENTITY destinationSerial "destinationSerial">
	<!ENTITY pinpointIdPrefix "co_pp_">
	<!ENTITY database "db">
	<!ENTITY signon "signon">
	<!ENTITY refType "refType">
	<!ENTITY customDigest "customDigest">
	<!ENTITY keyNumber "keyNumber">
	<!ENTITY keytext "keytext">
]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!--
	IMPORTANT NOTE:
	This should only build cite.query and blob URLs correctly for Web2... all of the other links will be Cobalt-based and broken.
	-->

	<!-- Global Variables -->
	<xsl:variable name="missingRefTypes" select="'AB;AD;AE;AG;AM;BD;CD;CH;CL;CO;CR;CV;CX;C1;DD;DF;DI;DJ;DK;DM;DN;DO;DQ;DS;DU;DV;D1;EA;EC;EG;EI;EM;EV;FE;FM;FN;FT;FW;GB;GC;GD;GG;GK;GL;GS;G1;HD;HI;IA;IE;IJ;IL;IM;IN;IP;IQ;JI;JR;KE;KP;KS;LD;LE;LH;LJ;LL;LM;LN;LV;MD;MJ;MP;MR;MU;M1;NE;NF;NG;NH;NJ;NO;NS;NT;NV;OP;PE;PF;PH;PI;PJ;PL;PR;PT;PW;QH;QT;RA;RR;RU;RX;R9;SD;SE;SG;SS;SU;SV;TB;TC;TD;TF;TJ;TK;TL;TM;TQ;UA;UC;UD;UE;UJ;UL;UM;UO;UT;UX;UY;UZ;U1;VO;VS;VV;WD;WK;XX;ZA;ZZ;Z9'" />
	<xsl:variable name="approvedRefTypes" select="'AA;AN;BR;CA;CC;DA;DE;EW;GA;HN;JV;KD;KW;LQ;LR;NA;NR;PA;PD;PS;RB;RC;RE;RM;RP;SA;SP;ST;TG;TR;TS;VB;VE;VP;VQ'" />
	<xsl:variable name="dbOverrideRefTypes" select="'CL;CM;CN;CP;DA;DN;ES;EW;FS;GA;GK;HI;LB;LD;LF;LK;LN;LQ;MC;NE;NF;NG;NH;QH;RQ;RW;SB;SG;SP;TC;TG;TM;TQ;UA'" />

	<!-- URL Encoding variables -->
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
					<xsl:variable name="hex-digit2" select="substring($hex,$codepoint mod 16 + 1,1)"/>
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


	<!-- Persistent Link Builder Templates -->

	<!-- Create persistent Document Service FullText uris -->
	<xsl:template name="createDocumentRequestPersistentUrl">
		<xsl:param name="host" />
		<xsl:param name="docGuid" />
		<xsl:param name="docFamilyGuid"/>
		<xsl:param name="originationContext" />
		<xsl:choose>
			<xsl:when test="string-length($host) &gt; 0">
				<xsl:value-of select="$host"/>
			</xsl:when>
		</xsl:choose>
		<xsl:text>/&document;/</xsl:text>
		<xsl:choose>
			<xsl:when test="string-length($docGuid) &gt; 0">
				<xsl:value-of select="$docGuid"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&noGuidFound;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>&fullTextPath;</xsl:text>
		<xsl:variable name="queryString">
			<xsl:if test="string-length($docFamilyGuid) &gt; 0">
				<xsl:text>&docFamilyGuid;=</xsl:text>
				<xsl:value-of select="$docFamilyGuid"/>
			</xsl:if>
			<xsl:call-template name="addOriginationContextQueryParam">
				<xsl:with-param name="originationContext" select="$originationContext" />
				<xsl:with-param name="addAmpersand" select="string-length($docFamilyGuid) &gt; 0" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($queryString, '?')">
				<xsl:value-of select="$queryString"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>?</xsl:text>
				<xsl:value-of select="$queryString"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Make persistent Document Service FullText uris from search results -->
	<xsl:template name="makeWebsiteDocumentRequestUrl">
		<xsl:param name="host" />
		<xsl:param name="guid" />
		<xsl:param name="searchResultId"/>
		<xsl:param name="searchType" />
		<xsl:param name="contentType" />
		<xsl:param name="rank" />
		<xsl:param name="originationContext" />
		<xsl:choose>
			<xsl:when test="string-length($host) &gt; 0">
				<xsl:value-of select="$host"/>
			</xsl:when>
		</xsl:choose>
		<xsl:text>/&document;/</xsl:text>
		<xsl:choose>
			<xsl:when test="string-length($guid) &gt; 0">
				<xsl:value-of select="$guid"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&noGuidFound;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>&fullTextPath;</xsl:text>
		<xsl:variable name="queryString">
			<xsl:variable name="searchQueryParams">
				<xsl:if test="string-length($searchResultId) &gt; 0 and string-length($searchType) &gt; 0 and $searchType = '&searchTypeValueBrowse;'">
					<xsl:text>&searchResultIdUrlParam;=</xsl:text>
					<xsl:value-of select="$searchResultId"/>
					<xsl:text>&amp;</xsl:text>
					<xsl:text>&searchTypeUrlParam;=</xsl:text>
					<xsl:value-of select="$searchType"/>
				</xsl:if>
				<xsl:if test="string-length($searchResultId) &gt; 0 and string-length($searchType) &gt; 0 and $searchType = '&searchTypeValueSearch;' and string-length($contentType) &gt; 0 and string-length($rank) &gt; 0">
					<xsl:text>&searchResultIdUrlParam;=</xsl:text>
					<xsl:value-of select="$searchResultId"/>
					<xsl:text>&amp;</xsl:text>
					<xsl:text>&searchTypeUrlParam;=</xsl:text>
					<xsl:value-of select="$searchType"/>
					<xsl:text>&amp;</xsl:text>
					<xsl:text>&contentTypeUrlParam;=</xsl:text>
					<xsl:value-of select="$contentType"/>
					<xsl:text>&amp;</xsl:text>
					<xsl:text>&rankUrlParam;=</xsl:text>
					<xsl:value-of select="$rank"/>
				</xsl:if>
			</xsl:variable>
			<xsl:value-of select="$searchQueryParams"/>
			<xsl:call-template name="addOriginationContextQueryParam">
				<xsl:with-param name="originationContext" select="$originationContext" />
				<xsl:with-param name="addAmpersand" select="string-length($searchQueryParams) &gt; 0 and string-length($originationContext) &gt; 0" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($queryString, '?')">
				<xsl:value-of select="$queryString"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>?</xsl:text>
				<xsl:value-of select="$queryString"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Create persistent uris for Pinpoint links to cited documents -->
	<xsl:template name="createCitedDocumentRequestPersistentUrl">
		<xsl:param name="host" />
		<xsl:param name="docGuid" />
		<xsl:param name="docFamilyGuid"/>
		<xsl:param name="originationContext" />
		<xsl:param name="sourceSerial"/>
		<xsl:param name="destinationSerial"/>
		<xsl:choose>
			<xsl:when test="string-length($host) &gt; 0">
				<xsl:value-of select="$host"/>
			</xsl:when>
		</xsl:choose>
		<xsl:text>/&document;/</xsl:text>
		<xsl:choose>
			<xsl:when test="string-length($docGuid) &gt; 0">
				<xsl:value-of select="$docGuid"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&noGuidFound;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>&fullTextPath;</xsl:text>
		<xsl:variable name="queryString">
			<xsl:if test="string-length($docFamilyGuid) &gt; 0">
				<xsl:text>&docFamilyGuid;=</xsl:text>
				<xsl:value-of select="$docFamilyGuid"/>
			</xsl:if>
			<xsl:call-template name="addOriginationContextQueryParam">
				<xsl:with-param name="originationContext" select="$originationContext" />
				<xsl:with-param name="addAmpersand" select="string-length($docFamilyGuid) &gt; 0" />
			</xsl:call-template>
			<xsl:if test="string-length($sourceSerial) &gt; 0">
				<xsl:if test="string-length($docFamilyGuid) &gt; 0 or string-length($originationContext) &gt; 0">
					<xsl:text>&amp;</xsl:text>
				</xsl:if>
				<xsl:text>&sourceSerial;=</xsl:text>
				<xsl:value-of select="$sourceSerial"/>
			</xsl:if>
			<xsl:if test="string-length($destinationSerial) &gt; 0">
				<xsl:if test="string-length($docFamilyGuid) &gt; 0 or string-length($originationContext) &gt; 0 or string-length($sourceSerial) &gt; 0">
					<xsl:text>&amp;</xsl:text>
				</xsl:if>
				<xsl:text>&destinationSerial;=</xsl:text>
				<xsl:value-of select="$destinationSerial"/>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($queryString, '?')">
				<xsl:value-of select="$queryString"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>?</xsl:text>
				<xsl:value-of select="$queryString"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Create persistent uris for Headnote links to cited case documents -->
	<xsl:template name="createHeadnotesCitedCaseRefPersistentUrl">
		<xsl:param name="linkHost" />
		<xsl:param name="guid" />
		<xsl:param name="headnoteId" />
		<xsl:param name="originationContext" />

		<xsl:if test="string-length($guid) &gt; 0 and string-length($headnoteId) &gt; 0">
			<xsl:value-of select="$linkHost"/>
			<xsl:text>/&link;/&relatedInfo;/&docHeadnoteLinkSegment;</xsl:text>
			<xsl:variable name="queryString">
				<xsl:text>&docGuid;=</xsl:text>
				<xsl:value-of select="$guid"/>
				<xsl:if test="string-length($headnoteId) &gt; 0">
					<xsl:if test="string-length($guid) &gt; 0">
						<xsl:text>&amp;</xsl:text>
					</xsl:if>
					<xsl:text>&headnoteId;=</xsl:text>
					<xsl:value-of select="$headnoteId"/>
				</xsl:if>
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="originationContext" select="$originationContext" />
					<xsl:with-param name="addAmpersand" select="string-length(concat($guid,$originationContext)) &gt; 0" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($queryString) &gt; 0">
				<xsl:text>?</xsl:text>
				<xsl:value-of select="$queryString"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Create persistent uris for links to images in a document -->
	<xsl:template name="createBlobPersistentUrl">
		<xsl:param name="guid" />
		<xsl:param name="highResolution" select="false()" />
		<xsl:param name="targetType" />
		<xsl:param name="mimeType" />
		<xsl:param name="maxHeight" />
		<xsl:param name="linkHost" />
		<xsl:param name="originationContext" />

		<xsl:if test="string-length($guid) &gt; 0 and string-length($mimeType) &gt; 0">
			<xsl:value-of select="$linkHost"/>
			<xsl:text>/print/images.aspx</xsl:text>
			<!--<xsl:if test="$highResolution">
				<xsl:text>&highResolutionSegment;/</xsl:text>
			</xsl:if>-->
			<xsl:variable name="queryString">
				<xsl:text>?rs=WLW0.09&amp;vr=2.0&amp;mt=Westlaw&amp;uw=1&amp;imguid=</xsl:text>
				<xsl:value-of select="$guid"/>
				<xsl:choose>
					<xsl:when test="$mimeType = 'application/pdf'">
						<!--<xsl:text>.pdf</xsl:text>-->
						<xsl:if test="string-length($targetType) &gt; 0">
							<xsl:text>&amp;imcnt=NRS-IMAGE&amp;it=</xsl:text>
							<xsl:value-of select="$targetType"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="$mimeType = 'image/jpeg'">
						<xsl:text>.jpg</xsl:text>
						<xsl:if test="string-length($maxHeight) &gt; 0">
							<xsl:text>?&maxHeight;=</xsl:text>
							<xsl:value-of select="$maxHeight"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="$mimeType = 'application/x-wgsl'">
						<xsl:text>.amz</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<!-- Unexpected image type -->
						<xsl:text>?&targetType;=</xsl:text>
						<xsl:value-of select="$targetType"/>
						<xsl:text>&amp;&mimeType;=</xsl:text>
						<xsl:value-of select="$mimeType"/>
						<xsl:text>&amp;&maxHeight;=</xsl:text>
						<xsl:value-of select="$maxHeight"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="$queryString" />
			<xsl:call-template name="addOriginationContextQueryParam">
				<xsl:with-param name="originationContext" select="$originationContext" />
				<xsl:with-param name="addAmpersand" select="contains($queryString, '?')" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Create a persistent uri for the KeyCite flag link -->
	<xsl:template name="createKeyCiteFlagPersistentUrl">
		<xsl:param name="linkHost" />
		<xsl:param name="docFamilyGuid" />
		<xsl:param name="docGuid" />
		<xsl:param name="originationContext" />

		<xsl:if test="string-length($docGuid) &gt; 0">
			<xsl:variable name="queryParams">
				<xsl:if test="string-length($docFamilyGuid) &gt; 0">
					<xsl:text>&docFamilyGuid;=</xsl:text>
					<xsl:value-of select="$docFamilyGuid"/>
				</xsl:if>
				<xsl:if test="string-length($docFamilyGuid) &gt; 0 and string-length($docGuid) &gt; 0">
					<xsl:text>&amp;</xsl:text>
				</xsl:if>
				<xsl:if test="string-length($docGuid) &gt; 0">
					<xsl:text>&docGuid;=</xsl:text>
					<xsl:value-of select="$docGuid"/>
				</xsl:if>
				<xsl:call-template name="addOriginationContextQueryParam">
					<xsl:with-param name="originationContext" select="$originationContext" />
					<xsl:with-param name="addAmpersand" select="true()" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:value-of select="$linkHost"/>
			<xsl:text>/&link;/&relatedInfo;/&flag;?</xsl:text>
			<xsl:value-of select="$queryParams"/>
		</xsl:if>
	</xsl:template>

	<!-- Create a persistent uri for the NotesOfDecisions link to Related Info -->
	<xsl:template name="createNotesOfDecisionsPersistantUrl">
		<xsl:param name="linkHost" />
		<xsl:param name="docGuid" />
		<xsl:param name="originationContext" />

		<xsl:if test="string-length($docGuid) &gt; 0">
			<xsl:value-of select="$linkHost"/>
			<xsl:text>/&link;/&relatedInfo;/Annotations.NOD?&docGuid;=</xsl:text>
			<xsl:value-of select="$docGuid"/>
			<xsl:call-template name="addOriginationContextQueryParam">
				<xsl:with-param name="originationContext" select="$originationContext" />
				<xsl:with-param name="addAmpersand" select="true()" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Create persistent uris for Cite.Query links.  These links are resolved at a later time through the link resolver service.
	There are two formats of the Cite.Query element and both formats are handled in this template. -->
	<xsl:template name="createCiteQueryPersistentUrl">
		<xsl:param name="linkElement" />
		<xsl:param name="linkHost" />
		<xsl:param name="originationContext" />
		<xsl:param name="originatingDoc" />
		<xsl:choose>
			<xsl:when test="$linkElement[self::cite.query] and $linkElement/@target">
				<xsl:call-template name="parseCentRCiteQuery">
					<xsl:with-param name="linkElement" select="$linkElement" />
					<xsl:with-param name="linkHost" select="$linkHost" />
					<xsl:with-param name="originationContext" select="$originationContext" />
					<xsl:with-param name="originatingDoc" select="$originatingDoc" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="($linkElement[self::cite.query] or $linkElement[self::md.toggle.link]) and $linkElement/@w-ref-type">
				<xsl:call-template name="parseStandardCiteQuery">
					<xsl:with-param name="linkElement" select="$linkElement" />
					<xsl:with-param name="linkHost" select="$linkHost" />
					<xsl:with-param name="originationContext" select="$originationContext" />
					<xsl:with-param name="originatingDoc" select="$originatingDoc" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Parse the standard format of Cite.Query based links -->
	<xsl:template name="parseStandardCiteQuery">
		<xsl:param name="linkElement" />
		<xsl:param name="linkHost" />
		<xsl:param name="originationContext" />
		<xsl:param name="originatingDoc" />

		<xsl:call-template name="createCiteQueryBasedUrl">
			<xsl:with-param name="refType" select="$linkElement/@w-ref-type" />
			<xsl:with-param name="pubNumber" select="$linkElement/@w-pub-number" />
			<xsl:with-param name="serialNumber" select="$linkElement/@w-serial-number" />
			<xsl:with-param name="normalizedCite" select="$linkElement/@w-normalized-cite" />
			<xsl:with-param name="pinpointPage" select="$linkElement/@w-pinpoint-page" />
			<xsl:with-param name="linkHost" select="$linkHost" />
			<xsl:with-param name="originationContext" select="$originationContext" />
			<xsl:with-param name="originatingDoc" select="$originatingDoc" />
			<xsl:with-param name="text">
				<xsl:value-of select="$linkElement"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Parse the semi-colon delimited, single-attribute based format of Cite.Query based links -->
	<xsl:template name="parseCentRCiteQuery">
		<xsl:param name="linkElement" />
		<xsl:param name="linkHost" />
		<xsl:param name="originationContext" />
		<xsl:param name="originatingDoc" />

		<xsl:variable name="sourceSerialNumber">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$linkElement/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="0" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetSeqNumber">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$linkElement/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="1" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetSerialNumber">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$linkElement/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="2" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetRefType">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$linkElement/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="3" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetCite">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$linkElement/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="4" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetPubNumber">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$linkElement/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="5" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="targetPinPointPage">
			<xsl:call-template name="subStringByIndex">
				<xsl:with-param name="string" select="$linkElement/@target" />
				<xsl:with-param name="pattern" select="';'" />
				<xsl:with-param name="index" select="6" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="createCiteQueryBasedUrl">
			<xsl:with-param name="serialNumber" select="$targetSerialNumber" />
			<xsl:with-param name="refType" select="$targetRefType" />
			<xsl:with-param name="normalizedCite" select="$targetCite" />
			<xsl:with-param name="pubNumber" select="$targetPubNumber" />
			<xsl:with-param name="pinpointPage" select="$targetPinPointPage" />
			<xsl:with-param name="linkHost" select="$linkHost" />
			<xsl:with-param name="originationContext" select="$originationContext" />
			<xsl:with-param name="originatingDoc" select="$originatingDoc" />
		</xsl:call-template>
	</xsl:template>

	<!-- Build the Cite.Query based link -->
	<xsl:template name="createCiteQueryBasedUrl">
		<xsl:param name="refType" />
		<xsl:param name="pubNumber" />
		<xsl:param name="serialNumber" />
		<xsl:param name="normalizedCite" />
		<xsl:param name="pinpointPage" />
		<xsl:param name="linkHost" />
		<xsl:param name="originationContext" />
		<xsl:param name="originatingDoc" />
		<xsl:param name="text" />

		<xsl:choose>
			<xsl:when test="string-length($refType) = 0 or not(contains($approvedRefTypes, $refType) or contains($missingRefTypes, $refType))">
				<!-- Swallow these as they are not valid or not turned on -->
			</xsl:when>
			<xsl:when test="$refType = 'DB'">
				<xsl:call-template name="createDBRefTypeUrl">
					<xsl:with-param name="linkHost" select="$linkHost" />
					<xsl:with-param name="normalizedCite" select="$normalizedCite" />
					<xsl:with-param name="text" select="$text" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$refType = 'KD' or $refType = 'KW'">
				<xsl:call-template name="createCustomDigestPersistentUrl">
					<xsl:with-param name="linkHost" select="$linkHost" />
					<xsl:with-param name="guid" select="$originatingDoc"/>
					<xsl:with-param name="keyNumber">
						<xsl:choose>
							<xsl:when test="contains($normalizedCite, 'TT_')">
								<xsl:value-of select="substring-before($normalizedCite, 'TT_')"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$normalizedCite"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="findType">
					<xsl:call-template name="mapRefTypeToFindType">
						<xsl:with-param name="refType" select="$refType" />
						<xsl:with-param name="normalizedCite" select="$normalizedCite" />
					</xsl:call-template>
				</xsl:variable>

				<xsl:if test="string-length($findType) &gt; 0">
					<xsl:variable name="queryParams">
						<xsl:if test="$findType">
							<xsl:text>&findType;=</xsl:text>
							<xsl:value-of select="$findType"/>
						</xsl:if>
						<xsl:if test="$serialNumber and string-length($serialNumber) &gt; 0">
							<xsl:text>&amp;&serialNumber;=</xsl:text>
							<xsl:call-template name="padSerialNumberWithPreceedingZeros">
								<xsl:with-param name="serialNumber" select="$serialNumber" />
							</xsl:call-template>
						</xsl:if>
						<xsl:variable name="outputPubNumber">
							<xsl:call-template name="processSpecialRulesForPubNumber">
								<xsl:with-param name="refType" select="$refType"/>
								<xsl:with-param name="pubNumber" select="$pubNumber"/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:if test="$outputPubNumber and string-length($outputPubNumber) &gt; 0">
							<xsl:text>&amp;&pubNumber;=</xsl:text>
							<xsl:value-of select="$outputPubNumber"/>
						</xsl:if>
						<xsl:if test="$normalizedCite">
							<xsl:text>&amp;&cite;=</xsl:text>
							<xsl:call-template name="processSpecialRulesForNormalizedCite">
								<xsl:with-param name="normalizedCite" select="$normalizedCite" />
								<xsl:with-param name="refType" select="$refType" />
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="$pinpointPage and string-length($pinpointPage) &gt; 0 and $refType = 'CN'">
							<xsl:text>&amp;&sourceSerial;=</xsl:text>
							<xsl:value-of select="$pinpointPage"/>
						</xsl:if>
						<xsl:if test="$originatingDoc and string-length($originatingDoc) &gt; 0">
							<xsl:text>&amp;&originatingDoc;=</xsl:text>
							<xsl:value-of select="$originatingDoc"/>
						</xsl:if>
						<xsl:if test="$refType and string-length($refType) &gt; 0 and contains($dbOverrideRefTypes, $refType)">
							<xsl:text>&amp;&refType;=</xsl:text>
							<xsl:value-of select="$refType"/>
						</xsl:if>
						<xsl:call-template name="addOriginationContextQueryParam">
							<xsl:with-param name="originationContext" select="$originationContext" />
							<xsl:with-param name="addAmpersand" select="true()" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:value-of select="$linkHost"/>
					<xsl:text>/find/default.wl?rs=WLW0.09&amp;vr=2.0&amp;mt=Westlaw&amp;uw=1&amp;</xsl:text>
					<xsl:value-of select="$queryParams"/>
					<xsl:variable name="fragmentIdentifier">
						<xsl:if test="$pinpointPage and string-length($pinpointPage) &gt; 0">
							<xsl:choose>
								<xsl:when test="$refType = 'CN'" />
								<xsl:when test="$refType = 'HN'">
									<xsl:text>&pinpointIdPrefix;</xsl:text>
									<xsl:value-of select="translate($pinpointPage, ';', '')"/>
								</xsl:when>
								<xsl:when test="$refType = 'RB' or $refType = 'RE' or $refType = 'SP' or $refType = 'VB' or $refType = 'VE' or $refType = 'VS'">
									<xsl:text>&pinpointIdPrefix;</xsl:text>
									<xsl:value-of select="$pinpointPage"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>&pinpointIdPrefix;</xsl:text>
									<xsl:text>sp_</xsl:text>
									<xsl:value-of select="number($pubNumber)"/>
									<xsl:text>_</xsl:text>
									<xsl:value-of select="$pinpointPage"/>
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
	</xsl:template>

	<!-- Mapping and Processing Rules -->

	<!-- Template containing RefTypes to FindTypes mapping rules -->
	<xsl:template name="mapRefTypeToFindType">
		<xsl:param name="refType" />
		<xsl:param name="normalizedCite" />
		<xsl:variable name="refTypesMappedToaFindTypes" select="'KP'" />
		<xsl:variable name="refTypesMappedToHashFindTypes" select="'GL'" />
		<xsl:variable name="refTypesMappedTogFindTypes" select="'UD;UX'" />
		<xsl:variable name="refTypesMappedTohFindTypes" select="'EW;MC;ME;RQ'" />
		<xsl:variable name="refTypesMappedToiFindTypes" select="'WK'" />
		<xsl:variable name="refTypesMappedTolFindTypes" select="'AG;CM;CN;CP;DN;LK;LL;SB;SL'" />
		<xsl:variable name="refTypesMappedToLFindTypes" select="'DU;GA;IQ;LQ;LV;PS;RB;RE;SP;VB;VE;VP;VS'" />
		<xsl:variable name="refTypesMappedToMFindTypes" select="'RR'" />
		<xsl:variable name="refTypesMappedTo0FindTypes" select="'DJ'" />
		<xsl:variable name="refTypesMappedToVFindTypes" select="'TC'" />
		<xsl:variable name="refTypesMappedToVQFindTypes" select="'VQ'" />
		<xsl:variable name="refTypesMappedToYFindTypes" select="'AA;AN;BR;CA;CC;DA;DE;DM;DS;EA;FR;FS;FT;GC;GG;HN;IA;IC;IP;JR;JV;LR;NA;NR;OP;PA;PD;PE;PH;PJ;PL;PW;RA;RC;RM;RN;RP;R9;SA;SD;SS;ST;SU;TG;TR;TS;TV;UA;UC;UJ;UL;UT;UY;UZ;VO;VV'" />
		<xsl:variable name="refTypesMappedToUNKNOWNFindTypes" select="'AB;AD;AE;AM;BD;CD;CH;CL;CO;CR;CV;CX;C1;DD;DF;DI;DK;DO;DQ;DV;D1;EC;EG;EI;EM;EV;FE;FM;FN;FW;GB;GD;GK;GS;G1;HD;HI;IE;IJ;IL;IM;IN;JI;KE;KS;LD;LE;LH;LJ;LM;LN;MD;MJ;MP;MR;MU;M1;NE;NF;NG;NH;NJ;NO;NS;NT;NV;PF;PI;P;PT;QH;QT;RU;RX;SE;SG;SV;TB;TD;TF;TJ;TK;TL;TM;TQ;UE;UM;UO;U1;WD;XX;ZA;ZZ;Z9'" />

		<xsl:choose>
			<xsl:when test="contains($refTypesMappedToYFindTypes, $refType)">
				<xsl:text>Y</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedToLFindTypes, $refType)">
				<xsl:choose>
					<xsl:when test="$refType='GA' and string-length($normalizedCite) = 0">
						<!--Special Rule for handling GA RefType with empty normalized-cite-->
						<xsl:text>Y</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>L</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedToVQFindTypes, $refType)">
				<xsl:text>VQ</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedTolFindTypes, $refType)">
				<xsl:text>l</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedTo0FindTypes, $refType)">
				<xsl:text>0</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedToaFindTypes, $refType)">
				<xsl:text>a</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedTogFindTypes, $refType)">
				<xsl:text>g</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedToHashFindTypes, $refType)">
				<xsl:text>#</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedTohFindTypes, $refType)">
				<xsl:text>h</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedToVFindTypes, $refType)">
				<xsl:text>V</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedToMFindTypes, $refType)">
				<xsl:text>M</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedToiFindTypes, $refType)">
				<xsl:text>i</xsl:text>
			</xsl:when>
			<xsl:when test="contains($refTypesMappedToUNKNOWNFindTypes, $refType)">
				<xsl:text>UNKNOWN</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Template for processing RefType-based special rules for NormalizedCite attribute of Cite.Query element -->
	<xsl:template name="processSpecialRulesForNormalizedCite">
		<xsl:param name="refType" />
		<xsl:param name="normalizedCite" />
		<xsl:choose>
			<xsl:when test="$refType='AG'">
				<xsl:text>ARCHIVE-GUID(</xsl:text>
				<xsl:value-of select="$normalizedCite" />
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="($refType='CN' or $refType='CP' or $refType='DN' or $refType='SL') and not(contains($normalizedCite, 'UUID('))">
				<xsl:text>UUID(</xsl:text>
				<xsl:value-of select="$normalizedCite" />
				<xsl:text>)</xsl:text>
			</xsl:when>
			<xsl:when test="$refType='LK'">
				<xsl:text>LK("</xsl:text>
				<xsl:value-of select="$normalizedCite" />
				<xsl:text>")</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$normalizedCite" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Template for processing RefType-based special rules for PubNumber attribute of Cite.Query element -->
	<xsl:template name="processSpecialRulesForPubNumber">
		<xsl:param name="refType" />
		<xsl:param name="pubNumber" />

		<xsl:choose>
			<xsl:when test="string-length($pubNumber) = 0 and ($refType = 'EQ' or $refType = 'EW')">
				<xsl:text>176285</xsl:text>
			</xsl:when>
			<xsl:when test="string-length($pubNumber) = 0 and $refType = 'RQ'">
				<xsl:text>176284</xsl:text>
			</xsl:when>
			<xsl:when test="$pubNumber = 1037 and ($refType = 'CN' or $refType = 'CP')">
				<xsl:text>184736</xsl:text>
			</xsl:when>
			<xsl:when test="$refType = 'TG'">
				<xsl:choose>
					<xsl:when test="$pubNumber = 220 or $pubNumber = 221 or $pubNumber = 222 or $pubNumber = 223 or $pubNumber = 224 or $pubNumber = 225 or $pubNumber = 226 or $pubNumber = 231 or $pubNumber = 233 or $pubNumber = 4040 or $pubNumber = 4041 or $pubNumber = 4083">
						<xsl:text>CA-ORCS</xsl:text>
					</xsl:when>
					<xsl:when test="$pubNumber = 154 or $pubNumber = 155 or $pubNumber = 550 or $pubNumber = 551 or $pubNumber = 596 or $pubNumber = 605 or $pubNumber = 7048 or $pubNumber = 7049 or $pubNumber = 7050 or $pubNumber = 7196">
						<xsl:text>NY-ORCS</xsl:text>
					</xsl:when>
					<xsl:when test="$pubNumber = 251 or $pubNumber = 799 or $pubNumber = 800 or $pubNumber = 804">
						<xsl:text>WA-ORCS</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$pubNumber"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$pubNumber"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- Build a DB link -->
	<xsl:template name="createDBRefTypeUrl">
		<xsl:param name="linkHost" />
		<xsl:param name="normalizedCite" />
		<xsl:param name="text" />

		<xsl:variable name="dbName">
			<xsl:choose>
				<xsl:when test="string-length($normalizedCite) &gt; 0">
					<xsl:value-of select="$normalizedCite" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getDBNameFromText">
						<xsl:with-param name="text" select="$text" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="string-length($dbName) &gt; 0">
			<xsl:value-of select="$linkHost"/>
			<xsl:text>/&link;/&database;/&signon;/</xsl:text>
			<xsl:value-of select="$dbName"/>
		</xsl:if>
	</xsl:template>

	<!-- Create persistent uris for Custom Digest -->
	<xsl:template name="createCustomDigestPersistentUrl">
		<xsl:param name="linkHost" />
		<xsl:param name="guid" />
		<xsl:param name="keyNumber" />

		<xsl:if test="string-length($guid) &gt; 0 and string-length($keyNumber) &gt; 0">
			<xsl:value-of select="$linkHost"/>
			<xsl:text>/&link;/&searchTypeValueSearch;/&customDigest;</xsl:text>
			<xsl:variable name="queryString">
				<xsl:text>&docGuid;=</xsl:text>
				<xsl:value-of select="$guid"/>
				<xsl:if test="string-length($keyNumber) &gt; 0">
					<xsl:if test="string-length($guid) &gt; 0">
						<xsl:text>&amp;</xsl:text>
					</xsl:if>
					<xsl:text>&keyNumber;=</xsl:text>
					<xsl:value-of select="$keyNumber"/>
				</xsl:if>
				<xsl:if test="string-length(../../keytext) &gt; 0">
					<xsl:text>&amp;&keytext;=</xsl:text>
					<xsl:call-template name="url-encode">
						<xsl:with-param name="str">
							<xsl:value-of select="../../keytext"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</xsl:variable>
			<xsl:if test="string-length($queryString) &gt; 0">
				<xsl:text>?</xsl:text>
				<xsl:value-of select="$queryString"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Get the DB name from a text string. If there is no params, use the text. If there are params, use the text in the last set. -->
	<xsl:template name="getDBNameFromText">
		<xsl:param name="text" />
		<xsl:choose>
			<xsl:when test="contains($text, '(')">
				<xsl:call-template name="getDBNameFromText">
					<xsl:with-param name="text" select="substring-after($text,'(')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains($text, ')')">
				<xsl:call-template name="getDBNameFromText">
					<xsl:with-param name="text" select="substring-before($text,')')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!--Utility/Helper Templates-->

	<!-- Helper method to add OriginationContext as a Query Param -->
	<xsl:template name="addOriginationContextQueryParam">
		<xsl:param name="originationContext" />
		<xsl:param name="addAmpersand" />

		<xsl:if test="string-length($originationContext) &gt; 0">
			<xsl:choose>
				<xsl:when test="$addAmpersand">
					<xsl:text>&amp;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>?</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>&originationContextUrlParam;=</xsl:text>
			<xsl:value-of select="$originationContext"/>
		</xsl:if>
	</xsl:template>

	<!-- Pad value of SerialNumber attribute in Cite.Query element so it is a 10 digit number with prepended Zeros -->
	<xsl:template name="padSerialNumberWithPreceedingZeros">
		<xsl:param name="serialNumber" />
		<xsl:call-template name="prependZeros">
			<xsl:with-param name="length" select="10" />
			<xsl:with-param name="padString" select="$serialNumber" />
		</xsl:call-template>
	</xsl:template>

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

</xsl:stylesheet>
