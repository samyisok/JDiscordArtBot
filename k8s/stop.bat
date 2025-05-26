@echo off
setlocal enabledelayedexpansion

:: Load environment variables from .env file
for /f "usebackq tokens=1,* delims==" %%a in (".env") do (
    set "%%a=%%b"
)

echo Using kubeconfig: !KUBECONFIG!
kubectl config current-context

echo Deleting all resources in sarah-bot namespace...
kubectl delete -k .

echo Deleting pods...
kubectl -n sarah-bot delete pod --all

echo Deleting secrets...
kubectl -n sarah-bot delete secret sarah-secrets

echo Checking status...
kubectl -n sarah-bot get pods
kubectl -n sarah-bot get pvc

echo Service shutdown complete.

endlocal