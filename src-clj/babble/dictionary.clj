(ns babble.dictionary
  (:use [cheshire.core :only (generate-string)])
  (:require [clojure.tools.logging :as log]
            [clj-time.core :as time]
            [clojure.string :as string]))

(def PREFIX :prefix)
(def SUFFIX :suffix)
(def ENDING_PUNCTUATION "ending punctuation")
(def NONENDING_PUNCTUATION "nonending punctuation")

(defn get-prefixes []
  (sorted-map "pro-" {:type PREFIX}
              "anti-" {:type PREFIX}
              "post-" {:type PREFIX}
              "pre-" {:type PREFIX}
              "de-" {:type PREFIX}
              "super-" {:type PREFIX}
              "ex-" {:type PREFIX}
              "un-" {:type PREFIX}
              "tri-" {:type PREFIX}
              "centi-" {:type PREFIX}
              "kilo-" {:type PREFIX}
              "milli-" {:type PREFIX}
              "multi-" {:type PREFIX}
              "semi-" {:type PREFIX}
              "mis-" {:type PREFIX}
              "ultra-" {:type PREFIX}
              "re-" {:type PREFIX}
              "dis-" {:type PREFIX}
              "in-" {:type PREFIX}
              "non-" {:type PREFIX}))


(defn get-suffixes []
  (sorted-map "-'d" {:type SUFFIX}
              "-'ll" {:type SUFFIX}
              "-'s" {:type SUFFIX}
              "-able" {:type SUFFIX}
              "-al" {:type SUFFIX}
              "-ance" {:type SUFFIX}
              "-ate" {:type SUFFIX}
              "-cide" {:type SUFFIX}
              "-ed" {:type SUFFIX}
              "-ee" {:type SUFFIX}
              "-en" {:type SUFFIX}
              "-er" {:type SUFFIX}
              "-ese" {:type SUFFIX}
              "-est" {:type SUFFIX}
              "-ful" {:type SUFFIX}
              "-fy" {:type SUFFIX}
              "-holic" {:type SUFFIX}
              "-ic" {:type SUFFIX}
              "-icious" {:type SUFFIX}
              "-ify" {:type SUFFIX}
              "-ing" {:type SUFFIX}
              "-ion" {:type SUFFIX}
              "-ish" {:type SUFFIX}
              "-ism" {:type SUFFIX}
              "-ist" {:type SUFFIX}
              "-itude" {:type SUFFIX}
              "-ity" {:type SUFFIX}
              "-ive" {:type SUFFIX}
              "-ize" {:type SUFFIX}
              "-less" {:type SUFFIX}
              "-ly" {:type SUFFIX}
              "-ment" {:type SUFFIX}
              "-n't" {:type SUFFIX}
              "-ness" {:type SUFFIX}
              "-ous" {:type SUFFIX}
              "-phagia" {:type SUFFIX}
              "-s" {:type SUFFIX}
              "-y" {:type SUFFIX}))

(defn get-punctuation []
  (sorted-map "'" {:type NONENDING_PUNCTUATION}
              "," {:type NONENDING_PUNCTUATION}
              "-" {:type NONENDING_PUNCTUATION}
              "." {:type ENDING_PUNCTUATION}
              "..." {:type NONENDING_PUNCTUATION}
              "/" {:type NONENDING_PUNCTUATION}
              ":" {:type NONENDING_PUNCTUATION}
              ";" {:type NONENDING_PUNCTUATION}
              "?" {:type ENDING_PUNCTUATION}
              "!" {:type ENDING_PUNCTUATION}))

