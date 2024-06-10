
## UEFA Euro 2024 Prediction

Based on the methodology outlined in this [research paper](https://econpapers.repec.org/paper/innwpaper/2014-17.htm)
I've formulated predictions for the UEFA Euro 2024:

1. Gather quoted odds from [OddsChecker](https://www.oddschecker.com/football/euro-2024/winner) using `fetchData.py`. 
2. Convert the odds into probabilities and compute aggregated probabilities using `calcProbabilities.R`.
3. Conduct simulations utilizing a Java program with `probabilities2024xxxx.csv` as input.
4. Transfer all CSV files to the appropriate directory within the application and review the outcomes via the Shiny app.

You can also access the app on shinyapps.io: https://wittmann.shinyapps.io/UEFA_Euro_2024_Prediction/

Please ensure that you have downloaded the ChromeDriver from [here](https://googlechromelabs.github.io/chrome-for-testing/#stable) as Step 1 relies on Selenium, and place it in the designated path.





 






