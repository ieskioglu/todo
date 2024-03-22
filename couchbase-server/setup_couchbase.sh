#!/bin/bash
# used to start couchbase server - can't get around this as docker compose only allows you to start one command - so we have to start couchbase like the standard couchbase Dockerfile would 
# https://github.com/couchbase/docker/blob/master/enterprise/couchbase-server/7.0.3/Dockerfile#L82

/entrypoint.sh couchbase-server & 

# track if setup is complete so we don't try to setup again
FILE=/opt/couchbase/init/setupComplete.txt

if ! [ -f "$FILE" ]; then
  # used to automatically create the cluster based on environment variables
  # https://docs.couchbase.com/server/current/cli/cbcli/couchbase-cli-cluster-init.html

  echo $COUCHBASE_ADMINISTRATOR_USERNAME ":"  $COUCHBASE_ADMINISTRATOR_PASSWORD  

  sleep 10s 
  /opt/couchbase/bin/couchbase-cli cluster-init -c 127.0.0.1 \
  --cluster-username $COUCHBASE_ADMINISTRATOR_USERNAME \
  --cluster-password $COUCHBASE_ADMINISTRATOR_PASSWORD \
  --services data,index,query \
  --cluster-ramsize 512 \
  --cluster-index-ramsize 256

  sleep 2s 

  # used to auto create the bucket based on environment variables
  # https://docs.couchbase.com/server/current/cli/cbcli/couchbase-cli-bucket-create.html

  /opt/couchbase/bin/couchbase-cli bucket-create -c localhost:8091 \
  --username $COUCHBASE_ADMINISTRATOR_USERNAME \
  --password $COUCHBASE_ADMINISTRATOR_PASSWORD \
  --bucket todoBucket \
  --bucket-type couchbase \
  --bucket-ramsize 512
  sleep 2s 

  # used to auto create the sync gateway user based on environment variables  
  # https://docs.couchbase.com/server/current/cli/cbcli/couchbase-cli-user-manage.html#examples

  /opt/couchbase/bin/couchbase-cli user-manage \
  --cluster http://127.0.0.1 \
  --username $COUCHBASE_ADMINISTRATOR_USERNAME \
  --password $COUCHBASE_ADMINISTRATOR_PASSWORD \
  --set \
  --rbac-username $COUCHBASE_RBAC_USERNAME \
  --rbac-password $COUCHBASE_RBAC_PASSWORD \
  --roles bucket_full_access[*] \
  --auth-domain local

  sleep 2s 
  
  /opt/couchbase/bin/couchbase-cli collection-manage \
  --cluster http://localhost:8091 \
  --username $COUCHBASE_ADMINISTRATOR_USERNAME \
  --password $COUCHBASE_ADMINISTRATOR_PASSWORD \
  --bucket todoBucket \
  --create-scope todo_scope

  sleep 2s 
  
  /opt/couchbase/bin/couchbase-cli collection-manage -c localhost \
  --username $COUCHBASE_ADMINISTRATOR_USERNAME \
  --password $COUCHBASE_ADMINISTRATOR_PASSWORD \
  --bucket todoBucket \
  --create-collection todo_scope.todo_list \
  --max-ttl 0
  
  sleep 2s 
  
  /opt/couchbase/bin/couchbase-cli collection-manage -c localhost \
  --username $COUCHBASE_ADMINISTRATOR_USERNAME \
  --password $COUCHBASE_ADMINISTRATOR_PASSWORD \
  --bucket todoBucket \
  --create-collection todo_scope.todo_item \
  --max-ttl 0
  
  sleep 2s 
  
  /opt/couchbase/bin/couchbase-cli collection-manage -c localhost \
  --username $COUCHBASE_ADMINISTRATOR_USERNAME \
  --password $COUCHBASE_ADMINISTRATOR_PASSWORD \
  --bucket todoBucket \
  --create-collection todo_scope.user_list \
  --max-ttl 0
  
  sleep 2s 

  # create indexes using the QUERY REST API  
  /opt/couchbase/bin/curl -X POST http://localhost:8093/query/service \
  -H 'Content-Type: application/json' \
  -d '{"statement":"CREATE PRIMARY INDEX idx_todo_list_primary ON `todoBucket`.todo_scope.todo_list USING GSI;"}' \
  --user $COUCHBASE_ADMINISTRATOR_USERNAME:$COUCHBASE_ADMINISTRATOR_PASSWORD
      
  sleep 2s

  /opt/couchbase/bin/curl -X POST http://localhost:8093/query/service \
  -H 'Content-Type: application/json'\
  -d '{"statement":"CREATE PRIMARY INDEX idx_todo_item_primary ON `todoBucket`.todo_scope.todo_item USING GSI;"}' \
  --user $COUCHBASE_ADMINISTRATOR_USERNAME:$COUCHBASE_ADMINISTRATOR_PASSWORD
      
  sleep 2s

  /opt/couchbase/bin/curl -X POST http://localhost:8093/query/service \
  -H 'Content-Type: application/json' \
  -d '{"statement":"CREATE PRIMARY INDEX idx_user_list_primary ON `todoBucket`.todo_scope.user_list USING GSI;"}' \
  --user $COUCHBASE_ADMINISTRATOR_USERNAME:$COUCHBASE_ADMINISTRATOR_PASSWORD

  sleep 2s

  # create file so we know that the cluster is setup and don't run the setup again 
  touch $FILE
fi 
  # docker compose will stop the container from running unless we do this
  # known issue and workaround
  tail -f /dev/null