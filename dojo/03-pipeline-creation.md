# Goal
Create your end-to-end pipeline.  This will be used to deploy the IaC templates.  

# Steps

1. Inspect the [`cumulus-pipelinespec.yaml`](../cicd/cumulus-pipelinespec.yaml) file.  This is the configuration file we use to define the pipeline.  Note that this file has source and deploy stages.
In the source stage, update the repo name to point to your GitHub fork name.

1. You can create and deploy the pipeline and all ancillary resources using the below command.
    ```sh
    cumulus pipelines deploy --input-file cicd/cumulus-pipelinespec.yaml --profile tr-ihn-cicd-sandbox  --region eu-west-1 --require-approval never
    ```
