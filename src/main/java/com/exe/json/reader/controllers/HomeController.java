package com.exe.json.reader.controllers;

import com.exe.json.reader.helper.Helper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("jsonReader")
public class HomeController {

    private StringBuilder  pcm;


    private static final String  PUBLIC_CLASS = "public class ";
    private  static final String CURLY_BRACES_OPEN = " { ";
    private  static final String CURLY_BRACES_CLOSE = " } ";
    private  static final String PRIVATE_VARIABLE_MAKER = "private String ";
    private  static final String PRIVATE_OBJECT_VARIABLE_MAKER = "private ";
    private  static final String SEMI_COLON = " ; ";
    private  static final String LIST_VAR_OPEN="List< ";
    private  static final String LIST_VAR_CLOSE=" > ";
    private  static final String NEXT_LINE = " \n ";
    private static final String PACKAGE_KEYWORD = "package ";
   private ArrayList<String> deepList = null;
   private Map<String,String> classProcessMakerList= null;
    private static final String  PACKAGE_PATH= "src/main/java/com/exe/json/reader/entities";
    private static final String  PACKAGE_NAME= "com.exe.json.reader.entities";
    private static final String  IMPORT_LIST = "import java.util.List;";

    private static final String  DOT = ".";

    private static final String IMPORT = "import ";


    @PostMapping("/jp")
    public String JsonParserWithCom(@RequestBody String jsonP) {
        System.out.println("****************Prepare To Fly*********************");
        pcm = new StringBuilder();
        classProcessMakerList = new HashMap<>();
        deepList = new ArrayList<>();

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
        for (String classFinder : deepList) {
            String splitClass[] = stringBuilderVar.split("[{]" + classFinder + "[}]");
            splitSTR = splitClass[1];
            stringBuilderVar = stringBuilderVar.replace(splitSTR, "");
            stringBuilderVar = stringBuilderVar.replace("{" + classFinder + "}", "");
            classProcessMakerList.put( classFinder , splitSTR );
        }

        System.out.println("************************");
        //System.out.println("ClassMakerProcessList START" + classProcessMakerList.toString() + " ClassMakerProcessList END ");
        this.classMaker();
        return classProcessMakerList.toString();
    }


    public void classMaker()
    {
        System.out.println( "Class Name :: " + getClass().getName());
        System.out.println( "Package name ::" + PACKAGE_NAME);

        for(String classes_key : classProcessMakerList.keySet())
        {
            try {
                     // Creates a Writer using FileWriter
                     FileWriter output = new FileWriter(PACKAGE_PATH + File.separator + classes_key + ".java");
                     // Writes the program to file

                     output.write(PACKAGE_KEYWORD + PACKAGE_NAME + SEMI_COLON + NEXT_LINE);

                     if(classProcessMakerList.get(classes_key).contains("List"))
                     {
                         output.write(IMPORT_LIST + NEXT_LINE);
                     }

                     output.write(classProcessMakerList.get(classes_key) + NEXT_LINE + CURLY_BRACES_CLOSE);
                    // Closes the writer
                    output.close();
                    System.out.println( "Java File Created Success :: " + classes_key);
            }
            // Catch block to handle if exception occurs
            catch (IOException e) {
                // Print the exception
                System.out.print(e.getMessage());
            }
        }
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
        pcm.append( PUBLIC_CLASS + Helper.firstLetterCap(className.toLowerCase()));
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


    // Driver code
}

