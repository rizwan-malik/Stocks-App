package com.rizwan.secondtry;

public class DetailsInterfaces {

//    Metadata Inner class as an interface
    public static class MetaData{
        private final String ticker;
        private final String name;
        private final String startDate;
        private final String description;
        private final String exchangeCode;

        public MetaData(String ticker, String name, String startDate, String description, String exchangeCode) {
            this.ticker = ticker;
            this.name = name;
            this.startDate = startDate;
            this.description = description;
            this.exchangeCode = exchangeCode;
        }

    public String getTicker() {
        return this.ticker;
    }

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDescription() {
        return this.description;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }
}

    public static class PriceData{
        private final String timestamp;
        private final String bidSize;
        private final String lastSaleTimeStamp;
        private final String low;
        private final String bidPrice;
        private final String prevClose;
        private final String quoteTimeStamp;
        private final String last;
        private final String askSize;
        private final String volume;
        private final String lastSize;
        private final String ticker;
        private final String high;
        private final String mid;
        private final String askPrice;
        private final String open;
        private final String tngoLast;

        public String getTimestamp() {
            return timestamp;
        }

        public String getBidSize() {
            return bidSize;
        }

        public String getLastSaleTimeStamp() {
            return lastSaleTimeStamp;
        }

        public String getLow() {
            return low;
        }

        public String getBidPrice() {
            return bidPrice;
        }

        public String getPrevClose() {
            return prevClose;
        }

        public String getQuoteTimeStamp() {
            return quoteTimeStamp;
        }

        public String getLast() {
            return last;
        }

        public String getAskSize() {
            return askSize;
        }

        public String getVolume() {
            return volume;
        }

        public String getLastSize() {
            return lastSize;
        }

        public String getTicker() {
            return ticker;
        }

        public String getHigh() {
            return high;
        }

        public String getMid() {
            return mid;
        }

        public String getAskPrice() {
            return askPrice;
        }

        public String getOpen() {
            return open;
        }

        public String getTngoLast() {
            return tngoLast;
        }

        public PriceData(String timestamp, String bidSize, String lastSaleTimeStamp, String low, String bidPrice, String prevClose, String quoteTimeStamp, String last, String askSize, String volume, String lastSize, String ticker, String high, String mid, String askPrice, String open, String tngoLast) {
            this.timestamp = timestamp;
            this.bidSize = bidSize;
            this.lastSaleTimeStamp = lastSaleTimeStamp;
            this.low = low;
            this.bidPrice = bidPrice;
            this.prevClose = prevClose;
            this.quoteTimeStamp = quoteTimeStamp;
            this.last = last;
            this.askSize = askSize;
            this.volume = volume;
            this.lastSize = lastSize;
            this.ticker = ticker;
            this.high = high;
            this.mid = mid;
            this.askPrice = askPrice;
            this.open = open;
            this.tngoLast = tngoLast;
        }
    }

    public static class HistoricData{

        private final String date;
        private final String close;
        private final String high;
        private final String low;
        private final String open;
        private final String volume;
        private final String adjClose;
        private final String adjHigh;
        private final String adjLow;
        private final String adjOpen;
        private final String adjVolume;
        private final String divCash;
        private final String splitFactor;

        public HistoricData(String date, String close, String high, String low, String open, String volume, String adjClose, String adjHigh, String adjLow, String adjOpen, String adjVolume, String divCash, String splitFactor) {
            this.date = date;
            this.close = close;
            this.high = high;
            this.low = low;
            this.open = open;
            this.volume = volume;
            this.adjClose = adjClose;
            this.adjHigh = adjHigh;
            this.adjLow = adjLow;
            this.adjOpen = adjOpen;
            this.adjVolume = adjVolume;
            this.divCash = divCash;
            this.splitFactor = splitFactor;
        }

        public String getDate() {
            return date;
        }

        public String getClose() {
            return close;
        }

        public String getHigh() {
            return high;
        }

        public String getLow() {
            return low;
        }

        public String getOpen() {
            return open;
        }

        public String getVolume() {
            return volume;
        }

        public String getAdjClose() {
            return adjClose;
        }

        public String getAdjHigh() {
            return adjHigh;
        }

        public String getAdjLow() {
            return adjLow;
        }

        public String getAdjOpen() {
            return adjOpen;
        }

        public String getAdjVolume() {
            return adjVolume;
        }

        public String getDivCash() {
            return divCash;
        }

        public String getSplitFactor() {
            return splitFactor;
        }
    }

    public static class NewsData{
        private final String title;
        private final String url;
        private final String urlToImage;
        private final String publishedAt;
        private final String description;
        private final String source;


        public NewsData(String title, String url, String urlToImage, String publishedAt, String description, String source) {
            this.title = title;
            this.url = url;
            this.urlToImage = urlToImage;
            this.publishedAt = publishedAt;
            this.description = description;
            this.source = source;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getUrlToImage() {
            return urlToImage;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public String getSource() {
            return source;
        }

        public String getDescription() {
            return description;
        }
    }
}
