package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.provider;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.thomsonreuters.uscl.ereader.xpp.strategy.BundleFileHandlingStrategy;
import com.thomsonreuters.uscl.ereader.xpp.strategy.provider.BundleFileHandlingStrategyProvider;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step.strategy.PlaceXppMetadataStrategy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step.strategy.provider.PlaceXppMetadataStrategyProviderImpl;
import org.junit.Before;
import org.junit.Test;

public final class PlaceXppMetadataStrategyProviderImplTest
{
    private BundleFileHandlingStrategyProvider<PlaceXppMetadataStrategy> tocGenerationStrategyProvider;

    @Before
    public void onTestSetUp()
    {
        final List<PlaceXppMetadataStrategy> strategies = new ArrayList<>();

        for (final BundleFileType fileType : BundleFileType.values())
        {
            final PlaceXppMetadataStrategy strategy = mock(PlaceXppMetadataStrategy.class);
            when(strategy.getBundleFileTypes()).thenReturn(new HashSet<>(Arrays.asList(fileType)));
            strategies.add(strategy);
        }

        tocGenerationStrategyProvider = new PlaceXppMetadataStrategyProviderImpl(strategies);
    }

    @Test
    public void shouldReturnMainContentStrategy()
    {
        testStrategyProvider("1-CHAL_APX_21.DIVXML.main", BundleFileType.MAIN_CONTENT);
    }

    @Test
    public void shouldReturnSummaryTableOfContentsStrategy()
    {
        testStrategyProvider("100018-volume_1_Summary_Table_of_Contents.DIVXML.xml", BundleFileType.SUMMARY_TABLE_OF_CONTENTS);
    }

    @Test
    public void shouldReturnCorrelationTableStrategy()
    {
        testStrategyProvider("100018-volume_1_Table_of_Added_LRRE.DIVXML.xml", BundleFileType.TABLE_OF_ADDED_LRRE);
    }

    @Test
    public void shouldReturnIndexStrategy()
    {
        testStrategyProvider("1001-CHAL_Volume_3_Index.DIVXML.xml", BundleFileType.INDEX);
    }

    @Test
    public void shouldReturnAddedRevisedJudgesCardsStrategy()
    {
        testStrategyProvider("100018-volume_1_AddedRevised_Judges_Cards.DIVXML.xml", BundleFileType.ADDED_REVISED_JUDGES_CARDS);
    }

    @Test
    public void shouldReturnFullSetJudgesCardsStrategy()
    {
        testStrategyProvider("100018-volume_1_Full_Set_Judges_Cards.DIVXML.xml", BundleFileType.FULL_SET_JUDGES_CARDS);
    }

    @Test
    public void shouldReturnTableOfAddedLRREStrategy()
    {
        testStrategyProvider("100018-volume_1_Table_of_Added_LRRE.DIVXML.xml", BundleFileType.TABLE_OF_ADDED_LRRE);
    }

    @Test
    public void shouldReturnTableOfLRREStrategy()
    {
        testStrategyProvider("30007-volume_3_Table_of_LRRE.DIVXML.xml", BundleFileType.TABLE_OF_LRRE);
    }

    @Test
    public void shouldReturnTableOfCasesStrategy()
    {
        testStrategyProvider("30008-volume_3_Table_of_Cases.DIVXML.xml", BundleFileType.TABLE_OF_CASES);
    }

    @Test
    public void shouldReturnKeyNumberTableStrategy()
    {
        testStrategyProvider("100018-volume_1_Key_Number_Table.DIVXML.xml", BundleFileType.KEY_NUMBER_TABLE);
    }

    @Test
    public void shouldReturnTableOfAddedCasesStrategy()
    {
        testStrategyProvider("100018-volume_1_Table_of_Added_Cases.DIVXML.xml", BundleFileType.TABLE_OF_ADDED_CASES);
    }

    @Test
    public void shouldReturnDetailedTableOfContentsStrategy()
    {
        testStrategyProvider("30002-volume_3_Detailed_Table_of_Contents.DIVXML.xml", BundleFileType.DETAILED_TABLE_OF_CONTENTS);
    }

    @Test
    public void shouldReturnFrontStrategy()
    {
        testStrategyProvider("0-CHAL_Front_vol_3.DIVXML.xml", BundleFileType.FRONT);
    }

    @Test
    public void shouldReturnFillingInstructionStrategy()
    {
        testStrategyProvider("1001-Filing_Instructions.DIVXML.xml", BundleFileType.FILLING_INSTRUCTIONS);
    }

    @Test
    public void shouldReturnSummaryAndDetailedTableOfContentsStrategy()
    {
        testStrategyProvider("10002-volume_1_Summary_and_Detailed_Table_of_Contents.DIVXML.xml", BundleFileType.SUMMARY_AND_DETAILED_TABLE_OF_CONTENTS);
    }

    @Test
    public void shouldReturnTableOfAddedKeyNumbersStrategy()
    {
        testStrategyProvider("100018-volume_1_Table_of_Added_Key_Numbers.DIVXML.xml", BundleFileType.TABLE_OF_ADDED_KEY_NUMBERS);
    }

    @Test
    public void shouldReturnImpositionListStrategy()
    {
        testStrategyProvider("100018-volume_1_Imposition_List.DIVXML.xml", BundleFileType.IMPOSITION_LIST);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowUnsupportedOperationException()
    {
        tocGenerationStrategyProvider
            .getStrategy(BundleFileType.getByFileName("100018-volume_1_Table_of_Added_LRRE.FROMXSF.txt"));
    }

    private void testStrategyProvider(final String fileName, final BundleFileType expectedFileType)
    {
        final BundleFileHandlingStrategy strategy = tocGenerationStrategyProvider
            .getStrategy(BundleFileType.getByFileName(fileName));
        assertThat(strategy.getBundleFileTypes(), hasItem(expectedFileType));
    }
}
