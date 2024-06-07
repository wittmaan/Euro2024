from datetime import datetime

import nest_asyncio
import pandas as pd
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By

nest_asyncio.apply()

input_url = "https://www.oddschecker.com/football/euro-2024/winner"

chrome_driver_path = 'C:\\Software\\chromedriver\\chromedriver.exe'
service = Service(chrome_driver_path)

options = Options()
options.headless = True

driver = webdriver.Chrome(service=service, options=options)

driver.implicitly_wait(30)
driver.get(input_url)

bookmaker_list_raw = driver.find_element(By.XPATH, "//*[@id='oddsTableContainer']/table/thead/tr[5]")
alt_texts = [img.get_attribute('alt') for img in bookmaker_list_raw.find_elements(By.TAG_NAME, "img")]

bookmaker_list = []
for alt_text in alt_texts:
    bookmaker_list.append(alt_text)

odds_list_raw = driver.find_element(By.XPATH, "//*[(@id = 'oddsTableContainer')]")

odds_list_raw = odds_list_raw.text.split("\n")
index_quickbet = odds_list_raw.index("QuickBet")

odds_list_raw = odds_list_raw[index_quickbet + 1:]
country_list = odds_list_raw[::2]
odds_list = odds_list_raw[1::2]

print(f"{len(bookmaker_list)} -> {bookmaker_list}")
print(f"{len(country_list)} -> {country_list}")
print(f"{len(odds_list)} -> {odds_list}")


def fraction_to_float(fraction_str):
    if '/' in fraction_str:
        numerator, denominator = map(float, fraction_str.split('/'))
        return numerator / denominator
    else:
        return float(fraction_str)


odds_list_evaluated = []
for odds in odds_list:
    odds_entry = odds.replace("/ ", "/").split(" ")
    odds_entry = [fraction_to_float(_) for _ in odds_entry]
    print(f"{len(odds_entry)} -> {odds_entry}")
    odds_list_evaluated.append(odds_entry)

df_odds = pd.DataFrame(odds_list_evaluated, columns=bookmaker_list)
df_odds["team"] = country_list

df_groups = pd.read_csv("../data/euro_groups_teams.csv", sep=";")
df_odds.merge(df_groups, on="team")

current_date = datetime.now().strftime("%Y%m%d")
df_odds.to_csv(f"../data/bettingOdds_{current_date}.csv", index=False)

driver.quit()
