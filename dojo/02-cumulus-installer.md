# Goal
Provision the infrastructure used by Cumulus pipelines.  This includes the Blue/Green deployer, the deployment engine, and other resources used by these two components.


# Steps
1. Run the following command in your virtual environment to install the Cumulus CLI:  
`pip install cumulus-cli`  
> If you get an error along the lines of `unable to find cumulus-cli`, this likely means you don't have BAMS setup as a pip repo.  See [Prerequisites](https://thehub.thomsonreuters.com/docs/DOC-2914661#jive_content_id_SAMI_Bams_Credentials_and_Usage)

1. Inspect the file [cumulus-installspec.yaml](cumulus-installspec.yaml).  This file does exactly what it says it does.  It provides the installer with the information necessary to install everything.  
In the installspec, Ensure those lines with `# FIXME` in them are accurate.  The others can remain the same.  
More information about this file can be found in the [configuration guide](https://thehub.thomsonreuters.com/docs/DOC-2914507).
1. cd into your local repository (where the `cumulus-installspec.yaml` file is located and run the command below to create your resources.  It will take about 10-15 minutes.  
```sh
AWS_PROFILE="tr-tax-prof1-cicd-nonprod"
cumulus installer install --profile-name ${AWS_PROFILE} --installer-file cumulus-installspec.yaml
```
1. You should get an email from AWS SNS to confirm your subscription to a new topic.  Approve this.  If you don't get the email after 5 minutes, search through your Outlook folders as you may have a rule set up to move, mark as read, or even delete emails from SNS.


### Details of how it Works (if you're interested)
The Cumulus installer behaves sort of like a wrapper.  Most of the hard work is handled by a CodeBuild project that the installer creates.  The wrapper downloads the necessary git repos that contain the code for each of the tools it will deploy (CloudFormation templates, Python projects, etc.).  It bundles these up, uploads them to S3 and uses the CodeBuild project to create the resources.  This was deemed ideal as there are many steps required to create these solutions, including uploading CloudFormation templates, baking a container image, deploying a scheduled ECS task, building and deploying many Lambda functions, etc.  It seemed to make more sense to do this in a controlled environment such as CodeBuild than having all fo these things run on each persons laptop and hope for consistency. See [the README](https://git.sami.int.thomsonreuters.com/project-cumulus/python-cumulus-installer) for further details.
