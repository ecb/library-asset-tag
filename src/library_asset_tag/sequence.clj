(ns library-asset-tag.sequence
  (:require [library-asset-tag.db :as database]
            [datomic.api :as datomic]))

(defn get []
  (let [db (-> (database/get-connection) datomic/db)
        next (datomic/pull db
                           [:sequence/next]
                           [:sequence/id 0])]
    (println "next:" next)
    (str next)))

(defn set [value]
  (let [conn (database/get-connection)]
    (datomic/transact conn {:tx-data [{:sequence/id 0
                                       :sequence/next value}]})))
