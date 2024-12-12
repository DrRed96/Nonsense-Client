import os
import subprocess

def main():
    os.chdir("native-image")
    subprocess.run([
        "C:\\Program Files\\Java\\jdk-23\\bin\\java.exe",
        "-agentlib:native-image-agent=config-output-dir=native-image-config",
        "-Xms1024M", "-Xmx4096M",
        "-jar", "../build/libs/all.jar",
        "--version", "Nonsense",
        "--accessToken", "0",
        "--username", "Nonsense",
        "--gameDir", os.environ.get("appdata") + "/.minecraft",
        "--assetsDir", "../run/assets",
        "--assetIndex", "1.8",
        "--userProperties", "{}"
    ])

if __name__ == "__main__":
    main()