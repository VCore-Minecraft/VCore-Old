/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.files.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 25.06.2021 23:02
 */
public class JsonConfig extends JSONObject{
    private transient final File folder;
    private transient final File saveFile;

    public JsonConfig(File folder, File file){
        this.folder = folder;
        this.saveFile = file;
        putAll(readFile());
        onLoad();
        save();
    }

    public void onLoad(){

    }

    public <S> S get(String key, Class<? extends S> type, S defaultValue){
        if(!containsKey(key))
            put(key, defaultValue);
        return type.cast(get(key));
    }

    public void save(){
        try{
            if(!saveFile.exists()) {
                folder.mkdirs();
                saveFile.createNewFile();
            }
            try (FileWriter fileWriter = new FileWriter(saveFile)){
                writeJSONString(fileWriter);
                fileWriter.flush();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {

        }
    }

    public JSONObject readFile(){
        if(saveFile.exists()){
            JSONParser jsonParser = new JSONParser();
            try {
                return (JSONObject) jsonParser.parse(new FileReader(saveFile));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }


}
