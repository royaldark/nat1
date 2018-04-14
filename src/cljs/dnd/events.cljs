(ns dnd.events
  (:require [ajax.core :as ajax]
            [re-frame.core :as re-frame]
            [dnd.api :as api]
            [dnd.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-event-fx
  ::api/roll-die
  (fn [{:keys [db] :as cofx}
       [_ id sides modifier]]
    {:db (assoc-in db [::api/roll-die-results id] 0)
     :http-xhrio {:method          :post
                  :uri             (str "http://localhost:3000/roll-die")
                  :params          {:sides sides
                                    :modifier modifier}
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::api/roll-die-success id]
                  :on-failure      [::api/roll-die-failure id]}}))

(re-frame/reg-event-db
  ::api/roll-die-success
  (fn [db [_ id result]]
    (assoc-in db [::api/roll-die-results id] (:roll result))))

(re-frame/reg-event-db
  ::api/roll-die-failure
  (fn [db [_ id result]]
    (println result)
    #_(assoc db :api-result result)
    db))
