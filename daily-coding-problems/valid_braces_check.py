

'''
Daily Coding Problem: Problem #27

This problem was asked by Facebook.

Given a string of round, curly, and square open and closing brackets, return whether the brackets are balanced (well-formed).

For example, given the string "([])[]({})", you should return true.

Given the string "([)]" or "((()", you should return false.

'''
def isValidBracesString(inputString):
    flowerBraceCounter=0
    squareBraceCounter=0
    curvyBraceCounter=0
    isValidString = False
    endingBrancesArray = []
    for a in inputString:
        if(a=='{'):
            flowerBraceCounter =flowerBraceCounter+1
            endingBrancesArray.append('}')
        elif(a=='['):
            squareBraceCounter =squareBraceCounter+1
            endingBrancesArray.append(']')
        elif(a=='('):
            curvyBraceCounter = curvyBraceCounter+1
            endingBrancesArray.append(')')
        else:
            currPop = endingBrancesArray.pop()
            if(currPop != a):
                isValidString=False
                return isValidString
            else:
                if(currPop=='}'):
                    flowerBraceCounter =flowerBraceCounter-1
                elif(currPop==']'):
                    squareBraceCounter =squareBraceCounter-1
                elif(currPop==')'):
                    curvyBraceCounter = curvyBraceCounter-1                

    if(curvyBraceCounter==squareBraceCounter==flowerBraceCounter==0):
        isValidString=True    
    return isValidString