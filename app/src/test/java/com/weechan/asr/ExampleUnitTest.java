package com.weechan.asr;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        int[] a = new int[]{4,5,1,6,2,7,3,8};
        qs(a, 0, 6);
        for (int i : a) {
            System.out.println(i);
        }

        System.out.println(GetLeastNumbers_Solution(a,4));

    }

    public ArrayList<Integer> GetLeastNumbers_Solution(int[] input, int k) {
        if (input.length < k || input == null) return new ArrayList<>();
        int index = partition(input, 0, input.length - 1);
        int start = 0;
        int end = input.length - 1;
        while (index != k - 1) {
            if (index > k - 1) {
                end = index - 1;
                index = partition(input, start, end);
            }

            if (index < k - 1) {
                start = index + 1;
                index = partition(input, start, end);
            }
        }
        ArrayList<Integer> r = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            r.add(input[i]);
        }
        return r;
    }

    public void qs(int[] input, int l, int r) {
        if(l >= r ) return;
        int mi = partition(input, l, r);
        qs(input, l, mi - 1);
        qs(input, mi + 1, r);
    }

    public int partition(int[] input, int l, int r) {
        int space = input[l];
        while (l < r) {
            while (l < r && input[r] > space) {
                r--;
            }
            input[l] = input[r];
            while (l < r && input[l] <= space) {
                l++;
            }
            input[r] = input[l];
        }
        input[l] = space;
        return l;
    }

}