(ns trendme.handler
  (:import (org.jsoup Jsoup))
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as str]
            [selmer.parser :as selmer]
            [clojure.java.io :as io]))

(def LANGS ["javascript" "typescript" "python" "html" "clojure"])

(defn get-html-result [lang]

  (slurp (str "https://github.com/trending/" lang "?since=weekly")))

(defn map-article [article]
  (let [h1 (first (.getElementsByTag article "h1")) description (first (.getElementsByTag article "p"))]
    {"title" (second (str/split (.text h1) #" / "))
     "author" (first (str/split (.text h1) #" / "))
     "link" (str "https://github.com" (.attr (first (.getElementsByTag h1 "a")) "href"))
     "description" (if description (.text description) nil)}))

(defn parse-repos [html]
  (let [soup (Jsoup/parse html)
        paragraphs (.getElementsByTag soup "article")]
    (for [article paragraphs] (map-article article))))

(defn get-all-data []
  (let [repos (for [lang LANGS] (future {:lang lang :data (parse-repos (get-html-result lang))}))]
    (->> repos (map deref))))

(defroutes app-routes
  (GET "/" [] (selmer/render-file (io/resource "home.html") {:langs (get-all-data)}))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
