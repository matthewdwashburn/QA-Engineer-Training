package com.revature;

public class FindOutlier {
  static int find(int[] integers) {
    int length = integers.length;
    boolean odd_list = false;
    int odd_count = 0;
    
    for(int i = 0; i < 3; i++) {
      if(integers[i] % 2 != 0) {
        odd_count++;
      }
    }
    
    if(odd_count > 1) {
      odd_list = true;
    }
    
    if(odd_list) { // Odd list, even outlier
      for(int i = 0; i < length; i++) {
        if(integers[i] % 2 == 0) {
          return integers[i];
        }
      }
    } else { // Even list, odd outlier
      for(int i = 0; i < length; i++) {
        if(integers[i] % 2 != 0) {
          return integers[i];
        }
      }
    }
    return 0;
  }
}