import sys
import urllib

from fastapi import Body, FastAPI, status
from fastapi.responses import JSONResponse
from enum import Enum
import requests
import urllib.parse
import numpy as np
import wikipediaapi
from fastapi.responses import HTMLResponse

app = FastAPI()


@app.get("/", response_class=HTMLResponse)
async def root():
    with open("index.html", "r") as f:
        return "\n".join(f.readlines())


def calc_mean(X: dict):
    return [np.mean(X["mins"]), np.mean(X["maxs"]), np.mean(X["P"]), np.mean(X["wind"]), np.mean(X["clouds"]),
            np.mean(X["UV_index"])]


class LastDays(str, Enum):
    yesterday = "yesterday"
    tomorrow = "tomorrow"
    week = "week"
    month_beg = "month_beg"
    normal = "normal"


def get_str(x):
    if x is LastDays.normal:
        return ""
    if x is LastDays.yesterday:
        return "yesterday"
    if x is LastDays.tomorrow:
        return "tomorrow"
    if x is LastDays.week:
        return "next7days"
    return "monthtodate"


def create_AI_request(days, data, town_quote):
    for x in days:
        data["mins"].append(x["tempmin"])
        data["maxs"].append(x["tempmax"])
        data["P"].append(x["pressure"])
        data["wind"].append(x["windspeed"])
        data["UV_index"].append(x["uvindex"])
        data["clouds"].append(x["cloudcover"])

    avg_min, avg_max, avg_P, avg_wind, cloud_cov, avg_UV_index = calc_mean(data)
    gemini_req = f"Please tell me a poem about a weather in {town_quote} based on following information:\n"
    gemini_req += f"minimum average temperature(in Celsius degrees): {avg_min}\n" + f"maximum average temperature(in Celsius degrees): {avg_max}\n"
    gemini_req += f"Average_pressure (in hPa): {avg_P}\n"
    gemini_req += f"Average_wind speed (in km/h): {avg_wind}\n"
    gemini_req += f"Average cloud cover (percent): {cloud_cov}\n"
    gemini_req += f"Avg UV index (in international unit standard): {avg_UV_index}\n"
    return gemini_req


def get_weather_response(town_quote: str, typ: LastDays):
    my_key1 = ""
    enum_addon = get_str(typ)

    if enum_addon != "":
        enum_addon += "/"

    addr1 = f"https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/{town_quote}{enum_addon}?unitGroup=metric&key={my_key1}&contentType=json"
    response_weather = requests.get(addr1)
    return response_weather


def get_ai_response(text):
    my_key2 = ""
    AI_response = requests.post(
        f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={my_key2}",
        json={"contents": [{"parts": [{"text": text}]}]})
    return AI_response


def get_error(code):
    if 400 <= code < 500:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content="Nie znaleziono")
    elif 500 <= code < 600:
        return JSONResponse(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, content="Serwis niedostepny")
    else:
        return JSONResponse(status_code=status.HTTP_501_NOT_IMPLEMENTED, content="Niezaimplementowane")


@app.get("/ai_weather/{country_name}/{town_name}/{typ}")
async def ask_for_weather(country_name: str, town_name: str, typ: LastDays):
    town_quote = urllib.parse.quote(town_name, safe='') + ", " + urllib.parse.quote(country_name, safe='')

    response_weather = get_weather_response(town_quote, typ)
    code = response_weather.status_code
    if code != 200:
        return get_error(code)

    for_today = response_weather.json()

    data = {"mins": [], "maxs": [], "P": [], "wind": [], "clouds": [], "UV_index": []}
    days = for_today["days"]
    gemini_req = create_AI_request(days, data, town_quote)

    AI_response = get_ai_response(gemini_req + "\nReturn only the poem.")
    code = AI_response.status_code
    if code != 200:
        return get_error(code)

    AI_json = AI_response.json()["candidates"]

    return {"ans": AI_json[0]["content"]["parts"][0]["text"]}


@app.get("/sightseeing/{town_name}")
async def ask_for_attractions(town_name: str):
    wiki_wiki = wikipediaapi.Wikipedia(user_agent='MyProjectName (merlin@example.com)', language='en')
    page_py = wiki_wiki.page(town_name)
    if not page_py.exists():
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content="Nie znaleziono")

    Q = "Based on text from Wikipedia provided above, what three topics should I search Wikipedia for, in order to write comprehensive text about best places in this city?\n"
    Q += "Give me only 3 lines of output, with each keyword in a seperate line, all suitable to be found in English Wikipedia.\n"
    Q += "If a text is not about any city, give only one line of output with a word: error"

    AI_response = get_ai_response(f"{page_py.text}\n\n\n{Q}")

    code = AI_response.status_code
    if code != 200:
        return get_error(code)

    AI_json = AI_response.json()["candidates"]
    lines = AI_json[0]["content"]["parts"][0]["text"].strip().split("\n")
    if len(lines) == 1 and lines[0] == "error":
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content="Nie znaleziono")

    all_text = ""
    for place in lines:
        my_page = wiki_wiki.page(place)
        all_text += f"\nHere begin the article about {place}\n\n"
        all_text += my_page.text

    Q = "Based on text from Wikipedia provided above write quite short and comprehensive text about best places in this city. Do not use markdown formatting for output\n"

    AI_response = get_ai_response(f"{page_py.text}\n\n{all_text}\n\n{Q}")

    code = AI_response.status_code
    if code != 200:
        return get_error(code)

    AI_json = AI_response.json()["candidates"]
    return {"ans": AI_json[0]["content"]["parts"][0]["text"].strip()}
