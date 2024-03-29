package app.manny.databasecacherestapi.util;

public class Constants {

    public static final String BASE_URL = "https://recipesapi.herokuapp.com/";

    // YOU NEED YOUR OWN API KEY!!!!!!!!!!!!! https://www.food2fork.com/about/api
    public static final String API_KEY = "dadc63b6325aaf398163b40fea9b5e79";

    public static final int CONNECTION_TIMEOUT = 10; // 10 seconds
    public static final int READ_TIMEOUT = 2; // 2 seconds
    public static final int WRITE_TIMEOUT = 2; // 2 seconds

    public static final long RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30; // 30 days to refresh recipe

    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
}
