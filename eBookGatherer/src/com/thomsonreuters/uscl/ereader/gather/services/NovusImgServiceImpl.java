package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageConverter;
import com.thomsonreuters.uscl.ereader.gather.img.util.ImageMetadataHandler;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.thomsonreuters.uscl.ereader.gather.util.images.ImageConverterException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.MediaType;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.westgroup.novus.productapi.BLOB;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * @deprecated Should be removed after related integration test will be fixed
 */
public class NovusImgServiceImpl implements NovusImgService
{
    // "missing_image_guids.txt"
    private String missingImageGuidsFileBasename;
    private NovusFactory novusFactory;
    private NovusUtility novusUtility;
    private ImageConverter imageConverter;
    /** Milliseconds to sleep between each meta-data/bytes fetch */
    private long sleepIntervalBetweenImages;

    private static final Logger Log = LogManager.getLogger(NovusImgServiceImpl.class);

    int missingImageCount;
    int missingMetadataCount;

    private static final String TIF = "tif";
    private static final String PING_EXTENTION = "png";
    private static final String PING_FORMAT = "PNG";
    private static final String[] KNOWN_IMG_FORMATS = {"PNG", "TIF", "JPG", "GIF", "BMP"};
    private List<String> uniqueImageGuids;

    public List<String> getUniqueImageGuids()
    {
        return uniqueImageGuids;
    }

    public void setUniqueImageGuids(final List<String> uniqueImageGuids)
    {
        this.uniqueImageGuids = uniqueImageGuids;
    }

    /**
     * Reads imageGuids from file Format/doc-to-image-manifest.txt and gets
     * images from Novus and writes images to Gather/Images/Dynamic directory.
     */
    @Override
    public GatherResponse getImagesFromNovus(
        final File imgToDocManifestFile,
        final File dynamicImageDirectory,
        final boolean isFinalStage)
    {
        try
        {
            // Create list of image guids gathered from a previous step and
            // stored in a flat text file, one per line
            final Map<String, String> imgDocGuidMap = readLinesFromTextFile(imgToDocManifestFile);
            return fetchImages(imgDocGuidMap, dynamicImageDirectory, isFinalStage);
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
        }
        return new GatherResponse();
    }

    @Override
    public GatherResponse fetchImages(
        final Map<String, String> imgDocGuidMap,
        final File imageDestinationDirectory,
        final boolean isFinalStage) throws Exception
    {
        final GatherResponse gatherResponse = new GatherResponse();
        Novus novus = null;
        try
        {
            novus = novusFactory.createNovus(isFinalStage);
        }
        catch (final NovusException e)
        {
            final GatherException ge = new GatherException(
                "Novus error occurred while creating Novus object " + e,
                GatherResponse.CODE_NOVUS_ERROR);
            throw ge;
        }

        final List<ImgMetadataInfo> imageMetadataList = new ArrayList<ImgMetadataInfo>();
        final File missingImagesFile = new File(imageDestinationDirectory.getParent(), missingImageGuidsFileBasename);
        final FileOutputStream stream = new FileOutputStream(missingImagesFile);
        final Writer fileWriter = new OutputStreamWriter(stream, "UTF-8");
        missingImageCount = 0;
        missingMetadataCount = 0;
        try
        {
            final Find find = novus.getFind();
            find.setResolveIncludes(true);

            uniqueImageGuids = new ArrayList<String>();

            // Iterate the image GUID's and first fetch image data and then
            // download the image bytes
            for (final String docGuid : imgDocGuidMap.keySet())
            {
                final List<String> deDupImagesArray = new ArrayList<String>();
                final String imgGuidList = imgDocGuidMap.get(docGuid);

                if (imgGuidList != null)
                {
                    final String[] imgDocsArray = imgGuidList.split(",");
                    for (final String imgGuid : imgDocsArray)
                    {
                        if (!deDupImagesArray.contains(imgGuid))
                        {
                            final ImgMetadataInfo imgMetadataInfo =
                                getImagesAndMetadata(find, imgGuid, fileWriter, docGuid, imageDestinationDirectory);
                            if (imgMetadataInfo != null)
                            {
                                imageMetadataList.add(imgMetadataInfo);
                            }

                            // Intentionally pause between invocations of the
                            // Image Vertical REST service as not to pound on it
                            Thread.sleep(sleepIntervalBetweenImages);
                            // Add the image guid to the unique list
                            deDupImagesArray.add(imgGuid);
                        } // end of for-loop
                    }
                } // end of for-loop
            }
        }
        finally
        {
            novus.shutdownMQ();
            gatherResponse.setImageMetadataList(imageMetadataList);
            gatherResponse.setMissingImgCount(missingImageCount);
            fileWriter.close();
        }
        return gatherResponse;
    }

