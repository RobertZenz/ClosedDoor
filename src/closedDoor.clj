(def tag-start
	"The long/normal start tag."
	"<\\?clj")
(def tag-end
	"The long/normal end tag."
	"(\\?>|\\Z)")


(def echo-start
	"The short/echo start tag."
	"<%")
(def echo-end
	"The short/echo end tag."
	"%>")


(def tag-pattern
	"The compiled pattern for the long/normal tags."
	(re-pattern (str tag-start "((?s:.+?))" tag-end)))
(def echo-pattern
	"The compiled pattern for the short/echo tags."
	(re-pattern (str echo-start "((?s:.+?))" echo-end)))


(defn process-match
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

(defn process-match-echo-wrapped
	"Process the given match, but wraps it first in the echo function."
	[[match group]]
	(process-match [match (str "(echo " group ")")]))

(defn parse
	"Parses the given input and processes the matches. The long/normal tags
	are processd first, after that the short/echo tags. Order of appereance
	does not matter."
	[input]
	(clojure.string/replace
		(clojure.string/replace
			input
			tag-pattern
			process-match
		)
		echo-pattern
		process-match-echo-wrapped
	)
)

(defn process
	"Processes the given source."
	[source]
	(print (parse (slurp source))))


; The main function follows.
(if (empty? *command-line-args*)
	(process *in*)
	(doseq [arg *command-line-args*]
		(if (= arg "-")
			(process *in*)
			(process arg))))	

