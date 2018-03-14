<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<xsl:include href="PublicRecordsFein.xsl"/>
	
	<xsl:variable name="BIRTHDATE">sensitivedate</xsl:variable>
	<xsl:variable name="CURRENCY">currency</xsl:variable>
	<xsl:variable name="DATE">date</xsl:variable>
	<xsl:variable name="DUNS">duns</xsl:variable>
	<xsl:variable name="FEIN">fein</xsl:variable>
	<xsl:variable name="LICENSE">license</xsl:variable>
	<xsl:variable name="LICENSE_EXPIRATION_DATE">sensitivedate</xsl:variable>
	<xsl:variable name="LICENSEPLATE">plate</xsl:variable>
	<xsl:variable name="NUMBER">number</xsl:variable>
	<xsl:variable name="PHONE">phone</xsl:variable>
	<xsl:variable name="SENSITIVE_DATE">sensitivedate</xsl:variable>
	<xsl:variable name="SSN">ssn</xsl:variable>
	<xsl:variable name="STATE">state</xsl:variable>
	<xsl:variable name="VINNUMBER">vin</xsl:variable>
	<xsl:variable name="COMPANY">company</xsl:variable>

	<xsl:template name="wrapWithTableHeader">
		<xsl:param name="contents"/>
		<th>
			<xsl:if test="string-length($contents) &gt; 0">
				<xsl:copy-of select="$contents"/>
			</xsl:if>
		</th>
	</xsl:template>

	<xsl:template name="wrapWithTableValue">
		<xsl:param name="contents"/>
		<td>
			<xsl:choose>
				<xsl:when test="string-length($contents) &gt; 0">
					<xsl:copy-of select="$contents"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>

	<xsl:template name="wrapPublicRecordsIndexedItem">
		<xsl:param name="defaultLabel"/>
		<xsl:param name="selectNodes" select="/.."/>
		<xsl:param name="nodeType"/>
		<xsl:param name="indexNode" select="/.."/>
		<xsl:param name="indexNumber" select="/.."/>
		<xsl:variable name="index">
			<xsl:choose>
				<xsl:when test ="$indexNode">
					<xsl:value-of select="$indexNode"/>
					<xsl:if test="not(contains($indexNode, '.'))">
						<xsl:text>.</xsl:text>
					</xsl:if>
				</xsl:when>
				<xsl:when test ="$indexNumber">
					<xsl:value-of select="$indexNumber"/>
					<xsl:if test="not(contains($indexNumber, '.'))">
						<xsl:text>.</xsl:text>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="wrapPublicRecordsItem">
			<xsl:with-param name="defaultLabel" select="$defaultLabel"/>
			<xsl:with-param name="selectNodes" select="$selectNodes"/>
			<xsl:with-param name="nodeType" select="$nodeType"/>
			<xsl:with-param name="index" select="$index"/>
		</xsl:call-template>
	</xsl:template>

	<!-- This method takes in a defaultLabel and either uses the label in the document (if it
			 exists) or the default label passed in (if the label in the document does not exist).
			 This method should always be given a default label.  The wrapWithSpan template calls
			 apply-templates using selectNodes, if specified.
			 ASSUMPTION: This template assumes that labels are always in a node with tag name "l"
									 or "label".  If the label ever falls outside this assumption, then this
									 template needs to be revisited. -->
	<xsl:template name="wrapPublicRecordsItem">
		<xsl:param name="defaultLabel"/>
		<xsl:param name="selectNodes" select="/.."/>
		<xsl:param name="nodeType"/>
		<xsl:param name="index"/>
		<xsl:param name="divClass"/>
		<xsl:variable name="label">
			<xsl:choose>
				<xsl:when test="l">
					<xsl:value-of select="l"/>
				</xsl:when>
				<xsl:when test="label">
					<xsl:value-of select="label"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$defaultLabel"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="descendant-or-self::*/text()">
			<tr>
				<xsl:attribute name="class">
					<xsl:text>&pr_item;</xsl:text>
					<xsl:if test="$divClass">
						<xsl:value-of select="concat(' ', $divClass)"/>
					</xsl:if>
				</xsl:attribute>
				<xsl:if test="$index">
					<xsl:call-template name="wrapWithTableHeader">
						<xsl:with-param name="contents" select="$index" />
					</xsl:call-template>
				</xsl:if>
				<xsl:call-template name="wrapWithTableHeader">
					<xsl:with-param name="contents" select="$label" />
				</xsl:call-template>
				<xsl:call-template name="wrapWithTableValueExceptLabel">
					<xsl:with-param name="nodes" select="$selectNodes" />
					<xsl:with-param name="type" select="$nodeType" />
				</xsl:call-template>
			</tr>
		</xsl:if>
	</xsl:template>

	<!-- Is similar to the wrapWithSpan template except that it does not apply-templates to the
			 label node.
			 ASSUMPTION: This template assumes that labels are always in a node with tag name "l"
									 or "label".  If the label ever falls outside this assumption, then this
									 template needs to be revisited. -->
	<xsl:template name="wrapWithTableValueExceptLabel">
		<xsl:param name="nodes" select="/.."/>
		<xsl:param name="type"/>
		<td>
			<xsl:choose>
				<xsl:when test="$type = $NUMBER">
					<xsl:call-template name="FormatNumber"/>
				</xsl:when>
				<xsl:when test="$type = $CURRENCY">
					<xsl:call-template name="FormatCurrency"/>
				</xsl:when>
				<xsl:when test="$type = $DATE">
					<xsl:choose>
						<xsl:when test="$nodes">
							<xsl:call-template name="FormatNonSensitiveDate">
								<xsl:with-param name="dateNode" select="$nodes"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatNonSensitiveDate"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$type = $PHONE">
					<xsl:choose>
						<xsl:when test="$nodes">
							<xsl:call-template name="FormatPhone">
								<xsl:with-param name="phone" select="$nodes"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatPhone"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$type = $SENSITIVE_DATE">
					<xsl:choose>
						<xsl:when test="$nodes">
							<xsl:call-template name="FormatNonSensitiveDate">
								<xsl:with-param name="dateNode" select="$nodes"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatNonSensitiveDate" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$type = $SSN">
					<xsl:choose>
						<xsl:when test="$nodes">
							<xsl:call-template name="SSNProcess">
								<xsl:with-param name="ssnvalue" select="$nodes"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="SSNProcess" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$type = $FEIN">
					<xsl:choose>
						<xsl:when test="$nodes">
							<xsl:call-template name="FormatFein">
							<xsl:with-param name="feinNumber" select="$nodes"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatFein">
								<xsl:with-param name="feinNumber" select="."/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$type = $VINNUMBER or $type = $LICENSEPLATE or $type = $DUNS">
					<xsl:choose>
						<xsl:when test="$nodes">
							<xsl:call-template name="FormatLink">
								<xsl:with-param name="node" select="$nodes"/>
								<xsl:with-param name="type" select="$type"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatLink">
								<xsl:with-param name="node" select="."/>
								<xsl:with-param name="type" select="$type"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$type = $LICENSE">
					<xsl:choose>
						<xsl:when test="$nodes">
							<xsl:call-template name="FormatLink">
								<xsl:with-param name="node" select="$nodes"/>
								<xsl:with-param name="type" select="$type"/>
								<xsl:with-param name="searchableLink" select="'false'"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatLink">
								<xsl:with-param name="node" select="."/>
								<xsl:with-param name="type" select="$type"/>
								<xsl:with-param name="searchableLink" select="'false'"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$type = $COMPANY">
					<xsl:choose>
						<xsl:when test="$nodes">
							<xsl:call-template name="FormatCompany">
								<xsl:with-param name="companyName" select="$nodes"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="FormatCompany"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>

				<xsl:when test="$type = $STATE">
					<xsl:call-template name="GetFullStateName"/>
				</xsl:when>
				<xsl:when test="$nodes">
					<xsl:apply-templates select="text() | $nodes"/>
				</xsl:when>

				<xsl:otherwise>
					<!-- This is the part where we exclude the label. -->
					<xsl:apply-templates select="text() | *[not(self::l | self::label)]"/>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>

	<xsl:template name="wrapDocketBankruptcySSN">
		<xsl:param name="defaultLabel"/>
		<xsl:param name="nodes" select="/.."/>
		<xsl:param name="type"/>

		<tr class="&docketsRowClass;">
			<td class="&docketsRowLabelClass;">
				<xsl:value-of select="$defaultLabel"/>
			</td>

			<td class="&docketsRowTextClass;">
				<xsl:choose>
					<xsl:when test="$nodes">
						<xsl:call-template name="SSNProcess">
							<xsl:with-param name="ssnvalue" select="$nodes"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="SSNProcess" />
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<!-- Template to format currency values and to make the value highlighted when searched-->
	<xsl:template name="FormatCurrency">
		<xsl:param name="currValue" select="."/>
		<xsl:choose>
			<!-- If it is formatted currency display as it is-->
			<xsl:when test="contains($currValue,'$')">
				<xsl:apply-templates/>
			</xsl:when>
			<!-- If the value is already formated number with the decimal like '12,345.00',put currency only-->
			<xsl:when test="contains($currValue,',') and contains($currValue,'.')">
				<xsl:text>&#36;</xsl:text>
				<xsl:apply-templates/>
			</xsl:when>
			<!-- If the value is not formated number but has the decimal like '12345.00'-->
			<xsl:when test="contains($currValue,'.') and not(contains($currValue,'0.'))">
				<xsl:variable name="formattedNumber" select="format-number($currValue, '$###,###.##')"/>
				<xsl:variable name="tempValue" select="substring-after($formattedNumber, '.')"/>
				<xsl:choose>
					<xsl:when test="string-length($tempValue)=0">
						<xsl:value-of select="concat($formattedNumber,'.00')"/>
					</xsl:when>
					<xsl:when test="string-length($tempValue)=1">
						<xsl:value-of select="concat($formattedNumber,'0')"/>
					</xsl:when>
					<xsl:when test="string-length($tempValue)=2">
						<xsl:value-of select="$formattedNumber"/>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<!-- If the value is not formated number but has the decimal like '12340.00'-->
			<xsl:when test="contains($currValue,'0.')">
				<xsl:variable name="formattedCurrency" select="format-number($currValue, '$#,###,##0.######')"/>
				<xsl:variable name="tempCurrency" select="substring-after($formattedCurrency, '.')"/>
				<xsl:choose>
					<xsl:when test="string-length($tempCurrency)=0">
						<xsl:value-of select="concat($formattedCurrency,'.00')"/>
					</xsl:when>
					<xsl:when test="string-length($tempCurrency)=1">
						<xsl:value-of select="concat($formattedCurrency,'0')"/>
					</xsl:when>
					<xsl:when test="string-length($tempCurrency)>=2">
						<xsl:value-of select="$formattedCurrency"/>
					</xsl:when>
				</xsl:choose>
			</xsl:when>
			<!-- If the value is already formated number with out the decilal like '12,345',put currency and '.00' decimal-->
			<xsl:when test="contains($currValue,',') and not(contains($currValue,'.'))">
				<xsl:text>&#36;</xsl:text>
				<xsl:apply-templates/>
				<xsl:text>.00</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<!-- If not formated number like '12345', format it for currency-->
				<xsl:value-of select="format-number($currValue, '$###,###,###,###,###,###,###,##0.00')" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="FormatPhone">
		<xsl:param name="phone" select="."/>
		<xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>
		<xsl:variable name="phoneDigits">
			<xsl:value-of select="normalize-space($phone)"/>
		</xsl:variable>
		<xsl:variable name="linkContents">
			<xsl:choose>
				<!-- If phone contains any non-numeric and non-space, assume it is already formatted. -->
				<xsl:when test="string-length(translate($phone, '0123456789 ', '')) &gt; 0">
					<xsl:value-of select="$phone"/>
				</xsl:when>
				<xsl:when test="string-length($phoneDigits) = 7">
					<xsl:value-of select="substring($phoneDigits, 1, 3)" />
					<xsl:text>-</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 4, 4)" />
				</xsl:when>
				<xsl:when test="string-length($phoneDigits) = 8">
					<xsl:value-of select="substring($phoneDigits,1, 2)" />
					<xsl:text>&nbsp;</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 3, 2)" />
					<xsl:text>&nbsp;</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 5, 2)" />
					<xsl:text>&nbsp;</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 7, 2)" />
				</xsl:when>
				<xsl:when test="string-length($phoneDigits) = 9">
					<xsl:value-of select="substring($phoneDigits,1, 2)" />
					<xsl:text>&nbsp;</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 3, 3)" />
					<xsl:text>&nbsp;</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 6, 4)" />
				</xsl:when>
				<xsl:when test="string-length($phoneDigits) = 10">
					<xsl:value-of select="substring($phoneDigits,1, 3)" />
					<xsl:text>-</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 4, 3)" />
					<xsl:text>-</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 7, 3)" />
				</xsl:when>
				<xsl:when test="string-length($phoneDigits) = 11">
					<xsl:value-of select="substring($phoneDigits,1, 1)" />
					<xsl:text>-</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 2, 3)" />
					<xsl:text>-</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 5, 3)" />
					<xsl:text>-</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 8, 4)" />
				</xsl:when>
				<xsl:when test="string-length($phoneDigits) = 12">
					<xsl:text>+</xsl:text>
					<xsl:value-of select="substring($phoneDigits,1, 2)" />
					<xsl:text>&nbsp;</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 3, 3)" />
					<xsl:text>&nbsp;</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 6, 3)" />
					<xsl:text>&nbsp;</xsl:text>
					<xsl:value-of select="substring($phoneDigits, 9, 4)" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$phone"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
				<xsl:variable name="searchUrl">
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=Phone', concat('phone=', $phone), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
				</xsl:variable>
				<a>
					<xsl:attribute name="class">
						<xsl:text>&pr_link;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$searchUrl"/>
					</xsl:attribute>
					<xsl:copy-of select="$linkContents"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$linkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
  <xsl:template name="FormatCompany">
    <xsl:param name="companyName" select="."/>
    <xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>
    <xsl:choose>
      <!--xsl:when test="$searchableLink='nope' or $searchableLink='nope'"-->
      <xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
        <xsl:variable name="searchUrl">
          <xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=CompanyName', concat('CompanyName=', $companyName), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
        </xsl:variable>
        <a>
          <xsl:attribute name="class">
            <xsl:text>&pr_link;</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="href">
            <xsl:copy-of select="$searchUrl"/>
          </xsl:attribute>
          <xsl:value-of select="$companyName"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$companyName"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

	<xsl:template name="wrapPublicRecordsEmail">
			<xsl:param name="label" select="'&pr_email;'"/>
		<xsl:param name="email" select="/.."/>
		<xsl:param name="user" select="/.."/>
		<xsl:param name="domain" select="/.."/>
		<tr class="&pr_item;">
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="$label"/>
			</xsl:call-template>
			<td>
				<xsl:call-template name="FormatEmailAddress">
					<xsl:with-param name="email" select="$email"/>
					<xsl:with-param name="user" select="$user"/>
					<xsl:with-param name="domain" select="$domain"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="FormatEmailAddress">
		<xsl:param name="email" select="/.."/>
		<xsl:param name="user" select="/.."/>
		<xsl:param name="domain" select="/.."/>
		<xsl:param name="searchableLink" select="'false'"/>

		<xsl:variable name="linkContents">
			<xsl:choose>
				<xsl:when test="string-length($email) > 0">
					<xsl:value-of select="$email"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$user"/>
					<xsl:if test="not(contains($domain, '@'))">
						<xsl:text>@</xsl:text>
					</xsl:if>
					<xsl:value-of select="$domain"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
				<xsl:variable name="searchUrl">
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'searchType=Email', concat('email=', $linkContents), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
				</xsl:variable>
				<a>
					<xsl:attribute name="class">
						<xsl:text>&pr_link;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$searchUrl"/>
					</xsl:attribute>
					<xsl:copy-of select="$linkContents"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$linkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="FormatLink">
		<xsl:param name="node" select="/.."/>
		<xsl:param name="type" select="/.."/>
		<xsl:param name="searchableLink" select="/Document/n-docbody/LinkedTextData/ShowSearchLink"/>

		<xsl:variable name="searchType">
			<xsl:choose>
				<xsl:when test="$type = $LICENSE">
					<xsl:text>DriversLicense</xsl:text>
				</xsl:when>
				<xsl:when test="$type = $VINNUMBER">
					<xsl:text>VIN</xsl:text>
				</xsl:when>
				<xsl:when test="$type = $LICENSEPLATE">
					<xsl:text>LicensePlate</xsl:text>
				</xsl:when>
				<xsl:when test="$type = $FEIN">
					<xsl:text>FEIN</xsl:text>
				</xsl:when>
				<xsl:when test="$type = $DUNS">
					<xsl:text>DUNS</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="linkContents">
			<xsl:value-of select="$node"/>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$searchableLink='true' or $searchableLink='TRUE'">
				<xsl:variable name="searchUrl">
					<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', concat('searchType=', $searchType), concat(concat($type, '='), $linkContents), concat('PU=', $PermissibleUse), $specialVersionParamVariable, $specialRequestSourceParamVariable)"/>
				</xsl:variable>
				<a>
					<xsl:attribute name="class">
						<xsl:text>&pr_link;</xsl:text>
					</xsl:attribute>
					<xsl:attribute name="href">
						<xsl:copy-of select="$searchUrl"/>
					</xsl:attribute>
					<xsl:copy-of select="$linkContents"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$linkContents"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="FormatNumber">
		<xsl:param name ="number" select="."/>
		<xsl:choose>
			<!-- If it is formatted currency display as it is-->
			<xsl:when test="contains($number,',') or contains($number,'.') or string-length($number)&lt;=3">
				<xsl:apply-templates/>
			</xsl:when>
			<xsl:otherwise>
				<!-- If not formated number like '12345', format it for standard number format-->
				<xsl:value-of select="format-number($number,'#,###,###,##0')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="displayLabelValue">
		<xsl:param name="label" select="'label'"/>
		<xsl:param name="value" select="'value'"/>
		<tr class="&pr_item;">
			<xsl:call-template name="wrapWithTableHeader">
				<xsl:with-param name="contents" select="$label"/>
			</xsl:call-template>
			<td>
				<xsl:value-of select="$value"/>
			</td>
		</tr>
	</xsl:template>
    
	<xsl:template name="RPTransactionSearch"> 
		<xsl:param name ="novusQuery" select="."/>
		<xsl:param name ="linkContents" select="."/>
		<xsl:choose>
		<xsl:when test="$novusQuery">			
			<xsl:variable name="searchUrl">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'categoryPageUrl=Home/PublicRecords/PublicRecordsTemplates/RealPropertyRecords/RealPropertyTransactions', 'searchType=Composite', 'originationContext=Default', 'runAsWLClassicQuery=true', concat('PU=', $PermissibleUse), concat('query=', $novusQuery))"/>
			</xsl:variable>
			<br/><a>
				<xsl:attribute name="class">
					<xsl:text>pr_RPLink</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="href">
					<xsl:copy-of select="$searchUrl"/>
				</xsl:attribute>
				<xsl:copy-of select="$linkContents"/>
			</a><br/>
	    </xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RPAssessorSearch">
		<xsl:param name ="novusQuery" select="."/>
		<xsl:param name ="linkContents" select="."/>
		<xsl:if test="$novusQuery">
			<xsl:variable name="searchUrl">
				<xsl:value-of select="UrlBuilder:CreatePersistentUrl('Page.PublicRecordsSearch', 'categoryPageUrl=Home/PublicRecords/PublicRecordsTemplates/RealPropertyRecords/RealPropertyTaxAssessorRecords', 'searchType=Composite', 'originationContext=Default', 'runAsWLClassicQuery=true', concat('PU=', $PermissibleUse), concat('query=', $novusQuery))"/>
			</xsl:variable>
			<br/><a>
				<xsl:attribute name="class">
					<xsl:text>pr_RPLink</xsl:text>
				</xsl:attribute>				
				<xsl:attribute name="href">
					<xsl:copy-of select="$searchUrl"/>
				</xsl:attribute>
				<xsl:copy-of select="$linkContents"/>
			</a><br/>
	    </xsl:if>
	</xsl:template>

</xsl:stylesheet>
