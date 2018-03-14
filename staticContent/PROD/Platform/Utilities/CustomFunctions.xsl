<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Start a block of unchunkable output -->
	<xsl:template name="startUnchunkableBlock">
		<xsl:param name="currentNode" select="." />
		<xsl:param name="text" select="generate-id($currentNode)" />

		<xsl:processing-instruction name="startUnchunkableBlock">
			<xsl:value-of select="$text" />
		</xsl:processing-instruction>
	</xsl:template>

	<!-- End a block of unchunkable output -->
	<xsl:template name="endUnchunkableBlock">
		<xsl:param name="currentNode" select="." />
		<xsl:param name="text" select="generate-id($currentNode)" />

		<xsl:processing-instruction name="endUnchunkableBlock">
			<xsl:value-of select="$text" />
		</xsl:processing-instruction>
	</xsl:template>

	<!-- Force a chunk before or after the content -->
	<xsl:template name="forcePageBreak">
		<xsl:param name="before" select="false" />
		<xsl:param name="after" select="false" />
		<xsl:param name="content" />

		<xsl:if test="$before">
			<input type="hidden" id="&forceChunkId;" />
		</xsl:if>

		<xsl:copy-of select="$content" />

		<xsl:if test="$after">
			<input type="hidden" id="&forceChunkId;" />
		</xsl:if>

	</xsl:template>

	<!-- Repeat some content a given number of times -->
	<xsl:template name="repeat">
		<xsl:param name="contents"/>
		<xsl:param name="repetitions" select="0"/>
		<xsl:if test="string(number($repetitions)) != 'NaN'">
			<xsl:if test="string-length($contents) &gt; 0 and $repetitions &gt; 0">
				<xsl:copy-of select="$contents" />
				<xsl:call-template name="repeat">
					<xsl:with-param name="contents" select="$contents" />
					<xsl:with-param name="repetitions" select="$repetitions - 1" />
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>



  <!-- Replace all instances of a substring with another string-->
  <xsl:template name="replace">
    <xsl:param name="string" select="." />
    <xsl:param name="pattern" select="''" />
    <xsl:param name="replacement" select="''" />
    <xsl:param name="disable-output-escaping" select="'no'" />
    <xsl:choose>
      <xsl:when test="contains($string, $pattern)">
        <xsl:variable name="before" select="substring-before($string, $pattern)"/>
        <xsl:choose>
          <xsl:when test="$disable-output-escaping = 'yes'">
            <xsl:value-of disable-output-escaping="yes" select="$before" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="no" select="$before" />
          </xsl:otherwise>
        </xsl:choose>

        <xsl:copy-of select="$replacement" />

        <xsl:call-template name="replace">
          <xsl:with-param name="string" select="substring-after($string, $pattern)" />
          <xsl:with-param name="pattern" select="$pattern" />
          <xsl:with-param name="replacement" select="$replacement" />
          <xsl:with-param name="disable-output-escaping" select="$disable-output-escaping" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$disable-output-escaping = 'yes'">
            <xsl:value-of disable-output-escaping="yes" select="$string" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of disable-output-escaping="no" select="$string" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

	<!-- Returns the uppercase equivalent of the string -->
	<xsl:template name="upper-case">
		<xsl:param name="string" select="." />
		<xsl:value-of select="translate($string, '&alphabetLowercase;', '&alphabetUppercase;')" />
	</xsl:template>


	<!-- Returns the lowercase equivalent of the string -->
	<xsl:template name="lower-case">
		<xsl:param name="string" select="." />
		<xsl:value-of select="translate($string, '&alphabetUppercase;', '&alphabetLowercase;')" />
	</xsl:template>


	<!-- Verifies if the string ends with the given substring -->
	<xsl:template name="ends-with">
		<xsl:param name="string1" select="." />
		<xsl:param name="string2" select="''" />

		<xsl:variable name="startIndex">
			<xsl:value-of select="string-length($string1) - string-length($string2) + 1" />
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$startIndex &gt; 0 and $startIndex &lt;= string-length($string1)">
				<xsl:choose>
					<xsl:when test="substring($string1, $startIndex) = $string2">
						<xsl:value-of select="true()"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="false()"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- Replace all instances of "[\.-_][a-z]{1}" with "[A-Z]{1}" -->
	<xsl:template name="escape-to-class">
		<xsl:param name="xmlElementName" select="local-name()" />
		<xsl:param name="prefix" select="'x_'" />
		<xsl:value-of select="$prefix" />
		<xsl:call-template name="remove-punctuation-and-enforce-camel-case">
			<xsl:with-param name="string" select="$xmlElementName" />
		</xsl:call-template>
	</xsl:template>

	<!-- Subtemplate for the "escape-to-class" template -->
	<xsl:template name="remove-punctuation-and-enforce-camel-case">
		<xsl:param name="string" select="." />
		<xsl:variable name="normalizedString">
			<xsl:call-template name="lower-case">
				<xsl:with-param name="string" select="translate($string, '-_', '..')" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($normalizedString, '.')">
				<!-- Variables for storage -->
				<xsl:variable name="beforePeriodString" select="substring-before($normalizedString, '.')"/>
				<xsl:variable name="afterPeriodString" select="substring-after($normalizedString, '.')"/>
				<xsl:variable name="charToUpper" select="substring($afterPeriodString, 1, 1)"/>
				<xsl:variable name="afterUpperCharString" select="substring($afterPeriodString, 2)"/>

				<!-- Actual Output -->
				<xsl:value-of select="$beforePeriodString" />
				<xsl:call-template name="upper-case">
					<xsl:with-param name="string" select="$charToUpper" />
				</xsl:call-template>

				<!-- Recurse! -->
				<xsl:call-template name="remove-punctuation-and-enforce-camel-case">
					<xsl:with-param name="string" select="$afterUpperCharString"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$normalizedString"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- Trim all whitespace from the front and end of a string, but do not normalize any spaces within the main text -->
	<xsl:template name="trim">
		<xsl:param name="string" select="." />
		<xsl:variable name="stringAfterTrimStart">
			<xsl:call-template name="trim-start">
				<xsl:with-param name="string" select="$string" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="trim-end">
			<xsl:with-param name="string" select="$stringAfterTrimStart" />
		</xsl:call-template>
	</xsl:template>


	<!-- Trim all whitespace from the front of a string -->
	<xsl:template name="trim-start">
		<xsl:param name="string" select="." />
		<xsl:choose>
			<xsl:when test="starts-with($string, ' ')">
				<xsl:call-template name="trim-start">
					<xsl:with-param name="string" select="substring-after($string, ' ')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- Trim all whitespace from the end of a string -->
	<xsl:template name="trim-end">
		<xsl:param name="string" select="." />
		<xsl:variable name="endsWithSpace">
			<xsl:call-template name="ends-with">
				<xsl:with-param name="string1" select="$string" />
				<xsl:with-param name="string2" select="' '" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$endsWithSpace = 'true'">
				<xsl:call-template name="trim-end">
					<xsl:with-param name="string" select="substring($string, 1, string-length($string) - 1)" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- Normalize whitespace within a string, but do not trim the start or end to less than 1 space -->
	<xsl:template name="normalize-space-without-trimming">
		<xsl:param name="string" select="." />
		<xsl:if test="string-length($string) &gt; 0">
			<xsl:choose>
				<xsl:when test="string-length(normalize-space($string)) &gt; 0">
					<!-- If starts with spaces, preserve one space; otherwise, no space -->
					<xsl:variable name="spaceToPreserveFromStart">
						<xsl:if test="starts-with($string, ' ')">
							<xsl:value-of select="' '"/>
						</xsl:if>
					</xsl:variable>
					<!-- If ends with spaces, preserve one space; otherwise, no space -->
					<xsl:variable name="spaceToPreserveFromEnd">
						<xsl:variable name="endsWithSpace">
							<xsl:call-template name="ends-with">
								<xsl:with-param name="string1" select="$string" />
								<xsl:with-param name="string2" select="' '" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:if test="$endsWithSpace = 'true'">
							<xsl:value-of select="' '"/>
						</xsl:if>
					</xsl:variable>
					<xsl:value-of select="$spaceToPreserveFromStart"/>
					<xsl:value-of select="normalize-space($string)"/>
					<xsl:value-of select="$spaceToPreserveFromEnd"/>
				</xsl:when>
				<xsl:otherwise>
					<!-- Else if string only contains spaces, pump out a single space -->
					<xsl:value-of select="' '"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>


	<!-- Get the XPath of a given node -->
	<xsl:template name="get-xpath-of-node">
		<xsl:param name="node" select="." />

		<xsl:variable name="thisNodeType">
			<xsl:call-template name="get-node-type">
				<xsl:with-param name="node" select="$node" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<!-- If the root node is selected -->
			<xsl:when test="$thisNodeType = 'root'">
				<xsl:call-template name="get-node-name">
					<xsl:with-param name="node" select="$node" />
				</xsl:call-template>
			</xsl:when>
			<!-- Otherwise, iterate through self and ancestor nodes (but ignore the root node) -->
			<xsl:otherwise>
				<xsl:for-each select="$node/ancestor-or-self::node()">
					<xsl:variable name="nodeName">
						<xsl:call-template name="get-node-name" />
					</xsl:variable>
					<xsl:variable name="nodeType">
						<xsl:call-template name="get-node-type" />
					</xsl:variable>
					<xsl:if test="string-length($nodeName) &gt; 0 and $nodeType != 'root'">
						<xsl:text>/</xsl:text>
						<xsl:value-of select="$nodeName"/>
						<xsl:if test="$nodeType != 'attribute' and $nodeType != 'namespace'">
							<xsl:variable name="countOfPrecedingSiblingsWithSameNodeName">
								<xsl:call-template name="get-count-of-preceding-siblings-with-same-node-name">
									<xsl:with-param name="currentNodeName" select="$nodeName" />
								</xsl:call-template>
							</xsl:variable>
							<xsl:text>[</xsl:text>
							<xsl:value-of select="number($countOfPrecedingSiblingsWithSameNodeName) + 1" />
							<xsl:text>]</xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Subtemplate for the "get-xpath-of-node" template -->
	<xsl:template name="get-node-name">
		<xsl:param name="node" select="." />

		<xsl:variable name="nodeName" select="name($node)" />
		<xsl:variable name="nodeType">
			<xsl:call-template name="get-node-type">
				<xsl:with-param name="node" select="$node" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$nodeType = 'element'">
				<xsl:value-of select="$nodeName" />
			</xsl:when>
			<xsl:when test="$nodeType = 'attribute'">
				<xsl:text>@</xsl:text>
				<xsl:value-of select="$nodeName" />
			</xsl:when>
			<xsl:when test="$nodeType = 'text'">
				<xsl:text>text()</xsl:text>
			</xsl:when>
			<xsl:when test="$nodeType = 'comment'">
				<xsl:text>comment()</xsl:text>
			</xsl:when>
			<xsl:when test="$nodeType = 'processing-instruction'">
				<xsl:text>processing-instruction("</xsl:text>
				<xsl:value-of select="$nodeName" />
				<xsl:text>")</xsl:text>
			</xsl:when>
			<xsl:when test="$nodeType = 'namespace'">
				<xsl:text>namespace::</xsl:text>
				<xsl:value-of select="$nodeName" />
			</xsl:when>
			<xsl:when test="$nodeType = 'root'">
				<xsl:text>/</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Subtemplate for the "get-xpath-of-node" template -->
	<xsl:template name="get-node-type">
		<xsl:param name="node" select="." />
		<xsl:choose>
			<xsl:when test="$node/self::*">
				<xsl:text>element</xsl:text>
			</xsl:when>
			<xsl:when test="$node/self::text()">
				<xsl:text>text</xsl:text>
			</xsl:when>
			<xsl:when test="$node/self::comment()">
				<xsl:text>comment</xsl:text>
			</xsl:when>
			<xsl:when test="$node/self::processing-instruction()">
				<xsl:text>processing-instruction</xsl:text>
			</xsl:when>
			<xsl:when test="count($node | /) = 1">
				<xsl:text>root</xsl:text>
			</xsl:when>
			<xsl:when test="count($node | $node/parent::*/@*) = count($node/parent::*/@*)">
				<xsl:text>attribute</xsl:text>
			</xsl:when>
			<xsl:when test="count($node | $node/parent::*/namespace::*) = count($node | $node/parent::*/namespace::*)">
				<xsl:text>namespace</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- Subtemplate for the "get-xpath-of-node" template -->
	<xsl:template name="get-count-of-preceding-siblings-with-same-node-name">
		<xsl:param name="currentNode" select="." />
		<xsl:param name="currentCount" select="0" />
		<xsl:param name="currentNodeName">
			<xsl:call-template name="get-node-name">
				<xsl:with-param name="node" select="$currentNode" />
			</xsl:call-template>
		</xsl:param>

		<xsl:variable name="precedingSib" select="$currentNode/preceding-sibling::node()[1]" />
		<xsl:choose>
			<xsl:when test="$precedingSib">
				<xsl:variable name="precedingSibName">
					<xsl:call-template name="get-node-name">
						<xsl:with-param name="node" select="$precedingSib" />
					</xsl:call-template>
				</xsl:variable>
				<!-- This makes a number out the Boolean result of testing the node names for equality (0 for false, 1 for true) -->
				<xsl:variable name="matchedNodeName" select="number($precedingSibName = $currentNodeName)" />
				<xsl:call-template name="get-count-of-preceding-siblings-with-same-node-name">
					<xsl:with-param name="currentNode" select="$precedingSib" />
					<xsl:with-param name="currentCount" select="$currentCount + $matchedNodeName" />
					<xsl:with-param name="currentNodeName" select="$currentNodeName" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$currentCount" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="url-encode">
		<xsl:param name="str"/>
		<!-- Characters we'll support.  We could add control chars 0-31 and 127-159, but we won't unless proven needed -->
		<xsl:variable name="ascii"> !"#$%&amp;'()*+,-./0123456789:;&lt;=&gt;?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~</xsl:variable>
		<xsl:variable name="latin1">&#160;&#161;&#162;&#163;&#164;&#165;&#166;&#167;&#168;&#169;&#170;&#171;&#172;&#173;&#174;&#175;&#176;&#177;&#178;&#179;&#180;&#181;&#182;&#183;&#184;&#185;&#186;&#187;&#188;&#189;&#190;&#191;&#192;&#193;&#194;&#195;&#196;&#197;&#198;&#199;&#200;&#201;&#202;&#203;&#204;&#205;&#206;&#207;&#208;&#209;&#210;&#211;&#212;&#213;&#214;&#215;&#216;&#217;&#218;&#219;&#220;&#221;&#222;&#223;&#224;&#225;&#226;&#227;&#228;&#229;&#230;&#231;&#232;&#233;&#234;&#235;&#236;&#237;&#238;&#239;&#240;&#241;&#242;&#243;&#244;&#245;&#246;&#247;&#248;&#249;&#250;&#251;&#252;&#253;&#254;&#255;</xsl:variable>
		<!-- Characters that shouln't need to be escaped -->
		<xsl:variable name="safe">!'()*-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~</xsl:variable>
		<xsl:variable name="hex" >0123456789ABCDEF</xsl:variable>
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
								<xsl:message terminate="no">Warning: string contains a character that is out of range! Substituting "?".</xsl:message>
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

	<xsl:template name="json-encode">
		<xsl:param name="str"/>
		<xsl:if test="$str">
			<xsl:variable name="strWithSlash">
				<xsl:call-template name="replace">
					<xsl:with-param name="string" select="$str" />
					<xsl:with-param name="pattern" select="'\'" />
					<xsl:with-param name="replacement" select="'\\'"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="strWithEncodedSlash">
				<xsl:call-template name="replace">
					<xsl:with-param name="string" select="$strWithSlash" />
					<xsl:with-param name="pattern" select="'&amp;#092;'" />
					<xsl:with-param name="replacement" select="'\\'"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$strWithEncodedSlash" />
				<xsl:with-param name="pattern" select="'&quot;'" />
				<xsl:with-param name="replacement" select="'\&quot;'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- Clean up phone numbers - remove text like 'Fax:'. -->
	<xsl:template name="clean-phone-number">
		<xsl:param name="rawPhoneNumber" select="." />

		<!-- Replace 'Fax:' with '' (Example Guid: I138154501DD211B2AED8A5006205D942). -->
		<xsl:variable name="phoneNumberWithFaxRemoved">
			<xsl:choose>
				<xsl:when test="contains($rawPhoneNumber, 'Fax: ')">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="$rawPhoneNumber" />
						<xsl:with-param name="pattern" select="'Fax: '" />
						<xsl:with-param name="replacement" select="''"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="contains($rawPhoneNumber, 'Fax:')">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="$rawPhoneNumber" />
						<xsl:with-param name="pattern" select="'Fax:'" />
						<xsl:with-param name="replacement" select="''"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$rawPhoneNumber"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:value-of select="$phoneNumberWithFaxRemoved"/>
	</xsl:template>

	<!-- Clean up urls - remove text like 'Blog:'. -->
	<xsl:template name="clean-url">
		<xsl:param name="rawUrl" select="." />

		<!-- Replace 'Fax:' with '' (Example Guid: I138154501DD211B2AED8A5006205D942). -->
		<xsl:variable name="urlWithBlogRemoved">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$rawUrl" />
				<xsl:with-param name="pattern" select="'Blog:'" />
				<xsl:with-param name="replacement" select="''"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- For future
		<xsl:variable name="urlWith???Removed">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$urlWithBlogRemoved" />
				<xsl:with-param name="pattern" select="'Website:'" />
				<xsl:with-param name="replacement" select="''"/>
			</xsl:call-template>
		</xsl:variable>		
		-->

		<xsl:value-of select="normalize-space($urlWithBlogRemoved)"/>
	</xsl:template>

</xsl:stylesheet>
