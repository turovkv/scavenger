class myClass {

    public int myFun() {
        int a1 = 1;
        int a2 = a1 + 1;
        int a3 = a2 + 1;
        int a4 = a3 + 1;
        int sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += 1;
        }
        return a4;
    }
}