(defn get-combos []
  (sorted-map "I" {:combos {"is" "I'm"
                            "are" "I'm"
                            "am" "I'm"
                            "-ed" "I'd"}}
              "who" {:combos {"-ed" "who'd"
                              "-s" "whose"}}
              "we" {:combos {"-ed" "we'd"
                             "is" "we're"}}
              "he" {:combos {"-ed" "he'd"
                             "-er" "her"}}

              "Alaska" {:combos {"-er" "Alaskan"}}
              "Albania" {:combos {"-er" "Albanian"}}
              "Angola" {:combos {"-er" "Angolan"}}
              "Antarctica" {:combos {"-er" "Antarctican"}}
              "Arkansas" {:combos {"-er" "Arkansan"}}
              "Asia" {:combos {"-er" "Asian"}}
              "Australia" {:combos {"-er" "Australian"}}
              "Austria" {:combos {"-er" "Austrian"}}
              "Belgium" {:combos {"-er" "Belgian"}}
              "California" {:combos {"-er" "Californian"}}
              "Canada" {:combos {"-er" "Canadian"}}
              "China" {:combos {"-er" "Chinese"}}
              "Denmark" {:combos {"-er" "Danish"}}
              "Ethiopia" {:combos {"-er" "Ethiopian"}}
              "Europe" {:combos {"-er" "European"}}
              "Finland" {:combos {"-er" "Finnish"}}
              "Florida" {:combos {"-er" "Floridan"}}
              "France" {:combos {"-er" "French"}}
              "Georgia" {:combos {"-er" "Georgian"}}
              "Germany" {:combos {"-er" "German"}}
              "Greece" {:combos {"-er" "Greek"}}
              "Hungary" {:combos {"-er" "Hungarian"}}
              "Illinois" {:combos {"-er" "Illinois"}}
              "India" {:combos {"-er" "Indian"}}
              "Iraq" {:combos {"-er" "Iraqi"}}
              "Israel" {:combos {"-er" "Israeli"}}
              "Italy" {:combos {"-er" "Italian"}}
              "Japan" {:combos {"-er" "Japanese"}}
              "Korea" {:combos {"-er" "Korean"}}
              "Laos" {:combos {"-er" "Laotian"}}
              "Maine" {:combos {"-er" "Mainer"}}
              "Massachusetts" {:combos {"-er" "Masshole"}}
              "Mexico" {:combos {"-er" "Mexican"}}
              "Montenegro" {:combos {"-er" "Montenegrin"}}
              "Nebraska" {:combos {"-er" "Nebraskan"}}
              "Ohio" {:combos {"-er" "Ohioan"}}
              "Pakistan" {:combos {"-er" "Pakistani"}}
              "Paraguay" {:combos {"-er" "Paraguayan"}}
              "Pennsylvania" {:combos {"-er" "Pennsylvanian"}}
              "Poland" {:combos {"-er" "Polish"}}
              "Russia" {:combos {"-er" "Russian"}}
              "Samoa" {:combos {"-er" "Samoan"}}
              "Spain" {:combos {"-er" "Spanish"}}
              "Syria" {:combos {"-er" "Syrian"}}
              "Texas" {:combos {"-er" "Texan"}}
              "Tunisia" {:combos {"-er" "Tunisian"}}
              "Turkey" {:combos {"-er" "Turkish"}}
              "USA" {:combos {"-er" "American"}}
              "Utah" {:combos {"-er" "Utahn"}}
              "Vatican" {:combos {"-er" "Vaticanese"}}
              "Virginia" {:combos {"-er" "Virginian"}}
              "Zimbabwe" {:combos {"-er" "Zimbabwean"}}
              "^" {:combos {"!" "I"}}
              "am" {:combos {"-ed" "was"}}
              "are" {:combos {"-ed" "were"}}
              "as" {:combos {"-ed" "assed"}}
              "awake" {:combos {"-ed" "awoke"}}
              "babysit" {:combos {"-ed" "babysat"}}
              "be" {:combos {"-ing" "being"}}
              "become" {:combos {"-ed" "became"}}
              "begin" {:combos {"-ed" "began"}}
              "bend" {:combos {"-ed" "bent"}}
              "bite" {:combos {"-ed" "bit"}}
              "bleed" {:combos {"-ed" "bled"}}
              "blow" {:combos {"-ed" "blew"}}
              "break" {:combos {"-ed" "broke"}}
              "breed" {:combos {"-ed" "bred"}}
              "bring" {:combos {"-ed" "brought"}}
              "build" {:combos {"-ed" "built"}}
              "buy" {:combos {"-ed" "bought"}}
              "can't" {:combos {"-ed" "couldn't"}}
              "catch" {:combos {"-ed" "caught"}}
              "choose" {:combos {"-ed" "chose"}}
              "come" {:combos {"-ed" "came"}}
              "create" {:combos {"-er" "creator"}}
              "cut" {:combos {"-ed" "cut"}}
              "deal" {:combos {"-ed" "dealt"}}
              "do" {:combos {"-ed" "did"}}
              "doesn't" {:combos {"-ed" "didn't"}}
              "draw" {:combos {"-ed" "drew"}}
              "drink" {:combos {"-ed" "drank"}}
              "drive" {:combos {"-ed" "drove"}}
              "eat" {:combos {"-ed" "ate"}}
              "educate" {:combos {"-er" "educator"}}
              "elevate" {:combos {"-er" "elevator"}}
              "equip" {:combos {"-ed" "equipped"}}
              "escalate" {:combos {"-er" "escalator"}}
              "fall" {:combos {"-ed" "fell"}}
              "feed" {:combos {"-ed" "fed"}}
              "feel" {:combos {"-ed" "felt"}}
              "fight" {:combos {"-ed" "fought"}}
              "find" {:combos {"-ed" "found"}}
              "fling" {:combos {"-ed" "flung"}}
              "fly" {:combos {"-ed" "flew"}}
              "forget" {:combos {"-ed" "forgot"}}
              "forgive" {:combos {"-ed" "forgave"}}
              "freeze" {:combos {"-ed" "froze"}}
              "get" {:combos {"-ed" "got"}}
              "give" {:combos {"-ed" "gave"}}
              "go" {:combos {"-ed" "went"}}
              "grow" {:combos {"-ed" "grew"}}
              "hang" {:combos {"-ed" "hung"}}
              "has" {:combos {"-ed" "had"}}
              "have" {:combos {"-ed" "had"}}
              "hear" {:combos {"-ed" "heard"}}
              "hide" {:combos {"-ed" "hid"}}
              "hold" {:combos {"-ed" "held"}}
              "how" {:combos {"-ed" "how'd"}}
              "hurt" {:combos {"-ed" "hurt"}}
              "imitate" {:combos {"-er" "imitator"}}
              "is" {:combos {"-ed" "was"}}
              "it" {:combos {"-ed" "it'd"}}
              "keep" {:combos {"-ed" "kept"}}
              "know" {:combos {"-ed" "knew"}}
              "lay" {:combos {"-ed" "laid"}}
              "lead" {:combos {"-ed" "led"}}
              "leave" {:combos {"-ed" "left"}}
              "let" {:combos {"-ed" "let"}}
              "light" {:combos {"-ed" "lit"}}
              "limit" {:combos {"-ed" "limited"}}
              "lose" {:combos {"-ed" "lost"}}
              "make" {:combos {"-ed" "made"}}
              "mean" {:combos {"-ed" "meant"}}
              "meet" {:combos {"-ed" "met"}}
              "pay" {:combos {"-ed" "paid"}}
              "person" {:combos {"-fy" "personify"}}
              "quip" {:combos {"-ed" "quipped"}}
              "quit" {:combos {"-ed" "quit"}}
              "quiver" {:combos {"-ed" "quivered"}}
              "read" {:combos {"-ed" "read"}}
              "refer" {:combos {"-ed" "referred"}}
              "ride" {:combos {"-ed" "rode"}}
              "ring" {:combos {"-ed" "rang"}}
              "rise" {:combos {"-ed" "rose"}}
              "run" {:combos {"-ed" "ran"}}
              "say" {:combos {"-ed" "said"}}
              "see" {:combos {"-ed" "saw"}}
              "seek" {:combos {"-ed" "sought"}}
              "sell" {:combos {"-ed" "sold"}}
              "send" {:combos {"-ed" "sent"}}
              "set" {:combos {"-ed" "set"}}
              "shake" {:combos {"-ed" "shook"}}
              "she" {:combos {"-ed" "she'd"}}
              "shoot" {:combos {"-ed" "shot"}}
              "shrink" {:combos {"-ed" "shrunk"}}
              "sing" {:combos {"-ed" "sang"}}
              "sink" {:combos {"-ed" "sank"}}
              "sit" {:combos {"-ed" "sat"}}
              "sleep" {:combos {"-ed" "slept"}}
              "slide" {:combos {"-ed" "slid"}}
              "sling" {:combos {"-ed" "slung"}}
              "speak" {:combos {"-ed" "spoke"}}
              "spend" {:combos {"-ed" "spent"}}
              "spin" {:combos {"-ed" "spun"}}
              "spirit" {:combos {"-ed" "spirited"}}
              "spit" {:combos {"-ed" "spat"}}
              "spring" {:combos {"-ed" "sprung"}}
              "stand" {:combos {"-ed" "stood"}}
              "steal" {:combos {"-ed" "stole"}}
              "stick" {:combos {"-ed" "stuck"}}
              "sting" {:combos {"-ed" "stung"}}
              "stink" {:combos {"-ed" "stunk"}}
              "strike" {:combos {"-ed" "struck"}}
              "strip" {:combos {"-er" "stripper"}}
              "succubus" {:combos {"-s" "succubi"}}
              "sweep" {:combos {"-ed" "swept"}}
              "swim" {:combos {"-ed" "swam"}}
              "swing" {:combos {"-ed" "swung"}}
              "take" {:combos {"-ed" "took"}}
              "tan" {:combos {"-ed" "tanned"
                              "-ing" "tanning"}}
              "teach" {:combos {"-ed" "taught"}}
              "tear" {:combos {"-ed" "tore"}}
              "tell" {:combos {"-ed" "told"}}
              "they" {:combos {"-ed" "they'd"}}
              "think" {:combos {"-ed" "thought"}}
              "throw" {:combos {"-ed" "threw"}}
              "understand" {:combos {"-ed" "understood"}}
              "wake" {:combos {"-ed" "woke"}}
              "wear" {:combos {"-ed" "wore"}}
              "weave" {:combos {"-ed" "wove"}}
              "weep" {:combos {"-ed" "wept"}}
              "win" {:combos {"-ed" "won"}}
              "withdraw" {:combos {"-ed" "withdrew"}}
              "withhold" {:combos {"-ed" "withheld"}}
              "won't" {:combos {"-ed" "wouldn't"}}
              "write" {:combos {"-ed" "wrote"}}
              "you" {:combos {"-ed" "you'd"}}))

(defn dict-js []
  ;; any words not specified in the dictionary are assumed to be ordinary,
  ;; well-behaved English words without any special cases or anything.
  (str "var dictionary = " (generate-string (merge (get-prefixes)
                                                   (get-suffixes)
                                                   (get-punctuation)
                                                   (get-combos))
                                            {:pretty true
                                             :escape-non-ascii true})))
