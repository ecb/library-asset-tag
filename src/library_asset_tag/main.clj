(ns library-asset-tag.main
  (:require [library-asset-tag.handler :as handler]
            [library-asset-tag.db :as db]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(def options
  [["-h" "--help"]
   [nil "--port PORT" "The port to host our service endpoint"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 65536) "Must be a number between 0 and 65536"]]
   [nil "--db-host HOST" "The host for our database"
    :default "localhost"]
   [nil "--db-port PORT" "The port for our database"
    :default 4334
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 65536) "Must be a number between 0 and 65536"]]
   [nil "--db-name NAME" "The name of our database on the db server"
    :default "library-asset-tag"]])

(defn exit [status msg & rest]
  (do
    (apply println msg rest)
    status))

(defn prep-usage [msg] (->> msg flatten (string/join \newline)))

(defn usage [options-summary]
  (prep-usage ["Usage: library-asset-tagd [options] action"
               ""
               "Options:"
               options-summary]))

(defn -app [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args options)]
    (cond

      (:help options)
      (exit 0 (usage summary))

      (not= errors nil)
      (exit -1 "Error: " (string/join errors))

      :else
      (let [{:keys [port db-host db-port db-name]} options
            db-url (str "datomic:free://" db-host ":" db-port "/" db-name)
            db (db/connect db-url)]
        (run-jetty handler/app {:port port})))))

(defn -main [& args]
  (System/exit (apply -app args)))
