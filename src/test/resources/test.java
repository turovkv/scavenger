class myClass {

    public int myFun() {
        int a = 1;
        int r;
        if (a > 1) {
        }
        if (a > 2) {
            int b = 6;
            b += 3;
        }
        r = a;
        myFun2(a);
        return 5;
    }

    public int myFun2(int arg) {
        return arg;
    }
}
