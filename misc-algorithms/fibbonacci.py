memoization ={}
def fibbo(n):
    fib = 0
    if(memoization.get(n)!=None):
        return memoization.get(n)
    else:
        if n == 0:
            fib = 0
        elif n == 1:  
            fib = 1
        else:
            fib = fibbo(n-1) + fibbo(n - 2)            
        memoization[n]= fib
        return (fib)

def find_fibbo(nterms):
    # uncomment to take input from the user
    #nterms = int(input("How many terms? "))

    # check if the number of terms is valid
    if nterms <= 0:
        print("Plese enter a positive integer")
    else:
        print("Fibonacci sequence:")
    for i in range(nterms):
        print(fibbo(i))
    print('MEMOIZATION:')
    print(memoization)