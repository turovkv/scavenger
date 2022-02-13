class myClass {
    public int myFun() {
        int a = 1;
        int z, r;
        int removeMe = 90;
        if (a > 1) {
            int b = 5;
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