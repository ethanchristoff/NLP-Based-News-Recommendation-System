package com.example.ethan_perera_2331419;

import java.io.IOException;
import java.util.Scanner;

public class multiThreading_content implements Runnable{

    private final int thread_no;

    public multiThreading_content(int thread_no){
        this.thread_no = thread_no;
    }

    @Override
    public void run(){
        for(int i=1;i<=10;i++){
            System.out.println("From thread No: "+thread_no+": "+i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args){
        multiThreading_content MTC;
        Scanner sc = new Scanner(System.in);
        System.out.println("How many threads would you like to run?");
        int num = sc.nextInt()<=0 ? 1: sc.nextInt();
        for (int i=1;i<=num;i++){
            MTC = new multiThreading_content(i);
            Thread myThread = new Thread(MTC);
            myThread.start();
            if (i == 3){
                try {
                    myThread.join();// In thread 3 the code will wait for it to finish executing to concurrently run the rest
                } catch (InterruptedException e){
                    System.out.println(e);
                }
            }
        }
    }
}