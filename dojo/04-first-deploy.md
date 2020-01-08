# Goal
Deploy the first version of your application.  See how the pipeline progresses through the stages and where the deployment waits for testing.

# Steps
1. Zip up the source files from your local folder, ensuring the name of the zip file is what your pipeline expects.  
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

1. Navigate to the console | CodePipeline and search for your TEN-Acct-Id and watch your pipeline work!
1. When it gets to the Deploy_dev stage's Running step, click Details to see the step function driving the blue/green deployment.
1. Eventually you will get an email letting you know that your application is ready for review.  As this is the initial deploy, go ahead and approve it.  Next time we deploy however, we will look at the listener rules to understand how we do blue/green.  
> :pushpin: **NOTE:** If you do not receive the email, see the Appendix at the bottom of this page.

1. The step function should finish a few seconds later.
1. Navigate to the console in the target account (as opposed to the CICD account where we have been previously) to EC2 | Loadbalancers.  You should be able to find your ALB by searching the name.  It will take the syntax `a206296-dojo-TEN-Acct-Id-dev`
1. Find the DNS name for your ALB.  For example, mine is `internal-a206296-dojo-u6065223-dev-23278987.us-east-1.elb.amazonaws.com`
1. Modify the variables below and run the following command using cloud-tool:  
  ```sh
  ALB_DNS_NAME="Your alb DNS name"
  AWS_PROFILE="tr-tax-prof1-preprod"
  REGION="us-east-1"
  cloud-tool --profile ${AWS_PROFILE} --region ${REGION} generic-ssh-tunnel -c ${ALB_DNS_NAME} -q 80 -r 8080
  ```
1. Open your browser and navigate to `http://localhost:8080`
1. Notice the version in the top right.  To simulate a deploy, we will change this value in the source code and redeploy later.

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
