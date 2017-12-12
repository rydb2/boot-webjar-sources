(ns rydb2.boot-webjar-sources
  ;; {:boot/export-tasks true}
  (:require
   [clojure.java.io :as io]
   [boot.pod        :as pod]
   [boot.core       :as core]
   [boot.util       :as util]
   [clojure.string   :as str])
  (:import [java.util.jar JarFile JarEntry]))

(def ^:private webjars-pattern
  #"META-INF/resources/webjars/([^/]+)/([^/]+)/(.*)$")

(defn asset-path [resource]
  (let [[_ name version path] (re-matches webjars-pattern resource)]
    (str name "/" path)))

(defn webjars-path-regex [name]
  (let [re-str (format ".*/webjars/%s/.*/%s-.*\\.jar$" name name)]
    (re-pattern re-str)))

(defn asset-webjar [name paths]
  (filter #(re-matches (webjars-path-regex name) %) paths))

(defn get-file-name [path]
  (last (str/split path #"/")))


(defn get-jar-file [name]
  (if-let [jar-path (first (asset-webjar name (str/split (:fake-class-path pod/env) #":")))]
    (JarFile. jar-path)))

(defn get-entries [name matching]
  (if-let [jar-file (get-jar-file name)]
    (->> jar-file
         (.entries)
         (enumeration-seq)
         (filter (defn f [entry]
                   (let [name (.getName ^JarEntry entry)]
                     (and
                       (re-matches webjars-pattern name)
                       (re-matches (re-pattern matching) name))))))
    (print "error")))

(core/deftask webjar-sources
  "copy webjar sources"
  [n name str "webjar name"
   m matching str "sources path regex string in webjar"
   t target str "output dir"]
  (let [entries (get-entries name matching)
        jar-file (get-jar-file name)]
    (core/with-pre-wrap fileset
      (doseq [entry entries]
        (let [jar-name (.getName ^JarEntry entry)
              in-file (.getInputStream jar-file entry)
              out-file (doto
                           (io/file target (get-file-name jar-name))
                           io/make-parents)
              tmp-in-files (core/input-files fileset)]
          (do
            (util/info "%s\ncopy %s \nto   %s\n" name (.getName ^JarEntry entry) out-file)
            (io/make-parents (.getParent out-file))
            (io/copy in-file out-file))))
     (-> fileset
         (core/add-resource (io/file (.getParent (io/file target))))
         core/commit!))))

