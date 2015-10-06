(ns reagent-motion-demos.demo0
  (:require [reagent.core :as reagent]
            [reagent-motion-demos.common :as hc]))

(defn Child
  [{x :x}]
  [:div {:style {:border-radius "4px"
                 :background-color "rgb(240,240,232)"
                 :position "relative"
                 :margin "5px 3px 10px"
                 :width "450px"
                 :height "50px"}}
   [:div
    {:style
     {:position "absolute"
      :width "50px"
      :height "50px"
      :border-radius "4px"
      :background-color "rgb(130, 181, 198)"
      :-webkit-transform (str "translateX(" x "px)")
      :transform (str "translateX(" x "px)")}}]])

(def Child-comp (reagent/reactify-component Child))

(defn show-demo
  [state data]
  [:div
   [:button.btn.btn-primary
    {:on-mouse-down #(swap! state update :open not)
     :on-touch-start (fn [e]
                       (.preventDefault e)
                       (swap! state update :open not))}
    "Toggle"]
   [hc/Motion {:style {:x (hc/spring (if (:open data)
                                       400
                                       0))}}
    (fn [x]
      (reagent/create-element Child-comp x))]])
