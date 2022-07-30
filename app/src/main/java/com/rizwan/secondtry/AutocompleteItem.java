package com.rizwan.secondtry;

public class AutocompleteItem {

    private String ticker;
    private String name;

    public AutocompleteItem(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
    }

    @Override
    public String toString() {
        String result = ticker + " | " + name;
        return result;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }
}
