# Goal
Deploy a new version of your code.  See how the deploy impacts your listener rules.

# Steps
1. Navigate in the source code to [`app/views/employee.ejs`](app/views/employee.ejs).
1. On line 36 you will see where the version is set that you saw earlier.  Change this to whatever you want and save the file.  Then we'll redeploy to see the change.
1. Run the same small script as before - repeated below - to zip up this code and upload to S3.  
    > :pushpin: **NOTE:** If you are viewing this in the remote Git repo, note that the find/replace values are incorrect.  However, you can directly copy/paste these commands from within your local file.
    ```sh
    SOURCE_ZIPFILE_NAME="pipeline-source.zip"
    S3_BUCKET_NAME="a206296-tr-tax-prof1-cicd-nonprod-us-east-1-cfn"
    S3_SOURCE_BUCKET_DIR="TEN-Acct-Id" # FIXME Change this to your u ID (e.x. u0106226)
    AWS_PROFILE="tr-tax-prof1-cicd-nonprod"
    REGION="us-east-1"
    zip -q -r ${SOURCE_ZIPFILE_NAME} .
    aws --profile ${AWS_PROFILE} --region ${REGION} s3 cp ${SOURCE_ZIPFILE_NAME} s3://${S3_BUCKET_NAME}/${S3_SOURCE_BUCKET_DIR}/
    rm ${SOURCE_ZIPFILE_NAME}
    ```

1. Navigate back to the Pipeline to see it travel through the stages again.
1. When you get the email, **DO NOT** press approve yet.  Let us navigate into the console again to the ALB.
1. Click the listeners tab | view/edit rules
1. Notice how the priorities are set up to allow for traffic without a header to get to the old version of your app.  Further, at a higher priority (lower numerically), there is another rule to allow traffic to your new version at the same path but with the addition of your header.  
This is how you can test your app if you wish!
1. Approve the release of your new version.  
    > :pushpin: **NOTE:** If you do not receive the email, see the Appendix at the bottom of this page.

1. Reestablish the tunnel:
    ```sh
    ALB_DNS_NAME="Your alb DNS name"
    AWS_PROFILE="tr-tax-prof1-cicd-nonprod"
    REGION="us-east-1"
    cloud-tool --profile ${AWS_PROFILE} --region ${REGION} generic-ssh-tunnel -c ${ALB_DNS_NAME} -q 80 -r 8080
    ```
1. Go back into the browser tab with your app and hit refresh.  


# Appendix
If you do not receive the email to approve the release, there is a way to do in from the command line.  To do so, follow these instructions:
1. Run this command to list the cumulus tables in this account.
    ```sh
    cumulus bluegreen list-tables
    ```

1. Find the table with your group name, then use it to run the following command to find the deployment ID you need to approve.
    ```sh
    cumulus bluegreen list-pending-deployments --table a206296-u0106226-bluegreen-deployer-table-nonprod-v1
    ```

3. Use the blue green ID and the table name to approve the release.
      ```sh
    cumulus) bens-mbp:docker-ccng-sampleapp-v1 ben$ cumulus bluegreen approve-deployment --table a206296-u0106226-bluegreen-deployer-table-nonprod-v1 --id fc15314b-d2ac-452e-bbdf-322b65a6672e --go
      ```