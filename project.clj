(defproject webdev "0.1.0-SNAPSHOT"
  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.2"]
                 [compojure "1.3.1"]
                 [http-kit "2.1.16"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [java-jdbc/dsl "0.1.1"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [hiccup "1.0.5"]
                 [environ "1.0.0"]]

  :min-lein-version "2.0.0"

  :uberjar-name "webdev.jar"

  :main webdev.core

  :profiles {
             :dev {
                   :env {:database-url "jdbc:postgresql://localhost/webdev"}
                   :main webdev.core/-dev-main
                   :dependencies [[ring/ring-mock "0.2.0"]]}
             :test {
                    :env {:database-url "jdbc:postgresql://localhost/webdev_test"}}}

  :plugins [[lein-environ "1.0.0"]])
