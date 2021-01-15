package ch.set.ucd.ucd4u.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * SearchResult
 */
@Getter
@Setter
public class SearchResult {

    private String url;
    private String name;

    public SearchResult() {

    }

    public SearchResult(String name, String url) {
        this.url = url;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }


}