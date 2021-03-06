(ns reviews-next.handlers.review-event
  (:require [hiccup.core :as h]
            [hiccup.page :as page]
            [clojure.pprint :as pp]
            [reviews-next.db.reviews :as reviews]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.util.response :refer [response]]))

(defn insert-into-db [_request]
  (let [title (get-in _request [:params "title"])
        date (get-in _request [:params "review_date"])]
    (response (str (reviews/insert {:title title :review_date date})))))

(defn not-found [_request]
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body (h/html
          (page/html5
           [:head]
           [:body
            [:div "Not found"]]))})
