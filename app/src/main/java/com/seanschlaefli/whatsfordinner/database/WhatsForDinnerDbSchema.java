package com.seanschlaefli.whatsfordinner.database;

public class WhatsForDinnerDbSchema {

    public static final class RecipeTable {
        public static final String NAME = "recipe";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String DIRECTIONS = "directions";
            public static final String IMAGE = "image";
        }
    }

    public static final class IngredientTable {
        public static final String NAME = "ingredient";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String NUMERATOR = "numerator";
            public static final String DENOMINATOR = "denominator";
            public static final String UNITS = "units";
            public static final String RECIPE_ID = "recipe_id";
        }
    }

    public static final class MealTable {
        public static final String NAME = "meal";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String UUID = "uuid";
            public static final String DAY = "day";
            public static final String TIME = "time";
            public static final String RECIPE_ID = "recipe_id";
        }
    }
}
