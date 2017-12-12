(def +version+ "0.1.0-SNAPSHOT")

(set-env!
  :resource-paths #{"src"}
  :dependencies   '[[org.clojure/clojure "1.8.0" :scope "provided"]
                    [org.webjars/materializecss "0.100.2" :scope "test"]
                    [adzerk/bootlaces "0.1.13" :scope "test"]])

(require '[rydb2.boot-webjar-sources :refer [webjar-sources]]
         '[adzerk.bootlaces :refer :all])

(def +version+ "0.0.1")

(task-options!
  webjar-sources {:name "materializecss"
                  :matching ".*/fonts/.*.(woff|woff2|ttf)$"
                  :target "target/fonts"}
  pom {:project     'rydb2/boot-webjar-sources
       :version     +version+
       :description "Boot task to copy sources in webjar (like fonts)"
       :url         "https://github.com/rydb2/boot-webjar-sources"
       :scm         {:url "https://github.com/deraen/boot-less"}
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

