public class methodFile {
    public int recur() {
        int a = 0;
        a = a + 1;
        if (a == 1) {
            a++;
        } else if (a == 2) {
            a--;
        } else {
            a = a + 3;
        }
        return a;
    }
}