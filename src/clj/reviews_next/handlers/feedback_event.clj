(ns reviews-next.handlers.feedback-event
  (:require [hiccup.core :as h]
            [hiccup.page :as page]
            [clojure.pprint :as pp]
            [reviews-next.db.reviews :as reviews]
            [reviews-next.db.users :as users]
            [reviews-next.db.user-reviews :as user-reviews]
            [reviews-next.db.user-feedback :as user-feedback]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.util.response :refer [response]]))

; change publish ito to create
(defn insert-into-db [_request]
  (let [title (get-in _request [:params "title"])
        date (get-in _request [:params "review_date"])
        from_uid (get-in _request [:params "from_uid"])
        participants (get-in _request [:params "participants"])
        review_id (reviews/insert-and-get-last-id {:title title
                                                   :review_date date})]
    (doseq [participant participants]
       (user-reviews/insert {:from_uid from_uid :to_uid participant :review_id review_id}))
    (response (str "Posted"))))

(defn users-list [_request]
 (let [review_id (get-in _request [:params :review_id])
       user-ids (user-reviews/users-for-review-id review_id)]
   (response (users/users-for-given-ids user-ids))))

(defn create_user_review_map [user_id review_id] 
  {:user_id user_id :review_id review_id})

(defn get-user-and-review-ids [_request]
  (let [reviews-list (user-reviews/get-reviews-for-user "U2")
        user_ids (map :from_uid reviews-list)
        review_ids (map :review_id reviews-list)
        user_review_list (map create_user_review_map user_ids review_ids)]
    
    (response  user_review_list)))

(defn reviews-list [_request]
   (response (reviews/get-list)))

;; (defn get-reviews-for-user [_request]
;;   (response (user-reviews/get-reviews-for-user "U2")))

(defn into-user-feedback
  [_request]
  (let [from_uid (get-in _request [:params "from_uid"])
        to_uid (get-in _request [:params "to_uid"])
        review_id (get-in _request [:params "review_id"])
        feedback (get-in _request [:params "feedback"])
        level (get-in _request [:params "level"])]
    (response (str user-feedback/insert {:from_uid from_uid
                                         :to_uid to_uid
                                         :review_id review_id
                                         :feedback feedback
                                         :level level}))))