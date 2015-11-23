#!/bin/bash
cd /home/tlol/TLOL/TLOL_TRUNK/Resource
svn up
cd /home/tlol/TLOL/TLOL_TRUNK/
svn up
ant
~/TLOL/restart.sh

