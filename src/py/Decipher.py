#!/usr/bin/env python
""" generated source for module Decipher1 """

class Decipher(object):
    """ generated source for class Decipher1 """
    dictionary = {}
    patterns = {}
    globalSources = []
    globalCipher = []
    globalCipherWords = []
    globalCipherText = ""
    wordFreqCount = 0
    possibleKeys = []
    wordsByLength = {}

    @classmethod
    def main(cls, args):
        """ generated source for method main """
        #
        # Reading Source Files including your data structures
        #

        # You should do some pre-processing on given text sources and store them in a file so as to not do pre-processing each time.
		# In this part you should read the files and fill the required data structures, like arrays, and assign values to some variables that you will use for finding the plaintext.
        # 		
        cls.dictionary = {}
        cls.patterns = {}
        cls.possibleKeys = []
        cls.wordsByLength = {}
        cls.wordFreqCount = 0

        #
        # 18 is the max word length in sources 1-3
        #
        for i in range(19):
            cls.wordsByLength[str(i)] = {}

        if not cls.loadDictionary("dictionary.txt") or not cls.loadSources(args["sources"]):
            for src in args["sources"]:
                print "Parsing " + src + "..."
                cls.parseDictionary(src)

            cls.outputDictionary()  
            cls.loadDictionary("dictionary.txt")
            cls.loadSources(args["sources"])

        #
        # Reading cipher text from input file
        #
        inputFileName = args["cp"]
        timeLimit = args["tl"]
        
        cls.parseCipherText(inputFileName)

        for cipherText in cls.globalCipher:

            startTime = long(round(time.time()*1000))
            currentTime = long(round(time.time()*1000))

            while currentTime - startTime < timeLimit:
               plainText = cls.beginDecipher(cipherText)
               currentTime = long(round(time.time ()*1000))
               if not plainText == "":
                   break

            endTime = long(round(time.time()*1000))
            totalTime = endTime - startTime

            try:
                outputFile = open("Results.txt", "w+")

                if plainText == "":
                    outPut = ", ".join(["?", str(totalTime)])
                    print(output + "\n")
                    outputFile.write(output)
                else:
                    output = ", ".join([plainText, str(totalTime)])
                    print(output + "\n")
                    outputFile.write(output)

            except IOError as e:
                print e

        print "Deciphering complete..."


    @classmethod
    def loadDictionary(cls, fileName):
        """ loadDictionary(fileName) """
        try:
            f = open(fileName)
            for line in f:
                line = line.split(" ")
                word = line[0]
                freq = int(line[1])
                cls.wordFreqCount += freq
                if not word in cls.dictionary:
                    cls.dictionary[word] = freq
                    matches = {}
                    if not word in matches:
                        cls.wordsByLength[str(len(word))] = (word, freq)
                cls.patternMatches(word)
     
            print fileName, "loaded..." 
        except IOError as e:
            return False
       
        return True


    @classmethod
    def loadSources(cls, fileNames):
        try:
            for f in fileNames:
                with open("parsed-" + f, "r") as srcFile:
                    cls.globalSources.append(srcFile.read())
                    print f, "loaded..."
        except IOError as e:
            return False

        return True


    @classmethod
    def outputDictionary(cls):
        """ outputDictionary() """
        try:
            outputFile = open("dictionary.txt", "w+")
            for key in sorted(cls.dictionary):
                outputFile.write(key + " " + str(cls.dictionary[key]) + "\n")
       
        except IOError as e:
            print e


    @classmethod
    def printDictionary(cls):
        """ printDictionary() """
        for key in sorted(cls.dictionary):
            print key, cls.dictionary[key]


    @classmethod
    def patternMatches(cls, word):
        """ patternMatches(word) """
        pat = cls.pattern(word)
        if not pat in cls.patterns:
            cls.patterns[pat] = []
            cls.patterns[pat].append(word)
        elif not word in cls.patterns[pat]:
            cls.patterns[pat].append(word)


    @classmethod
    def pattern(cls, word):
        """ pattern(word) """
        # Patterns are defined by the word's sequence of unique characters relative to their normed ASCII value
        # Ex 1: HELLO has a pattern mapping of ABCCD
        # Ex 2: ANOMALY has a pattern mapping of ABCDAE
        patternMapping = Mapping()
        count = 0
        
        # Construct new pattern mapping using count to distinguish unique chars
        for ch in word:
            if patternMapping.getKey()[ord(ch) - ord('A')] == "#":
                patternMapping.getKey()[ord(ch) - ord('A')] = chr(count + ord('A'))
                count += 1

        # Construct pattern for word
        pattern = ""
        for ch in word:
            pattern += patternMapping.getKey()[ord(ch) - ord('A')]

        return pattern


    @classmethod
    def parseText(cls, rawInput):
        text = rawInput.upper()
        text = re.sub("\.", "", text)
        text = re.sub("[\W\d_\s]", " ", text)
        text = re.sub("\s+", " ", text)
        text = re.sub(" $", "", text)

        return text


    @classmethod
    def parseDictionary(cls, fileName):
        try:
            with open(fileName, "r") as srcFile:
                rawInput = srcFile.read().replace('\n', '')
            
            fileInput = cls.parseText(rawInput)

            with open("parsed-" + fileName, "w+") as outFile:
                outFile.write(fileInput)

            wordList = fileInput.split(" ")
            for word in wordList:
                if word in cls.dictionary:
                    cls.dictionary[word] += 1
                else:
                    cls.dictionary[word] = 1

        except IOError as e:
            print e


    @classmethod
    def parseCipherText(cls, fileName):
        cipherChunks = []
        try:
            srcFile = open(fileName, "r")
            for line in srcFile:
                rawInput = line.replace('\n', ' ')
                cipher = cls.parseText(rawInput)
                cipherChunks.append(cipher)

            cls.globalCipher = cipherChunks
        
        except IOError as e:
            print e


    @classmethod
    def beginDecipher(cls, cipher):
        plainText = ""
        
        cls.globalCipherText = cipher
        cipherWords = cipher.split(" ")
        cipherWords = sorted(cipherWords, key=len)
        #cipherWords.reverse()

        finalMap = Mapping()
       
        cipherWord = cipherWords.pop()
        pattern = cls.pattern(cipherWord)

        # Find valid mapping
        candidates = cls.patterns[pattern]
        for translation in candidates:
            tempMap = Mapping()
            tempMap.setKey(tempMap.fromTranslation(cipherWord, translation))
            tempMap = cls.decipher(tempMap, cipherWords[:])
            finalMap.setKey(tempMap.getKey()[:])
            if cls.validKey(finalMap):
                break

        # Do validation check on finalMap against other possible mappings that surfaced
        if not cls.validKey(finalMap):
           for keyMapping in cls.possibleKeys:
                possibleMap = Mapping()
                possibleMap.setKey(keyMapping.getKey()[:])
                if cls.evaluate(finalMap) < cls.evaluate(possibleMap):
                    finalMap.setKey(possibleMap.getKey()[:])
        
        plainText = cls.decrypt(finalMap, cipher)
        if not plainText == "":
            print "Final Mapping: ",
            print finalMap.getKey()

        return plainText


    @classmethod
    def decipher(cls, currentMap, cipherWords):
        if not cipherWords:
            return currentMap

        preservedMapping = Mapping()
        preservedMapping.setKey(currentMap.getKey()[:])

        cipherWord = cipherWords.pop()
        pattern = cls.pattern(cipherWord)

        candidates = cls.patterns[pattern]
        for translation in candidates:  
            tempMap = Mapping()
            tempMap.setKey(tempMap.fromTranslation(cipherWord, translation))
            if cls.isConsistent(currentMap, tempMap):
                # Construct consistent merged key mapping
                tempMap = cls.mergeKeys(currentMap, tempMap)
                mergedMap = Mapping()
                mergedMap.setKey(tempMap.getKey()[:])

                # Recursively traverse through remaining cipherWords
                tempMap = cls.decipher(mergedMap, cipherWords[:])
                newMap = Mapping()
                newMap.setKey(tempMap.getKey()[:])
                if cls.validKey(newMap):
                    currentMap.setKey(newMap.getKey()[:])
                    return currentMap
                elif cls.validKey(mergedMap):
                    currentMap.setKey(mergedMap.getKey()[:])
                    return currentMap
                else:
                    # Stores consistent key mappings in case current mapping traversal is incorrect
                    cls.possibleKeys.append(currentMap)
                    currentMap.setKey(preservedMapping.getKey()[:])

        if not cipherWords or not cipherWords[-1] == cipherWord:
            cipherWords.append(cipherWord)
        
        return currentMap

    @classmethod
    def isConsistent(cls, currentMapping, newMapping):
        currentKey = currentMapping.getKey()
        newKey = newMapping.getKey()
        
        for i in range(26):
            if currentKey[i] == "#":
                continue 
            for j in range(26):
                if newKey[j] == "#":
                    continue 
                if newKey[j] == currentKey[i] and i != j:
                    return False
                if not newKey[j] == currentKey[i] and i == j:
                    return False

        return True


    @classmethod
    def mergeKeys(cls, currentMapping, newMapping):
        currentKey = currentMapping.getKey()
        newKey = newMapping.getKey()
 
        for i in range(26):
            if currentKey[i] == "#":
                currentKey[i] = newKey[i]
 
        currentMapping.setKey(currentKey[:])

        return currentMapping


    @classmethod
    def decrypt(cls, mapping, cipher):
        key = mapping.getKey()
        plainText = ""
        
        for ch in cipher:
            if ord(ch) - ord('A') < 0:
                plainText += " "
            elif key[ord(ch) - ord('A')] == "#":
                plainText += "*"
            else:
                plainText += key[ord(ch) - ord('A')]

        return plainText


    @classmethod
    def validKey(cls, mapping):
        numCorrect = 0
        
        cipherWords = cls.globalCipherText.split(" ")
        for cw in cipherWords: 
            message = cls.decrypt(mapping, cw)
            if message in cls.dictionary:
                numCorrect += 1

        if numCorrect == len(cipherWords):
            return True

        return False


    @classmethod
    def evaluate(cls, mapping):
        score = 0

        cipherWords = cls.globalCipherText.split(" ")
        for cw in cipherWords:
            message = cls.decrypt(mapping, cw)
            if len(cw) == len(message) and message in cls.dictionary:
                score += (0.35) * len(message)

        #for cipher in cls.globalCipher:
        #    message = decrypt(mapping, cipher)


        return score


if __name__ == '__main__':
    import sys
    import re
    import argparse
    import time
    from Mapping import *
    
    parser = argparse.ArgumentParser(description="Deciphers a given cipher text with the provided source files.", usage="%(prog)s [options]")
    parser.add_argument("sources", type=str, nargs="+", default="", help="Path to files of original text")
    parser.add_argument("--cp", type=str, nargs="?", default="", help="Path to file containing cipher text", required=True)
    parser.add_argument("--tl", type=long, nargs="?", default=3000L, help="Set time limit for deciphering text in milliseconds")

    args = parser.parse_args()
    args = vars(args)

    # TODO: Add copy constructor to Mapping class

    Decipher.main(args)

