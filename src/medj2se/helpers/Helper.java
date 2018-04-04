package medj2se.helpers;

import java.util.*;

public class Helper{

    public static <T> ArrayList<T> createArrayList(T ... elements){
    ArrayList<T> list = new ArrayList<T>();
    for(T element: elements){
        list.add(element);
    }
    return list; 
    }

    public static <T> void printArrayList(ArrayList<T> elements){
        for(T element: elements){
            System.out.print(element+" "); 
        }
        System.out.println();
    }

    public static <T> void printHashMap(HashMap<T,T> elements){
        for(Map.Entry<T,T> entry: elements.entrySet()) {
            System.out.print(entry.getKey() + "=>" + entry.getValue()+", ");
        }
        System.out.println(); 
    }

    public static HashMap<String, String> createHashMap(String... data){
    HashMap<String, String> result = new HashMap<String, String>();

    if(data.length % 2 != 0) 
        throw new IllegalArgumentException("Odd number of arguments");      

    String key = null;
    Integer step = -1;

    for(String value : data){
        step++;
        switch(step % 2){
        case 0: 
            if(value == null)
                throw new IllegalArgumentException("Null key value"); 
            key = value;
            continue;
        case 1:             
            result.put(key, value);
            break;
        }
    }

    return result;
}

    public static double calculateDelta(ArrayList firstList, 
            ArrayList secondList){
        double deltaPercent =0;

        if(firstList.isEmpty() && secondList.isEmpty()) {
            return 100.0;
        }

        ArrayList biggerList= new ArrayList();
        ArrayList smallerList =new ArrayList();
        int beforeListSize = 0;
        int afterListSize = 0;

        if( firstList.size() > secondList.size()){
                biggerList = firstList; 
                smallerList = secondList;
        }else if(firstList.size() < secondList.size()){
                biggerList = secondList; 
                smallerList = firstList;
        }else{
        biggerList = secondList;  
        smallerList = firstList;
        }

        beforeListSize = biggerList.size();
        biggerList.removeAll(smallerList);
        afterListSize = biggerList.size(); 

        int howManyMatch = beforeListSize - afterListSize;
        //System.out.println(howManyMatch);


        deltaPercent = (howManyMatch/(double)beforeListSize) * 100;
        //System.out.println("DeltaPercent:==== "+deltaPercent);

        if (Double.isNaN(deltaPercent)){
            deltaPercent = 0.0; 
        }

        return deltaPercent;
    }

    public static <T> void debug(String desc, T element){
        System.out.println(desc+": " + element);
    }
}
