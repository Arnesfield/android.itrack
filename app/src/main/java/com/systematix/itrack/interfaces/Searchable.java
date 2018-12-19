package com.systematix.itrack.interfaces;

public interface Searchable {
    int getId();
    boolean onSearch(String query);
}
