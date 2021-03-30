> :information_source:  **NOTE:**  This sample application, like all Cumulus examples, use eu-west-1 as the region of choice.

# Goal
Clone this repo and personalize it to distinguish it from others deployed in the same account.

# Steps
1. Clone this repo to your machine.  
`git clone https://github.com/tr/cumulus_sample-application-nodejs.git`

1. Using cloud-tool, login to the `tr-tax-prof-cicd-sandbox` (462066144630) account as a206296-PowerUser2.
    ```shell
    cloud-tool --profile "tr-tax-prof-cicd-sandbox" --region eu-west-1 login --role human-role/a206296-PowerUser2 --account-id 462066144630
    ```

1. Follow the same steps to login to the pre-prod account.
    ```shell
    cloud-tool --profile "tr-tax-prof-sandbox" --region eu-west-1 login --role human-role/a206296-PowerUser2 --account-id 330062551827
    ```


1. Login to the AWS console in the CICD account (tr-tax-prof-cicd-sandbox) as the a206296-PowerUser2 role.  Check to ensure you are in the eu-west-1 (Ireland) region.
1. In a different browser, or in your browser's private mode, login to the Nonprod account (tr-tax-prof-sandbox) as the a206296-PowerUser2 role.
1. Open the project in an editor (Visual Studio Code or Atom) so that you can modify all the files.  There are several we need to change.
1. Do a recursive find and replace on all the files in this repo for the following items:
  * `TEN-Acct-Id` => your TEN account ID (e.g. u0123456)  **This should be all lowercase**
  * `firstname.lastname@tr.com` => Your email
