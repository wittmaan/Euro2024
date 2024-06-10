
library(shiny)
library(rCharts)


shinyUI(navbarPage(
    title = 'UEFA Euro 2024 Prediction',
    tabPanel('Explanation',
             p('This Shiny app uses bookmaker betting odds (see tab \'Betting Odds\') to calculate the abilities and probabilities of the 24 teams participating in UEFA Euro 2024.'),
             br(),
             p('To achieve this, the following steps are undertaken:'),
             p(HTML('<b>1. Remove Bookmaker Overround:</b> To calculate accurate winning probabilities, we first remove the profit margin of the bookmakers
                  (also known as the <a href="https://en.wikipedia.org/wiki/Mathematics_of_bookmaking#Overround_on_multiple_bets">overround</a>).')),
             p(HTML('<b>2. Infer Team Abilities:</b> The odds without overround reflect the abilities of the teams. These abilities are then used to calculate pairwise winning probabilities (see the \'Pairwise Comparisons\' tab).')),
             p(HTML('The <a href="https://en.wikipedia.org/wiki/Bradley%E2%80%93Terry_model">Bradley-Terry-Modell</a> can be used
             to calculate the winning probability of team A defeating team B as: P(A strikes B) = ability(A) / (ability(A) + ability(B))')),
             p(HTML('<b>3. Average Bookmaker Ratings:</b> The averaged bookmaker ratings (see the\'Bookmaker consensus rating\' tab) are obtained using the log-odds, which are transformed via the logit function. The inverse logit function is then used to calculate the winning probabilities.')),
             p(HTML('<b>4. Iterative Approach:</b> Using an iterative approach, the abilities and pairwise winning probabilities are refined to match the bookmakers\' winning probabilities. These pairwise winning probabilities are then used to simulate the entire tournament 100,000 times.')),
             p(HTML('<b>5. Calculate Team Probabilities:</b> Using the calculated abilities, the probability for each team to reach the round of 16, quarter-finals, semi-finals, finals, and to win the tournament can be determined (see the \'Team Probabilites\' tab).')),
             p(HTML('<b>6. Estimate Group Standings:</b> Additionally, the probabilities for the final standings of each group after the group phase can be estimated (see the \'Group Probabilites\' tab).')),
             br(),br(),br(),br(),
             p(HTML('Literature: 
                  <a href="http://econpapers.repec.org/paper/innwpaper/2014-17.htm">Zeileis A, Leitner C, Hornik K (2014): Home Victory for Brazil in the 2014 FIFA World Cup</a>,
                  <a href="http://econpapers.repec.org/paper/innwpaper/2016-15.htm">Zeileis A, Leitner C, Hornik K (2016): Predictive Bookmaker Consensus Model for the UEFA Euro 2016</a>'))),
    tabPanel('Betting Odds',
             p(HTML('Betting odds from 27 online bookmakers for the 24 teams participating in UEFA Euro 2024. 
              They are obtained on 2024-06-09 from <a href="https://www.oddschecker.com/football/euro-2024/winner">https://www.oddschecker.com/football/euro-2024/winner</a>')),
             DT::dataTableOutput('odds')),
    tabPanel('Pairwise Comparisons',
             p('Winning probabilities for pairwise comparisons of all UEFA Euro 2024 teams.'), 
             p('For example, see the rightmost column of the top row: the probability of England defeating Romania is estimated to be 77.85%.'),
             uiOutput("ui_heatmap")),  
    tabPanel('Bookmaker consensus rating',
             p('UEFA Euro 2024 winning probabilities based on bookmaker consensus ratings.'),
             plotlyOutput("chart"),
             p('Bookmaker consensus ratings for UEFA Euro 2024, derived from 27 online bookmakers. For each team, the consensus winning probability (in %), corresponding log-odds, simulated log-abilities, and tournament group are provided.'),
             DT::dataTableOutput('rating')),
    tabPanel('Team Probabilites',
             p('Probabilities (in %) for each team to advance past the group phase.'),
             selectInput(inputId = "group1", label = "group", choices = sort(unique(datOdds$group)), selected = "A"),
             showOutput("teamChart", "nvd3")),
    tabPanel('Group Probabilites',
             p('Probabilities (in %) for the final standings of each group after the group phase.'),
             selectInput(inputId = "group2", label = "group", choices = sort(unique(datOdds$group)), selected = "A"),
             DT::dataTableOutput('group')),
    tabPanel('Match Probabilities',
             selectInput(inputId = "round", label = "round", choices = unique(knockoutStageResult$round), selected = unique(knockoutStageResult$round)[1]),
             DT::dataTableOutput('matches'))
))