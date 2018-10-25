package net.coahr.three3.three.Util;

public abstract  class CustomRunnable implements Runnable,Comparable<CustomRunnable> {
    private int priority;

    public CustomRunnable(int priority) {

        if (priority<0)

            throw new IllegalArgumentException();

        this.priority = priority;

    }



    @Override

    public int compareTo(CustomRunnable another) {

        int my = this.getPriority();

        int other = another.getPriority();

        if (my>other){

            return  -1;

        }else{

            return 0;

        }



    }







    @Override

    public void run() {

        doRun();

    }



    public abstract void doRun();



    public int getPriority() {

        return priority;

    }
}
