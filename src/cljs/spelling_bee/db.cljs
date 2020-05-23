(ns spelling-bee.db)

(def default-db           ;; what gets put into app-db by default.
  {:value ""
   :word-list []
   :game-data {:chars {:main "R" :rest ["A" "B" "F" "O" "P" "Y"]}
               :answers ["babyproof" "afar" "affray" "afro" "arbor" "array" "arroy" "bar" "barf" "boar" "boor" "bray" "farro" "fora" "foray" "fray" "parry" "poor" "pray" "proof" "prop" "pyro" "roar" "roof" "ropy"]}})
