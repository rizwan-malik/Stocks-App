# Stocks-App
Android app which allows users to search for different stock symbols/tickers and look at the detailed information about them. Additionally, the users can trade with virtual money and create a portfolio. Users can also favorite stock symbols to track their stock prices.

I have written details of all the functionalities and specifications of the app below. However, if you wish to skip them then go ahead and checkout this screen capture [demo](#demo) of the app.

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

Below the action bar, there are 4 fields: stocks symbol, **current price** with ‘$’ sign, **company name** and the **change price** with ‘$’ sign (the text color is green, red or grey based on the change price value being positive, negative or zero respectively). The App then has a WebView element which is blank till the chart loads.

The **Portfolio section** allows the user to trade the shares of the stock. It contains a left section which shows the market value of the stock in the user portfolio and the number of shares the user owns. The right section contains the trade button. Initially, when the user starts the app for the first time, they will not have any stocks/shares in the portfolio and an initial pre-loaded amount of $20,000 to trade on the app. This amount can change based on the trading done by the user. (For example, if the user sells shares at a loss, it can become less than 20,000 and so forth)

The **Stats section** displays the trading statistics for the given stock in a grid. The grid has 7 fields namely: *Current price, Low, Bid price, Open price, Mid, High and Volume*. The **GridView element** is used for this section.

The **About section** displays the description of the company. If the description is longer than 2 lines, it ellipsizes the end of the 2nd line and displays a ‘Show more…’ button. On clicking this button, the complete description becomes visible and the button text changes to ‘Show less’.

The **News section** displays the news articles related to the given stock symbol. The first article has a different format/layout than the rest of the articles in the list. On clicking the news article, the original article is opened in chrome using the article URL. On long press, a dialog box opens with options to share on twitter and open in chrome. For each article, the information displayed is *Article source, Article title, Article image* and the *time ago* when the article was published. The news section uses **RecyclerView** and **ArticleDialog elements**.

The Trade button in the **Portfolio section** opens a new dialog box for trading. The dialog shows an input box which only accepts numeric input. Below the input field, there is a calculation text box which updates based on the numeric input to display the final price of the trade. The trade dialog also displays the current available amount to trade for the user. The user can either buy or sell the shares. Based on the trade, the amount available to trade will be updated accordingly. There are 5 error conditions that are checked before executing the trade and displaying the trade successful dialog. The error conditions are:
1. **Users try to sell more shares than they own** - The trade dialog box should remain open and a toast message with text ‘Not enough shares to sell’ should be displayed.
2. **User tries to buy more shares than money available** - The trade dialog box should remain open and a toast message with text ‘Not enough money to buy’ should be displayed.
3. **User tries to sell zero or negative shares** - The trade dialog box should remain open and a toast message with text ‘Cannot sell less than 0 shares’ should be displayed.
4. **User tries to buy zero or negative shares** - The trade dialog box should remain open and a toast message with text ‘Cannot buy less than 0 shares’ should be displayed.
5. **User enters invalid input like text or punctuations** - The trade dialog box should remain open and a toast message with text ‘Please enter valid amount’ should be displayed.

## HighCharts in Android
The Chart section in the detailed stock information screen uses a **WebView element** to load the HighCharts stock chart. To load the chart, the App loads a local HTML file with the necessary JavaScript to request the data from the NodeJS server and displays the chart when the data is fetched.

## Demo

https://user-images.githubusercontent.com/70775208/182162837-9080533c-ec59-412d-a033-3bd03f4672f8.mp4

