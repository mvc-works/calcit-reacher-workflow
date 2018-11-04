
(ns app.page
  (:require [app.schema :as schema]
            [cljs.reader :refer [read-string]]
            [app.config :as config]
            [app.util :refer [get-env!]]
            [build.util :refer [get-ip!]]
            ["react-dom/server" :refer [renderToString]]
            ["react" :as React]
            ["fs" :as fs]
            [reacher.core :refer [div html head body link style script title meta']])
  (:require-macros [clojure.core.strint :refer [<<]]))

(def base-info
  {:title (:title config/site), :icon (:icon config/site), :ssr nil, :inline-html nil})

(defn make-page [info]
  (renderToString
   (html
    {}
    (head {} (title {:title info}) (meta' {:charSet "utf8"}))
    (body
     {}
     (div {:className "app"})
     (clj->js (map (fn [src] (script {:src src, :key src})) (:scripts info)))))))

(defn dev-page []
  (make-page
   (merge
    base-info
    {:styles [(<< "http://~(get-ip!):8100/main.css") "/entry/main.css"],
     :scripts ["/client.js"],
     :inline-styles []})))

(def local-bundle? (= "local-bundle" (get-env! "mode")))

(defn slurp [file-path] (fs/readFileSync file-path "utf8"))

(defn prod-page []
  (let [assets (read-string (slurp "dist/assets.edn"))
        cdn (if local-bundle? "" (:cdn-url config/site))
        prefix-cdn (fn [x] (str cdn x))]
    (make-page
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
