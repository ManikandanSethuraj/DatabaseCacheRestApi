package app.manny.databasecacherestapi.persistance;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import app.manny.databasecacherestapi.models.Recipe;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDAO {


    /**
     * Conflict = ignore, don't add it to the database if the item exists
     * Conflict = replace,
     * @param recipes
     * @return
     * the long will return list of _ids inserted..
     * {id1, id2, id3,....}
     * {id1, -1, id3, id4, -1,...}, -1 denotes conflict
     */


    // The reason why onConflict is set to ingore is we do not want to replace the some of the details like ingredients and timeStamp
    // of the Recipe which is been collected from different call.
    @Insert(onConflict = IGNORE)
    long[] insertAllRecipes(Recipe... recipes);


    @Insert(onConflict = REPLACE)
    void insertRecipe(Recipe recipe);


    // Custom update statement so ingredients and timestamp don't get removed
    @Query("UPDATE recipes SET title = :title, publisher = :publisher, image_url = :image_url, " +
            "social_rank = :social_rank WHERE recipe_id = :recipe_id")
    void updateRecipe(String recipe_id, String title, String publisher, String image_url, float social_rank);

    // NOTE: The SQL query sometimes won't return EXACTLY what the api does since the API might use a different query
    // or even a different database. But they are very very close.
    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR " + " ingredients LIKE '%' || :query || '%'" +
            " ORDER BY social_rank DESC LIMIT (:pageNumber * 30)")
    LiveData<List<Recipe>> searchRecipes(String query, int pageNumber);

    @Query("SELECT * FROM recipes WHERE recipe_id = :recipeId")
    LiveData<Recipe> getRecipe(String recipeId);










}