    @Override
    public ImgMetadataInfo getImagesAndMetadata(
        final Find find,
        final String imageGuid,
        final Writer missingImageFileWriter,
        final String docGuid,
        final File imageDirectory) throws GatherException, IOException
    {
        final Integer imgRetryCount = new Integer(novusUtility.getImgRetryCount());
        Integer novusRetryCounter = 0;
        final String collection = null;
        ImgMetadataInfo imgMetadataInfo = null;
        boolean missingImage = true;

        while (novusRetryCounter < imgRetryCount)
        {
            missingImage = true;
            try
            {
                final BLOB blob = find.getBLOB(collection, imageGuid);
                final String mimeType = blob.getMimeType();
                final String imgMetadata = blob.getMetaData();

                if (!isEmpty(mimeType) && !isEmpty(imgMetadata))
                {
                    final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                    parserFactory.setNamespaceAware(true);
                    final XMLReader reader = parserFactory.newSAXParser().getXMLReader();
                    final ImageMetadataHandler imageMetadataHandler = new ImageMetadataHandler();
                    reader.setContentHandler(imageMetadataHandler);
                    reader.parse(new InputSource(new StringReader(imgMetadata)));
                    imgMetadataInfo = imageMetadataHandler.getImgMetadataInfo();

                    final MediaType mediaType = MediaType.valueOf(mimeType);

                    String extension = null;
                    File imageFile = null;

                    FileOutputStream fileOuputStream = null;
                    try
                    {
                        if (mediaType.getType().equals("image") && mediaType.getSubtype().equals(TIF))
                        {
                            imageFile = new File(imageDirectory, imageGuid + "." + PING_EXTENTION);
                            extension = PING_EXTENTION;
                            imageConverter.convertByteImg(blob.getContents(), imageFile.getAbsolutePath(), PING_FORMAT);
                        }
                        else
                        {
                            imageFile = new File(imageDirectory, imageGuid + "." + mediaType.getSubtype());
                            fileOuputStream = new FileOutputStream(imageFile);
                            fileOuputStream.write(blob.getContents());
                            extension = mediaType.getSubtype();
                        }
                        if (mediaType.getType().equals("image")
                            && !ArrayUtils.contains(KNOWN_IMG_FORMATS, mediaType.getSubtype().toUpperCase()))
                        {
                            Log.debug(
                                " Unfamiliar Image format "
                                    + mediaType.getSubtype()
                                    + " found for imageGuid "
                                    + imageGuid);
                        }

                        imgMetadataInfo.setMimeType(mediaType.getType() + "/" + extension);
                        imgMetadataInfo.setSize(imageFile.length());
                        imgMetadataInfo.setDocGuid(docGuid);
                        imgMetadataInfo.setImgGuid(imageGuid);
                    }
                    catch (IOException | ImageConverterException ex)
                    {
                        Log.error("Failed while writing the image for imageGuid " + imageGuid);
                        final GatherException ge = new GatherException(
                            "IMG IO Exception for imageGuid " + imageGuid,
                            ex,
                            GatherResponse.CODE_FILE_ERROR);
                        throw ge;
                    }
                    finally
                    {
                        if (fileOuputStream != null)
                        {
                            fileOuputStream.close();
                        }
                    }

                    missingImage = false;
                }
                else
                {
                    Log.error(
                        "MimeType/Metadata is null for image Guid "
                            + imageGuid
                            + ". Throwing NOVUS exception explicitly");
                    throw new Exception("NOVUS Exception");
                }

                break;
            }
            catch (final Exception exception)
            {
                try
                {
                    Log.error("Exception ocuured while retreiving image for imageGuid " + imageGuid);
                    novusRetryCounter = novusUtility.handleException(exception, novusRetryCounter, imgRetryCount);
                }
                catch (final Exception e)
                {
                    Log.error("Exception in handleException() call from getImagesAndMetadata() " + e.getMessage());
                    novusRetryCounter++;
                }
            }
        }

        if (missingImage)
        {
            Log.error("Could not find dynamic image in NOVUS for imageGuid " + imageGuid);
            if (!uniqueImageGuids.contains(imageGuid))
            {
                missingImageCount++;
                uniqueImageGuids.add(imageGuid);
            }

            writeFailedImageGuidToFile(missingImageFileWriter, imageGuid, docGuid);
        }

        return imgMetadataInfo;
    }

    private static void writeFailedImageGuidToFile(final Writer missingImageFileWriter, final String imageGuid, final String docGuid)
        throws IOException
    {
        missingImageFileWriter.write(imageGuid + "," + docGuid);
        missingImageFileWriter.write("\n");
    }

    public boolean isEmpty(final String string)
    {
        if (string == null)
        {
            return true;
        }
        else
        {
            return StringUtils.isEmpty(string.trim());
        }
    }

    /**
     * Reads the contents of a text file and return each line as an element in
     * the returned list. The file is assumed to already exist.
     *
     * @file textFile the text file to process
     * @return a map of text strings, representing each file of the specified
     *         file
     */
    private static Map<String, String> readLinesFromTextFile(final File textFile) throws IOException
    {
        final Map<String, String> imgDocGuidMap = new HashMap<String, String>();
        final FileReader fileReader = new FileReader(textFile);
        final BufferedReader reader = new BufferedReader(fileReader);
        try
        {
            String textLine;
            while ((textLine = reader.readLine()) != null)
            {
                if (StringUtils.isNotBlank(textLine))
                {
                    final String[] imgGuids = textLine.split("\\|");
                    if (imgGuids.length > 1)
                    {
                        imgDocGuidMap.put(imgGuids[0].trim(), imgGuids[1]);
                    }
                    else
                    {
                        imgDocGuidMap.put(imgGuids[0].trim(), null);
                    }
                }
            }
        }
        finally
        {
            if (fileReader != null)
            {
                fileReader.close();
            }
            if (reader != null)
            {
                reader.close();
            }
        }
        return imgDocGuidMap;
    }

    @Required
    public void setMissingImageGuidsFileBasename(final String basename)
    {
        missingImageGuidsFileBasename = basename;
    }

    @Required
    public void setNovusFactory(final NovusFactory factory)
    {
        novusFactory = factory;
    }

    @Required
    public void setImageConverter(final ImageConverter imageConverter)
    {
        this.imageConverter = imageConverter;
    }

    @Required
    public void setNovusUtility(final NovusUtility novusUtil)
    {
        novusUtility = novusUtil;
    }

    @Required
    public void setSleepIntervalBetweenImages(final long interval)
    {
        sleepIntervalBetweenImages = interval;
    }
}
