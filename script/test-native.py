import os
import subprocess

def main():
    os.chdir("native-image")
    subprocess.run([
        "Nonsense.exe",
        # "-Xms1024M", "-Xmx4096M",
        # "-jar", "../build/libs/all.jar",
        "--version", "Nonsense",
        "--accessToken", "0",
        "--username", "Nonsense",
        "--gameDir", os.environ.get("appdata") + "/.minecraft",
        "--assetsDir", "assets",
        "--assetIndex", "1.8",
        "--userProperties", "{}"
    ])

if __name__ == "__main__":
    main()