'''

Write an algorithm to justify text. Given a sequence of words and an integer line length k, return a list of strings which represents each line, fully justified.

More specifically, you should have as many words as possible in each line. There should be at least one space between each word. Pad extra spaces when necessary 
so that each line has exactly length k. Spaces should be distributed as equally as possible, with the extra spaces, if any, distributed starting from the left.

If you can only fit one word on a line, then you should pad the right-hand side with spaces.

Each word is guaranteed not to be longer than k.

For example, given the list of words ["the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog"] and k = 16, you should return the following:

["the  quick brown", # 1 extra space on the left
"fox  jumps  over", # 2 extra spaces distributed evenly
"the   lazy   dog"] # 4 extra spaces distributed evenly

'''

def justify_text(arrayOfWords, k):
    newArray = []
    line = ''
    spaces=''
    for word in arrayOfWords:
        tempLine = line+' '+word
        if(len(tempLine)<k):
            line=tempLine
        else:
            availableSpaces=k-len(line)
            lineSpaceCount=len(line.split(' '))
            if(lineSpaceCount>1):
                lineSpaceCount=lineSpaceCount-1
            justifySpaces=int(availableSpaces/lineSpaceCount)+1
            reminderSpaces=availableSpaces%lineSpaceCount
            print("{} {}".format("New Spaces Count ", justifySpaces))
            spaces += ' '*justifySpaces
            print("{} {} {}".format("[", spaces,"]"))
            line.replace(' ',spaces)
            # print ('justifySpaces '+justifySpaces)
            # print ('reminderSpaces '+reminderSpaces)
            newArray.append(line)
            line=word

    newArray.append(line)
    print(newArray)