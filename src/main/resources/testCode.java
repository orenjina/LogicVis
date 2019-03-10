public class testCode {
    public int recur(int a) {
        a = a + 1;
        if (a == 1) {
            a++;
        } else if (a == 2) {
            return recur(a--);
        } else {
            recur(a);
        }
        if (this.recur(a+1) == 1)
            return 0;
        return a;
    }
}

public class testCode {
    public int recur(int a) {
        if (a % 3 == 1) {
            return 0;
        } else {
            return recur(a--);
        }
    }
}