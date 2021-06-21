package app.manny.databasecacherestapi.persistance;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Converters {

    @TypeConverter
    public String fromStringArray(String[] array){
        Gson gson = new Gson();
        return gson.toJson(array);

    }

    @TypeConverter
    public String[] fromString(String value){
        Type type = new TypeToken<String[]>(){}.getType();
        return new Gson().fromJson(value,type);
    }
}
