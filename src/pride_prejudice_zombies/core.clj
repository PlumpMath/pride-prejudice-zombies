(ns pride-prejudice-zombies.core)

;; Load in the Pride and Prejudice text downloaded from Project Guttenberg
(def pride (slurp "resources/pride.txt"))

;; Cast text as lowercase and split by non-word characters
(def words (clojure.string/split (clojure.string/lower-case pride) #"\W+"))

;; Drop the first 1000 (which include the Guttenberg legal soup) and take 1000 from there
(def first-thousand-words (take 1000 (drop 1000 words)))

;; Partition the words into pairs, using a 'step' of 1 so we include all words in pairs
;; i.e. instead of:
;; [(the quick) (brown fox) (jumps over)]
;; we get:
;; [(the quick) (quick brown) (brown fox) (fox jumps) (jumps over)]
(def pairs
  (partition 2 1 first-thousand-words))

;; Return all the successors of a given word
(defn successors [word]
  (map second (filter #(= word (first %)) pairs)))

;; Create a map with each of the words as keys and all their successors as values
(def markov (into {} (map (fn [word] [word (successors word)]) (distinct first-thousand-words))))

;; Given a starting-word, recursively pick a random successor, returns a lazy-seq
(defn jane [starting-word]
  (iterate
    (fn [word-a]
      (let [next-words (seq (markov word-a))]
        (if (not (nil? next-words))
          (rand-nth next-words)
          (rand-nth first-thousand-words))))
    starting-word))


(defn -main []
  ;; Take 1000 randomly walked successors starting with a random word
  (def random-1000-jane (take 1000 (jane (rand-nth first-thousand-words))))

  ;; Write this to a text file
  (spit "prejudice.txt" (clojure.string/join " " random-1000-jane))

  ;; Print success message
  (print "Wrote prejudice.txt"))