@echo off
cd /d d:\github库\SE-work
start "SE-Server" /B javaw -cp "lib\mysql-connector-j-8.0.33.jar;backend" main
