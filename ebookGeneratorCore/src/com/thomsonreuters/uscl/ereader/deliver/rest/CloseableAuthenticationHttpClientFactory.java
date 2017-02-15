package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.net.InetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.Assert;

/**
 * Serves up configured instances of {@link CloseableHttpClient} with the provided credentials.
 *
 *
 * <p>This factory is immutable once created. The {@link AuthScope} is set to {@link AuthScope.ANY_PORT}</p>
 *
 * @author <a href="mailto:dong.kim@thomsonreuters.com">Dong Kim</a> u0155568
 *
 */
public class CloseableAuthenticationHttpClientFactory
{
    private InetAddress host;
    private String username;
    private String password;

    public CloseableAuthenticationHttpClientFactory(final String hostname, final String username, final String password)
        throws Exception
    {
        Assert.isTrue((hostname != null), "hostname must not be null");
        Assert.isTrue(StringUtils.isNotBlank(username), "username must not be null");
        Assert.isTrue(StringUtils.isNotBlank(password), "password must not be null");
        host = InetAddress.getByName(hostname);
        this.username = username;
        this.password = password;
    }

    public HttpClient getCloseableAuthenticationHttpClient()
    {
        // Get the changeable host name from the configuration
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(host.getHostName(), AuthScope.ANY_PORT),
            new UsernamePasswordCredentials(username, password));

        final CloseableHttpClient closeableHttpClient =
            HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();

        return closeableHttpClient;
    }

    /**
     * The allows for the dynamic change of the
     * @param host
     */
    public void setHost(final InetAddress host)
    {
        this.host = host;
    }
}
