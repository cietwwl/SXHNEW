#!/bin/bash
cd /home/tlol/TLOL/TLOL_TRUNK/Resource
svn up
cd /home/tlol/TLOL/TLOL_TRUNK/
ant res
wget -O /dev/null --tries=0 "http://192.168.0.202:18088/invoke?operation=reloadLua&objectname=TLOL:name%3DTLOL+MANAGER";
echo reloaded resources!

