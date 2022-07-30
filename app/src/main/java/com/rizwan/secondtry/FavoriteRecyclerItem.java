package com.rizwan.secondtry;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class FavoriteRecyclerItem {
    private String nameOrShares;
    private String ticker;
    private String currentPrice;
    private String change;

    public FavoriteRecyclerItem(String nameOrShares, String ticker, String currentPrice, String change) {
        this.nameOrShares = nameOrShares;
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        this.change = change;
    }

    public String getNameOrShares() {
        return nameOrShares;
    }

    public String getTicker() {
        return ticker;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public String getChange() {
        return change;
    }

    public void setNameOrShares(String nameOrShares) {
        this.nameOrShares = nameOrShares;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setChange(String change) {
        this.change = change;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteRecyclerItem that = (FavoriteRecyclerItem) o;
        return ticker.equals(that.ticker);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }
}
