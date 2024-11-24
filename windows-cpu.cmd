@echo off
setlocal

rem Set download URL and target file path
set "url=https://huggingface.co/mukel/Meta-Llama-3.1-8B-Instruct-GGUF/resolve/main/Meta-Llama-3.1-8B-Instruct-Q4_0.gguf"
set "target_file=.\models\llama3.1-8b.gguf"

rem Create target directory if it doesn't exist
if not exist ".\models" (
    mkdir ".\models"
)

rem Check if the file already exists
if exist "%target_file%" (
    echo "%target_file%" already exists, skipping download.
) else (
    rem Download the file using curl with following redirects
    curl -L -o "%target_file%" "%url%"
    if %errorlevel% equ 0 (
        echo Download complete: "%target_file%"
    ) else (
        echo Download failed.
    )
)

endlocal

start cmd /c ".\bin\windows-cpu\llama-server.exe -m .\models\llama3.1-8b.gguf --port 8088"
start cmd /c ".\bin\windows-cpu\llama-server.exe -m .\models\llama3.1-8b.gguf --port 8086 --embedding"
timeout /t 5
start java -jar ./DatasetAnalyser.jar
timeout /t 10
start http://localhost:8383