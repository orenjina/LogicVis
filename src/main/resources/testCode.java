public class testCode {
    public int recur() {
        int a = 0;
        a = a + 1;
        if (a >= 0) {
            a++;
        } else if (a == 1){
            a--;
        } else {
            a = 0;
        }
        return a;
    }
}