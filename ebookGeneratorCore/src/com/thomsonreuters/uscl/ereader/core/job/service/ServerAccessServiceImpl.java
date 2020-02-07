package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.notification.service.EmailService;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobUserInfo;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceService;
import com.thomsonreuters.uscl.ereader.util.EBookServerException;
import com.thomsonreuters.uscl.ereader.util.Ssh;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stops or starts all the generator and gather instances from server,
 * notifies user group about server shutdown.
 * update all the unfinished jobs to failed exit status.
 *
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
@Service("serverAccessService")
public class ServerAccessServiceImpl implements ServerAccessService {
    private static final Logger log = LogManager.getLogger(ServerAccessServiceImpl.class);

    private enum Operation {
        START,
        STOP
    };

    private final JobCleanupService jobCleanupService;
    private final UserPreferenceService userPreferenceService;
    private final EmailService emailService;

    @Autowired
    public ServerAccessServiceImpl(final JobCleanupService jobCleanupService,
        final UserPreferenceService userPreferenceService, final EmailService emailService) {
        this.jobCleanupService = jobCleanupService;
        this.userPreferenceService = userPreferenceService;
        this.emailService = emailService;
    }

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
    @Override
    public String stopServer(
        final String serverNames,
        final String userName,
        final String password,
        final String appNames,
        final String emailGroup) throws EBookServerException {
        final String status =
            checkParametersAndOperate(serverNames, userName, password, appNames, emailGroup, Operation.STOP);
        notifyJobOwnerOnServerShutdown(emailGroup);
        updateJobsInProgress();

        return status;
    }

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
    @Override
    public String startServer(
        final String serverNames,
        final String userName,
        final String password,
        final String appNames,
        final String emailGroup) throws EBookServerException {
        return checkParametersAndOperate(serverNames, userName, password, appNames, emailGroup, Operation.START);
    }

    /**
     * Checks the parameters exists
     *
     * @param serverNames ',' separated server names.
     * @param userName
     * @param password
     * @param appNames	',' separated application names (eBookGatherer,eBookGenerator)
     * @param emailGroup
     * @param operation action to perform on the server.  Start or stop.
     * @throws EBookServerException
     */
    private String checkParametersAndOperate(
        final String serverNames,
        final String userName,
        final String password,
        final String appNames,
        final String emailGroup,
        final Operation operate) throws EBookServerException {
        log.debug(
            "In ServerAccessServiceImpl serverNames = "
                + serverNames
                + " userName = "
                + userName
                + " password = "
                + password
                + " appNames = "
                + appNames
                + " emailGroup = "
                + emailGroup);
        if (serverNames == null || serverNames.isEmpty()) {
            throw new EBookServerException(
                "Failed to " + operate.toString().toLowerCase() + " server(s) as server name is empty.");
        }

        if (appNames == null || appNames.isEmpty()) {
            throw new EBookServerException(
                "Failed to " + operate.toString().toLowerCase() + " server(s) as application name is empty.");
        }

        if (emailGroup == null || emailGroup.isEmpty()) {
            throw new EBookServerException(
                "Failed to " + operate.toString().toLowerCase() + " end users as emailGroup is empty.");
        }

        return createCommandAndExecute(serverNames, userName, password, appNames, operate);
    }

    /**
     * Iterate over list of servers.In Production & QA environment multiple server instances could be running.
     *
     * @param serverNames ',' separated server names.
     * @param userName
     * @param password
     * @param appNames	',' separated application names (eBookGatherer,eBookGenerator)
     * @param operation action to perform on the server.  Start or stop.
     * @throws EBookServerException
     */
    private String createCommandAndExecute(
        final String serverNames,
        final String userName,
        final String password,
        final String appNames,
        final Operation operation) throws EBookServerException {
        final String[] targetServerArray = StringUtils.split(serverNames, ",");
        final String[] appNamesArray = StringUtils.split(appNames, ",");
        String cmd = null;

        final StringBuffer buffer = new StringBuffer();
        for (final String serverName : targetServerArray) {
            for (final String applicationName : appNamesArray) {
                cmd = "for i in `ls /appserver/tomcat/"
                    + applicationName
                    + "_*[^X]/bin/"
                    + operation.toString().toLowerCase()
                    + "Server.sh`; do $i; done";

                buffer.append(operation.toString()).append(" server: ");
                buffer.append(serverName).append(":").append(applicationName);
                buffer.append("\n status: ");
                buffer.append(execute(serverName, userName, password, cmd));
                buffer.append("\n");
                log.info(buffer.toString());
            }
        }

        return buffer.toString();
    }

    /**
     * update jobs job exit status from 'undefined' to failed.
     * @throws EBookServerException
     *
     */
    private void updateJobsInProgress() throws EBookServerException {
        jobCleanupService.cleanUpDeadJobs();
    }

