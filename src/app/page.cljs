
(ns app.page
  (:require [app.schema :as schema]
            [cljs.reader :refer [read-string]]
            [app.config :as config]
            [app.util :refer [get-env!]]
            [build.util :refer [get-ip!]]
            ["react-dom/server" :refer [renderToString]]
            ["react" :as React]
            ["fs" :as fs]
            [reacher.core :refer [div html head body link style script title meta']]
            [app.comp.container :refer [comp-container]]
            [app.schema :as schema])
  (:require-macros [clojure.core.strint :refer [<<]]))

(def base-info
  {:title (:title config/site), :icon (:icon config/site), :ssr nil, :inline-html nil})

(defn make-page [content info]
  (renderToString
   (html
    {}
    (head
     {}
     (title {:title (:title info)})
     (link {:ref "icon", :type "image/png", :href (:icon info)})
     (meta' {:charSet "utf8"})
     (meta'
      {:name "viewport",
       :content "width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"})
     (->> (:inline-styles info)
          (map-indexed
           (fn [idx css] (style {:key idx, :dangerouslySetInnerHTML {:__html css}})))
          (apply array)))
    (body
     {}
     (div {:className "app", :dangerouslySetInnerHTML {:__html content}})
     (->> (:scripts info)
          (map-indexed (fn [idx src] (script {:src src, :key idx})))
          (apply array))))))

(defn dev-page []
  (make-page
   ""
   (merge
    base-info
    {:styles [(<< "http://~(get-ip!):8100/main.css") "/entry/main.css"],
     :scripts ["/client.js"],
     :inline-styles []})))

(def local-bundle? (= "local-bundle" (get-env! "mode")))

(defn slurp [file-path] (fs/readFileSync file-path "utf8"))

(defn prod-page []
  (let [content (renderToString (comp-container schema/store))
        assets (read-string (slurp "dist/assets.edn"))
        cdn (if local-bundle? "" (:cdn-url config/site))
        prefix-cdn (fn [x] (str cdn x))]
    (make-page
     content
     (merge
      base-info
      {:styles [(:release-ui config/site)],
       :scripts (map #(-> % :output-name prefix-cdn) assets),
       :ssr "respo-ssr",
       :inline-styles [(slurp "./entry/main.css")]}))))

(defn spit [file-path content] (fs/writeFileSync file-path content))

(defn main! []
  (if (contains? config/bundle-builds (get-env! "mode"))
    (spit "dist/index.html" (prod-page))
    (spit "target/index.html" (dev-page))))
