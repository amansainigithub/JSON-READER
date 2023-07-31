package com.exe.json.reader.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;


@RestController
@RequestMapping("jsonReader")
public class HomeController {

    private StringBuilder  pcm;

    private String PUBLIC_CLASS = "public Class ";
    private String CURLY_BRACES_OPEN = " { ";
    private String CURLY_BRACES_CLOSE = " } ";
    private String PRIVATE_VARIABLE_MAKER = "private String ";
    private String PRIVATE_OBJECT_VARIABLE_MAKER = "private ";
    private String SEMI_COLON = " ; ";
    private String LIST_VAR_OPEN="List< ";
    private String LIST_VAR_CLOSE=" > ";
    private String NEXT_LINE = " \n ";
    ArrayList<String> deepList = null;
    ArrayList<String> classProcessMakerList= null;


    @PostMapping("/jp")
    public String JsonParserWithCom(@RequestBody String jsonP) {
        System.out.println("****************Prepare To Fly*********************");
        pcm = new StringBuilder();
        classProcessMakerList = new ArrayList<>();
        deepList = new ArrayList<String>();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonP);
        JsonObject obj = element.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {

            if ((obj.get(entry.getKey()).isJsonObject())) {
                pcm.append("{" + entry.getKey() + "}" + NEXT_LINE);
                deepList.add(entry.getKey());
                classMaker(entry.getKey());
                isJsonObject(obj.get(entry.getKey()));
                pcm.append("{" + entry.getKey() + "}" + NEXT_LINE);


            } else if (obj.get(entry.getKey()).isJsonArray()) {
                pcm.append("{" + entry.getKey() + "}" + NEXT_LINE);
                deepList.add(entry.getKey());
                classMaker(entry.getKey());
                isJsonArray(obj.get(entry.getKey()));
                pcm.append("{" + entry.getKey() + "}" + NEXT_LINE);
            } else if ((obj.get(entry.getKey()).isJsonPrimitive())) {
                System.out.println("JSON PREMITIVE :: " + entry.getKey());
            }
        }

        //calling Split Classes
        return this.splitClasses();
    }


    public String splitClasses()
    {
        //Reverse ArrayList [Helping to creating a class with proper manner]
        Collections.reverse(deepList);

        String stringBuilderVar = pcm.toString();
        System.out.println("REVERSE DEEP-LIST :: " + deepList);
        String splitSTR = null;
        for (String i : deepList) {
            String splitClass[] = stringBuilderVar.split("[{]" + i + "[}]");
            splitSTR = splitClass[1];
            stringBuilderVar = stringBuilderVar.replace(splitSTR, "");
            stringBuilderVar = stringBuilderVar.replace("{" + i + "}", "");
            classProcessMakerList.add(splitSTR);
        }

        System.out.println("************************");
        System.out.println("ClassMakerProcessList START" + classProcessMakerList.toString() + " ClassMakerProcessList END ");
        return classProcessMakerList.toString();
    }


    public void isJsonObject(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {

            //inside DEEP ARR
            if((obj.get(entry.getKey()).isJsonArray()))
            {
                privateListMakerVariable(entry.getKey());
                pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                deepList.add(entry.getKey());
                classMaker(entry.getKey());
                isJsonArray(obj.get(entry.getKey()));
                pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
            }

            //inside DEEP OBJ
            else if((obj.get(entry.getKey()).isJsonObject()))
            {
                privateObjectMakerVariable(entry.getKey());
                deepList.add(entry.getKey());
                pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                classMaker(entry.getKey());
                isJsonObject(obj.get(entry.getKey()));
                pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
            }else {
                //APPEND KEYS TO PRIVATE VAR [DEEP OBJECT]
                privateVariableMaker(entry.getKey());
            }
        }
    }


    public void isJsonArray(JsonElement element) {
        JsonArray jsonArray = element.getAsJsonArray();
        for(JsonElement ele : jsonArray) {
            JsonObject obj = ele.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {

                //inside DEEP ARR
                if((obj.get(entry.getKey()).isJsonArray()))
                {
                    privateListMakerVariable(entry.getKey());
                    pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                    deepList.add(entry.getKey());
                    classMaker(entry.getKey());
                    isJsonArray(obj.get(entry.getKey()));
                    pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                }
                //inside DEEP OBJ
                else if((obj.get(entry.getKey()).isJsonObject()))
                {
                    privateObjectMakerVariable(entry.getKey());
                    deepList.add(entry.getKey());
                    pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                    classMaker(entry.getKey());
                    isJsonArray(obj.get(entry.getKey()));
                    pcm.append("{"+entry.getKey()+"}" + NEXT_LINE);
                }
                else
                {
                    //APPEND KEYS TO PRIVATE VAR [DEEP OBJECT]
                    privateVariableMaker(entry.getKey());
                }
            }
        }
    }



    public void classMaker(String className)
    {
        pcm.append( PUBLIC_CLASS + firstLetterCap(className.toLowerCase()));
        curlsBracesOpening();
    }
    public void curlsBracesOpening()
    {
        pcm.append( NEXT_LINE + CURLY_BRACES_OPEN + NEXT_LINE );
    }
    public void curlsBracesClosing()
    {
        pcm.append( NEXT_LINE + CURLY_BRACES_CLOSE + NEXT_LINE );
    }
    public void privateVariableMaker(String variableName)
    {
        pcm.append( PRIVATE_VARIABLE_MAKER + variableName.toLowerCase() + SEMI_COLON + NEXT_LINE );
    }
    public void privateObjectMakerVariable(String variableName)
    {
        String declaration = variableName.substring(0, 1).toUpperCase() + variableName.substring(1).toLowerCase();
        pcm.append( PRIVATE_OBJECT_VARIABLE_MAKER + declaration +" "+ variableName.toLowerCase() +  SEMI_COLON + NEXT_LINE );
    }
    public void privateListMakerVariable(String variableName)
    {
        pcm.append( PRIVATE_OBJECT_VARIABLE_MAKER + LIST_VAR_OPEN + variableName.substring(0, 1).toUpperCase() + variableName.substring(1) + LIST_VAR_CLOSE  + variableName + SEMI_COLON + NEXT_LINE );
    }

    static String firstLetterCap( String s )
    {
        // to keep track of spaces
        int ctr = 0 ;
        // variable to hold the length of the string
        int n = s.length( ) ;
        // converting the string expression to character array
        char ch[ ] = s.toCharArray( ) ;
        // // keep track of indices of ch[ ] array
        int c = 0 ;
        // traversing through each character of the array
        for ( int i = 0; i < n; i++ )
        {
            // The first position of the array i.e., the first letter must be
            // converted to the upper case. We checked this before the second
            // if statement as that statement is executed only when it encounters space and,
            // there is no space before the first letter of a string.
            if( i == 0 )
                // converting to upper case using the toUpperCase( ) in-built function
                ch[ i ] = Character.toUpperCase( ch[ i ] ) ;
            // as we need to remove all the spaces in between, we check for empty
            // spaces
            if ( ch[ i ] == ' ' )
            {
                // incrementing the space counter by 1
                ctr++ ;
                // converting the letter immediately after the space to upper case
                ch[ i + 1 ] = Character.toUpperCase( ch[ i + 1] ) ;
                // continue the loop
                continue ;
            }
            // if the space is not encountered simply copy the character
            else
                ch[ c++ ] = ch[ i ] ;
        }
        // new string will be reduced as the spaces have been removed
        // Thus returning the new string with new size
        return String.valueOf( ch, 0, n - ctr ) ;
    }
    // Driver code
}

