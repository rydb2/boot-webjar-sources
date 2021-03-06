(def +version+ "0.1.0-SNAPSHOT")

(defn read-password [guide]
  (String/valueOf (.readPassword (System/console) guide nil)))

(set-env!
  :resource-paths #{"src"}
  :dependencies   '[[org.clojure/clojure "1.8.0" :scope "provided"]
                    [org.webjars/materializecss "0.100.2" :scope "test"]
                    [adzerk/bootlaces "0.1.13" :scope "test"]]
  :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"
                                     :username "rydb2"
                                     :password (read-password "Clojars password: ")}]))

(require '[rydb2.boot-webjar-sources :refer [webjar-sources]]
         '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0")

(task-options!
  webjar-sources {:name "materializecss"
                  :matching ".*/fonts/.*.(woff|woff2|ttf)$"
                  :target "fonts/roboto"}
  pom {:project     'rydb2/boot-webjar-sources
       :version     +version+
       :description "Boot task to copy sources in webjar (like fonts)"
       :url         "https://github.com/rydb2/boot-webjar-sources"
       :scm         {:url "https://github.com/boot-webjar-sources"}
       :license     {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})


(deftask dev []
  (comp (webjar-sources)
        (target :dir #{"target"})))

(bootlaces! +version+)

(deftask build []
  (comp
   (pom)
   (jar)
   (install)))

(deftask deploy []
  (comp
   (pom)
   (jar)
   (push :repo "clojars" :gpg-sign false)))

