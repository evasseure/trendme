(ns github-trendme.core
  (:import (org.jsoup Jsoup))
  (:require [clojure.string :as str]))

(defn get-html-result [lang]
  (slurp (str "https://github.com/trending/" lang "?since=weekly")))

(defn map-article [article]
  (let [h1 (first (.getElementsByTag article "h1")) description (first (.getElementsByTag article "p"))]
    {"title" (second (str/split (.text h1) #" / "))
     "author" (first (str/split (.text h1) #" / "))
     "link" (str "https://github.com" (.attr (first (.getElementsByTag h1 "a")) "href"))
     "description" (if description (.text description) nil)
     "stars" (.text (first (.getElementsByClass article "Link--muted d-inline-block mr-3")))}))

(defn fetch-repos [html]
  (let [soup (Jsoup/parse html)
        paragraphs (.getElementsByTag soup "article")]
    {:results (mapv #(map-article %) paragraphs)}))

(defn -main [& args] (println (fetch-repos (get-html-result "rust"))))

