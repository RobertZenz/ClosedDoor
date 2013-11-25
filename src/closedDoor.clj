;; The default tags
(def tagStart "<\\?clj")
(def tagEnd "(\\?>|\\Z)")

;; The shorthand echo tags.
(def echoStart "<%")
(def echoEnd "%>")


;; Let's compile the pattern.
(def tagPattern (re-pattern (str tagStart "((?s:.+?))" tagEnd)))
(def echoPattern (re-pattern (str echoStart "((?s:.+?))" echoEnd)))


;; Parses the match.
(defn parseMatch
	[[match group]]
	
	;; Create a StringBuilder which we'll use for our content.
	(def buffer (StringBuilder.))
	
	;; Helper function for the StringBuilder.
	(defn echo
		[string]
		(.append buffer string)
	)
	
	;; Now load what the regex devliered to use.
	(load-string group)
	
	;; Return the buffer.
	(.toString buffer)
)

;; Parses the match, but wraps it first with the echo function.
(defn parseMatchEchoWrapped
	[[match group]]
	(parseMatch [match (str "(echo " group ")")])
)

;; Parses the given input.
(defn parse
	[input]
	(clojure.string/replace
		(clojure.string/replace
			input
			tagPattern
			parseMatch
		)
		echoPattern
		parseMatchEchoWrapped
	)
)


(if (empty? *command-line-args*)
	(print (parse (slurp *in*)))
	(doseq [arg *command-line-args*]
		(print (parse (slurp arg)))
	)
)	

