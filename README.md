## boot-webjar-sources
-----
[![Clojars Project](https://img.shields.io/clojars/v/rydb2/boot-webjar-sources.svg)](https://clojars.org/rydb2/boot-webjar-sources)  
  A simple `webjar-sources` task that allows boot copy sources in webjar (like font file), into `target` directory (set in boot task).

-----
### Usage
add `rydb2/boot-webjar-sources` to `build.boot` dependencies  

#### params:
- `name`: webjar name
- `matching`: regex string, will used to filter the sources in webjar
- `target`: path that files passed matching in webjar will be copied to

#### Example of build.boot
`cpy` task will copy *.woff/*.woff2 in `org.webjars/materializecss` to `target/fonts/roboto/`
```clojure
(set-env!
  :resource-paths #{"src"}
  :dependencies   '[[org.clojure/clojure "1.8.0" :scope "provided"]
                    [org.webjars/materializecss "0.100.2" :scope]
                    [rydb2/boot-webjar-sources "<version>" :scope "provided"]])

(require '[rydb2.boot-webjar-sources :refer [webjar-sources]])

(deftask cpy []
  (comp (webjar-sources :name "materializecss"
                        :matching ".*/fonts/.*.(woff|woff2)$"
                        :target "fonts/roboto")
        (target :dir #{"target"})))
```
