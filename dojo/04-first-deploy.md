# Goal
Deploy the first version of your application.  See how the pipeline progresses through the stages and where the deployment waits for testing.

# Steps
1. Modify the [`cumulus-deployspec.yaml`](../cicd/cumulus-deployspec.yaml), changing the `# FIXME` lines.  The values for these lines will come from your Cloud IaC project.  Specifically, the outputs from the CloudFormation stacks Cloud IaC created for your infrastructure.  
    > :star: ***HINT***: There are four items under each environment that need changing.  
    > The `ListenerARN` and `APPLICATION_DNS` values come from the ALB stack, while the `SecurityGroup` and `ExecutionRole` values come from the ecs stack.

    This file tells the deployment engine what to do.  Among other things, it says to use the blue-green deployer and what CloudFormation templates to use as well as their parameters.
1. Zip up the source files from your local folder, ensuring the name of the zip file is what your pipeline expects.  
    > :pushpin: **NOTE:** If you are viewing this in the remote Git repo, note that the find/replace values are incorrect.  However, you can directly copy/paste these commands from within your local file.  
    ```sh
    SOURCE_ZIPFILE_NAME="pipeline-source.zip"
    S3_SOURCE_BUCKET_DIR="TEN-Acct-Id" # FIXME Change this to your u ID (e.x. u0123456)
    S3_BUCKET_NAME="a206296-tr-ihn-cicd-sandbox-eu-west-1-dojo"
    AWS_PROFILE="tr-ihn-cicd-sandbox"
    REGION="eu-west-1"
    zip -q -r ${SOURCE_ZIPFILE_NAME} . -x '.git/*'
    aws --profile ${AWS_PROFILE} --region ${REGION} s3 cp ${SOURCE_ZIPFILE_NAME} s3://${S3_BUCKET_NAME}/${S3_SOURCE_BUCKET_DIR}/
    rm ${SOURCE_ZIPFILE_NAME}
    ```

1. Navigate to the console | CodePipeline and search for TEN-Acct-Id to watch your pipeline work!
1. When it gets to the Deploy stage, click Details to see the deployment engine step function.  This is very simple (only one step) in this example, however if you take advantage of any of our perimeter Lambdas, then this will look different.  In any case, click on the step `Deploy using BlueGreen Deployer` then click the link under "Resource" to go to the step function that drives the BlueGreen deployment.
1. You will first get an email mentioning that the automated testing has started. If you click on the `CodeBuildTest` step and then click on the `Resource` link, you will see the CodeBuild testing execution live.
    - Once the testing is done, you can click on the `Reports` tab to see an overview of the tests.
1. Once the automated tests have finished, you will get an email letting you know that your application is ready for release.  As this is the initial deploy, go ahead and approve it.  Next time we deploy however, we will look at the listener rules to understand how we do blue/green.  
    > :pushpin: **NOTE:** If you do not receive the email, see the Appendix at the bottom of this page.

1. The step function should finish a few seconds later.
1. Navigate to the console in the target account (as opposed to the CICD account where we have been previously) to EC2 | Loadbalancers.  You should be able to find your ALB by searching the name.  It will take the syntax `a206296-dojo-TEN-Acct-Id-dev`
1. Find the DNS name for your ALB.  For example, mine is `internal-a206296-dojo-u6065223-dev-23278987.eu-west-1.elb.amazonaws.com`.
    You can get your DNS name from the `DNSName` value in the `a206296-TEN-Acct-Id-ems-dojo-dev-alb-ecs-allconfig` stack that was deployed with Cloud IaC.
    Keep in mind that there are two DNS names - one for `dev` and one for `qa`. Make sure you pick the one from the `dev` stack.
1. Modify the variables below and run the following command using cloud-tool:
    ```sh
    ALB_DNS_NAME="Your alb DNS name"
    AWS_PROFILE="tr-ihn-sandbox"
    REGION="eu-west-1"
    cloud-tool --profile ${AWS_PROFILE} --region ${REGION} generic-ssh-tunnel -c ${ALB_DNS_NAME} -q 80 -r 8080
    ```
1. Open your browser and navigate to `http://localhost:8080`
1. Notice the version in the top right.  To simulate a deploy, we will change this value in the source code and redeploy later.

# Appendix
If you do not receive the email to approve the release, you can find the command to run via the Cumulus CLI.  To do so, follow these instructions:

1. Run the following command to find the deployment ID and table name you will need to approve the release.
    ```sh
    cumulus bluegreen list-pending-deployments --profile tr-ihn-cicd-sandbox  --region eu-west-1

    ```

1. Use the blue green ID and the table name to approve the release.
    ```sh
    cumulus bluegreen approve-deployment --profile tr-ihn-cicd-sandbox  --region eu-west-1 --table a206296-TEN-Acct-Id-bluegreen-deployer-table-nonprod-v1-eu-west-1 --id fc15314b-d2ac-452e-bbdf-322b65a6672e --go
    ```
