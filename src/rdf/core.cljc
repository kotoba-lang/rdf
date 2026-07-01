(ns rdf.core
  "EDN-first RDF terms, triples, quads, and datasets.")

(def rdf-lang-string "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString")
(def xsd-string "http://www.w3.org/2001/XMLSchema#string")

(defn iri [value]
  {:rdf/type :iri :value value})

(defn bnode [id]
  {:rdf/type :blank :id id})

(defn literal
  ([value] (literal value {}))
  ([value {:keys [datatype language]}]
   (cond-> {:rdf/type :literal :value value}
     datatype (assoc :datatype (if (map? datatype) datatype (iri datatype)))
     language (assoc :language language))))

(defn term? [x]
  (and (map? x)
       (contains? #{:iri :blank :literal} (:rdf/type x))))

(defn triple [subject predicate object]
  {:subject subject :predicate predicate :object object})

(defn quad
  ([subject predicate object] (triple subject predicate object))
  ([subject predicate object graph]
   {:subject subject :predicate predicate :object object :graph graph}))

(defn dataset [quads]
  {:rdf/dataset (vec quads)})

(defn quads [dataset-or-quads]
  (if (map? dataset-or-quads)
    (:rdf/dataset dataset-or-quads)
    dataset-or-quads))

(defn errors [q]
  (cond-> []
    (not (term? (:subject q))) (conj {:error :rdf/invalid-subject :value (:subject q)})
    (not= :iri (:rdf/type (:predicate q))) (conj {:error :rdf/predicate-must-be-iri :value (:predicate q)})
    (not (term? (:object q))) (conj {:error :rdf/invalid-object :value (:object q)})
    (and (contains? q :graph) (not (term? (:graph q)))) (conj {:error :rdf/invalid-graph :value (:graph q)})))

(defn validate-quad [q]
  (let [es (errors q)]
    {:valid? (empty? es) :errors es}))

(defn validate [dataset-or-quads]
  (let [es (mapcat errors (quads dataset-or-quads))]
    {:valid? (empty? es) :errors (vec es)}))
