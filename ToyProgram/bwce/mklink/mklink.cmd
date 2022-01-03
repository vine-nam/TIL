@echo off 

@REM set /p project="Enter Project Name: "

set workspace="E:\Workspace\bwce"
set gitspace="E:\Git\KTG_EAI\EAI_APP"

if not exist "%workspace%\%1" mkdir %workspace%\%1

mklink /D %workspace%\%1\%1 %gitspace%\%1\%1
mklink /D %workspace%\%1\%1.application %gitspace%\%1\%1.application
