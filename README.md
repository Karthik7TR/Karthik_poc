# ECS Sample App
This is a Sample application created by the CCNG TechOps team's interns that can be used to test deployments.  
> :star: ***HINT:***
>
> You can follow along with a Cumulus engineer as they describe Cumulus and walk you through these steps.  
> [The videos are here.](https://thehub.thomsonreuters.com/docs/DOC-2945792)


# Infrastructure
Each implementer of the demo should have already deployed their infrastructure before following the Cumulus steps below.  The database is multi-tennant and will be shared by all apps deployed, so you do not have to deploy that.  

The database for this app is located in [this Cloud IaC project.](https://github.com/tr/cumulus_iac-sample-application-nodejs-rds).  
All other infrastructure is contained in [this Cloud IaC project.](https://github.com/tr/cumulus_iac-sample-application-nodejs-ecs).  For a typical app, the two would be in the same IaC project.  However, to speed up demo times we separated out the database.  It does so in these ways:
* It takes about 20 minutes per environment to create an RDS instance.
* No need for each member of a demo to deploy schemas.  The proper way to do this is with the DB change pipeline which really deserves its own dojo!

# Hands-On Cumulus Instructions
You will have the opportunity to create Cumulus pipelines and use them to deploy an ECS backed application in a blue/green fashion.  Please follow along with the videos linked to at the top of this page.  The videos will go more in-depth into what you're doing than the written instructions.  The files in this repo are all that you will need, however, we assume that the infrastructure has already been created.  If you have questions during this process, **Please** reach out to the Cumulus team on our [Teams Channel](https://teams.microsoft.com/l/channel/19%3ac72f735f407a48f1902ad18ad14f1265%40thread.skype/General?groupId=09374222-95d8-4cb6-bd1d-1bb9f8dfc625&tenantId=62ccb864-6a1a-4b5d-8e1c-397dec1a8258).  There are no dumb questions!

## Prerequisites
* [General prerequisites](https://thehub.thomsonreuters.com/docs/DOC-2914661)

## Steps
Perform the steps below in order.  However, if this is an onsite hands-on training, please do not progress to the next step until we are all there together.

1. [01-clone-and-modify](dojo/01-clone-and-modify.md)
1. [02-cumulus-installer](dojo/02-cumulus-installer.md)
1. [03-pipeline-creation](dojo/03-pipeline-creation.md)
1. [04-first-deploy](dojo/04-first-deploy.md)
1. [05-second-deploy](dojo/05-second-deploy.md)

## Cleanup
When you are finished, you can run this to cleanup all of the stuff the cumulus installer created.  
`cumulus installer uninstall --profile-name ${AWS_PROFILE} --installer-file installer_input.yaml -a ${SAMI_USERNAME}`

To delete the services, you'll have to delete the CloudFormation stacks manually.  They will follow the syntax `a206296-${ServiceName}-${Environment}-xxxxxxxxxxxxxxxxx` where xxxxxxxxxxxxxxxxx is a random string.
