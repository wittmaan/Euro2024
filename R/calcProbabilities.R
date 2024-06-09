library(data.table)
library(dplyr)

bettingsodds_filename <- "bettingOdds_20240609.csv"
dat <- fread(paste0("../data/", bettingsodds_filename))

calcOdds <- function(quotedOdds, delta=1) {
  (quotedOdds - 1) / delta
}

calcProbs <- function(quotedOdds, delta=1) {
  odds <- calcOdds(quotedOdds, delta)
  1 - odds / (1 + odds)
}

deltaOpt <- dat[, lapply(.SD, function(quotedOdds) {
  deltaOpt <- uniroot(function(delta) {
    sum(calcProbs(quotedOdds, delta), na.rm = TRUE) - 1
  }, interval = c(0.0001, 1.0))$root
  deltaOpt
  
}), .SDcols = 4:ncol(dat)]


probs <- lapply(colnames(deltaOpt), function(col) {
  calcProbs(dat[, col, with=FALSE], unlist(deltaOpt[, col, with=FALSE]))
}) %>% as.data.table()


logit <- function(x) log(x/(1-x))
invLogit <- function(p) { exp(p) / (1 + exp(p)) }


dat[, logOdds := rowMeans(logit(probs), na.rm = TRUE)]
dat[, probabilities := invLogit(logOdds)]

date_part <- sub("bettingOdds_(\\d{8})\\.csv", "\\1", bettingsodds_filename)
probabilities_filename <- paste0("probabilities_", date_part, ".csv")

fwrite(dat[, .(code, group, logOdds, probabilities)], paste0("../data/", probabilities_filename), sep = ";")