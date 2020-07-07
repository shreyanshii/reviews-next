(ns reviews-next.db.reviews
  (:require
          [clojure.pprint :as pp]
          [ragtime.jdbc :as jdbc]
          [clojure.java.jdbc :refer :all]
          [reviews-next.config :as config]
          [reviews-next.db.migrations :as migrations]))


(def data
  {:title "title-trial-2"
   :review_date "21-04-2020"})

(defn format-date
  [date]
  (.format
      (java.text.SimpleDateFormat. "yyyy-MM-dd")
      (.parse
        (java.text.SimpleDateFormat. "dd-MM-yyyy")
        date)))

(defn insert
  "execute insert and return lazy sequence"
  ([data] (insert data (config/connection-uri)))
  ([data connection-uri]
   (try
     (do
       (insert! connection-uri :reviews
                                        {:title (:title data)
                                         :review_date (format-date (:review_date data))})
       true)
    (catch Exception e
         (str e)))))


(defn delete-all
  ([] (delete-all (config/connection-uri)))
  ([connection-uri]
   (db-do-commands connection-uri "delete from reviews")))

(defn get-list
  ([] (get-list (config/connection-uri)))
  ([connection-uri]
   (try
      (query connection-uri ["select id, title from reviews order by review_date desc"])
     (catch Exception e
      false))))

(defn insert-and-get-last-id
  ([data] (insert-and-get-last-id data (config/connection-uri)))
  ([data connection-uri]
   (try
     (with-db-transaction [t-con connection-uri]
                          (insert! t-con :reviews data)
                          (get
                            (nth
                              (query t-con ["SELECT id FROM reviews ORDER BY id DESC LIMIT 1"]) 0) :id))
     (catch Exception e
      false))))
