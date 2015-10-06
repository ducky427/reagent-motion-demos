(ns ^:figwheel-always reagent-motion-demos.core
  (:require [reagent.core :as reagent]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljsjs.react]
            [reagent-motion-demos.demo0 :as demo0]
            [reagent-motion-demos.demo1 :as demo1]
            [reagent-motion-demos.demo2 :as demo2]
            [reagent-motion-demos.demo3 :as demo3]
            [reagent-motion-demos.demo4 :as demo4])
  (:import goog.History))

(enable-console-print!)

(def demos {:demo0 0
            :demo1 1
            :demo2 2
            :demo3 3
            :demo4 4})

(def description {:demo0 "Simple Transition"
                  :demo1 "Chat heads"
                  :demo2 "Draggable Balls"
                  :demo3 "Draggable List"
                  :demo4 "Photo Gallery"})

(def num-demos (count demos))

(defonce app-state (reagent/atom {:demo0 {:open false}
                                  :demo1 {:x 250 :y 300}
                                  :demo2 {:mouse       [0 0]
                                          :delta       [0 0]
                                          :last-press  nil
                                          :is-pressed? false
                                          :order       (vec (range demo2/NUM))}
                                  :demo3 {:mouse       [0 0]
                                          :delta       [0 0]
                                          :last-press  nil
                                          :is-pressed? false
                                          :order       (vec (range demo3/NUM))}
                                  :demo4 {:photos [[500 350]
                                                   [800 600]
                                                   [800 400]
                                                   [700 500]
                                                   [200 650]
                                                   [600 600]]
                                          :current 0}
                                  :current-page :demo0}))

(defmulti show-page (fn [x _] x))

(defmethod show-page :demo0
  [_ state]
  [demo0/show-demo state @state])

(defmethod show-page :demo1
  [_ state]
  [demo1/show-demo state @state])

(defmethod show-page :demo2
  [_ state]
  [demo2/show-demo state @state])

(defmethod show-page :demo3
  [_ state]
  [demo3/show-demo state @state])

(defmethod show-page :demo4
  [_ state]
  [demo4/show-demo state @state])

(defn current-page
  []
  (let [k              (:current-page @app-state)
        current-index  (k demos)]
    [:div
     [:h2
      [:span "Reagent"]
      [:span [:a {:href "https://github.com/chenglou/react-motion"}
              " react-motion "]]
      [:span "demos. "]
      [:a {:href "https://github.com/ducky427/reagent-motion-demos"}
       "Source code"]]
     [:h3 (description k)]
     [:div.row
      [:div.col-md-6
       (when (not= num-demos (inc current-index))
         [:a.pull-right {:href (str "#/demo" (inc current-index))}
          [:span.fa.fa-chevron-circle-right.fa-3x]])
       (when (pos? current-index)
         [:a.pull-left {:href (str "#/demo" (dec current-index))}
          [:span.fa.fa-chevron-circle-left.fa-3x]])]]
     [:div
      [show-page k (reagent/cursor app-state [k])]]]))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (swap! app-state assoc :current-page :demo0))

(secretary/defroute "/demo0" []
  (swap! app-state assoc :current-page :demo0))

(secretary/defroute "/demo1" []
  (swap! app-state assoc :current-page :demo1))

(secretary/defroute "/demo2" []
  (swap! app-state assoc :current-page :demo2))

(secretary/defroute "/demo3" []
  (swap! app-state assoc :current-page :demo3))

(secretary/defroute "/demo4" []
  (swap! app-state assoc :current-page :demo4))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root
  []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (hook-browser-navigation!)
  (mount-root))
