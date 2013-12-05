ClosedDoor
==========

A simple text templating system in Clojure.

Let me get a few things right out of the way:

 * This is not suitable for production systems
 * This uses regular expressions for parsing text (which is evil)
 * This was written for processing 'small' text files, so speed did not matter

So what is this at all? Easy, it's PHP, but with Clojure. ClosedDoor allows you
to embed Clojure into a simple text file and then execute it. Sounds stupid?
Heck yes! But I did not feel like using PHP for the small files I had.


Usage
=====

ClosedDoor accepts either an arbitrary number of files as input and outputs
the processed content to stdout, like this:

    clojure ClosedDoor.jar FILE [FILE...]

Or if no parameters are provided, it will read from stdin:

    cat FILE | clojure ClosedDoor.jar
    
Also if you pass - in it will read from stdin:

    cat FILE | clojure ClosedDoor.jar FILE FILE - FILE

The template files ClosedDoor accepts are looking like this:

    <?clj
        ; You can embed Clojure here now.
        (def yourVariable "Something")
    ?>
    This is a simple test file to show you <% something %>.


Syntax
======

Okay, let's break it down. Everything between 'normal' tags will be loaded
via load-string as pure Clojure without side effects:

    <?clj ; Clojure code goes here ?>

Everything between 'short' tags will be loaded and the output will be written
back into the place where the code was:

    The result of  1 + 1 is <% (+ 1 1) %>.
    
Yields:

    The result of 1 + 1 is 2.

Not convinced yet that this is stupid? Well, there's also the echo function:

    <?clj
        ; Some high quality Clojure code here
        (echo "Hello World!")
    ?>
    
Yields:

    Hello World!

Also if there only follows code, you can leave the tags open:

    This is text.
    <?clj
       ; Some code here

The normal tags are processed first, the echo tags after it. The order
of appereance in the file itself does not matter for this, all normal tags
are processed first.


Includes
========

There is no include-function as in PHP, but you can freely pass multiple files
into ClosedDoor, they will be evaluated in the same context.

