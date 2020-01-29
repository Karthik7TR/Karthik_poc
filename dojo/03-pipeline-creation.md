# Goal
Create your end-to-end pipeline.  This will be used to build the code, bake a container image and publish it to ECR, and deploy this image to the two ECS clusters you created with Cloud IaC.  

# Steps

1. Create the pipeline CloudFormation template  
    ```sh
    cumulus pipelines generate --input-file cumulus-pipelinespec.yaml --output-file pipeline-cfn.yaml
    ```

1. Create the pipeline CloudFormation stack using the template output from the command above.  Do this in the CICD account and keep the default values for all the parameters.  If any parameters are blank, leave them blank.  Eventually this will be automated but for now you'll have to do this in the console.  
  **OR**  
  You could use the script below.

  > :pushpin: **NOTE:** If you are viewing this in the remote Git repo, note that the find/replace values are incorrect.  However, you can directly copy/paste these commands from within your local file.
  ```sh
  RESOURCE_OWNER="firstname.lastname@tr.com" # FIXME Change to your email.
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
