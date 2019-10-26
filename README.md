# ECS Sample App
This is a Sample application created by the CCNG Techops team's interns that can be used to test deployments.

# Infrastructure
Each implementer of the demo should have already deployed their infrastructure before following the Cumulus steps below.  The database is multi-tennant and will be shared by all apps deployed.  

The database for this app is located in [this Cloud IaC project.](https://git.sami.int.thomsonreuters.com/ccng/iac-dojo-ems-rds-infra).  
All other infrastructure is contained in [this Cloud IaC project.](https://git.sami.int.thomsonreuters.com/ccng/iac-dojo-ems-ecs-infra).  For a typical app, the two would be in the same IaC project.  However, to speed up demo times we separated out the database.  It does so in these ways:
* It takes about 20 minutes per environment to create an RDS instance.
* No need for each member of a demo to deploy schemas.  The proper way to do this is with the DB change pipeline which really deserves its own dojo!

# Hands-On Cumulus Instructions
In this workshop we will create all the resources necessary to use Project Cumulus.  We will then create a pipeline that will deploy a sample application to the two environments previously created using Cloud IaC.  The files in this repo are all that you will need, however, we assume that the infrastructure has already been created.  We will go through them one by one and when we do, we will need to modify some of the values in some of the files.  We will walk you through this and explain the purpose of each file.  **Please** ask questions at any time and speak out if you need help or are unsure about something.

## Prerequisites
* [General prerequisites](https://thehub.thomsonreuters.com/docs/DOC-2914661)
* cloud-tool
* Python 3.7 (preferably in a virtual environment such as with [Anaconda](https://www.anaconda.com/distribution/).)
* BAMS setup as a [pip repository](https://thehub.thomsonreuters.com/docs/DOC-2735743)


## Steps
Perform the steps below in order.  However, if this is an onsite hands-on training, please do not progress to the next step until we are all there together.

1. [01-clone-and-modify](dojo/01-clone-and-modify.md)
1. [02-cumulus-installer](dojo/02-cumulus-installer.md)
1. [03-pipeline-creation](dojo/03-pipeline-creation.md)
1. [04-first-deploy](dojo/04-first-deploy.md)
1. [05-second-deploy](dojo/05-second-deploy.md)

## Cleanup
When you are finished, you can run this to cleanup all of the stuff the cumulus installer created.  
`cumulus-installer --profile-name ${AWS_PROFILE} --installer-file installer_input.yaml uninstall`

To delete the services, you'll have to delete the CloudFormation stacks manually.  They will follow the syntax `a206296-${ServiceName}-xxxxxxxxxxxxxxxxx` where xxxxxxxxxxxxxxxxx is a random string.
