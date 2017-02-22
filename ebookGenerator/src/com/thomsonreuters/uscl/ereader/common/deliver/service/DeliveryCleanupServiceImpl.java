package com.thomsonreuters.uscl.ereader.common.deliver.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.deliver.step.DeliverStep;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class DeliveryCleanupServiceImpl implements DeliveryCleanupService
{
    private static final Logger LOG = LogManager.getLogger(DeliveryCleanupServiceImpl.class);

    @Resource(name = "proviewHandler")
    private ProviewHandler proviewHandler;
    @Value("2")
    private Integer maxNumberOfRetries;
    @Value("15")
    private Integer sleepTimeInMinutes;
    @Value("2")
    private Integer baseSleepTimeInMinutes;

    @Override
    public void cleanup(final DeliverStep step)
    {
        for (final String splitTitle : step.getPublishedSplitTitles())
        {
            removeWithRetry(step, splitTitle);
        }
    }

    private void removeWithRetry(final DeliverStep step, final String splitTitle)
    {
        waitProview(baseSleepTimeInMinutes);
        for (int i = 0; i <= maxNumberOfRetries; i++)
        {
            try
            {
                removeTitle(step, splitTitle);
                return;
            }
            catch (final ProviewException e)
            {
                if (e.getMessage().equalsIgnoreCase(CoreConstants.TTILE_IN_QUEUE))
                {
                    LOG.warn(
                        String.format(
                            "Retriable status received: waiting %d minutes (retryCount: %d)",
                            sleepTimeInMinutes,
                            i + 1));
                    waitProview(sleepTimeInMinutes);
                }
                else
                {
                    throw new ProviewRuntimeException(e.getMessage());
                }
            }
        }
        throw new ProviewRuntimeException(
            String.format(
                "Tried %d times to remove part of the split title %s. Proview might be down "
                    + "or still in the process of loading the book. Please try again later.",
                maxNumberOfRetries + 1,
                splitTitle));
    }

    private void removeTitle(final DeliverStep step, final String splitTitle) throws ProviewException
    {
        final Version bookVersion = step.getBookVersion();
        final String response = proviewHandler.removeTitle(splitTitle, bookVersion);
        if (response.contains("200"))
        {
            proviewHandler.deleteTitle(splitTitle, bookVersion);
        }
    }

    void waitProview(final Integer timeInMinutes)
    {
        // Most of the books should finish in two minutes
        try
        {
            TimeUnit.MINUTES.sleep(timeInMinutes);
        }
        catch (final InterruptedException e)
        {
            LOG.error("InterruptedException during HTTP retry", e);
            Thread.currentThread().interrupt();
        }
    }

    void setMaxNumberOfRetries(final Integer maxNumberOfRetries)
    {
        this.maxNumberOfRetries = maxNumberOfRetries;
    }
}
