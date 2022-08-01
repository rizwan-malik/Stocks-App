# Stocks-App
Android app which allows users to search for different stock symbols/tickers and look at the detailed information about them. Additionally, the users can trade with virtual money and create a portfolio. Users can also favorite stock symbols to track their stock prices. The App contains 2 screens: Home screen and the Detailed Stock Information screen. However, the App has multiple features on each of these screens.
There are 4 calls to the **tiingo** APIs for *company description, stock prices, autocomplete* and *chart data points*, and the additional *newsapi* endpoint.
Backend is based on NodeJS which handles all the API calls.
## Home Screen
There are 2 sections on the home screen:
1. **Portfolio Section** - This section shows the total net worth of the user, which is calculated as the sum of number of shares of a stock multiplied by the current price, plus uninvested cash. This is followed by the list of stocks in the user portfolio with their current price, change in price and total shares owned information.
2. **Favorites Section** - This section shows all the stocks that have been favorited by the user to allow the user to easily check the prices of stocks in their watchlist. In case the favorited stock is present in the user portfolio, instead of the company name, the stocks owned is displayed.
Each stock listing also has a button on the extreme right, next to the current price field. On clicking the button or the stock listing, the detailed information screen is opened for the selected stock.
The home screen view supports multiple functionalities like:
- The **swipe to delete** functionality allows the user to remove/delete the stock from the favorite section. On removing a stock from the favorite section, the stock is removed from the favorite stocks in the local storage and the view.
- The **drag and reorder** functionality allows the user to reorder the stocks in either section. The user is able to long press the stock listing and drag it to the new position. The list is updated accordingly to ensure the new order going forward. Note: The user cannot drag the stock from the favorite section into the portfolio section. The stock can only be dragged and dropped in the same section.
