package com.example.mahmud.travelmate.POJO;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.example.mahmud.travelmate.POJO.SearchSuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;
    public SearchSuggestionProvider(){
        setupSuggestions(AUTHORITY,MODE);
    }
}
