package app.manny.databasecacherestapi.persistance;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import app.manny.databasecacherestapi.models.Recipe;

@Database(entities = {Recipe.class},version = 1)
@TypeConverters({Converters.class})
public abstract class RecipeDatabase extends RoomDatabase {

    private static RecipeDatabase instance;
    private static final String DATABASE_NAME = "recipe_db";

    public static RecipeDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    RecipeDatabase.class,
                    DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract RecipeDAO getRecipeDAO();
}
