#!/bin/bash
set -e # Exit with nonzero exit code if anything fails

###########
# Variables setup
###########
if [[ "$TRAVIS_BRANCH" = 'master' ]]; then
  RSA_FILE="prod_server_rsa"
  DEPLOY_PATH=${PROD_DEPLOY_PATH}
  SERVER_IP=${PROD_SERVER_IP}
else
  RSA_FILE="stg_server_rsa"
  DEPLOY_PATH=${STG_DEPLOY_PATH}
  SERVER_IP=${STG_SERVER_IP}
fi;

ENCRYPTED_KEY_VAR="encrypted_${ENCRYPTION_LABEL}_key"
ENCRYPTED_IV_VAR="encrypted_${ENCRYPTION_LABEL}_iv"
ENCRYPTED_KEY=${!ENCRYPTED_KEY_VAR}
ENCRYPTED_IV=${!ENCRYPTED_IV_VAR}

###########
# SSH keys
###########
openssl aes-256-cbc -K ${ENCRYPTED_KEY} -iv ${ENCRYPTED_IV} -in .travisci/private_rsa_keys.tar.gz.enc -out .travisci/private_rsa_keys.tar.gz -d
tar -xf .travisci/private_rsa_keys.tar.gz -C .travisci
chmod 600 .travisci/${RSA_FILE}
eval `ssh-agent -s`
ssh-add .travisci/${RSA_FILE}

cat ".travisci/${RSA_FILE}.pub" >> $HOME/.ssh/known_hosts

###########
# Deploy to server
# we deploy to new_* to actually wipe old assets
###########
scp pm2.json learndesk@${SERVER_IP}:${DEPLOY_PATH}
scp build/libs/backend-0.1.0-all.jar learndesk@${SERVER_IP}:${DEPLOY_PATH}/new_learndesk.jar
ssh learndesk@${SERVER_IP} "
cd ${DEPLOY_PATH}
pm2 stop pm2.json
mv ${DEPLOY_PATH}/new_learndesk.jar ${DEPLOY_PATH}/learndesk.jar
pm2 start pm2.json
"
