(ns rydb2.boot-webjar-sources
  {:boot/export-tasks true}
  (:require
   [clojure.java.io :as io]
   [boot.pod        :as pod]
   [boot.core       :as core]
   [boot.util       :as util]
   [clojure.sting   :as str])
  (:import [java.util.jar JarFile JarEntry]))

(def ^:private webjars-pattern
  #"META-INF/resources/webjars/([^/]+)/([^/]+)/(.*)")

(defn ^:private asset-path [resource]
  (let [[_ name version path] (re-matches webjars-pattern resource)]
    (str name "/" path)))

(defn ^:private webjars-path-regex [name]
  (let [re-str (format ".*/webjars/%s/.*/%s-.*\\.jar$" name name)]
    (re-pattern re-str)))

(defn asset-webjar [name paths]
  (filter #(re-matches (webjars-path-regex name) %) paths))

(defn get-sources [name]
  (if-let [jar (first (asset-webjar name (str/split (:fake-class-path pod/env) ":")))]
    (enumeration-seq (.entries jar))))
