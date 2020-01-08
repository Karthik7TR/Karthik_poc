# Goal
Clone this repo and personalize it to distinguish it from others deployed in the same account.

# Steps
1. Clone this repo to your machine.  
`git clone https://git.sami.int.thomsonreuters.com/trta-techops/docker-ccng-sampleapp-v1.git`  
or  
`git clone git@git.sami.int.thomsonreuters.com:trta-techops/docker-ccng-sampleapp-v1.git`  

1. Using cloud-tool, login to the `tr-tax-prof1-cicd-nonprod` (307097860667) account as a206296-PowerUser2.
  > :pushpin: **NOTE:** If you are viewing this in the remote Git repo, note that the find/replace values are incorrect.  However, you can directly copy/paste these commands from within your local file.  
  ```shell
  # If you have push notifications from the Symantec VIP app setup on your phone.
  cloud-tool --profile "tr-tax-prof1-cicd-nonprod" --region us-east-1 vault-login --push-notification --vault-username TEN-Acct-Id --role human-role/a206296-PowerUser2 --account-id 307097860667
  ```  
  OR  
  ```shell
  # If you do not have push notifications setup on your phone.
  cloud-tool --profile "tr-tax-prof1-cicd-nonprod" --region us-east-1 login --role human-role/a206296-PowerUser2 --account-id 307097860667
  ```

1. Follow the same steps to login to the pre-prod account.
  > :pushpin: **NOTE:** If you are viewing this in the remote Git repo, note that the find/replace values are incorrect.  However, you can directly copy/paste these commands from within your local file.
  ```shell
  # If you have push notifications from the Symantec VIP app setup on your phone.
  cloud-tool --profile "tr-tax-prof1-preprod" --region us-east-1 vault-login --push-notification --vault-username TEN-Acct-Id --role human-role/a206296-PowerUser2 --account-id 773476038677
  ```  
  OR  
  ```shell
  # If you do not have push notifications setup on your phone.
  cloud-tool --profile "tr-tax-prof1-preprod" --region us-east-1 login --role human-role/a206296-PowerUser2 --account-id 773476038677
  ```

1. Login to the AWS console in the CICD account (tr-tax-prof1-cicd-nonprod) as the a206296-PowerUser2 role.
1. In a different browser, or in your browser's private mode, login to the Nonprod account (tr-tax-prof1-preprod) as the a206296-PowerUser2 role.
1. Open the project in an editor (Visual Studio Code or Atom) so that you can modify all the files.  There are several we need to change.
1. Do a recursive find and replace on all the files in this repo for the following items:
  * `TEN-Acct-Id` => your TEN account ID (e.g. u0106226)  **This should be all lowercase**
  * `firstname.lastname@tr.com` => Your email
