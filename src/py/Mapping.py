#!/usr/bin/env python
""" generated source for module Mapping """
class Mapping(object):
    """ generated source for class Mapping """
    key = []

    def __init__(self):
        """ generated source for method __init__ """
        self.key = ["#"]*26

    @classmethod
    def fromTranslation(self, cipher, translation):
        """ generated source for method __init___0 """
        newKey = ["#"]*26
        for i in range(len(translation)):
            newKey[ord(cipher[i]) - ord('A')] = translation[i]
        return newKey[:]

    def getKey(self):
        """ generated source for method getKey """
        return self.key

    def setKey(self, newKey):
        """ generated source for method setKey """
        self.key = newKey

