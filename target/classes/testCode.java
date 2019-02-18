public class testCode{
    public int recur() {
        int a = 0;
        a = a + 1;
        if (a >= 0) {
            a++;
        } else {
            a--;
        }
        for (int i = 0; i < 5; i++) {
            a++;
        }
        return a;
    }
}