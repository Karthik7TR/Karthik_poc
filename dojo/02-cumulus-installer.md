# Goal
Provision the infrastructure used by Cumulus pipelines.  This includes the Blue/Green deployer, the deployment engine, and other resources used by these two components.


# Steps
1. Run the following command in your virtual environment to install the installer:  
`pip3 install cumulus-installer`  
> If you get an error along the lines of `unable to find cumulus-installer`, this likely means you don't have BAMS setup as a pip repo.  See [Setup](#setup)

1. Modify the file [installer_input.yaml](installer_input.yaml).  This file does exactly what it says it does.  It provides the installer with the information necessary to install everything.  
Change those lines with `# FIXME` in them.  The others can remain the same.
1. Run the command below to create your resources.  It will take about 10-15 minutes.  
(Replace `${AWS_PROFILE}` with the profile you used when logging into cloud-tool.  If you didn't use one, omit the `--profile-name` argument.)  
```sh
cumulus-installer --profile-name ${AWS_PROFILE} --installer-file installer_input.yaml install
```
1.  Sometime after it completes, you'll get a standard email from your new SNS topic asking you to confirm your subscription.  Do so as we will be using it to approve promotions.

1. Also, note the last line of output from the cumulus-installer. It will look similar to this: ` 'DeploymentEngine': {'Provider': 'a206296-u0106226-Engine', 'Version': '1'}}`.  This will be needed in a future step.


### Details of how it Works (if you're interested)
What you are installing is essentially a wrapper.  Most of the hard work is handled by a CodeBuild project that the installer creates.  The wrapper downloads the necessary git repos that contain the code for each of the tools it will deploy (CloudFormation templates, Python projects, etc.).  It bundles these up, uploads them to S3 and uses the CodeBuild project to create the resources.  This was deemed ideal as there are many steps required to create these solutions, including uploading CloudFormation templates, baking a container image, deploying a scheduled ECS task, building and deploying many Lambda functions, etc.  It seemed to make more sense to do this in a controlled environment such as CodeBuild than having all fo these things run on each persons laptop and hope for consistency. See [the README](https://git.sami.int.thomsonreuters.com/project-cumulus/python-cumulus-installer) for further details.
