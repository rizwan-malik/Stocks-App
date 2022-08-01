# Stocks-App
Android app which allows users to search for different stock symbols/tickers and look at the detailed information about them. Additionally, the users can trade with virtual money and create a portfolio. Users can also favorite stock symbols to track their stock prices. The App contains 2 screens: Home screen and the Detailed Stock Information screen. However, the App has multiple features on each of these screens.
There are 4 calls to the **tiingo** APIs for *company description, stock prices, autocomplete* and *chart data points*, and the additional *newsapi* endpoint.
Backend is based on NodeJS which handles all the API calls.
## Home Screen
There are 2 sections on the home screen:
1. **Portfolio Section** - This section will show the total net worth of the user, which is calculated as the sum of number of shares of a stock multiplied by the current price, plus uninvested cash. This is followed by the list of stocks in the user portfolio with their current price, change in price and total shares owned information.
2. **Favorites Section** - This section will show all the stocks that have been favorited by the user to allow the user to easily check the prices of stocks in their watchlist. The stock symbol, current price, change in price and company name should be displayed as shown
