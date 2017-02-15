package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.jaxb.adapter.LevelAdapter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * Application and root log4j loggers log level configuration.
 */
@XmlRootElement(name = "miscConfig", namespace = "com.thomsonreuters.uscl.ereader.core.job.domain")
public class MiscConfig
{
    private static Logger log = LogManager.getLogger(MiscConfig.class);

    /**
     * Typesafe representation of the keys used to represent the throttling
     * configuration
     */
    public enum Key
    {
        appLogLevel,
        rootLogLevel,
        novusEnvironment,
        proviewHostname,
        disableExistingSingleTitleSplit,
        maxSplitParts
    };

    public static final Level DEFAULT_APP_LOG_LEVEL = Level.INFO;
    public static final Level DEFAULT_ROOT_LOG_LEVEL = Level.ERROR;
    public static final int MAX_EBOOK_SPLIT_SIZE = 5;

    /** Current application log4j logging level, String in order to serialize */
    private String appLogLevel = DEFAULT_APP_LOG_LEVEL.toString();
    /** Current root log4j logging level, String in order to serialize */
    private String rootLogLevel = DEFAULT_ROOT_LOG_LEVEL.toString();
    /** Novus environment */
    private NovusEnvironment novusEnvironment;
    /**
     * Proview service provider host - needed also as a String field because for
     * the JiBX mapping, InetAddress is not serializable, thus two properties to
     * store the same property value so that we can have a serializable String
     * field.
     */
    private String proviewHostname;
    private boolean disableExistingSingleTitleSplit;
    private int maxSplitParts;

    public MiscConfig()
    {
        super();
        setAppLogLevel(DEFAULT_APP_LOG_LEVEL);
        setRootLogLevel(DEFAULT_ROOT_LOG_LEVEL);
        setNovusEnvironment(NovusEnvironment.Client); // Initial default
        setDisableExistingSingleTitleSplit(true);
        setMaxSplitParts(MAX_EBOOK_SPLIT_SIZE);
        try
        {
            setProviewHost(InetAddress.getLocalHost());
        }
        catch (final UnknownHostException e)
        {
            log.error("Failed to set proview host", e);
        }
    }

    /**
     * Full constructor.
     */
    public MiscConfig(
        final Level appLogLevel,
        final Level rootLogLevel,
        final NovusEnvironment novusEnv,
        final String proviewHostname,
        final boolean disableExistingSingleTitleSplit,
        final int maxSplitParts)
    {
        setAllProperties(
            appLogLevel,
            rootLogLevel,
            novusEnv,
            proviewHostname,
            disableExistingSingleTitleSplit,
            maxSplitParts);
    }

    /**
     * Copy the property values from one object to this one.
     *
     * @param config
     *            the source object to copy property values from.
     */
    public void copy(final MiscConfig config)
    {
        setAllProperties(
            config.getAppLogLevel(),
            config.getRootLogLevel(),
            config.getNovusEnvironment(),
            config.getProviewHostname(),
            config.getDisableExistingSingleTitleSplit(),
            config.getMaxSplitParts());
    }

    private synchronized void setAllProperties(
        final Level appLogLevel,
        final Level rootLogLevel,
        final NovusEnvironment novusEnv,
        final String proviewHostname,
        final boolean disableExistingSingleTitleSplit,
        final int maxSplitParts)
    {
        setAppLogLevel(appLogLevel);
        setRootLogLevel(rootLogLevel);
        setNovusEnvironment(novusEnv);
        setProviewHostname(proviewHostname);
        setDisableExistingSingleTitleSplit(disableExistingSingleTitleSplit);
        setMaxSplitParts(maxSplitParts);
    }

    public int getMaxSplitParts()
    {
        return maxSplitParts;
    }

    @XmlElement(name = "maxSplitParts", required = true)
    public void setMaxSplitParts(final int maxSplitParts)
    {
        this.maxSplitParts = maxSplitParts;
    }

    public Level getAppLogLevel()
    {
        return Level.toLevel(appLogLevel);
    }

    public Level getRootLogLevel()
    {
        return Level.toLevel(rootLogLevel);
    }

    public NovusEnvironment getNovusEnvironment()
    {
        return novusEnvironment;
    }

    public InetAddress getProviewHost()
    {
        try
        {
            return InetAddress.getByName(proviewHostname);
        }
        catch (final UnknownHostException e)
        {
            return null;
        }
    }

    public String getProviewHostname()
    {
        return proviewHostname;
    }

    @XmlJavaTypeAdapter(LevelAdapter.class)
    @XmlElement(name = "appLogLevel", required = true)
    public void setAppLogLevel(final Level logLevel)
    {
        appLogLevel = (logLevel != null) ? logLevel.toString() : DEFAULT_APP_LOG_LEVEL.toString();
    }

    @XmlJavaTypeAdapter(LevelAdapter.class)
    @XmlElement(name = "rootLogLevel", required = true)
    public void setRootLogLevel(final Level logLevel)
    {
        rootLogLevel = (logLevel != null) ? logLevel.toString() : DEFAULT_ROOT_LOG_LEVEL.toString();
    }

    @XmlElement(name = "novusEnvironment", required = true)
    public void setNovusEnvironment(final NovusEnvironment novusEnvironment)
    {
        this.novusEnvironment = novusEnvironment;
    }

    @XmlTransient
    public void setProviewHost(final InetAddress host)
    {
        Assert.notNull(host);
        proviewHostname = host.getHostName();
    }

    @XmlElement(name = "proviewHostname", required = true)
    public void setProviewHostname(final String hostname)
    {
        proviewHostname = hostname;
    }

    public boolean getDisableExistingSingleTitleSplit()
    {
        return disableExistingSingleTitleSplit;
    }

    @XmlElement(name = "disableExistingSingleTitleSplit", required = false)
    public void setDisableExistingSingleTitleSplit(final boolean disableExistingSingleTitleSplit)
    {
        this.disableExistingSingleTitleSplit = disableExistingSingleTitleSplit;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appLogLevel == null) ? 0 : appLogLevel.hashCode());
        result = prime * result + ((rootLogLevel == null) ? 0 : rootLogLevel.hashCode());
        result = prime * result + (disableExistingSingleTitleSplit ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MiscConfig other = (MiscConfig) obj;
        if (appLogLevel == null)
        {
            if (other.appLogLevel != null)
                return false;
        }
        else if (!appLogLevel.equals(other.appLogLevel))
            return false;
        if (disableExistingSingleTitleSplit != other.disableExistingSingleTitleSplit)
            return false;
        if (rootLogLevel == null)
        {
            if (other.rootLogLevel != null)
                return false;
        }
        else if (!rootLogLevel.equals(other.rootLogLevel))
            return false;
        return true;
    }
}
