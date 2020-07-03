(ns reviews-next.events
  (:require
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [re-frame.core :as re-frame]
   [reviews-next.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   {:db db/initial-db}))

(re-frame/reg-event-db
 ::participants-from-backend
 (fn [db [_ participants-response]]
   (assoc db :participants (vec participants-response))))

(re-frame/reg-event-fx
 ::populate-participants
 (fn [_ _]
   {:http-xhrio {:method :get
                 :uri    "/api/users"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::participants-from-backend]
                 :on-fail    [::api-failed]}}))

(re-frame/reg-fx
 ::setup-google-signin-functions
 (fn []
   (set!
    (.. js/window -onSignIn)
    (clj->js (fn [& args] (apply (.-log js/console) args))))))

(re-frame/reg-event-db
  ::title-change
   (fn [db [_ new-title]]
    (assoc db :review-event-title new-title)))

(re-frame/reg-event-db
  ::date-change
   (fn [db [_ new-date]]
    (assoc db :review-date new-date)))

(re-frame/reg-event-db
  ::description-change
  (fn [db [_ new-desc]]
    (assoc db :review-description new-desc)))

(re-frame/reg-event-db
  ::all-fields-valid-change
  (fn [db [_ new-val]]
    (assoc db :all-fields-valid? new-val)))

(re-frame/reg-event-db
  ::set-participants
  (fn [db [_ new-val]]
    (assoc db :participants new-val)))

(re-frame/reg-event-db
  ::add-to-selected-participants
  (fn [db [_ new-val]]
    (assoc db :selected-participants (set (conj (get db :selected-participants) new-val)))))

(re-frame/reg-event-db
  ::remove-from-selected-participants
  (fn [db [_ new-val]]
    (assoc db :selected-participants (disj (set (get db :selected-participants)) new-val))))

(re-frame/reg-event-db
 ::remove-all-selected-participants
 (fn [db [_]]
   (assoc db :selected-participants [])))

(re-frame/reg-event-db
 ::clear-all-fields
 (fn [db [_ new_val]]
   (assoc db :clear-all-fields new_val)))