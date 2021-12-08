(ns trendme.handler
  (:import (org.jsoup Jsoup))
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as str]
            [selmer.parser :as selmer]
            [clojure.core.cache :as cache]
            [clojure.java.io :as io]))

(def C (cache/ttl-cache-factory {} :ttl (* 12 3600000))) ;; 12 hours
(def LANGS ["javascript" "typescript" "python" "html" "rust" "clojure"])

(defn get-date [] (.format (new java.text.SimpleDateFormat "yyyy-MM-dd") (java.util.Date.)))

(defn get-html-result [lang]
  (slurp (str "https://github.com/trending/" lang "?since=weekly")))

(defn map-article [article]
  (let [h1 (first (.getElementsByTag article "h1")) description (first (.getElementsByTag article "p"))]
    {"title" (second (str/split (.text h1) #" / "))
     "author" (first (str/split (.text h1) #" / "))
     "link" (str "https://github.com" (.attr (first (.getElementsByTag h1 "a")) "href"))
     "description" (if description (.text description) nil)
     "stars" (.text (first (.getElementsByClass article "Link--muted d-inline-block mr-3")))}))

(defn parse-repos [html]
  (let [soup (Jsoup/parse html)
        paragraphs (.getElementsByTag soup "article")]
    (for [article paragraphs] (map-article article))))

(defn get-all-data []
  (for [lang LANGS] {:lang lang :data (parse-repos (get-html-result lang))}))

(defroutes app-routes
  (GET "/" [] (selmer/render-file (io/resource "home.html") {:langs (get-all-data)}))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
