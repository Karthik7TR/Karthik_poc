# Goal
Deploy a new version of your code.  See how the deploy impacts your listener rules.

# Steps
1. Navigate in the source code to [`app/views/employee.ejs`](../app/views/employee.ejs#L36).
1. On line 36 you will see where the version is set that you saw earlier.  Change this to whatever you want and save the file.  Then we'll redeploy to see the change.
1. Run the same small script as before - repeated below - to zip up this code and upload to S3.  
    > :pushpin: **NOTE:** If you are viewing this in the remote Git repo, note that the find/replace values are incorrect.  However, you can directly copy/paste these commands from within your local file.
    ```sh
    SOURCE_ZIPFILE_NAME="pipeline-source.zip"
    S3_SOURCE_BUCKET_DIR="u6121086" # FIXME Change this to your u ID
    S3_BUCKET_NAME="a206296-tr-ihn-cicd-sandbox-eu-west-1-dojo"
    AWS_PROFILE="tr-ihn-cicd-sandbox"
    REGION="eu-west-1"
    zip -q -r ${SOURCE_ZIPFILE_NAME} . -x '.git/*'
    aws --profile ${AWS_PROFILE} --region ${REGION} s3 cp ${SOURCE_ZIPFILE_NAME} s3://${S3_BUCKET_NAME}/${S3_SOURCE_BUCKET_DIR}/
    rm ${SOURCE_ZIPFILE_NAME}
    ```

1. Navigate back to the Pipeline to see it travel through the stages again.
1. You will get a email notifying you that testing has started. Unless you've modified the tested code,
    the tests should succeed and you will eventually get the release approval email.
1. When you get the release approval email, **DO NOT** run the approve command yet.  Let us navigate into the console again to the ALB.
1. Click the listeners tab | view/edit rules
1. Notice how the priorities are set up to allow for traffic without a header to get to the old version of your app.  
    Further, at a higher priority (lower numerically), there is another rule to allow traffic to your new version at the same path but with the addition of your header.  
    The [Appendix](#setting-headers) has more information on how to add headers to your requests for testing. You can use these headers to test your application.
1. Approve the release of your new version.  
    > :pushpin: **NOTE:** If you do not receive the email, see the [Appendix](#approving-deployments) at the bottom of this page.

1. Go back into the browser tab with your app and hit refresh.  
1. While your application is undergoing linear deployment, you can keep hitting refresh to switch between the old version and the new version.  
    - The new version will be rare at first, and gradually become more common until it becomes the only version available.  
    When that happens, the deployment is complete.
    - If you want to access the old version while the new version is transitioning, you can [use the blue header](#setting-headers) just as you used the green header above.


# Appendix

## Approving Deployments
If you do not receive the email to approve the release, you can find the command to run via the Cumulus CLI.  To do so, follow these instructions:
1. Run the following command to find the deployment ID and table name you will need to approve the release.
    ```sh
    cumulus bluegreen list-pending-deployments --profile tr-ihn-cicd-sandbox  --region eu-west-1

    ```

1. Use the blue green ID and the table name to approve the release.
    ```sh
    cumulus bluegreen approve-deployment --profile tr-ihn-cicd-sandbox  --region eu-west-1 --table a206296-u6121086-bluegreen-deployer-table-nonprod-v1-eu-west-1 --id fc15314b-d2ac-452e-bbdf-322b65a6672e --go
    ```

## Setting Headers
To access different versions of your applications using different headers, you can install various plugins in your browser.
- For Chrome, you can use [Modify Headers for Google Chrome](https://chrome.google.com/webstore/detail/modify-headers-for-google/innpjfdalfhpcoinfnehdnbkglpmogdi).
    You can import [our configuration for this plugin](./extra/headers_plugin_export.json) to make sure everything is set up correctly.
- For Firefox, you can use [Simple Modify Headers](https://addons.mozilla.org/en-CA/firefox/addon/simple-modify-header/).
    You can import [the same configuration file](./extra/headers_plugin_export.json) as the one above to make sure everything is set up correctly.
- For the command line, you can use the `-H` option in `curl` to add a header. For example:
    ```sh
    curl -H "X-BlueGreen-Routing: blue" https://lab01.cumulus.0491.aws-int.thomsonreuters.com/
    curl -H "X-BlueGreen-Routing: green" https://lab01.cumulus.0491.aws-int.thomsonreuters.com/
    ```

    - If you want to limit the output of `curl` to the line with the version, you can do:
        ```sh
        curl -H "X-BlueGreen-Routing: blue" https://lab01.cumulus.0491.aws-int.thomsonreuters.com/ 2>&1 | grep -i version
        curl -H "X-BlueGreen-Routing: green" https://lab01.cumulus.0491.aws-int.thomsonreuters.com/ 2>&1 | grep -i version
        ```
