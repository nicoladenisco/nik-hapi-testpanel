#!/bin/bash

USERHOST="root@cal0.infomedica.it"
COPYCMD="rsync -azv --exclude CVS --exclude .cvsignore --partial --progress"

ZIP=nik-hapi-testpanel-1.0-SNAPSHOT-jar-with-dependencies.jar
DEST="/var/www/html/liveupdate"
LIVEUPDATEDEST="$USERHOST:$DEST"

echo ""
echo "--------------------------------------------------------"
echo "Copia dei files nel server LIVE UPDATE"
echo " dest=$LIVEUPDATEDEST"
echo "Premi INVIO per continuare o Ctrl+C per interrompere ..."
echo "--------------------------------------------------------"
read DUMMY

set -x
$COPYCMD \
  $FVER \
  target/$ZIP \
  $LIVEUPDATEDEST

ssh $USERHOST "cd $DEST; chmod 0664 $ZIP; chown www-data.www-data $ZIP"

