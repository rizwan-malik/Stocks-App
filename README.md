# Stocks-App
Android app which allows users to search for different stock symbols/tickers and look at the detailed information about them. Additionally, the users can trade with virtual money and create a portfolio. Users can also favorite stock symbols to track their stock prices. 

The App contains 2 screens: Home screen and the Detailed Stock Information screen. However, the App has multiple features on each of these screens.

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

Additionally the price information for each stock is updated every 15 seconds. The home screen has been implemented by using a **RecyclerView** with the **SectionedRecyclerViewAdapter**. Each of the stock listings has been implemented using **ConstraintLayout**, **TextView**, **ImageView**. The Search button on the toolbar opens the search bar to type the stock symbol to search. The search bar uses the autocomplete functionality.

## Detailed Stock Information Screen
On clicking the **Goto** button on any stock listing or searching for a stock symbol, the loading spinner symbol is displayed while the details are being fetched. Once the data has been fetched except the chart (since the chart takes longer to load), the spinner disappears and information regarding the stock is available to the user.

The top action bar should has the ‘Stock’ title and the back button to go back to the home screen (which has the filter values that were used for the current search if triggered by using the search functionality). The action bar also contains a favorite icon to add or remove the stock from favorites. The favorite icon will either be filled or bordered based on whether the stock is favorited or not. Adding/Removing the stock from favorites also displays a toast message as shown in the [video](#video).

Below the action bar, there should be 4 fields: stocks symbol, current price with ‘$’ sign, company name and the change price with ‘$’ sign (the text color should be green, red or grey based on the change price value being positive, negative or zero respectively). The App then has a WebView element which is blank till the chart loads. (More details later in this section)

[https://user-images.githubusercontent.com/70775208/182134838-d3ed17c2-e953-4c21-8a9b-1a55e3910b69.mp4](#video)


https://user-images.githubusercontent.com/70775208/182139254-1d65ddbd-97e1-4162-a19c-2131ae6d4d76.mp4

