package com.revature;

import java.util.List;
import java.util.Arrays;

public class VowelCount {
    public static int getCount(String str) {
    Character[] vowels = {'a', 'e', 'i', 'o', 'u'};
    List<Character> vowelList = Arrays.asList(vowels);
    int vowelCount = 0;
    for(char c : str.toCharArray()) {
      if(vowelList.contains(c)) {
        vowelCount++;
      }
    }
    return vowelCount;
  }
}
