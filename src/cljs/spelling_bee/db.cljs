(ns spelling-bee.db)

(def default-db           ;; what gets put into app-db by default.
  {:value ""
   :word-list []
   :current-score 0
   :game-data {:chars {:main "R" :rest ["A" "B" "F" "O" "P" "Y"]}
               :answers ["babyproof" "afar" "affray" "afro" "arbor" "array" "arroy" "barb" "barf" "boar" "boor" "bray" "farro" "fora" "foray" "fray" "parry" "poor" "pray" "proof" "prop" "pyro" "roar" "roof" "ropy"]
               :rankings (into (sorted-map) {0 "Beginner" 2 "Good Start" 5 "Moving Up" 8 "Good" 16 "Solid" 27 "Nice" 42 "Great" 53 "Amazing" 74 "Genius"})}})