    /**
     * sends email notification on startup only if any of the jobs were updated.
     * @param serverName
     * @param emailGroup
     * @throws EBookServerException
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void notifyJobOwnerOnServerStartup(final String serverName, final String emailGroup)
        throws EBookServerException {
        final List<JobUserInfo> jobListInfo;
        jobListInfo = jobCleanupService.findListOfDeadJobsByServerName(serverName);

        if (jobListInfo != null && jobListInfo.size() > 0) {
            sendEmailsToUsers(emailGroup, jobListInfo);
        } else {
            // Send email only to email group
            final StringBuffer emailBodySB = new StringBuffer();
            emailBodySB.append("Server: ");
            emailBodySB.append(serverName);
            emailBodySB.append(" is up");
            final String subject = "Server is up <EOM>";
            final String emailBody = emailBodySB.toString();

            log.debug("Notification email address : " + emailGroup);
            log.debug("Notification email subject : " + subject);
            log.debug("Notification email body : " + emailBody);

            emailService.send(emailGroup, subject, emailBody);
        }
    }

    /**
     * Email notification is sent to user group regarding server shutdown with list of jobs in progress.
     * These jobs will have to be re-submitted.
     * @param emailGroup
     * @throws EBookServerException
     */
    private void notifyJobOwnerOnServerShutdown(final String emailGroup) throws EBookServerException {
        final List<JobUserInfo> jobListInfo = jobCleanupService.findListOfDeadJobs();

        if (jobListInfo != null && jobListInfo.size() > 0) {
            sendEmailsToUsers(emailGroup, jobListInfo);
            // Send email only to email group
        } else {
            final StringBuffer emailBodySB = new StringBuffer();
            emailBodySB.append("Server is down");
            final String subject = "Server is down <EOM>";
            final String emailBody = emailBodySB.toString();

            log.debug("Notification email address : " + emailGroup);
            log.debug("Notification email subject : " + subject);
            log.debug("Notification email body : " + emailBody);

            emailService.send(emailGroup, subject, emailBody);
        }
    }

    private void sendEmailsToUsers(final String emailGroup, final List<JobUserInfo> jobListInfo)
        throws EBookServerException {
        if (emailGroup == null || emailGroup.isEmpty()) {
            throw new EBookServerException("Failed to notify end users as emailGroup is empty.");
        }

        final Map<String, List<JobUserInfo>> userMap = new HashMap<>();

        final StringBuffer jobInfoListSB = new StringBuffer();

        // Populate userMap to send users emails
        for (final JobUserInfo jobUserInfo : jobListInfo) {
            final String username = jobUserInfo.getUsername();

            // Create a list of JobUserInfo for each username
            if (!userMap.containsKey(username)) {
                // username does not exist in the map, create new list
                final List<JobUserInfo> info = new ArrayList<>();
                info.add(jobUserInfo);
                userMap.put(username, info);
            } else {
                // username already exists in the map, add to the existing list.
                final List<JobUserInfo> info = userMap.get(username);
                info.add(jobUserInfo);
                userMap.put(username, info);
            }

            // append all job info for message to email group
            jobInfoListSB.append(jobUserInfo.getInfoAsCsv());
            jobInfoListSB.append("\n");
        }

        // Email content
        final String subject = "Generator server is down, please resubmit these jobs";
        final String beginningMessage = "Please resubmit these jobs.\n";
        final StringBuffer emailBodySB = new StringBuffer();
        emailBodySB.append(beginningMessage);
        emailBodySB.append(jobInfoListSB);

        log.debug("Notification email address : " + emailGroup);
        log.debug("Notification email subject : " + subject);
        log.debug("Notification email body : " + emailBodySB.toString());

        // Send email to email group
        emailService.send(emailGroup, subject, emailBodySB.toString());

        // Send individual emails to users
        for (final Map.Entry<String, List<JobUserInfo>> entry : userMap.entrySet()) {
            final String username = entry.getKey();
            final UserPreference userPreference = userPreferenceService.findByUsername(username);

            if (userPreference != null) {
                final String emails = userPreference.getEmails();

                // check emails were added in UserPreference
                if (StringUtils.isNotBlank(emails)) {
                    final StringBuffer userJobInfoListSB = new StringBuffer();

                    // Create string for job info related to this username
                    for (final JobUserInfo jobUserInfo : entry.getValue()) {
                        userJobInfoListSB.append(jobUserInfo.getInfoAsCsv());
                        userJobInfoListSB.append("\n");
                    }
                    final StringBuffer userEmailBodySB = new StringBuffer();
                    userEmailBodySB.append(beginningMessage);
                    userEmailBodySB.append(userJobInfoListSB);

                    emailService.send(emails, subject, userEmailBodySB.toString());
                }
            }
        }
    }

    private String execute(final String server, final String user, final String password, final String cmd)
        throws EBookServerException {
        log.debug("Starting " + cmd + "...");

        log.debug("Server: " + server);
        log.debug("User: " + user);
        log.debug("Pass: " + password);

        final String retValue = Ssh.executeCommand(server, user, password, cmd);

        log.debug("Execute Command " + retValue + " value ");

        return retValue;
    }
}
