
(ns app.page
  (:require [app.schema :as schema]
            [cljs.reader :refer [read-string]]
            [app.config :as config]
            [cumulo-util.build :refer [get-ip!]]
            ["react-dom/server" :refer [renderToString]]
            ["react" :as React]
            ["fs" :as fs]
            [reacher.core :refer [div html head body link style script title meta']]
            [app.comp.container :refer [comp-container]]
            [app.schema :as schema]
            [applied-science.js-interop :as j])
  (:require-macros [clojure.core.strint :refer [<<]]))

(def base-info {:title (:title config/site), :icon (:icon config/site), :inline-html nil})

(defn make-page [content info]
  (renderToString
   (html
    {}
    (head
     {}
     (title {} (:title info))
     (link {:ref "icon", :type "image/png", :href (:icon info)})
     (meta' {:char-set "utf8"})
     (meta'
      {:name "viewport",
       :content "width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"})
     (->> (:styles info)
          (map
           (fn [css-url]
             (link {:rel "stylesheet", :type "text/css", :href css-url, :key css-url})))
          (apply array))
     (->> (:inline-styles info)
          (map-indexed
           (fn [idx css] (style {:key idx, :dangerouslySetInnerHTML {:__html css}})))
          (apply array)))
    (body
     {}
     (div {:class-name "app", :dangerouslySetInnerHTML {:__html content}})
     (->> (:scripts info)
          (map-indexed (fn [idx src] (script {:src src, :key idx})))
          (apply array))))))

(defn dev-page []
  (make-page
   ""
   (merge
    base-info
    {:styles [(<< "http://~(get-ip!):8100/main.css") "/entry/main.css" "/main.css"],
     :scripts ["/client.js"],
     :inline-styles []})))

(defn slurp [file-path] (fs/readFileSync file-path "utf8"))

(defn prod-page []
  (let [assets (read-string (slurp "dist/assets.edn"))
        cdn (if config/cdn? (:cdn-url config/site) "")
        prefix-cdn (fn [x] (str cdn x))
        stat (js/JSON.parse (slurp "dist/stats.json"))
        css-main (j/get-in stat [:assetsByChunkName :main 0])]
    (make-page
     ""
     (merge
      base-info
      {:styles [(:release-ui config/site) (prefix-cdn css-main)],
       :scripts (map #(-> % :output-name prefix-cdn) assets),
       :inline-styles [(slurp "./entry/main.css")]}))))

(defn spit [file-path content] (fs/writeFileSync file-path content))

(defn main! []
  (println "Running mode:" (if config/dev? "dev" "release"))
  (if config/dev?
    (spit "target/index.html" (dev-page))
    (spit "dist/index.html" (prod-page))))
