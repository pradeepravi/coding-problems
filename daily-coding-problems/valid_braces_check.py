
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