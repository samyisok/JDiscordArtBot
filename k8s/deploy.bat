@echo off
setlocal enabledelayedexpansion

:: Load environment variables from .env file
for /f "usebackq tokens=1,* delims==" %%a in (".env") do (
    set "%%a=%%b"
)

echo Using kubeconfig: !KUBECONFIG!
kubectl config current-context

echo Building and pushing new container image...
nerdctl.exe build --insecure-registry -t !REGISTRY_URL!/sarah-bot:latest ..
nerdctl.exe push --insecure-registry !REGISTRY_URL!/sarah-bot:latest

echo Deleting old pod...
kubectl -n sarah-bot delete pod --all

echo Creating/updating Secret...
kubectl create secret generic sarah-secrets --from-literal=jda-api-key=!JDA_API_KEY! -n sarah-bot --dry-run=client -o yaml | kubectl apply -f -

echo Applying all resources with Kustomize...
kubectl apply -k .

echo Waiting for pod to be ready...
timeout /t 5 /nobreak
kubectl -n sarah-bot get pods

echo Checking final deployment status...
kubectl -n sarah-bot get pods
kubectl -n sarah-bot get pvc

endlocal