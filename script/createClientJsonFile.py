# A python script to create the client.json file of Nonsense client (for those that want to run it through the vanilla launcher)

import hashlib
import json
import os
import urllib.request as request

def sha1Hash(file):
    sha1 = hashlib.sha1()
    with open(file, "rb") as f:
        while True:
            data = f.read(65536)
            if not data:
                break
            sha1.update(data)
            
    return sha1.hexdigest()
    

def main():
    data = {}
    
    data["id"] = "Nonsense"
    data["javaVersion"] = {
        "component": "java-runtime-gamma",
        "majorVersion": 17
    }
    data["assets"] = "1.8"
    data["complianceLevel"] = 0
    data["minimumLauncherVersion"] = 14
    data["mainClass"] = "net.minecraft.client.main.Main"
    
    with open("script/1.8.8.json", "r") as file:
        jsonObject = json.load(file)
        data["assetIndex"] = jsonObject["assetIndex"]
        data["downloads"] = jsonObject["downloads"]
        data["logging"] = jsonObject["logging"]
        data["minecraftArguments"] = jsonObject["minecraftArguments"]
        
    with open("build/libs/libraries.json", "r") as file:
        jsonObject = json.load(file)
        libs = []
        for library in jsonObject["libraries"]:
            
            artifact = library["repo"] + library["path"]
            
            request.urlretrieve(artifact, "temp.jar")
            
            hash = sha1Hash("temp.jar")
            size = os.stat("temp.jar").st_size
            
            os.remove("temp.jar")
            
            libs.append({
                "downloads": {
                    "artifact": {
                        "path": library["path"],
                        "sha1": hash,
                        "size": size,
                        "url": artifact
                    }
                },
                "name": library["name"]
            })
        data["libraries"] = libs
        
    
    with open("build/libs/nonsense.json", "w") as file:
        file.write(json.dumps(data, indent=4))    
    
if __name__ == "__main__":
    main()