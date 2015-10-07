(ns reagent-motion-demos.demo5
  (:require [reagent.core :as reagent]
            [goog.object :as gobj]
            [reagent-motion-demos.common :as hc]))

(defn handle-submit
  [state e]
  (.preventDefault e)
  (swap! state
         (fn [data]
           (let [t  (str (inc (count (:todos data))))]
             (assoc-in data [:todos t] {:text (:value data)
                                        :done? false})))))

(defn handle-toggle-all
  [state]
  (swap! state
         (fn [data]
           (let [all-done (every? #(:is-done? (val %)) (:todos data))]
             (update data
                     :todos
                     (fn [xs] (into {} (map (fn [[k v]]
                                              [k (assoc v :done? (not all-done))])
                                            xs))))))))

(defn get-default-value
  [data]
  (into {} (map (fn [[k v]]
                  [k {:height  (hc/spring 0)
                      :opacity (hc/spring 1)
                      :data    v}])
                (:todos data))))

(defn get-end-value
  [data]
  (->> (:todos data)
       (filter (fn [[k v]]
                 (and (>= (.indexOf (:text v) (.toUpperCase (:value data))) 0)
                      (or (= :all (:selected data))
                          (and (:done? v) (= :completed (:selected data)))
                          (and (not (:done? v)) (= :active (:selected data)))))))
       (map (fn [[k v]]
              [k {:height (hc/spring 60 (.-gentle hc/presets))
                  :opacity (hc/spring 1 (.-gentle hc/presets))
                  :data  v}]))
       (into {})))

(defn handle-done
  [state k]
  (swap! state update-in [:todos k :done?] not))

(defn handle-destroy
  [state k]
  (swap! state update-in [:todos] dissoc k))

(defn Child
  [d]
  (let [[configs state data] (:children d)]
    [:ul.todo-list
     (for [k (keys (:todos data))
           :let [x  (gobj/get configs k)]
           :when (some? x)]
       ^{:key k} [:li {:style {:height (gobj/get x "height")
                               :opacity (gobj/get x "opacity")}
                       :class (when (-> x
                                        (gobj/get "data")
                                        (gobj/get "done?"))
                                "completed")}
                  [:div.view
                   [:input.toggle {:type "checkbox"
                                   :on-change #(handle-done state k)
                                   :checked (-> x
                                                (gobj/get "data")
                                                (gobj/get "done?"))}]
                   [:label (-> x
                               (gobj/get "data")
                               (gobj/get "text"))]
                   [:button.destroy {:on-click #(handle-destroy state k)}]]])]))

(def Child-comp (reagent/reactify-component Child))

(defn handle-clear-completed
  [state]
  (swap! state
         (fn [data]
           (update data :todos (fn [xs]
                                 (into {}
                                       (remove (fn [[k v]]
                                                 (:done? v)) xs)))))))

(defn will-leave
  [date x]
  (js-obj "height" (hc/spring 0)
          "opacity" (hc/spring 0)
          "data" (.-data x)))

(defn will-enter
  [data date]
  (js-obj "height" (hc/spring 0)
          "opacity" (hc/spring 1)
          "data" (clj->js (get-in data [:todos date]))))

(defn show-demo
  [state data]
  [:section.todoapp
   [:header.header
    [:h1 "todos"]
    [:form {:on-submit (partial handle-submit state)}
     [:input.new-todo {:placeholder "What needs to be done?"
                       :auto-focus true
                       :value (:value data)
                       :on-change (fn [e]
                                    (swap! state assoc :value (-> e .-target .-value)))}]]]
   [:section.main
    [:input.toggle-all {:type "checkbox"
                        :on-change #(handle-toggle-all state)}]
    [hc/TransitionMotion {:defaultStyles (get-default-value data)
                          :styles (get-end-value data)
                          :willLeave will-leave
                          :willEnter (partial will-enter data)}
     (fn [configs]
       (reagent/create-element Child-comp #js {} [configs state data]))]]
   [:footer.footer
    [:span.todo-count
     [:strong (count (remove #(:done? (val %)) (:todos data)))]
     " item left"]
    [:ul.filters
     [:li [:a {:class (when (= :all (:selected data))
                        "selected")
               :on-click #(swap! state assoc :selected :all)}
           "All"]]
     [:li [:a {:class (when (= :active (:selected data))
                        "selected")
               :on-click #(swap! state assoc :selected :active)}
           "Active"]]
     [:li [:a {:class (when (= :completed (:selected data))
                        "selected")
               :on-click #(swap! state assoc :selected :completed)}
           "Completed"]]]
    [:button.clear-completed {:on-click #(handle-clear-completed state)}
     "Clear completed"]]])
