# Goal
Create your end-to-end pipeline.  This will be used to build the code, bake a container image and publish it to ECR, and deploy this image to the two ECS clusters you created with Cloud IaC.  

# Steps

1. Inspect the [`cumulus-pipelinespec.yaml`](../cicd/cumulus-pipelinespec.yaml) file.  This is the configuration file we use to define the pipeline.  Note that this file has a source, build, bake, and two deploy stages.

1. You can create and deploy the pipeline and all ancillary resources using the below command.
    ```sh
    cumulus pipelines deploy --input-file cicd/cumulus-pipelinespec.yaml --profile tr-ihn-cicd-sandbox  --region eu-west-1 --require-approval never
    ```
