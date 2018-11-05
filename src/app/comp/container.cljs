
(ns app.comp.container
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [app.config :refer [dev?]]
            ["react" :as React]
            ["react-dom" :as ReactDOM]
            [reacher.core
             :refer
             [create-comp div input span button a adorn dispatch! get-state get-value]]
            [respo-ui.core :as ui]
            [reacher.comp :refer [=< comp-inspect]]))

(def comp-container
  (create-comp
   {:state nil, :name :app-container}
   (fn [[store] state mutate!] (div {:style (adorn ui/global {})} "Containers2"))))
