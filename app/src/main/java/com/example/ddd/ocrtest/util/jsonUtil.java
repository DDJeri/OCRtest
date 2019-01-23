package com.example.ddd.ocrtest.util;

public class jsonUtil {

    public static String getResults(String response){
        if(response.indexOf("words_result") == -1){
            return null;
        }else{
            String results = "";
            int index = 0,tmp = 0;
            while((index = response.indexOf("words\":",tmp)) != -1){
                int lastresult = response.indexOf("}",index);
                results += response.substring(index+9,lastresult-1);
                tmp = lastresult;
            }
            if(results.isEmpty()){
                return "null";
            }else{
                return  results;
            }
        }
    }
}
