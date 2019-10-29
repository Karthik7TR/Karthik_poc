# Goal
Create your end-to-end pipeline.  This will be used to build the code, bake a container image and publish it to ECR, and deploy this image to the two ECS clusters you created with Cloud IaC.  

# Steps
1. Run the following command in your virtual environment to install the installer:  
`pip install pipeline-generator`
1. Modify the [`DeploySpec.yaml`](DeploySpec.yaml), changing the `# FIXME` lines.  The values for these lines will come from your Cloud IaC project.  Specifically, the outputs from the CloudFormation stacks Cloud IaC created for your infrastructure.  
**HINT**: There are four items under each environment that need changing.  All but the listener come from the ecs stack.  The listener comes from the ALB stack.  
This file tells the deployment engine what to do.  Among other things, it says to use the blue-green deployer (rather than the simple deployer) and what CloudFormation templates to use as well as their parameters.
1. Create the pipeline CloudFormation template  
```sh
pipeline-generator --input-file pipelinespec.yaml  --output-file pipeline-cfn.yaml
```

1. Create the pipeline CloudFormation stack using the template output from the command above.  Do this in the CICD account and keep the default values for all the parameters.  If any parameters are blank, leave them blank.  
Eventually this will be automated but for now you'll have to do this in the console.  Or, you could use the script below.

  > :pushpin: **NOTE:** If you are viewing this in the remote Git repo, note that the find/replace values are incorrect.  However, you can directly copy/paste these commands from within your local file.
  ```sh
  RESOURCE_OWNER="firstname.lastname@tr.com"
  TEN_ACCOUNT_ID="TEN-Acct-Id" # FIXME Change this to your u ID (e.x. u0106226)
  ASSET_ID="206296"
  STACK_NAME="a${ASSET_ID}-${TEN_ACCOUNT_ID}-cumulus-pipeline-pipeline"
  ENVIRONMENT_TYPE="DEVELOPMENT"
  TEMPLATE_FILENAME="pipeline-cfn.yaml"
  export AWS_PROFILE="tr-tax-prof1-cicd-nonprod"
  export AWS_REGION="us-east-1"
  aws cloudformation deploy --template-file ${TEMPLATE_FILENAME} \
    --stack-name ${STACK_NAME} \
    --tags \
      "tr:application-asset-insight-id=${ASSET_ID}" \
      "tr:environment-type=${ENVIRONMENT_TYPE}" \
      "tr:resource-owner=${RESOURCE_OWNER}" \
    --capabilities CAPABILITY_IAM
  ```
