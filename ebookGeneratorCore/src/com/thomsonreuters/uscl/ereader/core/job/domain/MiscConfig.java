package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;

/**
 * Application and root log4j loggers log level configuration.
 */
public class MiscConfig {
	private static Logger log = Logger.getLogger(MiscConfig.class);
	/** Typesafe representation of the keys used to represent the throttling configuration */
	public static enum Key { appLogLevel, rootLogLevel, novusEnvironment, proviewHostname };
	
	public static final Level DEFAULT_APP_LOG_LEVEL = Level.INFO;
	public static final Level DEFAULT_ROOT_LOG_LEVEL = Level.ERROR;
	
	/** Current application log4j logging level, String in order to serialize */
	private String appLogLevel;
	/** Current root log4j logging level, String in order to serialize */
	private String rootLogLevel;
	/** Novus environment */
	private NovusEnvironment novusEnvironment;
	/** Proview service provider host - needed also as a String field because for the JiBX mapping, InetAddress is not serializable, thus
	 * two properties to store the same property value so that we can have a serializable String field. */
	private String proviewHostname;
	
	
	public MiscConfig() {
		super();
		setAppLogLevel(DEFAULT_APP_LOG_LEVEL);
		setRootLogLevel(DEFAULT_ROOT_LOG_LEVEL);
		setNovusEnvironment(NovusEnvironment.Client); // Initial default
		try {
			setProviewHost(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			log.error("Failed to set proview host", e);
		}
	}
	/**
	 * Full constructor.
	 */
	public MiscConfig(Level appLogLevel, Level rootLogLevel,
					  NovusEnvironment novusEnv, String proviewHostname) {
		setAllProperties(appLogLevel, rootLogLevel, novusEnv, proviewHostname);
	}

	/**
	 * Copy the property values from one object to this one.
	 * @param config the source object to copy property values from.
	 */
	public void copy(MiscConfig config) {
		setAllProperties(config.getAppLogLevel(), config.getRootLogLevel(),
						 config.getNovusEnvironment(), config.getProviewHostname());
	}

	private synchronized void setAllProperties(Level appLogLevel, Level rootLogLevel,
											   NovusEnvironment novusEnv, String proviewHostname) {
		setAppLogLevel(appLogLevel);
		setRootLogLevel(rootLogLevel);
		setNovusEnvironment(novusEnv);
		setProviewHostname(proviewHostname);
	}

	public Level getAppLogLevel() {
		return Level.toLevel(appLogLevel);
	}
	public Level getRootLogLevel() {
		return Level.toLevel(rootLogLevel);
	}
	public NovusEnvironment getNovusEnvironment() {
		return novusEnvironment;
	}
	public InetAddress getProviewHost() {
		try {
			return InetAddress.getByName(proviewHostname);
		} catch (UnknownHostException e) {
			return null;
		}
	}
	public String getProviewHostname() {
		return proviewHostname;
	}
	public void setAppLogLevel(Level logLevel) {
		this.appLogLevel = (logLevel != null) ? logLevel.toString() : DEFAULT_APP_LOG_LEVEL.toString();
	}
	public void setRootLogLevel(Level logLevel) {
		this.rootLogLevel = (logLevel != null) ? logLevel.toString() : DEFAULT_ROOT_LOG_LEVEL.toString();
	}
	public void setNovusEnvironment(NovusEnvironment novusEnvironment) {
		this.novusEnvironment = novusEnvironment;
	}
	public void setProviewHost(InetAddress host) {
		Assert.notNull(host);
		this.proviewHostname = host.getHostName();
	}
	public void setProviewHostname(String hostname) {
		this.proviewHostname = hostname;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appLogLevel == null) ? 0 : appLogLevel.hashCode());
		result = prime * result
				+ ((rootLogLevel == null) ? 0 : rootLogLevel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MiscConfig other = (MiscConfig) obj;
		if (appLogLevel == null) {
			if (other.appLogLevel != null)
				return false;
		} else if (!appLogLevel.equals(other.appLogLevel))
			return false;
		if (rootLogLevel == null) {
			if (other.rootLogLevel != null)
				return false;
		} else if (!rootLogLevel.equals(other.rootLogLevel))
			return false;
		return true;
	}
}
