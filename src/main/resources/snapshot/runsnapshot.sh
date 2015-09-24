#
if [ "$#" -ne 1 ]; then
	echo "usage: $0 <app_name>"
	exit
fi

APP_NAME=${1}
SNAPSHOT_UTIL_DIR=/tmp/snapshot
TMPDIR=/tmp

PROTEX_URL=<protex_url>
PROTEX_USERNAME=<protex_username>
PROTEX_PASSWORD=<protex_password>

CC_URL=<codecenter_url>
CC_USERNAME=<codecenter_user>
CC_PASSWORD=<codecenter_password>

CC_APP_VERSION=Unspecified
CC_CLONED_APP_WORKFLOW=<workflow_name>

echo TMPDIR: ${TMPDIR}
echo APP_NAME: ${APP_NAME}
echo BUILD_ID: ${BUILD_ID}

echo PROTEX_URL: ${PROTEX_URL}
echo PROTEX_USERNAME: ${PROTEX_USERNAME}
echo PROTEX_PASSWORD: ${PROTEX_PASSWORD}

echo CC_URL: ${CC_URL}
echo CC_USERNAME: ${CC_USERNAME}
echo CC_PASSWORD: ${CC_PASSWORD}

echo CC_APP_VERSION: ${CC_APP_VERSION}
echo CC_CLONED_APP_WORKFLOW: ${CC_CLONED_APP_WORKFLOW}

CONFIG_FILENAME=${TMPDIR}/config_snapshot_${APP_NAME}_${BUILD_ID}.properties

echo "" > ${CONFIG_FILENAME}

echo "protex.server.name=${PROTEX_URL}" >> ${CONFIG_FILENAME}
echo "protex.user.name=${PROTEX_USERNAME}" >> ${CONFIG_FILENAME}
echo "protex.password=${PROTEX_PASSWORD}" >> ${CONFIG_FILENAME}

echo "cc.server.name=${CC_URL}" >> ${CONFIG_FILENAME}
echo "cc.user.name=${CC_USERNAME}" >> ${CONFIG_FILENAME}
echo "cc.password=${CC_PASSWORD}" >> ${CONFIG_FILENAME}

echo "cc.app.version=${CC_APP_VERSION}" >> ${CONFIG_FILENAME}
echo "cc.cloned.app.workflow=${CC_CLONED_APP_WORKFLOW}" >> ${CONFIG_FILENAME}

${SNAPSHOT_UTIL_DIR}/bin/Snapshot.sh ${CONFIG_FILENAME} ${APP_NAME}
