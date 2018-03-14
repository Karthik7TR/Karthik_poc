<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:msxsl="urn:schemas-microsoft-com:xslt">
	<xsl:include href="Universal.xsl" />
	<xsl:include href="Copyright.xsl" />
	<xsl:include href="FlatListTransform.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<xsl:variable name="australianCopyrightTextVar">
		<xsl:text>&australiaCopyrightPrefixText;</xsl:text>
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:text>&copy;</xsl:text>
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:value-of select="$currentYear"/>
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:copy-of select="'&australiaCopyrightText;'"/>
	</xsl:variable>
	
	<!-- Document -->
	<xsl:template match="Document">
		<div id="&documentClass;">

			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&australiaDocumentClass;'"/>
			</xsl:call-template>

			<xsl:call-template name="DocumentHead" />
			<xsl:apply-templates select="//n-docbody" />

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText" select="$australianCopyrightTextVar" />
				<xsl:with-param name="endOfDocumentCopyrightTextVerbatim" select="'true'" />
			</xsl:call-template>

		</div>
	</xsl:template>

	<!-- Document heading -->
	<xsl:template name="DocumentHead">
		<div class="&titleClass;">
			<xsl:apply-templates select="n-docbody/shared-metadata[1]/leg-title" />
			<xsl:apply-templates select="n-docbody/shared-metadata[1]/leg-jurisdiction" />
			<xsl:apply-templates select="n-docbody/shared-metadata[1]/leg-identifier" />
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
	</xsl:template>

	<xsl:template match="leg-title | leg-jurisdiction | leg-identifier">
		<xsl:apply-templates />
		<br />
	</xsl:template>

	<xsl:template match="head" priority="1">
		<xsl:choose>
			<xsl:when test="parent::schedule.struct">
				<div class="&sectionTitleClass; &alignHorizontalCenterClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:when test="child::heading and not(@role='sidenote')">
				<div class="&sectionTitleClass;">
					<xsl:apply-templates />
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="heading">
		<span class="&uBoldClass;">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<!-- Display headings inline and surround it by brackets -->
	<xsl:template match="heading[parent::head[@role='sidenote']]">
		<span class="&uBoldClass;">
			<xsl:call-template name="ContentInBrackets" />
			<xsl:text><![CDATA[ ]]></xsl:text>
		</span>
	</xsl:template>

	<xsl:template match="subprov.group/heading">
		<div>
			<i>
				<xsl:apply-templates />
			</i>
		</div>
	</xsl:template>

	<!--
		Get the count value indicating whether the first preceding sibling of a Note should render it explicitly.
		0 - This Note should be rendered implicitly.
		1 - This Note should be rendered explicitly from its preceding sibling.
	-->
	<xsl:template name="ExplicitPrecedingSiblingCount">
		<xsl:value-of select="count(./preceding-sibling::*[1][self::prov.body or self::legpara])" />
	</xsl:template>
	
	<!-- Notes without a label, root definition (suppressed by default) -->
	<xsl:template match="notes">
		<xsl:param name="forceNotesProcessing" select="false()" />
		<xsl:variable name="explicitPrecedingSiblingCount">
			<xsl:call-template name="ExplicitPrecedingSiblingCount" />
		</xsl:variable>

		<xsl:if test="$forceNotesProcessing or ($explicitPrecedingSiblingCount = 0)">
			<div>
				<xsl:attribute name="class">
					<xsl:call-template name="GetNotesClasses" />
				</xsl:attribute>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>
	
	<!-- Notes containing a label, root definition (suppressed by default) -->
	<xsl:template match="notes[./legnote[@type='note']/label]">
		<xsl:param name="forceNotesProcessing" select="false()" />
		<xsl:variable name="explicitPrecedingSiblingCount">
			<xsl:call-template name="ExplicitPrecedingSiblingCount" />
		</xsl:variable>

		<xsl:if test="$forceNotesProcessing or ($explicitPrecedingSiblingCount = 0)">
			<div>
				<xsl:attribute name="class">
					<xsl:call-template name="GetNotesClasses" />
				</xsl:attribute>
				<table>
					<xsl:apply-templates select="legnote" />
				</table>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Get classes for .Notes element -->
	<xsl:template name="GetNotesClasses">
		<xsl:choose>
			<xsl:when test="$DeliveryMode = 'True'">
				<xsl:value-of select="'&notesClass; &notesWideClass; &alignHorizontalLeftClass;'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'&notesClass; &alignHorizontalLeftClass;'" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Note label (table markup)-->
	<xsl:template match="notes/legnote[@type='note']/label">
		<span class="&uBoldClass;">
			<xsl:apply-templates />
			<xsl:value-of select="'&nbsp;'" />
		</span>
	</xsl:template>
	
	<!-- Notes containing a label, note content -->
	<xsl:template match="notes[./legnote[@type='note']/label]/legnote/note.body">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!-- First level paragraph of a note with a label -->
	<xsl:template match="notes[./legnote[@type='note']/label]/legnote/note.body/legpara">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!-- Legislation note without a label -->
	<xsl:template match="notes[not(./legnote[@type='note']/label)]/legnote">
		<div class="&indentLeft2Class;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!-- Legnote - table version -->
	<xsl:template match="notes[./legnote[@type='note']/label]/legnote">
		<tr>
			<td class="&notesLabelClass;">
				<xsl:apply-templates select="label[1]"/>
			</td>
			<td class="&notesContentClass;">
				<xsl:apply-templates select="note.body" />
			</td>
		</tr>
	</xsl:template>
	
	<!-- Label + Heading followed by prov.body pair, Heading definition (suppressed by default) -->
	<xsl:template match="heading[./preceding-sibling::*[1][self::label]][../following-sibling::prov.body]">
		<xsl:param name="suppressHeadingAfterLabel" select="true()" />

		<xsl:if test="not($suppressHeadingAfterLabel)">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!-- Generic label definition -->
	<xsl:template match="label">
		<span class="&uBoldClass;">
			<xsl:apply-templates />
			<xsl:value-of select="'&nbsp;'" />
		</span>
	</xsl:template>

	<!-- Label + Heading followed by prov.body pair definition -->
	<xsl:template match="label[./following-sibling::*[1][self::heading]][../following-sibling::prov.body]">
		<div class="&paraMainClass; &uBoldClass;">
			<xsl:apply-templates />
			<xsl:value-of select="' '" />
			<xsl:if test="../parent::schedule.struct">
				<xsl:text>- </xsl:text>
			</xsl:if>
			<xsl:apply-templates select="./following-sibling::*[1]">
				<xsl:with-param name="suppressHeadingAfterLabel" select="false()" />
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<!-- List start definitions -->
	<xsl:template match="label.para[not(./preceding-sibling::*[1][self::label.para])]
								| subprov[not(./preceding-sibling::*[1][self::subprov])]
								| note[not(./preceding-sibling::*[1][note])]">
		<div class="&paraMainClass;">
			<xsl:call-template name="TransformFlatListToHTMLList" />
		</div>
	</xsl:template>

	<!-- List elements definitions -->
	<xsl:template match="label.para[./preceding-sibling::*[1][self::label.para]]
								| subprov[./preceding-sibling::*[1][self::subprov]]
								| note[./preceding-sibling::*[1][note]]">
		<!-- Suppress -->
	</xsl:template>

	<xsl:template name="ListItems">
		<li>
			<div class="&paratextMainClass;">
				<xsl:apply-templates />
			</div>
		</li>
	</xsl:template>

	<xsl:template match="leg-history">
		<div class="&legHistoryClass;">
			<xsl:call-template name="ContentInBrackets" />
		</div>
	</xsl:template>

	<xsl:template name="ContentInBrackets">
		<xsl:text>[</xsl:text>
			<xsl:apply-templates />
		<xsl:text>]</xsl:text>
	</xsl:template>

	<xsl:template match="member">
		<div class="&simpleListMemberClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Prov.body or legpara definition -->
	<xsl:template match="prov.body | legpara">
		<div class="&paraMainClass; &indentLeft2Class;">
			<xsl:apply-templates />
			<xsl:if test="./following-sibling::notes">
				<xsl:apply-templates select="../notes">
					<xsl:with-param name="forceNotesProcessing" select="true()" />
				</xsl:apply-templates>
			</xsl:if>
		</div>
	</xsl:template>
	
	<!--
		Get the rowspan offset: the difference between the number of expected cell places and defined cell places.
		See: I000e8140550311e3a58ee8246a0d8a12 (namest/nameend table columns)
		See: If98b7df3dad311e3ab83f3bc313da523 (simple table columns)
		See: Idbb1a5dc26f911e1a43fd4ce27b6d22b (mixed column definitions)
		See: I00b952b91c6011e3bff3b67d5f7ffa46 (mixed column definitions, no position attributes)
	-->
	<xsl:template name="GetRowspanOffset">
		<xsl:param name="row" />
		
		<!-- Calculate named row positions (position attribute can be missing) -->
		<xsl:variable name="colspecWithPositions">
			<xsl:for-each select ="$row/../../colspec">
				<xsl:if test="@colname">
					<item>
						<xsl:attribute name="name">
							<xsl:value-of select="@colname" />
						</xsl:attribute>
						<xsl:attribute name="position">
							<xsl:value-of select="position()" />
						</xsl:attribute>
					</item>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		
		<!-- Number of cell places that should be occupied by columns in current row -->
		<xsl:variable name="placesShouldBeOccupied" select="
			number(count($row/entry[not(@namest or @nameend)]))
			+ sum(
				msxsl:node-set($colspecWithPositions)/item[@name = $row/entry[@namest and @nameend]/@nameend]/@position
			)
			- sum(
				msxsl:node-set($colspecWithPositions)/item[@name = $row/entry[@namest and @nameend]/@namest]/@position
			)
			+ number(count($row/entry[@namest and @nameend]))
		" />
		<xsl:variable name="cellsMaxPossible" select="number($row/../../@cols)" />
		<xsl:value-of select="number($cellsMaxPossible) - number($placesShouldBeOccupied)" />
	</xsl:template>
	
	<!-- Get the number of rowspans for a table cell defined by entry -->
	<xsl:template name="GetRowSpan">
		<xsl:param name="entry" />
		<xsl:choose>
			<xsl:when test="$entry/@morerows">
				<xsl:value-of select="number($entry/@morerows) + 1" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="number(0)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Get colspan for a table cell defined by entry -->
	<xsl:template name="GetColSpan">
		<xsl:param name="entry" />

		<xsl:choose>
			<!-- If a cell occupies more than 1 horizontal position -->
			<xsl:when test="$entry/@namest and $entry/@nameend and not($entry/@namest = $entry/@nameend)">
				<xsl:value-of select="
					number(
						number($entry/../../../colspec[@colname=$entry/@nameend]/@colnum) 
						- number($entry/../../../colspec[@colname=$entry/@namest]/@colnum)
						+ 1
					)" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="number(0)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
		Override product regular table entry processing in order to disable table cells repairing &
		fix colspan processing.
	-->
	<xsl:template match="tbody/row/entry">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
	
		<td>
			<xsl:call-template name="SetCellAttributesAndRenderCell">
				<xsl:with-param name="columnInfo" select="$columnInfo" />
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
			</xsl:call-template>
		</td>
	</xsl:template>
	
	<!--
		Override product heading table entry processing in order to disable table cells repairing &
		fix colspan processing.
	-->
	<xsl:template match="thead/row/entry | tgroup/row/entry">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
	
		<th>
			<xsl:call-template name="SetCellAttributesAndRenderCell">
				<xsl:with-param name="columnInfo" select="$columnInfo" />
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
			</xsl:call-template>
		</th>
	</xsl:template>
	
	<!--
		Put a row placeholder instead of a row and repeat the procedure for the next row
		if necessary.
	-->
	<xsl:template name="PutSpaceInsteadOfRow">
		<div>
			<xsl:value-of select="'&nbsp;'" />
		</div>
		<!-- Repeat for the next row if it is a part of an empty heading cluster -->
		<xsl:if test="
			not(./following-sibling::*[1][self::row]/entry/node())
			and count(./following-sibling::*[1]/entry) = ../../@cols">
			<xsl:apply-templates select="./following-sibling::*[1]">
				<xsl:with-param name="suppress" select="false()" />
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<!-- The row that should be replaced by the whitespace (suppressed by default) -->
	<xsl:template match="thead/row[
		not(./entry/node()) 
		and count(./entry) = ../../@cols
		and (
			./preceding-sibling::row and
			not(./preceding-sibling::*[1][self::row]/entry/node())
		)
	]">
		<xsl:param name="suppress" select="true()" />
			
		<xsl:if test="not($suppress)">
			<xsl:call-template name="PutSpaceInsteadOfRow" />
		</xsl:if>
	</xsl:template>
	
	<!-- Topmost row of an empty heading cluster -->
	<xsl:template match="thead/row[
			not(./entry/node())
			and count(./entry) = ../../@cols
			and (
				not(./preceding-sibling::row) or
				./preceding-sibling::*[1][self::row]/entry/node()
			)
		]">
		<tr>
			<td>
				<xsl:attribute name="colspan">
					<xsl:value-of select="../../@cols" />
				</xsl:attribute>
			
				<xsl:call-template name="PutSpaceInsteadOfRow" />
			</td>
		</tr>
	</xsl:template>
	
	<!-- Set all necessary cell attributes and render a content -->
	<xsl:template name="SetCellAttributesAndRenderCell">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		
		<!-- Rowspan value for a curent cell -->
		<xsl:variable name="rowspan">
			<xsl:call-template name="GetRowSpan">
				<xsl:with-param name="entry" select="." />
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Colspan value for a current cell -->
		<xsl:variable name="colspan">
			<xsl:call-template name="GetColSpan">
				<xsl:with-param name="entry" select="." />
			</xsl:call-template>
		</xsl:variable>
			
		<!-- Set the rowspan if necessary -->
		<xsl:if test="number($rowspan) &gt; 1">
			<xsl:attribute name="rowspan">
				<xsl:value-of select="$rowspan" />
			</xsl:attribute>
		</xsl:if>

		<!-- Set the colspan if necessary -->
		<xsl:if test="number($colspan) &gt; 1">
			<xsl:attribute name="colspan">
				<xsl:value-of select="$colspan" />
			</xsl:attribute>
		</xsl:if>
			
		<!-- Horizontal alignment for the whole column -->
		<xsl:variable name="wholeColumnAlign" select="$columnInfo/@align" />
			
		<xsl:variable name="align">
			<xsl:choose>
				<xsl:when test="wholeColumnAlign">
					<xsl:value-of select="$wholeColumnAlign" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$colalign" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Render cell content -->
		<xsl:call-template name="RenderTableCell">
			<xsl:with-param name="columnInfo" select="$columnInfo"/>
			<xsl:with-param name="colalign" select="$align" />
			<xsl:with-param name="colposition" select="$colposition" />
			<xsl:with-param name="colwidth" select="$colwidth" />
		</xsl:call-template>
	</xsl:template>

	<!-- Table definition -->
	<xsl:template match="legtable | informaltable">
		<xsl:choose>
			<xsl:when test="$DeliveryMode = 'True' and not(@frame = 'none') and number(tgroup/@colsep) = 0 and number(tgroup/@rowsep) = 0">
				<div class="&tableGroupClass; &tableGroupFrameClass;">
					<table>
						<tr>
							<td>
								<div class="&tableGroupClass;">
									<xsl:apply-templates />
								</div>
							</td>
						</tr>
					</table>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="InnerTable" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="InnerTable">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&tableGroupClass;</xsl:text>

				<xsl:if test="not(@frame = 'none')">
					<xsl:value-of select="' '" />
					<xsl:text>&tableGroupBorderClass;</xsl:text>
				
					<xsl:if test="number(tgroup/@colsep) = 0">
						<xsl:value-of select="' '" />
						<xsl:text>&tableGroupNoColumnBorderClass;</xsl:text>
					</xsl:if>
			
					<xsl:if test="number(tgroup/@rowsep) = 0">
						<xsl:value-of select="' '" />
						<xsl:text>&tableGroupNoRowBorderClass;</xsl:text>
					</xsl:if>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Definitions of terms container -->
	<xsl:template match="def-para">
		<div class="&paraMainClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- Definitions of terms -->
	<xsl:template match="term">
		<span class="&italicClass; &uBoldClass;">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<!-- Show image replacement text instead of an image -->
	<xsl:template match="mediaobject/imageobject">
		<div class="&paraMainClass;">
			<strong>
				<xsl:text>&imagesReplacementText;</xsl:text>
			</strong>
		</div>
	</xsl:template>

	<!-- Suppress image descriptions -->
	<xsl:template match="mediaobject/textobject[preceding-sibling::imageobject]" />

	<!-- Provision tables -->
	<xsl:template match="seglistitem[@role='legpart'] | seglistitem[@role='div']">
		<!-- First render the title of the group -->
		<div class="&paraMainClass;">
			<xsl:if test="seg">
				<xsl:choose>
					<xsl:when test="seg[parent::seglistitem/@role = 'div']">
						<div class="&paraMainClass;">
							<h3 class="&printHeadingClass;">
								<xsl:apply-templates select="seg" />
							</h3>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div class="&paraMainClass;">
							<h2 class="&printHeadingClass;">
								<xsl:apply-templates select="seg" />
							</h2>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:apply-templates select="seglistitem[@role='prov']" />
		</div>
	</xsl:template>

	<xsl:template match="seg[parent::seglistitem[@role='legpart'] and position()=1]">
		<xsl:choose>
			<xsl:when test="parent::seglistitem">
				<strong>
					<xsl:apply-templates />
				</strong>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="seg[parent::seglistitem and position() &gt; 1] ">
		<xsl:if test="preceding-sibling::seg">
			&#160;
		</xsl:if>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="seglistitem[@role='prov']">
		<div class="&paraMainClass; &indentLeft3Class;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="seglistitem[@role='schedule']">
		<div class="&paraMainClass; &alignHorizontalCenterClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="seglistitem[@role='schedule']/seg[1]">
		<span class="&uppercaseClass;">
			<xsl:apply-templates />
			<xsl:value-of select="'&dashSplitter;'" />
		</span>
	</xsl:template>

	<xsl:template match="seglistitem[@role='schedule']/seg[position() &gt; 1]">
		<span class="&uppercaseClass;">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<!--
		Links to file versions of documents
		See: I5859fd8a9cb911e088a4c4b2eb8a5af1
	-->
	<xsl:template match="download-binary[@format='link' and  @type='doc']" />
	
	<xsl:template match="anchor | shared-metadata | sectioninfo" />
	
</xsl:stylesheet>
