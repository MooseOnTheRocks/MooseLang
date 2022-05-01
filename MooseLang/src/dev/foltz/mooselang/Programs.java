package dev.foltz.mooselang;

public class Programs {
    public static final String program1 = """
                let greeting1 = "Hello"
                let greeting2 = "World"
                print(greeting1, greeting2)
                let numbers = [1, 2, 3, 4, 5]
                print(numbers)
                """;
    public static final String program2 = """
                let greetingOuter = "Hello"
                {
                    let greetingInner = "World"
                    print(greetingOuter)
                    print(greetingInner)
                }
                print(greetingOuter)
                """;
    public static final String program3 = """
                def userFunc(x) = {
                    print("Begin userFunc")
                    print(x)
                    print("End userFunc")
                }
                
                userFunc("Hello, userFunc!")
                """;
    public static final String program4 = """
                let x = 10
                def userFunc() = print(x)
                userFunc()
                """;
    public static final String program5 = """
                def func(0) = "Zero"
                def func(1) = "One"
                def func(2) = "Two"
                def func(n) = "Some number"
                
                let results = [func(0), func(1), func(2), func(3)]
                print(results)
                """;
    public static final String program6 = """
                def print2(0, 0) = print("0", "0")
                def print2("a", "a") = print("a", "a")
                def print2(a, b) = print("Two objects:", a, b)
                
                print2(0, 0)
                print2("a", "a")
                print2(0, 1)
                print2("a", "b")
                print2(0, "a")
                """;
    public static final String program7 = """
                def foreach(f, []) = None
                def foreach(f, ls) = {
                    let h = head(ls)
                    let rs = tail(ls)
                    f(h)
                    foreach(f, rs)
                }
                
                foreach(print, [1, 2, 3, 4])
                """;
    public static final String program8 = """
                let ls = cons(1, cons(2, cons(3, cons(4, []))))
                print(ls)
                """;
    public static final String program9 = """
                // Returns a list containing two of the given element.
                def double(e) = [e, e]
                
                // Returns a new list with the function f applied to each element of the list.
                def map(f, []) = []
                def map(f, ls) = {
                    let h = head(ls)
                    let rs = tail(ls)
                    cons(f(h), map(f, rs))
                }
                
                // Original list
                let myList = [1, 2, 3, 4]
                // Mapped list
                let mappedList = map(double, myList)
                print(myList)
                print(mappedList)
                """;
    public static final String program10 = """
                def printAll(ls) =
                    for elem in ls do print(elem)
                
                def map(f, []) = []
                def map(f, ls) = {
                    let h = head(ls)
                    let rs = tail(ls)
                    cons(f(h), map(f, rs))
                }
                
                def reversed([]) = []
                def reversed(ls) = {
                    // Inline function definition
                    def reversed'(acc, []) = { acc }
                    def reversed'(acc, rem) = {
                        let h = head(rem)
                        let rs = tail(rem)
                        reversed'(cons(h, acc), rs)
                    }
                    
                    // Code blocks implicitly return the last expression.
                    reversed'([], ls)
                }
                
                let nums = [1, 2, 3, 4]
                let nums' = reversed(nums)
                print(nums)
                print(nums')
                """;
    public static final String program11 = """
                def map(f, []) = []
                def map(f, ls) = {
                    let h = head(ls)
                    let rs = tail(ls)
                    cons(f(h), map(f, rs))
                }
                
                def reversed([]) = []
                def reversed(ls) = {
                    // Inline function definition
                    def reversed'(acc, []) = acc
                    def reversed'(acc, rem) = {
                        let h = head(rem)
                        let rs = tail(rem)
                        reversed'(cons(h, acc), rs)
                    }
                    
                    // Code blocks implicitly return the last expression.
                    reversed'([], ls)
                }
                
                def pair(a, b) = [a, b]
                
                // Lambda expressions (anonymous function definitions)
                let double = lambda x => pair(x, x)
                
                let nums = [1, 2, 3, 4]
                // Single quotes allowed in variable names
                let nums' = reversed(map(double, nums))
                print(nums')
                """;
    public static final String program12 = """
                let a = 10
                a = 20
                let b = 20
                {
                    a = 30
                    let b = "B"
                    print(a, b)
                }
                print(a, b)
                """;
    public static final String program13 = """
                def length([]) = 0
                def length(ls) =
                    let len = 0 in
                        for _ in ls then
                            len = sum(len, 1)
                        else 0
                
                def indices(ls) = range(length(ls))
                
                def printLen(ls) {
                    print("List:", ls)
                    print("Length:", length(ls))
                }
                
                let nums = [1, 2, 3, 4, 3, 2, 1]
                printLen(nums)
                """;
    public static final String program14 = """
                def double(x) = sum(x, x)
                
                def repeat(0, _) = []
                def repeat(n, x) =
                    let n' = sum(n, -1) in
                        cons(x, repeat(n', x))
                
                def take(0, _) = []
                def take(n, ls) =
                    let n' = sum(n, -1) in
                    let h = head(ls) in
                    let rs = tail(ls) in
                        cons(h, take(n', rs))
                
                def drop(0, ls) = ls
                def drop(n, ls) =
                    let n' = sum(n, -1) in
                    let rs = tail(ls) in
                        drop(n', rs)
                
                let nums = range(10)
                print(nums)
                let nums2 = map(double, nums)
                print(nums2)
                print(length(nums), length(nums2))
                print(repeat(5, "apple"))
                print(take(5, nums2))
                print(drop(5, range(10)))
                """;
    public static final String program15 = """
                
                // Notice no curly braces required
                def length(ls)
                    // This entire function body is one expression.
                    // let <binding> in
                    //     for <binding> in <list> then
                    //         <exprLoop>
                    //     else
                    //         <exprElse>
                    //
                    // Will return either the last evaluated <exprLoop>,
                    // or <exprElse> if <list> is empty.
                    let len = 0 in
                    for _ in ls then
                        len = sum(len, 1)
                    else 0
                
                // Curly braces required!
                def length(ls) {
                    // This let binding is a statement.
                    let len = 0
                    // This for-in-do loop is a statement
                    for _ in ls do
                        len = sum(len, 1)
                    // Code blocks return the last expression in them
                    len
                }
                
                let nums = [1, 2, 3, 4, 5]
                
                print(length(nums))
                print(length([]))
                
                """;
    public static final String program16 = """
                def counter(n) {
                    let maxCount = n
                    let count = 0
                    
                    def counter'() {
                        let count' = count
                        count = sum(1, count)
                        print("Count:", count', "/", maxCount)
                        count'
                    }
                }
                
                let C = counter(5)
                for _ in range(7) do
                    C()
                """;
    public static final String program17 = """
                def upper('a) = 'A
                def upper('b) = 'B
                def upper('c) = 'C
                def upper('d) = 'D
                def upper('e) = 'E
                def upper('f) = 'F
                def upper('g) = 'G
                def upper('h) = 'H
                def upper('i) = 'I
                def upper('j) = 'J
                def upper('k) = 'K
                def upper('l) = 'L
                def upper('m) = 'M
                def upper('n) = 'N
                def upper('o) = 'O
                def upper('p) = 'P
                def upper('q) = 'Q
                def upper('r) = 'R
                def upper('s) = 'S
                def upper('t) = 'T
                def upper('u) = 'u
                def upper('v) = 'V
                def upper('w) = 'W
                def upper('x) = 'X
                def upper('y) = 'Y
                def upper('z) = 'Z
                def upper(c) = c
                
                let greeting = "Hello, World!"
                print(greeting)
                print(reduce(concat, "", map(upper, greeting)))
                """;
    public static final String program18 = """
                let list = [1, 2, 3, 4]
                print(join(-1, list))
                """;
    public static final String program19 = """
                let fruits = ["apple", "banana", "orange", "grape"]
                def indices(ls) = range(length(ls))
                
                def zip(a, b) {
                    def zip'(acc, [], []) = acc
                    def zip'(acc, remA, remB) {
                        let headA = head(remA)
                        let headB = head(remB)
                        let tailA = tail(remA)
                        let tailB = tail(remB)
                        let pair = [headA, headB]
                        zip'(cons(pair, acc), tailA, tailB)
                    }
                    
                    reversed(zip'([], iter(a), iter(b)))
                }
                
                let a1 = [1, 2, 3]
                let a2 = ['a, 'b, 'c]
                print(a1, a2)
                print(zip(a1, a2))
                
                print(zip(indices(fruits), fruits))
                
                def enum(ls) = zip(indices(ls), ls)
                print(enum(fruits))
                """;
    public static final String program20 = """
                let list = [1, 2, 3, 4, 5]
                print(list)
                print(length(list))
                """;
    public static final String program21 = """
                let m = 10
                
                let b = 35
                """;

