#!/usr/bin/env bash
PARAM=$1
STRING="server $PARAM"

if grep -q "$STRING" /etc/nginx/nginx.conf; then
	echo IP Address is already in use
else 
	sed -E -i'' "s/server .+:/server $PARAM:/" /etc/nginx/nginx.conf
	/usr/sbin/nginx -s reload
fi

