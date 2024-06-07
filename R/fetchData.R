library(rvest)
library(data.table)
library(textclean)
library(stringr)
library(RSelenium)
library(stringi)

## read group data stuff

result <- fread("euro_groups_teams.csv")

## fetch betting odds

system('docker run -d -p 4445:4444 jfrog.hub.vwgroup.com/remote-docker-io/selenium/standalone-firefox')

remDr <- remoteDriver(remoteServerAddr = "localhost", port = 4445L, browserName = "firefox")
Sys.sleep(5)
remDr$open()
remDr$navigate("https://www.oddschecker.com/football/euro-2024/winner")

dataRaw <- read_html(remDr$getPageSource()[[1]]) %>% 
  html_nodes(xpath = "//*[(@id = 'oddsTableContainer')]") 

remDr$close()
system("docker stop $(docker ps -a -q)")
system("docker rm $(docker ps -a -q)")