    public static final String stdlib = """
                // Builtins:
                //     cons
                //     head
                //     tail
                //     range
                //     sum
                //     iter
                //     concat
                
                def map(f, []) = []
                def map(f, ls) =
                    let it = iter(ls) in
                    let h = head(it) in
                    let ts = tail(it) in
                    cons(f(h), map(f, ts))
                
                def filter(f, ls) =
                    let it = iter(ls) in
                    let h = head(it) in
                    let ts = tail(it) in
                    let rem = filter(f, ts) in
                    if f(h) then cons(h, rem)
                    else rem
                
                def reduce(f, init, ls) =
                    let acc = init in
                    for e in ls then
                        acc = f(acc, e)
                    else acc
                
                def length(ls) =
                    let len = 0 in
                    for _ in ls then
                        len = sum(len, 1)
                    else len
                
                def empty(ls) {
                    def empty'(0) = True
                    def empty'(_) = False
                    empty'(length(ls))
                }
                
                def reversed(ls) =
                    let list = [] in
                    for e in ls then
                        list = cons(e, list)
                    else list
                
                def join(_, []) = []
                def join(sep, ls) {
                    let h = head(ls)
                    let ts = tail(ls)
                    def join'(acc, s) = cons(s, cons(sep, acc))
                    
                    reversed(reduce(join', [h], ts))
                }
                
                def id(x) = x
                
                def const(x) = lambda _ => x
                
                def repeat(e, n) = map(const(e), range(n))
                
                """;
}
