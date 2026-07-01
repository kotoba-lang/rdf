# kotoba-lang/rdf

EDN-first RDF dataset helpers.

RDF terms are plain maps:

- `{:rdf/type :iri :value "..."}`
- `{:rdf/type :blank :id "..."}`
- `{:rdf/type :literal :value "..." :datatype ... :language ...}`

Triples and quads are maps with `:subject`, `:predicate`, `:object`, and
optional `:graph`.
