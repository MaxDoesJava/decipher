# Decipher

A Java-based deciphering program ported over to Python via [j2py](https://github.com/natural/java2python). Licensed under the [MIT License](http://www.opensource.org/licenses/mit-license.php).

Original source code is based off a final project done for CS167 - Introduction
to Cryptography while attending UCI.

The program uses a word-count pattern-matching lookup strategy to find the best
key mapping to decode the provided cipher text. The cipher text is based off of
the provided literary source files which are used to decipher the text.

# Usage

This page describes how to use both versions of the Decipher program.

### Java

JDK6 was used to compile and run the code with the following commands:

```bash
$ javac *.java
```

Once compiled, you can run the code by specifying the required arguments:

```bash
$ java Decipher [CIPHERPATH] [TIMELIMIT]
```

### Options and Arguments

The literary source files are assumed to be named [source1.txt][], [source2.txt][],
and [source3.txt][] located in the same dir as the code

  * `[CIPHERPATH]`

    Read the cipher text from the given file path.
  
  * `[TIMELIMIT]`

    Optionally specify the time limit in milliseconds.
	 
### Python

Python 2.7.2 was used to port and execute the code:

```bash
$ python Decipher.py [SOURCEFILES] [CIPHERPATH] [OPTIONS]
```

### Options and Arguments

  * `[SOURCEFILES]`
    Specify one or more source files to decipher the text from.
  * `--cp CIPHERPATH`
    Specify the cipher text file using the --cp tag.
  
  * `--tl TIMELIMIT`
    Optionally specify the time limit in milliseconds.


#### Note
This is not a stable version so use at your own discretion.
	
[downloads]: https://github.com/gul2u/decipher/downloads
[source1.txt]: https://github.com/gul2u/decipher/tree/master/src/py/source1.txt
[source2.txt]: https://github.com/gul2u/decipher/tree/master/src/py/source2.txt
[source3.txt]: https://github.com/gul2u/decipher/tree/master/src/py/source3.txt
[src]: https://github.com/gul2u/decipher/tree/master/src/