
(ns app.comp.container
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [app.config :refer [dev?]]
            ["react" :as React]
            ["react-dom" :as ReactDOM]
            [reacher.core :refer [div input span button a defcomp]]
            [respo-ui.core :as ui]
            [reacher.comp :refer [=< comp-inspect]]))

(defcomp comp-container (props) (div {:style ui/global} "Containers"))
