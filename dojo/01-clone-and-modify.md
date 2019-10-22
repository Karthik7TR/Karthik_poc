# Goal
Clone this repo and personalize it to distinguish it from others deployed in the same account.

# Steps
1. Login to cloud-tool into the CICD account (tr-tax-prof1-cicd-nonprod) as the a206296-PowerUser2 role
```sh
cloud-tool --profile tr-tax-prof1-cicd-nonprod --region us-east-1 login -a 307097860667 -r human-role/a206296-PowerUser2
```
1. Login to the AWS console into the CICD account (tr-tax-prof1-cicd-nonprod) as the a206296-PowerUser2 role
1. In a different browser, or in your browser's private mode, login to the Nonprod account (tr-tax-prof1-preprod) as the a206296-PowerUser2 role
1. Clone this repo to your machine.  
`git clone git@git.sami.int.thomsonreuters.com:trta-techops/docker-ccng-sampleapp-v1.git`  
or  
`git clone https://git.sami.int.thomsonreuters.com/trta-techops/docker-ccng-sampleapp-v1.git`
1. Open the project in an editor (Visual Studio Code or Atom) so that you can modify all the files.  There are several we need to change.
1. Do a recursive find and replace on all the files in this repo for the following items:
  * `TEN-Acct-Id` => your TEN account ID (e.g. u0106226)  **This should be all lowercase**
  * `firstname.lastname@tr.com` => Your email
