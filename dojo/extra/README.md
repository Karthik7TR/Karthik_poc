# Extra Files

This folder contains extra files that may be helpful for you to do the dojo.

## Header Configuration File

The [headers_plugin_export.json](./headers_plugin_export.json) file can be imported by various browser plugins such as 
[Modify Headers for Google Chrome](https://chrome.google.com/webstore/detail/modify-headers-for-google/innpjfdalfhpcoinfnehdnbkglpmogdi) or 
[Simple Modify Headers for Firefox](https://addons.mozilla.org/en-CA/firefox/addon/simple-modify-header/).


## Helper Scripts

The `*.sh` files in the `./scripts` folder can help you with certain parts of the dojos.

> :warning: **WARNING:** In order to get the most out of the dojo, we recommend you **do not** use these scripts.  
> This is for your own benefit! By doing the steps yourself, you can better understand all the moving parts of Cumulus.
>
> However, if you're repeating the dojo, or if you're trying to modify the dojo for your own purposes, these scripts may save you some time.

The available scripts are:

- **[get_stack_outputs.sh](./scripts/get_stack_outputs.sh)** will retrieve the FIXME values that you need to fill in the DeploySpec.

- **[trigger_pipeline.sh](./scripts/trigger_pipeline.sh)** will zip up the repo and copy it to the pipeline source, which triggers a new run of the pipeline.

- **[ping.sh](./scripts/ping.sh)** will hit your `localhost:8080` endpoint and look for a `Version` string in the response.

All of these scripts should be run from the root repository folder. For example,
```sh
./dojo/extra/scripts/trigger_pipeline.sh
```
