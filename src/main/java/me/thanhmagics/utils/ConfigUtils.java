package me.thanhmagics.utils;

import me.thanhmagics.DungeonGate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtils {

    public File file;

    public String name;

    private Map<String,String> datas = new HashMap<>();

    private Map<String, List<String>> list_datas = new HashMap<>();


    public ConfigUtils(String name) {
        this.name = name;
        this.file = new File(DungeonGate.getInstance().getDataFolder(), name + ".yml");
        load();
    }

    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(file);
    }

    public void initNames(String... names) {
        for (String n : names) {
            if (!datas.containsKey(n)) {
                datas.put(n,null);
            }
        }
    }

    public ConfigUtils initNameWithValue(String name,String value) {
        if (!datas.containsKey(name))
            datas.put(name,value);
        return this;
    }

    public ConfigUtils initListDataWithValue(String name, List<String> strings) {
        if (!list_datas.containsKey(name))
            list_datas.put(name,strings);
        return this;
    }

    public void save() {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        datas.forEach(configuration::set);
        list_datas.forEach(configuration::set);
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        if (!file.exists()) {
            DungeonGate.getInstance().saveResource(name,false);
        } else {
//            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
//            for (String s : configuration.getConfigurationSection("").getKeys(false)) {
//                if (s.startsWith("(tree) ")) {
//                    loadTree(s, configuration);
//                } else {
//                    pushIntoHashMap(s, configuration);
//                }
//            }
        }
    }

    private void pushIntoHashMap(String s,YamlConfiguration configuration) {
        if (s.startsWith("(list) ")) {
            if (!list_datas.containsKey(s)) {
                list_datas.put(s,configuration.getStringList(s));
            }
        } else {
            initNameWithValue(s, configuration.getString(s));
        }
    }

    private void loadTree(String path,YamlConfiguration config) {
        for (String s : config.getConfigurationSection(path).getKeys(false)) {
            if (s.startsWith("(tree) ")) {
                loadTree(path,config);
            } else {
                pushIntoHashMap(s,config);
            }
        }
    }

    public Map<String, List<String>> getList_datas() {
        return list_datas;
    }

    public Map<String, String> getDatas() {
        return datas;
    }

    public String getString(String key) {
        return datas.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(datas.get(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(datas.get(key));
    }

    public List<String> getStringList(String key) {
        return list_datas.get(key);
    }

    public ConfigUtils setString(String key,String value) {
        datas.replace(key,value);
        return this;
    }

    public ConfigUtils setInteger(String key,int value) {
        datas.replace(key,String.valueOf(value));
        return this;
    }


    public ConfigUtils setDouble(String key,double value) {
        datas.replace(key,String.valueOf(value));
        return this;
    }

    public ConfigUtils setStringList(String key,List<String> value) {
        list_datas.replace(key,value);
        return this;
    }
}
