(def tagStart
	"The long/normal start tag."
	"<\\?clj")
(def tagEnd
	"The long/normal end tag."
	"(\\?>|\\Z)")


(def echoStart
	"The short/echo start tag."
	"<%")
(def echoEnd
	"The short/echo end tag."
	"%>")


(def tagPattern
	"The compiled pattern for the long/normal tags."
	(re-pattern (str tagStart "((?s:.+?))" tagEnd)))
(def echoPattern
	"The compiled pattern for the short/echo tags."
	(re-pattern (str echoStart "((?s:.+?))" echoEnd)))


(defn processMatch
	"Processes the given match, and returns the output of
	the given match."
	[[match group]]
	
	; Create a StringBuilder which we'll use for our content.
	(def buffer (StringBuilder.))
	
	(defn echo
		"A small helper function to allow to echo things."
		[string]
		(.append buffer string))
	
	; Now load what the regex devliered to use.
	(load-string group)
	
	; Return the buffer.
	(clojure.string/replace (.toString buffer) "$" "\\$"))

(defn processMatchEchoWrapped
	"Process the given match, but wraps it first in the echo function."
	[[match group]]
	(processMatch [match (str "(echo " group ")")]))

(defn parse
	"Parses the given input and processes the matches. The long/normal tags
	are processd first, after that the short/echo tags. Order of appereance
	does not matter."
	[input]
	(clojure.string/replace
		(clojure.string/replace
			input
			tagPattern
			processMatch
		)
		echoPattern
		processMatchEchoWrapped
	)
)


; The main function follows.
(if (empty? *command-line-args*)
	(print (parse (slurp *in*)))
	(doseq [arg *command-line-args*]
		(if (= arg "-")
			(print (parse (slurp *in*)))
			(print (parse (slurp arg)))
		)
	)
)	

