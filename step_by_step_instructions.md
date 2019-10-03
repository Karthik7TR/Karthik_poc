# Introduction
In this workshop we will create all the resources necessary to use Project Cumulus.  We will then create a pipeline that will deploy a sample application to the two environments previously created using Cloud IaC.  The files in this repo are all that you will need.  We will go through them one by one and when we do, we will need to modify some of the values in some of the files.  We will walk you through this and explain the purpose of each file.  **Please** ask questions at any time and speak out if you need help or are unsure about something.

# Setup
You need to have the following installed on your machine:
* cloud-tool
* Python 3.7 (preferably in a virtual environment such as with [Anaconda](https://www.anaconda.com/distribution/).)
* BAMS setup as a [pip repository](https://thehub.thomsonreuters.com/docs/DOC-2735743)
* Login to cloud-tool into the CICD account (tr-tax-prof1-cicd-nonprod) as the a204820-PowerUser2 role
* Login to the AWS console into the CICD account (tr-tax-prof1-cicd-nonprod) as the a204820-PowerUser2 role
* In a different browser, or in your browser's private mode, login to the Nonprod account (tr-tax-prof1-preprod) as the a204820-PowerUser2 role

# Cumulus Installer
The cumulus installer will install into the CICD account all the resources necessary to perform each of the stages in your pipeline.  These include the Deployment engine and the blue/green deployer.  
If you would like to understand how it works, read on!  Otherwise skip down to the steps.

## Details of how it Works
What you are installing is essentially a wrapper.  Most of the hard work is handled by a CodeBuild project that the installer creates.  The wrapper downloads the necessary git repos that contain the code for each of the tools it will deploy (CloudFormation templates, Python projects, etc.).  It bundles these up, uploads them to S3 and uses the CodeBuild project to create the resources.  This was deemed ideal as there are many steps required to create these solutions, including uploading CloudFormation templates, baking a container image, deploying a scheduled ECS task, building and deploying many Lambda functions, etc.  It seemed to make more sense to do this in a controlled environment such as CodeBuild than having all fo these things run on each persons laptop and hope for consistency. See [the README](https://git.sami.int.thomsonreuters.com/project-cumulus/python-cumulus-installer) for further details.

## Steps
1. Run the following command in your virtual environment to install the installer:  
`pip3 install cumulus-installer`  
> If you get an error along the lines of `unable to find cumulus-installer`, this likely means you don't have BAMS setup as a pip repo.  See [Setup](#setup)

2. Modify the file [installer_input.yaml](installer_input.yaml).  Change those lines with `# FIXME` in them.  The others can remain the same.  
This file does exactly what it says it does.  It provides the installer with the information necessary to install everything.
3. Run the command below to create your resources.  It will take about 10-15 minutes.  
(Replace `${AWS_PROFILE}` with the profile you used when logging into cloud-tool.  If you didn't use one, omit the `--profile-name` argument.)  
`cumulus-installer --profile-name ${AWS_PROFILE} --installer-file installer_input.yaml install`

# Pipeline
We will now create your end-to-end pipeline.  This will be used to build the code, bake a container image and publish it to ECR, and deploy this image to the two ECS services you created with Cloud IaC.

## Deploy Version 1
1. Run the following command in your virtual environment to install the installer:  
`pip3 install pipeline-generator`
1. Modify the following files, changing the `# FIXME` lines:
  * [`pipelinespec.yaml`](pipelinespec.yaml)  
  This file tells the pipeline-generator what the pipeline should look like, what stages to include, where to get the source information from, etc.
  * [`DeploySpec.yaml`](DeploySpec.yaml)  
  This tells the deployment engine what to do.  Among other things, it says to use the blue-green deployer (rather than the rolling deployer) and what CloudFormation templates to use as well as their parameters.
  * [`bakespec.yml`](bakespec.yml)  
  This is the file the bake stage of the pipeline uses to bake the container image.
1. Create the pipeline CloudFormation template  
`pipeline-generator --input-file pipelinespec.yaml  --output-file pipeline-cfn.yaml`
1. Create the pipeline CloudFormation stack.  This can be done in the console in the CICD account.
1. Navigate in the console to S3 where you told your pipeline to expect the source (the `Source` object in the `pipelinespec.yaml` file).
1. zip up this repo and upload it to this place, ensuring the name of the file is what your pipeline expects.
> You can use these commands to do this if you have aws-cli installed.  Modify the variables and run from within this directory.
```shell
SOURCE_ZIPFILE_NAME="pipeline-source.zip"
S3_BUCKET_NAME="a204820-cloud-iac-lab-ccng-bucket-us-east-1-cfn"
S3_SOURCE_BUCKET_DIR="ben-dojo"
PROFILE="tr-tax-prof1-cicd-nonprod"
zip -q -r ${SOURCE_ZIPFILE_NAME} .
aws --profile ${PROFILE} s3 cp ${SOURCE_ZIPFILE_NAME} s3://${S3_BUCKET_NAME}/${S3_SOURCE_BUCKET_DIR}/
rm ${SOURCE_ZIPFILE_NAME}
```

1. Navigate to the console | CodePipeline and search for your GroupName and watch your pipeline work!
1. When it gets to the Deploy-dev stage's Running step, click Details to see the step function driving the blue/green deployment.
1. Eventually you will get an email letting you know that your application is ready for review.  As this is the initial deploy, go ahead and approve it.  Next time we deploy however, we will look at the listener rules to understand how we do blue/green.
1. The step function should finish a few seconds later.
1. Navigate into the console to EC2 | Loadbalancers.  You should be able to find your ALB by searching the name.  It will take the syntax `a204820-${projectName}-${GroupName}-dev`
1. Find the DNS name for your ALB.  For example, mine is `internal-a204820-dojo-u6065223-dev-23278987.us-east-1.elb.amazonaws.com`
1. Run the following command using cloud-tool:  
`cloud-tool --profile ${PROFILE_NAME} generic-ssh-tunnel -c ${ALB_DNS_NAME} -q 80 -r 8080`
1. Open your browser and navigate to `http://localhost:8080`
1. Notice the version in the top right.

## Deploy Version 2
1. Navigate in the source code to [`app/views/employee.ejs`](app/views/employee.ejs).
1. On line 36 you will see where the version is set that you saw earlier.  Change this to whatever you want and save the file.  Then we'll redeploy to see the change.
1. Run the same small script as above to zip up this code and upload to S3.
1. Navigate back to the Pipeline to see it travel through the stages again.
1. When you get the email, **DO NOT** press approve yet.  Let us navigate into the console again to the ALB.
1. Click the listeners tab | view/edit rules
1. Notice how the priorities are set up to allow for traffic without a header to get to the old version of your app.  Further, at a higher priority, there is another rule to allow traffic to your new version at the same path but with the addition of your header.  
This is how you can test your app if you wish!
1. Go back into the browser tab with your app and hit refresh.  
> Likely the tunnel will have timed out.  Simply run the same cloud-tool command as above to re-establish it.

# Cleanup
When you are ready, you can run this to cleanup all of the stuff the cumulus installer created.  
`cumulus-installer --profile-name ${AWS_PROFILE} --installer-file installer_input.yaml uninstall`

To delete the services, you'll have to delete the CloudFormation stacks manually.  They will follow the syntax `a204820-${ServiceName}-xxxxxxxxxxxxxxxxx` where xxxxxxxxxxxxxxxxx is a random string.
