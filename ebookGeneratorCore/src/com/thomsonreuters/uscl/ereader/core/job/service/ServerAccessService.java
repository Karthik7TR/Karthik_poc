package com.thomsonreuters.uscl.ereader.core.job.service;

import com.thomsonreuters.uscl.ereader.util.EBookServerException;

/**
 * Stops all the generator and gather instances from server,
 * notifies user group about server shutdown.
 * update all the unfinished jobs to failed exit status.
 *
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public interface ServerAccessService {
    /**
     * Stops all the generator and gather instances from server,
     * notifies user group about server shutdown.
     * update all the unfinished jobs to failed exit status.
     *
     * @param serverNames ',' separated server names.
     * @param userName
     * @param password
     * @param appNames	',' separated application names (eBookGatherer,eBookGenerator)
     * @param emailGroup
     * @throws EBookServerException
     */
    String stopServer(String serverNames, String userName, String password, String appNames, String emailGroup)
        throws EBookServerException;

    /**
     * Starts all the generator and gather instances from server,
     * notifies user group about server startup.
     * update all the unfinished jobs to failed exit status.
     *
     * @param serverNames ',' separated server names.
     * @param userName
     * @param password
     * @param appNames	',' separated application names (eBookGatherer,eBookGenerator)
     * @param emailGroup
     * @throws EBookServerException
     */
    String startServer(String serverNames, String userName, String password, String appNames, String emailGroup)
        throws EBookServerException;

    /**
     * Sends sends email notification on startup only if any of the jobs were updated.
     * @param serverName
     * @param emailGroup
     * @throws EBookServerException
     */
    void notifyJobOwnerOnServerStartup(String serverName, String emailGroup) throws EBookServerException;
}
